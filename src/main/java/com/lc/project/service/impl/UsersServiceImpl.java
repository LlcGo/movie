package com.lc.project.service.impl;

import java.util.*;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lc.project.common.ErrorCode;
import com.lc.project.exception.BusinessException;
import com.lc.project.mapper.UsersMapper;
import com.lc.project.model.dto.user.UpdatePassWord;
import com.lc.project.model.dto.user.UserQueryRequest;
import com.lc.project.model.entity.MyFriends;
import com.lc.project.model.entity.Users;
import com.lc.project.service.MyFriendsService;
import com.lc.project.service.UsersService;
import com.lc.project.utils.Aig;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.util.stream.Collectors;

import static com.lc.project.constant.UserConstant.USER_LOGIN_STATE;

/**
 * @author asus
 * @description 针对表【users】的数据库操作Service实现
 * @createDate 2023-12-31 15:17:14
 */
@Service
@Slf4j
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users>
        implements UsersService {
    @Resource
    private UsersMapper userMapper;

    @Resource
    @Lazy
    private MyFriendsService myFriendsService;

    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "Lc";

    //TODO 用户名设置随机
    @Override
    public String userRegister(String userAccount, String userPassword, String checkPassword) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 5 || checkPassword.length() < 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次输入的密码不一致");
        }
        synchronized (userAccount.intern()) {
            // 账户不能重复
            QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", userAccount);
            long count = userMapper.selectCount(queryWrapper);
            if (count > 0) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
            }
            // 2. 加密
            String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
            // 3. 插入数据
            Users user = new Users();
            user.setUsername(userAccount);
            user.setPassword(encryptPassword);
            boolean saveResult = this.save(user);
            if (!saveResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "注册失败，数据库错误");
            }
            return user.getId();
        }
    }

    @Override
    public Users userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号错误");
        }
        if (userPassword.length() < 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码错误");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", userAccount);
        queryWrapper.eq("password", encryptPassword);
        Users user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户不存在或密码错误");
        }
        // 3. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, user);
        return user;
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @Override
    public Users getLoginUser(HttpServletRequest request) {
        String id = request.getSession().getId();
        System.out.println(id);
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        Users currentUser = (Users) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接走缓存）
        String userId = currentUser.getId();
        currentUser = this.getById(userId);
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        return currentUser;
    }

    @Override
    public Users getLoginUser() {
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        Users currentUser = (Users) userObj;
        return currentUser;
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    //TODO
    @Override
    public boolean isAdmin(HttpServletRequest request) {
        // 仅管理员可查询
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        Users user = (Users) userObj;
//        return user != null && ADMIN_ROLE.equals(user.getUserRole());
        return user != null;
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public boolean userLogout(HttpServletRequest request) {
        if (request.getSession().getAttribute(USER_LOGIN_STATE) == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "未登录");
        }
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return true;
    }

    @Override
    public boolean updatePassWord(UpdatePassWord updatePassWord) {
        String newPassWord = updatePassWord.getNewPassword();
        String oldPassword = updatePassWord.getOldPassword();
        Users loginUser = getLoginUser();
        String password = loginUser.getPassword();
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + oldPassword).getBytes());
        if (!encryptPassword.equals(password)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "原密码不正确");
        }
        UpdateWrapper<Users> usersUpdateWrapper = new UpdateWrapper<>();
        String newEncryptPassword = DigestUtils.md5DigestAsHex((SALT + newPassWord).getBytes());
        usersUpdateWrapper.eq("id", loginUser.getId());
        usersUpdateWrapper.set("password", newEncryptPassword);
        return this.update(usersUpdateWrapper);
    }

    @Override
    public List<Users> searchFriend(UserQueryRequest userQueryRequest) {
        String nickname = userQueryRequest.getNickname();
        String sex = userQueryRequest.getSex();
//        String likeType = userQueryRequest.getLikeType();
        QueryWrapper<Users> wrapper = new QueryWrapper<>();
        wrapper.like(StrUtil.isNotBlank(nickname),"nickname",nickname);
        wrapper.eq(StrUtil.isNotBlank(sex),"sex",sex);
//        wrapper.like(StrUtil.isNotBlank(likeType),"likeType",likeType);
        Users loginUser = getLoginUser();
        String id = loginUser.getId();
        //过滤掉自己 还有朋友
        wrapper.ne("id",id);
        List<MyFriends> list = myFriendsService.getMyFriends(Long.parseLong(id));

        //我的每一个朋友的id
        List<String> myFriendIds = list.stream().map(myFriends -> {
            Users otherUsers = myFriends.getOtherUsers();
            return otherUsers.getId();
        }).collect(Collectors.toList());


        List<Users> search = this.list(wrapper);

        return search.stream().filter(searchUser -> {
            //每一个准备过滤的用户id
            String id1 = searchUser.getId();
            //我的朋友的id
            return !myFriendIds.contains(id1);
        }).collect(Collectors.toList());
    }



    @Override
    public List<Users> matchUsers(Integer num, Users loginUser) {
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id","likeType");
        queryWrapper.isNotNull("likeType");
        List<Users> userList = this.list(queryWrapper);
        String tags = loginUser.getLikeType();
        Gson gson = new Gson();
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        //long 为 分数
        ArrayList<Pair<Users,Long>> list = new ArrayList<>();
        for (Users user : userList) {
            String tagUser = user.getLikeType();
            if (StringUtils.isBlank(tagUser) || Objects.equals(loginUser.getId(), user.getId())) {
                continue;
            }
            List<String> userTagLists = gson.fromJson(tagUser, new TypeToken<List<String>>() {
            }.getType());
            long distance = Aig.minDistance(tagList, userTagLists);
            list.add(new Pair<>(user, distance));
        }
        List<Pair<Users, Long>> pairList = list.stream()
                .sorted((a, b) -> (int) (a.getValue() - b.getValue()))
                .limit(num)
                .collect(Collectors.toList());
        //取出每个用户的id 进行查询
        List<String> userIds = pairList.stream()
                .map(p -> p.getKey().getId())
                .collect(Collectors.toList());
        QueryWrapper<Users> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("id",userIds);
        //查询出了没有顺序的用户
        Map<String, List<Users>> unOrderUser = this.list(userQueryWrapper)
                .stream().collect(Collectors.groupingBy(Users::getId));
        ArrayList<Users> finalUsers = new ArrayList<>();
        userIds.forEach(id -> { finalUsers.add(unOrderUser.get(id).get(0));});

        List<MyFriends> myFriendsList = myFriendsService.getMyFriends(Long.parseLong(loginUser.getId()));

        //我的每一个朋友的id
        List<String> myFriendIds = myFriendsList.stream().map(myFriends -> {
            Users otherUsers = myFriends.getOtherUsers();
            return otherUsers.getId();
        }).collect(Collectors.toList());
        return  finalUsers.stream().filter(searchUser -> {
            //每一个准备过滤的用户id
            String id1 = searchUser.getId();
            //我的朋友的id
            return !myFriendIds.contains(id1);
        }).collect(Collectors.toList());
    }

}





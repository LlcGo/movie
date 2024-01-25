package com.lc.project.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.lc.project.common.BaseResponse;
import com.lc.project.common.DeleteRequest;
import com.lc.project.common.ErrorCode;
import com.lc.project.common.ResultUtils;
import com.lc.project.exception.BusinessException;
import com.lc.project.model.dto.security.QCCode;
import com.lc.project.model.dto.user.*;
import com.lc.project.model.entity.Users;
import com.lc.project.model.enums.PicCodeEnum;
import com.lc.project.model.vo.UserVo;
import com.lc.project.service.UsersService;
import com.lc.project.utils.CaptchaUtil;
import com.lc.project.utils.RedisUtils;
import com.lc.project.utils.WebServletUtil;
import io.lettuce.core.RedisException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.DataFormatException;

/**
 * 用户接口
 *
 * @author Lc
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UsersService userService;

    @Resource
    private RedisUtils redisUtils;

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<String> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword)) {
            return null;
        }
        String result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<Users> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Users user = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(user);
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    @GetMapping("/get/login")
    public BaseResponse<UserVo> getLoginUser(HttpServletRequest request) {
        Users user = userService.getLoginUser(request);
        UserVo userVO = new UserVo();
        BeanUtils.copyProperties(user, userVO);
        return ResultUtils.success(userVO);
    }


    /**
     * 创建用户
     *
     * @param userAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<String> addUser(@RequestBody UserAddRequest userAddRequest, HttpServletRequest request) {
        if (userAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Users user = new Users();
        BeanUtils.copyProperties(userAddRequest, user);
        boolean result = userService.save(user);
        if (!result) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR);
        }
        return ResultUtils.success(user.getId());
    }

    /**
     * 删除用户
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 更新用户
     *
     * @param userUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest, HttpServletRequest request) {
        if (userUpdateRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Users user = new Users();
        BeanUtils.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        return ResultUtils.success(result);
    }

    @PostMapping("/updatePassWord")
    public BaseResponse<Boolean> updateUser(@RequestBody UpdatePassWord updatePassWord, HttpServletRequest request) {
        if (updatePassWord == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean result = userService.updatePassWord(updatePassWord);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取用户
     *
     * @param id
     * @param request
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<UserVo> getUserById(int id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Users user = userService.getById(id);
        UserVo userVO = new UserVo();
        BeanUtils.copyProperties(user, userVO);
        return ResultUtils.success(userVO);
    }

    /**
     * 获取用户列表
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list")
    public BaseResponse<List<UserVo>> listUser(UserQueryRequest userQueryRequest, HttpServletRequest request) {

        Users userQuery = new Users();
        if (userQueryRequest != null) {
            BeanUtils.copyProperties(userQueryRequest, userQuery);
        }
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>(userQuery);
        List<Users> userList = userService.list(queryWrapper);
        List<UserVo> userVOList = userList.stream().map(user -> {
            UserVo userVO = new UserVo();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
        return ResultUtils.success(userVOList);
    }

    /**
     * 分页获取用户列表
     *
     * @param userQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<UserVo>> listUserByPage(UserQueryRequest userQueryRequest, HttpServletRequest request) {
        long current = 1;
        long size = 10;
        Users userQuery = new Users();
        if (userQueryRequest != null) {
            BeanUtils.copyProperties(userQueryRequest, userQuery);
            current = userQueryRequest.getCurrent();
            size = userQueryRequest.getPageSize();
        }
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>(userQuery);
        Page<Users> userPage = userService.page(new Page<>(current, size), queryWrapper);
        Page<UserVo> userVOPage = new PageDTO<>(userPage.getCurrent(), userPage.getSize(), userPage.getTotal());
        List<UserVo> userVOList = userPage.getRecords().stream().map(user -> {
            UserVo userVO = new UserVo();
            BeanUtils.copyProperties(user, userVO);
            return userVO;
        }).collect(Collectors.toList());
        userVOPage.setRecords(userVOList);
        return ResultUtils.success(userVOPage);
    }

    @PostMapping("/search/friend")
    public BaseResponse<List<Users>> searchFriends(@RequestBody UserQueryRequest userQueryRequest, HttpServletRequest request) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<Users> usersList = userService.searchFriend(userQueryRequest);
        return ResultUtils.success(usersList);
    }


    /**
     * 利用验证码工具类 ,生成验证码，将图形返回到页面
     *
     * @param codeTypeEnum loginCode:登录图形验证码 registerCode:注册图形验证码 forgetPwdCode:忘记密码图形验码
     * @param request
     * @param response
     * @throws IOException
     * @throws DataFormatException
     */
    @GetMapping("/{codeTypeEnum}/getVerifyCode")
    public void getVerifyCode(@PathVariable PicCodeEnum codeTypeEnum, HttpServletRequest request,
                              HttpServletResponse response) throws IOException, DataFormatException {
        String codeType = codeTypeEnum.toString();
        // 验证路径参数
        if (!("loginCode".equals(codeType) || "registerCode".equals(codeType)
                || "forgetPwdCode".equals(codeType))) {
            throw new DataFormatException("验证码参数有误");
        }
        // 通知浏览器不要缓存
        response.setDateHeader("Expires", 0);
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "No-cache");
//        request.getSession().removeAttribute(codeType);
        //图形验证码工具类
        CaptchaUtil util = CaptchaUtil.Instance();
        String code = util.getString();

        // 获取redis的key
        String key = CaptchaUtil.getRedisKey(codeTypeEnum, WebServletUtil.getClientIpAddress(request));
        // 将验证码保存进redis中
        boolean isSuccess = redisUtils.set(key, code, CaptchaUtil.PIC_CODE_EXPIRED_TIME);
        if (!isSuccess) {
            throw new RedisException("缓存验证码错误");
        }

        // 输出打web页面
        ImageIO.write(util.getImage(), "jpg", response.getOutputStream());
    }

    /**
     * 验证图形验证码
     *
     * @param codeTypeEnum 验证码类别{loginCode:登录图形验证码,registerCode:注册图形验证码,forgetPwdCode:忘记密码图形验码}
     * @param request
     * @return
     * @para QCCode 二维码数据
     */
    @PostMapping("/{codeTypeEnum}/comparePicCode")
    public BaseResponse<?> comparePicCode(@PathVariable PicCodeEnum codeTypeEnum, @RequestBody QCCode qcCode,
                                          HttpServletRequest request) throws DataFormatException {
//        log.debug("验证验证码");
        String codeType = codeTypeEnum.toString();
        String picCode = qcCode.getPicCode();
        String loginVerifyCodeRandom = qcCode.getLoginVerifyCodeRandom();
        if (picCode == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (loginVerifyCodeRandom == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 验证路径参数
        if (!("loginCode".equals(codeType))) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 客户端ip
        String clientIp = WebServletUtil.getClientIpAddress(request);
        // redis中验证码的key
        String key = CaptchaUtil.getRedisKey(codeTypeEnum, clientIp + ":" + loginVerifyCodeRandom);
        // 查看key是否存在
        if (!redisUtils.hasKey(key)) {
            // key不存在，返回验证码已失效
            throw new BusinessException(ErrorCode.QC_CODE_TIME_OUT);
        }
        // 从redis中获取验证码
        String code = (String) redisUtils.get(key);
        if (code == null) {
            throw new BusinessException(ErrorCode.QC_CODE_TIME_OUT);
        }
        log.debug(clientIp + " 的验证码为：" + code);
        if (picCode.equalsIgnoreCase(code)) {
            return ResultUtils.success("success");
        } else {
            return ResultUtils.error(ErrorCode.QC_CODE_ERROR);
        }
    }

    @GetMapping("/matchFriend")
    public BaseResponse<List<Users>> matchFriend(Integer num, HttpServletRequest request) {
        Users loginUser = userService.getLoginUser(request);
        List<Users> usersList = userService.matchUsers(num, loginUser);
        return ResultUtils.success(usersList);
    }

    @PostMapping("/updateUserImg")
    public BaseResponse<Boolean> updateUserImg(String imgUrI){
       Boolean flag = userService.updateUserImg(imgUrI);
       return ResultUtils.success(flag);
    }

}

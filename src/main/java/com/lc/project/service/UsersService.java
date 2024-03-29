package com.lc.project.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lc.project.model.dto.user.UpdatePassWord;
import com.lc.project.model.dto.user.UserQueryRequest;
import com.lc.project.model.entity.Users;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author asus
* @description 针对表【users】的数据库操作Service
* @createDate 2023-12-31 15:17:14
*/
public interface UsersService extends IService<Users> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户 id
     */
    String userRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    Users userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    Users getLoginUser(HttpServletRequest request);


    /**
     * 获取当前登录用户
     *
     * @param
     * @return
     */
    Users getLoginUser();
    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    boolean userLogout(HttpServletRequest request);

    boolean updatePassWord(UpdatePassWord updatePassWord);

    List<Users> searchFriend(UserQueryRequest userQueryRequest);

    List<Users> matchUsers(Integer num, Users loginUser);

    Boolean updateUserImg(String imgUrI);

    /**
     * 管理员登录
     * @param userAccount
     * @param userPassword
     * @param request
     * @return
     */
    Users adminLogin(String userAccount, String userPassword, HttpServletRequest request);

    QueryWrapper<Users> getqueryWrapper(Users userQuery);

    boolean addUser(Users user);

    boolean removeUser(Long id);

    boolean reUser(Long id);

    Boolean updateAdminPassword(String password, String id);
}

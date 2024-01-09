package com.lc.project.model.dto.favorites;

import lombok.Data;

import java.io.Serializable;

/**
 * 收藏请求
 *
 * @author Lc
 */
@Data
public class FavoritesAddRequest implements Serializable {

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 性别
     */
    private Integer gender;

    /**
     * 用户角色: user, admin
     */
    private String userRole;

    /**
     * 密码
     */
    private String userPassword;

    private static final long serialVersionUID = 1L;
}
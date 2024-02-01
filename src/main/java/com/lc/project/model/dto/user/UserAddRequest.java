package com.lc.project.model.dto.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户创建请求
 *
 * @author Lc
 */
@Data
public class UserAddRequest implements Serializable {



    /**
     * 用户名，账号
     */
    private String username;


    /**
     * 昵称
     */
    private String nickname;


    /**
     * 用户角色: user, admin
     */
    private String userRole;

    private static final long serialVersionUID = 1L;
}
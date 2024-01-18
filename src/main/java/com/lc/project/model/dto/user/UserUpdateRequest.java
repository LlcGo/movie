package com.lc.project.model.dto.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户更新请求
 *
 * @author Lc
 */
@Data
public class UserUpdateRequest implements Serializable {


    /**
     * 用户id
     */
    private String id;

    /**
     * 用户名，账号
     */
    private String username;



    /**
     * 我的头像，如果没有默认给一张
     */
    private String faceImage;


    /**
     * 昵称
     */
    private String nickname;

    /**
     * 性别
     */
    private String sex;

    /**
     * 喜欢的电影类型
     */
    private String likeType;


    /**
     * 个性签名
     */
    private String signature;




    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
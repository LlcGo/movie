package com.lc.project.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @TableName users
 */
@TableName(value ="users")
@Data
public class Users implements Serializable {
    /**
     *
     */
    @TableId
    private String id;

    /**
     * 用户名，账号
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 我的头像，如果没有默认给一张
     */
    @TableField("face_image")
    private String faceImage;

    /**
     *
     */
    @TableField("face_image_big")
    private String faceImageBig;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 新用户注册后默认后台生成二维码，并且上传到fastdfs
     */
    private String qrcode;

    /**
     *
     */
    private String cid;

    /**
     *
     */
    private Date createTime;

    /**
     *
     */
    private Date updateTime;

    /**
     * 0 未删除 1 已删除
     */
    private Integer isDelete;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
package com.lc.project.model.dto.user;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.lc.project.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户查询请求
 *
 * @author Lc
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserQueryRequest extends PageRequest implements Serializable {
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

    /**
     * 用户角色: user, admin
     */
    private String userRole;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
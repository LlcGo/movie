package com.lc.project.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName my_friends
 */
@TableName(value ="my_friends")
@Data
public class MyFriends implements Serializable {
    /**
     * 表示id
     */
    @TableId
    private String id;

    /**
     * 我的id
     */
    private String myUserId;

    /**
     * 用户的好友id
     */
    private String myFriendUserId;

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
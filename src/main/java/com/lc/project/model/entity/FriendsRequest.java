package com.lc.project.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName friends_request
 */
@TableName(value ="friends_request")
@Data
public class FriendsRequest implements Serializable {
    /**
     * 标识id
     */
    @TableId
    private String id;

    /**
     * 发送信息的用户id
     */
    private String sendUserId;

    /**
     * 接受信息的用户id
     */
    private String acceptUserId;

    /**
     * 发送请求的时间
     */
    private Date requestDateTime;


    /**
     * 0 发送请求 ， 1 拒绝请求 ，2 同意请求
     */
    private Integer state;

    /**
     *
     */
    private Date creatTime;

    /**
     *
     */
    private Date updateTime;

    @TableField(exist = false)
    private Users otherUsers;

    /**
     *
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
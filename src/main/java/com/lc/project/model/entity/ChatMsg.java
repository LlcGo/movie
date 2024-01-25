package com.lc.project.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName chat_msg
 */
@TableName(value ="chat_msg")
@Data
public class ChatMsg implements Serializable {
    /**
     * 信息标识
     */
    @TableId
    private String id;

    /**
     * 发送数据的用户id
     */
    private String sendUserId;

    /**
     * 接受信息的用户id
     */
    private String acceptUserId;

    /**
     * 信息
     */
    private String msg;

    /**
     * 消息是否签收状态
     * 1：签收0：未签收
     */
    private Integer signFlag;

    /**
     * 发送请求的时间
     */
    private Date createTime;

    /**
     *
     */
    private Date updateTime;

    /**
     *
     */
    private Integer isDelete;

    @TableField(exist = false)
    private Users otherUser;

    @TableField(exist = false)
    private Users myUser;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
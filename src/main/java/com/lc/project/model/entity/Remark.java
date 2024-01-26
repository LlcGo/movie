package com.lc.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 评论表
 * @TableName remark
 */
@TableName(value ="remark")
@Data
public class Remark implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private String userId;

    @TableField(exist = false)
    private Users user;
    /**
     * 
     */
    private Integer movieId;

    @TableField(exist = false)
    private Boolean like;

    @TableField(exist = false)
    private Boolean hate;

    @TableField(exist = false)
    private Integer total;
    /**
     * 评论内容
     */
    private String content;

    @TableField(exist = false)
    private RemarkUser remarkUser;
    /**
     * 评分
     */
    private Integer score;

    /**
     * 
     */
    private Date createTime;

    /**
     * 
     */
    private Date updateTime;

    /**
     * 喜欢
     */
    private Integer liked;

    /**
     * 不喜欢
     */
    private Integer disLiked;

    /**
     * 0 未删除 1 已删除
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
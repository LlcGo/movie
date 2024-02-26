package com.lc.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.models.auth.In;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 弹幕表
 * @TableName barrage
 */
@TableName(value ="barrage")
@Data
public class Barrage implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    @TableField(exist = false)
    private Integer total;
    @TableField(exist = false)
    private Movie movie;
    @TableField(exist = false)
    private Users user;
    /**
     * 
     */
    private Integer movieId;

    /**
     * 
     */
    private String userId;

    /**
     * 内容
     */
    private String content;

    /**
     * 弹幕出现时间
     */
    private String appTime;

    /**
     * 弹幕颜色
     */
    private String color;

    /**
     * 类型
     */
    private String barType;

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

    private Integer state;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
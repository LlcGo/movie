package com.lc.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 已购买电影表
 * @TableName purchased
 */
@TableName(value ="purchased")
@Data
public class Purchased implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 
     */
    private String userId;

    /**
     * 
     */
    private Integer movieId;

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
    private Movie movie;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
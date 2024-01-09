package com.lc.project.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 订单表
 * @TableName order
 */
@TableName(value ="movie_order")
@Data
public class Order implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 订单属性（0 开会员，1买电影）
     */
    private Integer state;

    /**
     * 
     */
    private String userId;

    /**
     * 
     */
    private Integer movieId;

    /**
     * 0 未下单 1已下单  2已取消
     */
    private Integer orderState;

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
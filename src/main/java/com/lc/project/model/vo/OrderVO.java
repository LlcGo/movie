package com.lc.project.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.lc.project.model.entity.Movie;
import lombok.Data;

import java.util.Date;

@Data
public class OrderVO {
    /**
     *
     */
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
    private Movie movie;

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
     * vip 开多久
     */
    private Integer vipType;

}

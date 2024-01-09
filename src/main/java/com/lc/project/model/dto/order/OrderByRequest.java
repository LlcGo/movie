package com.lc.project.model.dto.order;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单请求
 *
 * @author Lc
 */
@Data
public class OrderByRequest implements Serializable {



    private Integer orderId;

    /**
     * 订单属性（0 开会员，1买电影）
     */
    private Integer state;

    /**
     * 用户id 后端获取
     */
    private String userId;

    /**
     *
     */
    private Integer movieId;


    /**
     *
     */
    private String date;

}
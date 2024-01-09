package com.lc.project.model.dto.order;

import lombok.Data;

import java.io.Serializable;

/**
 * 订单请求
 *
 * @author Lc
 */
@Data
public class OrderAddRequest implements Serializable {



    /**
     * 订单属性（0 开会员，1买电影）
     */
    private Integer state;


    /**
     *
     */
    private Integer movieId;


}
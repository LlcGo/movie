package com.lc.project.model.dto.order;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.lc.project.common.PageRequest;
import com.lc.project.model.entity.Movie;
import com.lc.project.model.entity.Users;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 用户查询请求
 *
 * @author Lc
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class OrderQueryRequest extends PageRequest implements Serializable {

    /**
     *
     */
    private Integer id;

    /**
     * 订单属性（0 开会员，1买电影）
     */
    private Integer state;

    private String nickName;

    /**
     *
     */
    private String movieName;

    /**
     * 0 未下单 1已下单  2已取消
     */
    private Integer orderState;

    private List<String> date;

    /**
     * 开的是哪种类型vip
     */
    private Integer vipType;



    private static final long serialVersionUID = 1L;
}
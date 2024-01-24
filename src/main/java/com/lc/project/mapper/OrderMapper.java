package com.lc.project.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lc.project.model.entity.Order;

import java.util.List;

/**
* @author asus
* @description 针对表【order(订单表)】的数据库操作Mapper
* @createDate 2024-01-06 11:09:42
* @Entity com.lc.project.model.entity.Order
*/
public interface OrderMapper extends BaseMapper<Order> {

    List<Order> getOrderAndMovieByUserId(String id);
}





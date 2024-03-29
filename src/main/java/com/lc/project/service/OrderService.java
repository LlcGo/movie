package com.lc.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lc.project.model.dto.order.OrderByRequest;
import com.lc.project.model.dto.order.OrderQueryRequest;
import com.lc.project.model.entity.Movie;
import com.lc.project.model.entity.Order;

import java.util.List;
import java.util.Map;

/**
* @author asus
* @description 针对表【order(订单表)】的数据库操作Service
* @createDate 2024-01-06 11:09:42
*/
public interface OrderService extends IService<Order> {

    Page<Order> listPage(OrderQueryRequest orderQueryRequest);

    List<Order> getListOrder(OrderQueryRequest orderQuery);

    Order getOrderById(long id);

    boolean toUpdate(Order order);

    void validOrder(Order order, boolean addMovie);

    /**
     * 取消订单
     * @param id
     * @return
     */
    boolean removeOrderById(long id);

    Integer toAddOrder(Order order);

    Boolean orderBuy(OrderByRequest orderByRequest);

    List<Order> getOrderByUserId();

    /**
     * 直接购买
     * @param orderByRequest
     * @return
     */
    Boolean toBuy(OrderByRequest orderByRequest);

    /**
     * 用户下单统计（根据各个电影的类型进行统计出购买的数量）
     * @return
     */
    Map<Integer, List<Movie>> getEChars();

    /**
     * 用户vip 下单 月卡 季卡 年卡统计
     *
     * @return
     */
    Map<Integer, List<Order>> getVipEChars();

}

package com.lc.project.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.lc.project.model.dto.order.OrderByRequest;
import com.lc.project.model.dto.order.OrderQueryRequest;
import com.lc.project.model.entity.Order;
import com.lc.project.model.vo.OrderVO;

import java.util.List;

/**
* @author asus
* @description 针对表【order(订单表)】的数据库操作Service
* @createDate 2024-01-06 11:09:42
*/
public interface OrderService extends IService<Order> {

    Page<Order> listPage(OrderQueryRequest orderQueryRequest);

    List<Order> getListOrder(Order orderQuery);

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

    List<OrderVO> getOrderByUserId();

}

package com.lc.project.service;

import com.lc.project.model.entity.Order;
import com.lc.project.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class OrderTest {

    @Resource
    private OrderService orderService;

    @Test
    public void testOrderAdd(){
        Order order = new Order();
        order.setState(0);
        order.setUserId("1741446004448710657");
        order.setMovieId(2);
        orderService.toAddOrder(order);
    }

}

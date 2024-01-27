package com.lc.project.rabbitmq;

import com.lc.project.model.entity.Order;
import com.lc.project.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;

/**
 * 消费者
 * 监听死信队列
 */
@Slf4j
@Component
public class Consumer {

    @Resource
    private OrderService orderService;

    @RabbitListener(queues = "dead_queue", ackMode = "MANUAL")
    public void receiveMessage(Message message, Channel channel) {
        String msg = new String(message.getBody());
        int orderId = Integer.parseInt(msg);
        Order order = orderService.getById(orderId);
        Integer orderState = order.getOrderState();
        if (orderState != 1) {
            order.setOrderState(2);
            orderService.updateById(order);
            log.info("已自动修改订单id信息为已取消", new Date().toString(), msg);
        }
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //监听消息队列
    @RabbitListener(queues = "delay_queue", ackMode = "MANUAL")
    public void consumeMessage(Message message, Channel channel)  {
        String msg = new String(message.getBody());
        int orderId = Integer.parseInt(msg);
        Order order = orderService.getById(orderId);
        Integer orderState = order.getOrderState();
        if (orderState != 1) {
            order.setOrderState(2);
            orderService.updateById(order);
            log.info("已自动修改订单id为-->{}信息为已取消,时间{}", orderId, new Date().toString());
        }
        try {
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}

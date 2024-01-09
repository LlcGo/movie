package com.lc.project.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

import static com.lc.project.config.TtlQueueConfig.ORDER_CHANGE;
import static com.lc.project.config.TtlQueueConfig.ROUTING_KEY;

@Slf4j
@Component
public class RabbitMQUtils {

    @Value("${rabbit.ttl}")
    private Integer rabbitTTL;
    @Resource
    private RabbitTemplate rabbitTemplate;


    public void sendMessage(String OrderId){
        log.info("当前时间：{} 发送一条信息给两个TTL队列{}",new Date().toString(),OrderId);
        rabbitTemplate.convertAndSend(ORDER_CHANGE,ROUTING_KEY,OrderId);
    }
}

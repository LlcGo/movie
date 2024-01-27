package com.lc.project.rabbitmq;

import com.lc.project.constant.CommonConstant;
import com.lc.project.utils.RedisUtils;
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

    @Resource
    private RedisUtils redisUtils;

    public void sendMessage(String OrderId){
        log.info("当前时间：{} 发送一条信息给两个TTL队列{}",new Date().toString(),OrderId);
        rabbitTemplate.convertAndSend(ORDER_CHANGE,ROUTING_KEY,OrderId);
    }

    public void sendMessageDLX(String OrderId){
        Integer DDLTime;

        Object time = redisUtils.get(CommonConstant.ORDER_DDL_TIME);
        if(time != null){
            DDLTime = (Integer) time;
        } else {
            DDLTime = rabbitTTL;
        }
        log.info("当前时间：{} 发送一条信息给两个TTL队列{},延迟时间为{}",new Date().toString(),OrderId,DDLTime);
        rabbitTemplate.convertAndSend(
                "delay_exchange",
                "delay_key",
                OrderId, message -> {//配置消息的过期时间
                    message.getMessageProperties().setDelay(DDLTime);
                    return message;
                }
        );

    }
}

package com.lc.project.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * TTL队列
 */
@Configuration
public class TtlQueueConfig {

    //普通交换机名称
    public static final String ORDER_CHANGE = "order_change";
    //死信交换机名称
    public static final String ORDER_DEAD_CHANGE = "order_dead_change";
    //普通队列
    public static final String ORDER_QUEUE = "order_queen";

//    public static final String QUEUE_B = "QB";
    //死信队列
    public static final String DEAD_QUEUE = "dead_queue";

    public static final String ROUTING_KEY = "XA";
    //声明普通交换机
    @Bean("xExchange")
    public DirectExchange xExchange(){
        return new DirectExchange(ORDER_CHANGE);
    }
    //声明死信交换机
    @Bean("yExchange")
    public DirectExchange yExchange(){
        return new DirectExchange(ORDER_DEAD_CHANGE);
    }

    @Value("${rabbit.ttl}")
    private Integer rabbitTTL;

    //声明队列
    @Bean("queueA")
    public Queue queueA(){

        return QueueBuilder.durable(ORDER_QUEUE)
                .deadLetterExchange(ORDER_DEAD_CHANGE) //死信交换机
                .deadLetterRoutingKey("order_dead")  //死信RoutingKey
                .ttl(rabbitTTL)  //消息过期时间
                .build();
    }

//    @Bean("queueB")
//    public Queue queueB(){
//        return QueueBuilder.durable(QUEUE_B)
//                .deadLetterExchange(ORDER_DEAD_CHANGE) //死信交换机
//                .deadLetterRoutingKey("YD")  //死信RoutingKey
//                .ttl(40000)  //消息过期时间
//                .build();
//    }


    //死信队列
    @Bean("deadQ")
    public Queue queueD(){
        return QueueBuilder.durable(DEAD_QUEUE).build();
    }


    //  中转站绑定
    @Bean
    public Binding queueABindingX(@Qualifier("queueA") Queue queueA, @Qualifier("xExchange") DirectExchange xExchange){
        return BindingBuilder.bind(queueA).to(xExchange).with(ROUTING_KEY);
    }

    //绑定  X_CHANGE绑定queueB
//    @Bean
//    public Binding queueBBindingX(@Qualifier("queueB") Queue queueB,@Qualifier("xExchange") DirectExchange xExchange){
//        return BindingBuilder.bind(queueB).to(xExchange).with("XB");
//    }


    //死信交换机绑定
    @Bean
    public Binding queueDBindingY(@Qualifier("deadQ") Queue queueD,@Qualifier("yExchange") DirectExchange yExchange){
        return BindingBuilder.bind(queueD).to(yExchange).with("order_dead");
    }
}

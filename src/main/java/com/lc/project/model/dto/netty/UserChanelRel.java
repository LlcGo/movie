package com.lc.project.model.dto.netty;

import io.netty.channel.Channel;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户id 和channel 的关联关系处理
 */
public class UserChanelRel {
    public static HashMap<String, Channel> manage = new HashMap<>();

    public static  void put(String senderId,Channel channel){
        manage.put(senderId,channel);
    }

    public static Channel get(String senderId){
        return manage.get(senderId);
    }

    /**
     * 所有在线用户
     */
    public static void output(){
        for (Map.Entry<String,Channel> entry  :manage.entrySet()) {
            System.out.println("UserId:"+entry.getKey()
                    +",ChannelId:"+entry.getValue().id().asLongText()
            );
        }
    }

}

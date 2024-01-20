package com.lc.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.lc.project.mapper.ChatMsgMapper;
import com.lc.project.model.dto.netty.DataContent;
import com.lc.project.model.dto.netty.UserChanelRel;
import com.lc.project.model.entity.ChatMsg;
import com.lc.project.service.ChatMsgService;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.lc.project.websocket.ChatHandler.users;

/**
* @author asus
* @description 针对表【chat_msg】的数据库操作Service实现
* @createDate 2023-12-31 15:13:39
*/
@Slf4j
@Service
public class ChatMsgServiceImpl extends ServiceImpl<ChatMsgMapper, ChatMsg>
    implements ChatMsgService{

    @Override
    public List<ChatMsg> getUsersChat(Long userId, Long otherUserId) {
        QueryWrapper<ChatMsg> chatMsgQueryWrapper = new QueryWrapper<>();
        chatMsgQueryWrapper.eq("sendUserId",userId);
        chatMsgQueryWrapper.eq("acceptUserId",otherUserId);
        chatMsgQueryWrapper.orderByAsc("createTime");
        List<ChatMsg> myChat = this.list(chatMsgQueryWrapper);
        QueryWrapper<ChatMsg> otherMsgQueryWrapper = new QueryWrapper<>();
        otherMsgQueryWrapper.eq("sendUserId",otherUserId);
        otherMsgQueryWrapper.eq("acceptUserId",userId);
        otherMsgQueryWrapper.orderByAsc("createTime");
        List<ChatMsg> otherChat = this.list(otherMsgQueryWrapper);
        myChat.addAll(otherChat);
        myChat =myChat.stream().sorted(Comparator.comparing(ChatMsg::getId).reversed()).collect(Collectors.toList());
        return myChat;
    }

    @Override
    public List<ChatMsg> getUnread(long sendUserId) {
        QueryWrapper<ChatMsg> chatMsgQueryWrapper = new QueryWrapper<>();
        chatMsgQueryWrapper.eq("acceptUserId",sendUserId);
        chatMsgQueryWrapper.eq("signFlag",0);
        chatMsgQueryWrapper.orderByAsc("createTime");
        List<ChatMsg> list = this.list(chatMsgQueryWrapper);

        //webSocket推送信息
        Channel channel = UserChanelRel.get(String.valueOf(sendUserId));
        Channel findChanel = users.find(channel.id());
        Gson gson = new Gson();
        if (findChanel != null){
            DataContent dataContentMsg = new DataContent();
            dataContentMsg.setAction(6);
            dataContentMsg.setChatMsgList(list);
            findChanel.writeAndFlush( new TextWebSocketFrame(gson.toJson(dataContentMsg)));
        }
        log.info("用户id----->" + sendUserId + "有" + list.size() + "条未读的消息");

        return list;
    }

    @Override
    public Boolean readMessage(long sendUserId, long rOtherUserId) {
        UpdateWrapper<ChatMsg> chatMsgUpdateWrapper = new UpdateWrapper<>();
        //因为是接受消息处理信息
        //发送的id应该是对方的id
        chatMsgUpdateWrapper.eq("sendUserId",rOtherUserId);
        //接受的id应该的自己的id
        chatMsgUpdateWrapper.eq("acceptUserId",sendUserId);
        chatMsgUpdateWrapper.set("signFlag",1);
        return this.update(chatMsgUpdateWrapper);
    }
}





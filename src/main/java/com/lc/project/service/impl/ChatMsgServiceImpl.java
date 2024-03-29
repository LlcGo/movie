package com.lc.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.lc.project.mapper.ChatMsgMapper;
import com.lc.project.model.dto.netty.DataContent;
import com.lc.project.model.dto.netty.UserChanelRel;
import com.lc.project.model.entity.ChatMsg;
import com.lc.project.model.entity.FriendsRequest;
import com.lc.project.model.entity.RecentChat;
import com.lc.project.service.ChatMsgService;
import com.lc.project.service.FriendsRequestService;
import com.lc.project.service.RecentChatService;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.lc.project.websocket.ChatHandler.threadPoolExecutor;
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


    @Resource
    private ChatMsgMapper chatMsgMapper;

    @Resource
    private RecentChatService recentChatService;

    @Resource
    private FriendsRequestService friendsRequestService;

    @Override
    public List<ChatMsg> getUsersChat(Long userId, Long otherUserId) {
        CompletableFuture<List<ChatMsg>> future01 = CompletableFuture.supplyAsync(()->{
            return chatMsgMapper.getChatAndUsers(userId,otherUserId);
        },threadPoolExecutor);


        CompletableFuture<List<ChatMsg>> future02 = CompletableFuture.supplyAsync(()->{
           return chatMsgMapper.getChatAndOtherUsers(userId,otherUserId);
        },threadPoolExecutor);

        List<ChatMsg> myChat = null;
        List<ChatMsg> otherChat = null;
        try {
            myChat = future02.get();
            otherChat = future01.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        if(myChat.size() == 0 && otherChat.size() == 0){
            return new ArrayList<ChatMsg>();
        }

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
            findChanel.writeAndFlush(new TextWebSocketFrame(gson.toJson(dataContentMsg)));
        }
        log.info("用户id----->" + sendUserId + "有" + list.size() + "条未读的消息");
        return list;
    }

    @Override
    public Boolean readMessage(long sendUserId, long rOtherUserId) {
        //因为是接受消息处理信息
        //发送的id应该是对方的id
        int size = chatMsgMapper.updateByMyIdAndOtherId(sendUserId,rOtherUserId);
        //webSocket推送信息
        Channel channel = UserChanelRel.get(String.valueOf(sendUserId));
        Channel findChanel = users.find(channel.id());
        Gson gson = new Gson();
        if (findChanel != null){
            DataContent dataContentMsg = new DataContent();
            dataContentMsg.setAction(6);
            dataContentMsg.setChatMsgList(new ArrayList<>());
            dataContentMsg.setExtand("look:" + size);
            findChanel.writeAndFlush(new TextWebSocketFrame(gson.toJson(dataContentMsg)));
        }
        return size >= 0;
    }

    @Override
    public boolean saveAndSetRecent(ChatMsg chatMsg) {
        String sendUserId = chatMsg.getSendUserId();
        String acceptUserId = chatMsg.getAcceptUserId();
        QueryWrapper<RecentChat> recentChatQueryWrapper = new QueryWrapper<>();
        recentChatQueryWrapper.eq("userId",acceptUserId);
        recentChatQueryWrapper.eq("acceptUserId",sendUserId);
        long count = recentChatService.count(recentChatQueryWrapper);
        if(count == 0){
            RecentChat recentChat = new RecentChat();
            recentChat.setUserId(acceptUserId);
            recentChat.setAcceptUserId(sendUserId);
            recentChatService.save(recentChat);
        }
        return this.save(chatMsg);
    }

    @Override
    public void getUnreadMessage(String senderId) {
        QueryWrapper<FriendsRequest> friendsRequestQueryWrapper = new QueryWrapper<>();
        friendsRequestQueryWrapper.eq("sendUserId",senderId);
        friendsRequestQueryWrapper.in("state",0,3,4,5,6);
        //未读取的对方发送的请求消息
        long count = friendsRequestService.count(friendsRequestQueryWrapper);


        Channel channel = UserChanelRel.get(senderId);
        Channel findChanel = users.find(channel.id());
        Gson gson = new Gson();
        if (findChanel != null){
            DataContent dataContentMsg = new DataContent();
            dataContentMsg.setAction(6);
            dataContentMsg.setChatMsgList(new ArrayList<>());
            dataContentMsg.setExtand("messageRequest:" + count);
            findChanel.writeAndFlush(new TextWebSocketFrame(gson.toJson(dataContentMsg)));
        }
    }
}





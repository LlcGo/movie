package com.lc.project.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.lc.project.common.ErrorCode;
import com.lc.project.exception.BusinessException;
import com.lc.project.mapper.FriendsRequestMapper;
import com.lc.project.model.dto.netty.DataContent;
import com.lc.project.model.dto.netty.UserChanelRel;
import com.lc.project.model.entity.FriendsRequest;
import com.lc.project.model.entity.MyFriends;
import com.lc.project.model.entity.Users;
import com.lc.project.service.FriendsRequestService;
import com.lc.project.service.MyFriendsService;
import com.lc.project.service.UsersService;
import com.lc.project.utils.RedisUtils;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static com.lc.project.websocket.ChatHandler.threadPoolExecutor;
import static com.lc.project.websocket.ChatHandler.users;

/**
* @author asus
* @description 针对表【friends_request】的数据库操作Service实现
* @createDate 2023-12-31 15:15:05
*/
@Service
public class FriendsRequestServiceImpl extends ServiceImpl<FriendsRequestMapper, FriendsRequest>
    implements FriendsRequestService{

    @Resource
    private RedisUtils redisUtils;

    @Resource
    private UsersService usersService;

    @Resource
    private MyFriendsService myFriendsService;

    @Resource
    private FriendsRequestMapper friendsRequestMapper;


    @Override
    public Boolean requestFriends(String acceptUserId) {
        FriendsRequest friendsRequest = new FriendsRequest();
        friendsRequest.setAcceptUserId(acceptUserId);
        Users loginUser = usersService.getLoginUser();
        String id = loginUser.getId();
        QueryWrapper<FriendsRequest> friendsRequestQueryWrapper = new QueryWrapper<>();
        friendsRequestQueryWrapper.eq("sendUserId",id);
        friendsRequestQueryWrapper.eq("acceptUserId",acceptUserId);
        long count = this.count(friendsRequestQueryWrapper);
        if(count > 0){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"您已发送请求,请稍后");
        }

        //webSocket推送信息
        Channel channel = UserChanelRel.get(String.valueOf(acceptUserId));
        //如果对方在线
        if (channel != null){
            Channel findChanel = users.find(channel.id());
            Gson gson = new Gson();
            if (findChanel != null){
                DataContent dataContentMsg = new DataContent();
                dataContentMsg.setAction(6);
                dataContentMsg.setExtand("对方的好友请求:1");
                findChanel.writeAndFlush(new TextWebSocketFrame(gson.toJson(dataContentMsg)));
            }
        }


        friendsRequest.setSendUserId(id);
        return this.save(friendsRequest);
    }

    @Override
    public Boolean agreeFriend(String sendUserId, String requestId) {
        FriendsRequest friendsRequest = new FriendsRequest();
        Channel channel = UserChanelRel.get(sendUserId);
        //如果现在对方在线
        if(channel != null){
            Channel findChanel = users.find(channel.id());
            Gson gson = new Gson();
            if (findChanel != null){
                DataContent dataContentMsg = new DataContent();
                dataContentMsg.setAction(6);
                dataContentMsg.setChatMsgList(new ArrayList<>());
                dataContentMsg.setExtand("nowMessageRequest:" + 1);
                findChanel.writeAndFlush(new TextWebSocketFrame(gson.toJson(dataContentMsg)));
                //如果对方在线我们同意了设置为 6
                friendsRequest.setState(6);
            }else {
                //如果我们不在线的时候同意设置为 4
                friendsRequest.setState(4);
            }
        }else {
            //如果我们不在线的时候同意设置为 4
            friendsRequest.setState(4);
        }


        Users loginUser = usersService.getLoginUser();
        String currentId = loginUser.getId();
        friendsRequest.setId(requestId);
        threadPoolExecutor.execute(()->{
            // 先查询数据库是否有这俩条数据 如果有直接设置为 0
            QueryWrapper<MyFriends> myFriendsQueryWrapper = new QueryWrapper<>();
            myFriendsQueryWrapper.eq("myUserId",currentId);
            myFriendsQueryWrapper.eq("myFriendUserId",sendUserId);
            long count = myFriendsService.count(myFriendsQueryWrapper);

            if(count > 0){
                UpdateWrapper<MyFriends> myFriendsUpdateWrapper = new UpdateWrapper<>();
                myFriendsUpdateWrapper.set("state",0);
                myFriendsUpdateWrapper.eq("myUserId",currentId);
                myFriendsUpdateWrapper.eq("myFriendUserId",sendUserId);
                myFriendsService.update(myFriendsUpdateWrapper);
            }else {
                MyFriends myFriends = new MyFriends();
                myFriends.setMyFriendUserId(sendUserId);
                myFriends.setMyUserId(currentId);
                myFriendsService.save(myFriends);
            }

            QueryWrapper<MyFriends> myFriendsQueryWrapper2 = new QueryWrapper<>();
            myFriendsQueryWrapper2.eq("myUserId",sendUserId);
            myFriendsQueryWrapper2.eq("myFriendUserId",currentId);
            long count2 = myFriendsService.count(myFriendsQueryWrapper2);
            if(count2 > 0){
                UpdateWrapper<MyFriends> myFriendsUpdateWrapper = new UpdateWrapper<>();
                myFriendsUpdateWrapper.set("state",0);
                myFriendsUpdateWrapper.eq("myUserId",sendUserId);
                myFriendsUpdateWrapper.eq("myFriendUserId",currentId);
                myFriendsService.update(myFriendsUpdateWrapper);
            }else {
                MyFriends myFriends1 = new MyFriends();
                myFriends1.setMyFriendUserId(currentId);
                myFriends1.setMyUserId(sendUserId);
                myFriendsService.save(myFriends1);
            }
        });




        return this.updateById(friendsRequest);
    }


    @Override
    public Boolean deleteFriend(String requestId) {
//        FriendsRequest friendsRequest = new FriendsRequest();
//        friendsRequest.setState(2);
//        friendsRequest.setId(requestId);
//
//        return this.updateById(friendsRequest);
        return null;
    }

    @Override
    public List<FriendsRequest> getMyRequest() {
        Users loginUser = usersService.getLoginUser();
        String currentUserId = loginUser.getId();
        redisUtils.removeCurrent(currentUserId);
        //获得我发送的消息
        //我接受的消息
        List<FriendsRequest> receives = friendsRequestMapper.getReceiveByUserId(currentUserId);
        List<FriendsRequest> requests = friendsRequestMapper.getRequestByUserId(currentUserId);
        requests.addAll(receives);
        return requests.stream()
                .sorted(Comparator.comparing(FriendsRequest::getRequestDateTime).reversed())
                .collect(Collectors.toList());
    }


    @Override
    public Boolean rejectFriend(String requestId, String acceptUserId) {
        //设置为拒绝
        FriendsRequest friendsRequest = new FriendsRequest();

        Channel channel = UserChanelRel.get(acceptUserId);
        //如果对方在
        if(channel != null){
            Channel findChanel = users.find(channel.id());
            Gson gson = new Gson();
            if (findChanel != null){
                //在线
                DataContent dataContentMsg = new DataContent();
                dataContentMsg.setAction(6);
                dataContentMsg.setChatMsgList(new ArrayList<>());
                dataContentMsg.setExtand("nowMessageRequest:" + 1);
                findChanel.writeAndFlush(new TextWebSocketFrame(gson.toJson(dataContentMsg)));
                //对方在线的时候state设置 拒绝 1
                friendsRequest.setState(5);
            }else {
                //不在的时候拒绝设置为 3
                friendsRequest.setState(3);
            }
        }else {
            //不在线 3
            friendsRequest.setState(3);
        }

        friendsRequest.setId(requestId);

        return this.updateById(friendsRequest);
    }

    @Override
    public Integer setReadMessage() {
        Users loginUser = usersService.getLoginUser();
        String currentId = loginUser.getId();
        CompletableFuture<Integer> future01 = CompletableFuture.supplyAsync(() -> {
            return friendsRequestMapper.updateRequestMessageToOne(currentId);
        });
        CompletableFuture<Integer> future02 = CompletableFuture.supplyAsync(() -> {
            return friendsRequestMapper.updateRequestMessageToTwo(currentId);
        });

        CompletableFuture<Integer> future03 = CompletableFuture.supplyAsync(() -> {
            return friendsRequestMapper.updateRequestLineToOne(currentId);
        });
        CompletableFuture<Integer> future04 = CompletableFuture.supplyAsync(() -> {
            return friendsRequestMapper.updateRequestLineToTwo(currentId);
        });
        int count;
        try {
            Integer integer = future01.get();
            Integer integer1 = future02.get();
            Integer integer2 = future03.get();
            Integer integer3 = future04.get();
            count = integer + integer1 + integer2 + integer3;
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return count;
    }
}





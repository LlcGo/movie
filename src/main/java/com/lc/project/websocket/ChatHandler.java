package com.lc.project.websocket;

import com.github.xiaoymin.knife4j.core.util.StrUtil;
import com.google.gson.Gson;
import com.lc.project.model.dto.netty.DataContent;
import com.lc.project.model.dto.netty.UserChanelRel;
import com.lc.project.model.entity.ChatMsg;
import com.lc.project.model.entity.Users;
import com.lc.project.model.enums.MsgActionEnum;
import com.lc.project.service.ChatMsgService;
import com.lc.project.service.UsersService;
import com.lc.project.utils.RedisUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ChatHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {


    private static ChatMsgService chatMsgService;

    private static UsersService usersService;

    private static RedisUtils redisUtils;
    public static void setChatHandler(ChatMsgService chatService, UsersService userService, RedisUtils redisUtil){
        chatMsgService = chatService;
        usersService = userService;
        redisUtils = redisUtil;
    }

    //用于记录通道
    public static final ChannelGroup users =new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(15,
            100,
            1,
            TimeUnit.MINUTES,
            new LinkedBlockingDeque<>(1000),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy());

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame msg) throws Exception {
        Channel channel = ctx.channel();
        //客户端获得的消息
        String content = msg.text();
        System.out.println("接收到的消息" + content);
        Gson gson = new Gson();
        DataContent dataContent = gson.fromJson(content, DataContent.class);
        Integer action = dataContent.getAction();
        if(Objects.equals(action, MsgActionEnum.CONNECT.type)){

            //2.1 当websocket 第一次open的时候，初始化channel，把用的channel 和 userid 关联起来
            String senderId = dataContent.getChatMsg().getSendUserId();
            UserChanelRel.put(senderId,channel);
            //新启一个线程，在连接的时候查看这个用户有多少条未读的信息
            threadPoolExecutor.execute(()->{
                chatMsgService.getUnread(Long.parseLong(senderId));
            });

            //设置当前聊天对象
            String extand = dataContent.getExtand();
            if(StrUtil.isNotBlank(extand)){
                redisUtils.setCurrent(senderId,extand);
            }

            //测试
            for (Channel c: users) {
                System.out.println(c.id().asLongText());
            }
            UserChanelRel.output();
        } else if(Objects.equals(action, MsgActionEnum.CHAT.type)){
            //2.2 聊天类型的消息，把聊天记录保存到数据库，同时标记消息的签收状态[未签收]
            ChatMsg chatMsg = dataContent.getChatMsg();
            String msgContent = chatMsg.getMsg();
            String senderId = chatMsg.getSendUserId();
            Users user = usersService.getById(senderId);
            log.info("用户"+user.getUsername()+"发送了消息");
            String receiverId = chatMsg.getAcceptUserId();
            //保存消息到数据库，如果当前都在聊天设置未已经签收 如果没有就设置未签收
            if(redisUtils.isCurrentChat(senderId,receiverId)){
                chatMsg.setSignFlag(1);
            }
            boolean save = chatMsgService.save(chatMsg);
            chatMsg.setId(chatMsg.getId());
            DataContent dataContentMsg = new DataContent();
            dataContentMsg.setChatMsg(chatMsg);
            //发送消息
            Channel receiverChannel = UserChanelRel.get(receiverId);
            if(receiverChannel ==null){
                //离线用户
                log.info("对方用户离线");
            }else{
                //当receiverChannel 不为空的时候，从ChannelGroup 去查找对应的channel 是否存在
                Channel findChanel = users.find(receiverChannel.id());
                if(findChanel!=null){
                    //用户在线
                    log.info("用户在线id:" + receiverId +"发送的消息"+ gson.toJson(dataContentMsg));
                    receiverChannel.writeAndFlush(new TextWebSocketFrame(gson.toJson(dataContentMsg)));
                }else{
                    log.info("对方用户离线");
                }
            }
        } else if(Objects.equals(action, MsgActionEnum.SIGNED.type)){
            //2.3 签收消息类型，针对具体的消息进行签收，修改数据库中对应消息的签收状态[已签收]
            //扩展字段在signed 类型消息中 ，代表需要去签收的消息id，逗号间隔
//            String msgIdsStr = dataContent.getExtand();
//            String[] msgsId = msgIdsStr.split(",");
//
//            List<ChatMsg> chatList = new ArrayList<>();
//            for (String mid: msgsId) {
//                if(StringUtils.isNotBlank(mid)){
//                    ChatMsg chatMsg = new ChatMsg();
//                    chatMsg.setId(mid);
//                    chatMsg.setSignFlag(Integer.getInteger(mid));
//                }
//            }
//            if(CollectionUtils.isNotEmpty(chatList)){
//                //批量签收
//                chatMsgService.updateBatchById(chatList);
//            }
            //设置当前聊天对象
            String senderId = dataContent.getChatMsg().getSendUserId();
            String extand = dataContent.getExtand();
            if(StrUtil.isNotBlank(extand)){
                redisUtils.setCurrent(senderId,extand);
            }
        } else if(Objects.equals(action, MsgActionEnum.KEEPALIVE.type)){
            //2.4 心跳类型的消息
            System.out.println("收到来自channel 为【"+channel+"】的心跳包");
        }
    }

    /**
     * 用户进入客户端
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("连接成功---》" + ctx.channel().id().asLongText());
//        Users currentUser = usersService.getLoginUser();
//        log.info("连接的用户----》用户id："+ currentUser.getId() + "用户账户" + currentUser.getUsername());
        users.add(ctx.channel());
    }

    /**
     * 用户离开客户端
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        List<String> collect = UserChanelRel.manage.entrySet()
                .stream()
                .filter(kvEntry -> Objects.equals(kvEntry.getValue(), channel))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
        System.out.println(collect);
        String currentId = collect.get(0);
        redisUtils.removeCurrent(currentId);

//        clients.remove(ctx.channel());
        System.out.println("客户端断开连接，端id---》" + ctx.channel().id().asLongText());
        System.out.println("客户端断开连接，长id---》" + ctx.channel().id().asShortText());
    }

}

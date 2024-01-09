package com.lc.project.websocket;

import com.lc.project.service.ChatMsgService;
import com.lc.project.service.UsersService;
import com.lc.project.utils.RedisUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;

@Component
@Slf4j
public class WebSocketServer {

    //主线程 老板负责分发命令
   private NioEventLoopGroup bossGroup = new NioEventLoopGroup();
    //子线程 负责处理工作
   private NioEventLoopGroup workGroup = new NioEventLoopGroup();

    @Resource
    private  ChatMsgService chatMsgService;
    @Resource
    private  UsersService usersService;

    @Resource
    private RedisUtils redisUtils;

    @PostConstruct
    public void start() throws InterruptedException {
            //服务器类
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            //将主线程 和 子线程放入其中
            serverBootstrap.group(bossGroup,workGroup)
                    //放入一个服务器管道
                    .channel(NioServerSocketChannel.class)
                    //初始化管道
                    .childHandler(new ServerInitialzer());
            //绑定端口号，启动服务端
            ChannelFuture future = serverBootstrap.bind(7530).sync();
            //对关闭通道进行监听
//            future.channel().closeFuture().sync();
             ChatHandler.setChatHandler(chatMsgService,usersService,redisUtils);
            if (future.isSuccess()) {
                log.info("启动 Netty Server");
            }
    }

    @PreDestroy
    public void destory() throws InterruptedException {
        bossGroup.shutdownGracefully().sync();
        workGroup.shutdownGracefully().sync();
        log.info("关闭Netty");
    }

}

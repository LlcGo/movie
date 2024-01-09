package com.lc.project.websocket;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.springframework.stereotype.Component;

@Component
public class ServerInitialzer extends ChannelInitializer<SocketChannel> {


    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {

        //获取管道
        ChannelPipeline pipeline = socketChannel.pipeline();
        //http编解码器
        pipeline.addLast(new HttpServerCodec());
        //json处理器
        //写操作流添加
        pipeline.addLast(new ChunkedWriteHandler());
        //最大的聚合字节流大小
        pipeline.addLast(new HttpObjectAggregator(1024*64));
        //类似拦截器 只要是路径有/chat就会进行关于netty的操作
        pipeline.addLast(new WebSocketServerProtocolHandler("/chat"));
        //自定义处理器，正真处理的业务类
        pipeline.addLast(new ChatHandler());
    }
}

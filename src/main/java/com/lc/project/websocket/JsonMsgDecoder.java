package com.lc.project.websocket;

import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonMsgDecoder extends ChannelInboundHandlerAdapter {

    private Gson gson = new Gson();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("收到一个 Json 数据包 =》" + msg);
    }
}


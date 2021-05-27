package org.xinc.redis.upstream;

import io.netty.channel.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class AutoReconnectHandler extends ChannelInboundHandlerAdapter {

    UpstreamClient client=null;
    public AutoReconnectHandler(UpstreamClient client) {
        this.client=client;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        log.info("自动重连 redis server");
        ctx.channel().eventLoop().schedule(()->{
            client.reconnect();
        },2, TimeUnit.SECONDS);
    }
}

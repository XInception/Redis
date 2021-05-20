package org.xinc.redis.client;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RedisClientHandler extends ChannelDuplexHandler {

    Channel downStreamChanel = null;

    public RedisClientHandler(Channel downStreamChanel) {
        this.downStreamChanel = downStreamChanel;
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
        ctx.write(msg, promise);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
//        log.info("直接转发给下游");
        downStreamChanel.writeAndFlush(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        System.err.print("发生异常: ");
        cause.printStackTrace(System.err);
        ctx.close();
    }
}

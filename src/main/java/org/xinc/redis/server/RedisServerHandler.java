package org.xinc.redis.server;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.redis.*;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.KeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.xinc.redis.RedisInception;
import org.xinc.function.InceptionException;
import org.xinc.redis.RedisUpstreamPool;
import org.xinc.redis.client.RedisClient;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class RedisServerHandler extends ChannelDuplexHandler {

    RedisInception redisInception = new RedisInception();

    KeyedObjectPool<Map<String, Object>, RedisClient> upstreamPool = new GenericKeyedObjectPool<>(new RedisUpstreamPool());

    HashMap<String, Object> config = new HashMap<>();

    public RedisServerHandler(RedisInception redisInception) {
        this.redisInception = redisInception;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端已经离线 返还 redis 句柄");
        RedisClient redisClient = (RedisClient) ctx.channel().attr(AttributeKey.valueOf("redis_connect")).get();
        upstreamPool.returnObject(config, redisClient);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端已经上线 获取redis 句柄");
        config.put("downStream",ctx.channel());
        RedisClient redisClient = upstreamPool.borrowObject(config);
        ctx.channel().attr(AttributeKey.valueOf("redis_connect")).set(redisClient);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        try {
            redisInception.checkRule(msg);
        } catch (InceptionException e) {
            System.out.println(e.getMessage());
            ctx.writeAndFlush(new SimpleStringRedisMessage(e.getMessage()));
            return;
        }

        var s = msg.toString();
        log.info("转发请求给后端" + s);

        RedisClient redisClient = (RedisClient) ctx.channel().attr(AttributeKey.valueOf("redis_connect")).get();

        try {
            redisClient.forwordUpstream(msg);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("转发到后端报错");
        }

        try {
            redisInception.checkRule("");
        } catch (InceptionException e) {
            //返回结果或者预处理
            //邮件警告 ====
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

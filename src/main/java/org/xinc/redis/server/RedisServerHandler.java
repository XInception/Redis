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

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

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
        ctx.channel().attr(AttributeKey.valueOf("redis_len")).set(-1);
        ctx.channel().attr(AttributeKey.valueOf("redis_cmd")).set(new ArrayList<>());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ArrayList msgs=((ArrayList) ctx.channel().attr(AttributeKey.valueOf("redis_cmd")).get());
        if(msg instanceof ArrayHeaderRedisMessage){
            int len= Math.toIntExact(((ArrayHeaderRedisMessage)msg).length())*2+1 ;
            ctx.channel().attr(AttributeKey.valueOf("redis_len")).set(len);
            msgs.add(msg);
            return;
        }
        msgs.add(msg);
        if(msgs.size()==(int)ctx.channel().attr(AttributeKey.valueOf("redis_len")).get()){
            RedisClient redisClient = (RedisClient) ctx.channel().attr(AttributeKey.valueOf("redis_connect")).get();

            try {
                redisInception.checkRule(msgs);
                redisClient.forwordUpstream(msgs);
            } catch (InceptionException e) {
                System.out.println(e.getMessage());
                ctx.writeAndFlush(new ErrorRedisMessage(e.getMessage()));
            }
            ctx.channel().attr(AttributeKey.valueOf("redis_len")).set(-1);
            msgs.clear();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

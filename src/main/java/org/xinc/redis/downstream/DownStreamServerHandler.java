package org.xinc.redis.downstream;

import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.redis.ArrayHeaderRedisMessage;
import io.netty.handler.codec.redis.ErrorRedisMessage;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.KeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.xinc.function.InceptionException;
import org.xinc.redis.RedisInception;
import org.xinc.redis.RedisUpstreamPool;
import org.xinc.redis.upstream.UpstreamClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class DownStreamServerHandler extends ChannelDuplexHandler {

    RedisInception redisInception = null;

    KeyedObjectPool<Map<String, Object>, UpstreamClient> upstreamPool = new GenericKeyedObjectPool<>(new RedisUpstreamPool());

    HashMap<String, Object> config = new HashMap<>();

    public DownStreamServerHandler(RedisInception redisInception) {
        this.redisInception = redisInception;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端已经离线 返还 redis 句柄");
        UpstreamClient upstreamClient = (UpstreamClient) ctx.channel().attr(AttributeKey.valueOf("redis_connect")).get();
//        upstreamClient.close();
        if(upstreamClient!=null){
            upstreamClient.removeDownstream();
            upstreamPool.returnObject(config, upstreamClient);
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("客户端已经上线 获取redis 句柄");
        UpstreamClient upstreamClient = upstreamPool.borrowObject(config);
        upstreamClient.setDownStream(ctx.channel());
        ctx.channel().attr(AttributeKey.valueOf("redis_connect")).set(upstreamClient);
        ctx.channel().attr(AttributeKey.valueOf("redis_len")).set(-1);
        ctx.channel().attr(AttributeKey.valueOf("redis_cmd")).set(new ArrayList<>());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ArrayList msgs = ((ArrayList) ctx.channel().attr(AttributeKey.valueOf("redis_cmd")).get());
        if (msg instanceof ArrayHeaderRedisMessage) {
            int len = Math.toIntExact(((ArrayHeaderRedisMessage) msg).length()) * 2 + 1;
            ctx.channel().attr(AttributeKey.valueOf("redis_len")).set(len);
            msgs.add(msg);
            return;
        }
        msgs.add(msg);
        if (msgs.size() == (int) ctx.channel().attr(AttributeKey.valueOf("redis_len")).get()) {
            UpstreamClient upstreamClient = (UpstreamClient) ctx.channel().attr(AttributeKey.valueOf("redis_connect")).get();

            try {
                redisInception.checkRule(msgs);
                upstreamClient.forwordUpstream(msgs);
            } catch (InceptionException e) {
                log.info(e.getMessage());
                ctx.writeAndFlush(new ErrorRedisMessage(e.getMessage()));
            }catch (Exception e){
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

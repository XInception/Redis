package org.xinc.redis;


import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.redis.BulkStringHeaderRedisMessage;
import io.netty.handler.codec.redis.BulkStringRedisContent;
import io.netty.handler.codec.redis.DefaultLastBulkStringRedisContent;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.xinc.function.Inception;
import org.xinc.function.InceptionException;

import java.nio.charset.StandardCharsets;

@Slf4j
public class RedisInception implements Inception {
    @Override
    public void checkRule(Object source) throws InceptionException {
        if(source instanceof DefaultLastBulkStringRedisContent){
            ByteBuf byteBuf=((DefaultLastBulkStringRedisContent)source).content();
            System.out.println(byteBuf.toString(CharsetUtil.UTF_8));
        }
    }
}

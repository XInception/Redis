package org.xinc.redis;


import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.redis.BulkStringHeaderRedisMessage;
import io.netty.handler.codec.redis.BulkStringRedisContent;
import io.netty.handler.codec.redis.DefaultBulkStringRedisContent;
import io.netty.handler.codec.redis.DefaultLastBulkStringRedisContent;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.xinc.function.Inception;
import org.xinc.function.InceptionException;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
public class RedisInception implements Inception {
    @Override
    public void checkRule(Object source) throws InceptionException {
        List msg=(List)source;
        if (msg.get(2) instanceof DefaultBulkStringRedisContent) {
            String cmd = ((DefaultBulkStringRedisContent) msg.get(2)).content().toString(StandardCharsets.UTF_8);
            if("set".equals(cmd)){
                throw  new InceptionException("当前用户不支持 set 命令");
            }
        }
    }
}

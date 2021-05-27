package org.xinc.redis;


import io.netty.handler.codec.redis.DefaultBulkStringRedisContent;
import lombok.extern.slf4j.Slf4j;
import org.xinc.function.Inception;
import org.xinc.function.InceptionException;
import org.xinc.redis.downstream.DownStreamServerProperty;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

@Slf4j
public class RedisInception implements Inception {
    Properties properties=null;
    public RedisInception(DownStreamServerProperty downStreamServerProperty) {
        this.properties=downStreamServerProperty;
    }


    @Override
    public void checkRule(Object source) throws InceptionException {
        List msg = (List) source;
        if (msg.get(2) instanceof DefaultBulkStringRedisContent) {
            String cmd = ((DefaultBulkStringRedisContent) msg.get(2)).content().toString(StandardCharsets.UTF_8).toLowerCase(Locale.ROOT);
            String[] allowedCommand = this.properties.get("app.redis.allowed_commands").toString().split(",");
            for (int i = 0; i < allowedCommand.length; i++) {
               if(allowedCommand[i].equals(cmd)){
                   return;
               }
            }
            throw new InceptionException("当前用户不支持 " + cmd + " 命令");
        }
    }
}

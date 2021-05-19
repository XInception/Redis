package org.xinc.redis;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.xinc.redis.client.RedisClient;
import org.xinc.redis.client.RedisClientProperty;

import java.util.Map;

@Slf4j
public class RedisUpstreamPool extends BaseKeyedPooledObjectFactory<Map<String, Object>, RedisClient> {

    @Override
    public RedisClient create(Map<String, Object> stringObjectMap) throws Exception {
        log.info("获取客户端");
        return new RedisClient(new RedisClientProperty("/application-client.properties"),(Channel) stringObjectMap.get("downStream"));
    }

    @Override
    public PooledObject<RedisClient> wrap(RedisClient redisClient) {
        return new DefaultPooledObject<>(redisClient);
    }
}

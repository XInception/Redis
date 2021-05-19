package org.xinc.redis;


import org.apache.commons.pool2.KeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.junit.jupiter.api.Test;
import org.xinc.redis.client.RedisClient;

import java.util.HashMap;
import java.util.Map;

class RedisUpstreamPoolTest {

    @Test
    void create() throws Exception {
//        KeyedObjectPool<Map<String, Object>, RedisClient> upstreamPool = new GenericKeyedObjectPool<>(new RedisUpstreamPool());
//        HashMap<String,Object> config=new HashMap<>();
//        RedisClient redisClient= upstreamPool.borrowObject(config);
//
//        redisClient.run("get aa");
//        //返还
//        upstreamPool.returnObject(config,redisClient);
    }
}
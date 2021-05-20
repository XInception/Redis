package org.xinc.redis.upstream;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.xinc.redis.downstream.RedisServerTest;

import java.io.IOException;

@Slf4j
class GetTest extends RedisServerTest {

    @Test
    void ops() throws IOException, InterruptedException {
//        var i=1;
//        while (i<2){
//            client.run("set aa "+System.currentTimeMillis());
//            //发送get 请求
//            client.run("get aa");
//            i++;
//        }
    }

    @Test
    void get() throws IOException ,InterruptedException{
        log.info("启动redis 客户端");
        RedisClient upstreamClient = RedisClient.create("redis://localhost:6379/0");
        StatefulRedisConnection<String, String> connection = upstreamClient.connect();
        RedisCommands<String, String> syncCommands = connection.sync();
        String content="Hello, Redis!";
        syncCommands.set("key", content);
        String res=syncCommands.get("key");
        connection.close();
        upstreamClient.shutdown();
        log.info("完成");
        assert content.equals(res);
    }

}
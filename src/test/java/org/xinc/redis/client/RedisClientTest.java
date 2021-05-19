package org.xinc.redis.client;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;

@Slf4j
class RedisClientTest {

    static RedisClient client =null;

    @BeforeAll
    static void start() throws IOException {
//        log.info("start redis client");
//        client =new RedisClient(new RedisClientProperty("/application-client.properties"), (Channel) stringObjectMap.get("downStream"));
//        client.start();
    }
}
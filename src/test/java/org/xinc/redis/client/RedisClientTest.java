package org.xinc.redis.client;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class RedisClientTest {

    static RedisClient client =null;

    @BeforeAll
    static void start() {
        log.info("start redis client");
        client =new RedisClient();
        client.start();
    }
}
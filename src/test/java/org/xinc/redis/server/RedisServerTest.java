package org.xinc.redis.server;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;

@Slf4j
class RedisServerTest {

    static RedisServer server = null;

    @BeforeAll
    static void start() throws IOException {
        log.info("start redis server");
        server = new RedisServer();
        server.start(new ServerProperty("/application.properties"));
    }
}
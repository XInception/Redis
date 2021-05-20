package org.xinc.redis.downstream;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;

@Slf4j
public
class RedisServerTest {

    static DownStreamServer server = null;

    @BeforeAll
    static void start() throws IOException {
        log.info("start redis server");
        Thread t= new Thread(() -> {
            server = new DownStreamServer();
            try {
                server.start(new DownStreamServerProperty("/application-server.properties"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.start();

    }
}
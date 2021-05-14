package org.xinc.redis.client;

import org.junit.jupiter.api.Test;

import java.io.IOException;

class GetTest extends RedisClientTest {

    @Test
    void get() throws IOException, InterruptedException {
        //发送get 请求
        client.run("get aa");
    }

    @Test
    void set() throws IOException ,InterruptedException{
        client.run("set aa 222222");
        //发送get 请求
        client.run("se111t aa 222222");
    }

    @Test
    void ttl() throws IOException ,InterruptedException{
        client.run("ttl aa");
    }
}
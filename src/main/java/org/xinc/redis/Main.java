package org.xinc.redis;

import org.xinc.redis.downstream.DownStreamServer;
import org.xinc.redis.downstream.DownStreamServerProperty;

import java.io.IOException;

public class Main {

    private static DownStreamServer server;

    public static void main(String[] args) {
        server = new DownStreamServer();
        try {
            server.start(new DownStreamServerProperty("/application-server.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

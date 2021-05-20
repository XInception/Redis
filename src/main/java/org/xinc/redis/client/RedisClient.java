package org.xinc.redis.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.redis.*;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author crtrpt
 */
@Slf4j
public class RedisClient {

    private EventLoopGroup eventLoopGroup;

    private Bootstrap bootstrap;

    Channel channel;

    Channel downstreamChannel;

    RedisClientProperty clientProperty;

    public RedisClient(RedisClientProperty redisServerProperty, Channel downStream) {
        this.clientProperty = redisServerProperty;
        this.downstreamChannel=downStream;
        this.start();
    }

    public void start() {
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) {
                ch.pipeline().addLast(new LoggingHandler());
                ch.pipeline().addLast(new RedisEncoder());
                ch.pipeline().addLast(new RedisDecoder());
                ch.pipeline().addLast(new RedisClientHandler(downstreamChannel));
            }
        });
        var cf = bootstrap.connect(clientProperty.server, clientProperty.port);

        try {
            cf.sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!cf.isSuccess()) {
            throw new RuntimeException(cf.cause());
        }

        channel = cf.channel();
        log.info("服务器信息:" + channel.remoteAddress().toString());
    }


    public void forwordUpstream(Object msg) throws InterruptedException {
        this.channel.write(msg);
    }
    public  void sync() throws InterruptedException {
        this.channel.flush();
    }
}

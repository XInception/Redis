package org.xinc.redis.upstream;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.redis.DefaultBulkStringRedisContent;
import io.netty.handler.codec.redis.RedisDecoder;
import io.netty.handler.codec.redis.RedisEncoder;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author crtrpt
 */
@Slf4j
public class UpstreamClient {

    private static EventLoopGroup eventLoopGroup;

    private static Bootstrap bootstrap;

    Channel upstreamChannel;

    Channel downstreamChannel;

    UpstreamClientProperty clientProperty;

    public UpstreamClient(UpstreamClientProperty redisServerProperty, Channel downStream) {
        this.clientProperty = redisServerProperty;
        this.downstreamChannel = downStream;
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
//                ch.pipeline().addLast(new LoggingHandler());
                ch.pipeline().addLast(new RedisEncoder());
                ch.pipeline().addLast(new RedisDecoder());
                ch.pipeline().addLast(new UpstreamClientHandler(downstreamChannel));
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

        upstreamChannel = cf.channel();
        log.info("server info:" + upstreamChannel.remoteAddress().toString());
    }


    public void forwordUpstream(List<Object> msg) {
        for (Object m : msg) {
            this.upstreamChannel.write(m);
        }
        this.upstreamChannel.flush();
    }

    public void sync() throws InterruptedException {
        this.upstreamChannel.flush();
    }
}

package org.xinc.redis.upstream;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.redis.RedisDecoder;
import io.netty.handler.codec.redis.RedisEncoder;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author crtrpt
 */
@Slf4j
public class UpstreamClient implements Closeable, Reconnect {

    private final EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

    private final Bootstrap bootstrap = new Bootstrap();

    Channel upstreamChannel;

    Channel downstreamChannel;

    UpstreamClientProperty clientProperty;
    /**
     * 重连次数
     */
    int reconnectTimes = 0;
    int MAX_RECONNECT =5;

    public UpstreamClient(UpstreamClientProperty redisServerProperty) {
        this.clientProperty = redisServerProperty;
        this.start();
    }

    public void connect() {
        try {
            log.info("链接redis {} {} ", clientProperty.server, clientProperty.port);
            var cf = bootstrap.connect(clientProperty.server, clientProperty.port);
            cf.addListener(f -> {
                if (!cf.isSuccess()) {
                    if (reconnectTimes < MAX_RECONNECT) {
                        this.reconnectTimes = this.reconnectTimes + 1;
                    }
                    log.info("尝试重新链接后端redis " + this.reconnectTimes);
                    cf.channel().eventLoop().schedule(this::reconnect, reconnectTimes * 2, TimeUnit.SECONDS);
                } else {
                    log.info("redis 链接成功");
                    reconnectTimes = 0;
                    cf.channel().pipeline().addLast(new AutoReconnectHandler(this));
                    upstreamChannel = cf.channel();
                    log.info("server info:" + upstreamChannel.remoteAddress().toString());
                }
            }).sync();


        } catch (InterruptedException e) {
            System.out.println("链接失败");
            e.printStackTrace();
        }

    }

    public void start() {
        bootstrap.group(eventLoopGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) {
//                ch.pipeline().addLast(new LoggingHandler());
                ch.pipeline().addLast(new RedisEncoder());
                ch.pipeline().addLast(new RedisDecoder());
            }
        });
        this.connect();
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

    @Override
    public void close() throws IOException {
        log.info("关闭mysql链接");
        this.upstreamChannel.pipeline().remove(AutoReconnectHandler.class);
        this.upstreamChannel.closeFuture();
    }

    @Override
    public void reconnect() {
        log.info("重新链接redis");
        connect();
    }

    public void setDownStream(Channel channel) {
        this.downstreamChannel = channel;
        log.info("设置 downStream");
        this.upstreamChannel.pipeline().addLast(new UpstreamClientHandler(downstreamChannel));
    }

    public void removeDownstream() {
        this.downstreamChannel = null;
        if (upstreamChannel != null) {
            this.upstreamChannel.pipeline().remove(UpstreamClientHandler.class);
        }
    }
}

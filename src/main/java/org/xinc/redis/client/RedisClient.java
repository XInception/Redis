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
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author crtrpt
 */
@Slf4j
public class RedisClient {

    private EventLoopGroup eventLoopGroup;

    private Bootstrap bootstrap;

    Channel channel;

    BlockingQueue<Object> blockingQueue=new ArrayBlockingQueue<Object>(20);

    public void start(){
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {

            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new LoggingHandler());
                ch.pipeline().addLast(new RedisEncoder());
                ch.pipeline().addLast(new RedisDecoder());
                ch.pipeline().addLast(new RedisClientHandler(blockingQueue));
            }
        });
        var cf=bootstrap.connect("127.0.0.1",6379);

        try {
            cf.sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (!cf.isSuccess()) {
            throw new RuntimeException(cf.cause());
        }

        channel = cf.channel();
        log.info("服务器信息:"+channel.remoteAddress().toString());
    }

    public void run(String cmd) throws InterruptedException {
        this.channel.writeAndFlush(cmd).sync();
        Object o= blockingQueue.poll(1, TimeUnit.SECONDS);

        if(o instanceof ErrorRedisMessage) {
            System.out.println("错误的命令");
            System.out.println(((ErrorRedisMessage)o).content());
        }
        if(o instanceof SimpleStringRedisMessage) {
            System.out.println("执行成功");
            System.out.println(((SimpleStringRedisMessage)o).content());
        }
        if(o instanceof BulkStringHeaderRedisMessage) {
            System.out.println("多行字符串");
        }
        if(o instanceof IntegerRedisMessage) {
            System.out.println("数字");
        }
        if(o instanceof ArrayRedisMessage) {
            System.out.println("数组");
        }

        System.out.println(o);
    }

    /**
     * 异步执行
     * @param cmd
     * @param callback
     * @throws InterruptedException
     */
    public void run(String cmd, Function callback) throws InterruptedException {
        this.channel.writeAndFlush(new InlineCommandRedisMessage(cmd)).sync();
    }
}

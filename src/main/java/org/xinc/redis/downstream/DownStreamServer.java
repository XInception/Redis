package org.xinc.redis.downstream;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.redis.RedisDecoder;
import io.netty.handler.codec.redis.RedisEncoder;
import lombok.extern.slf4j.Slf4j;
import org.xinc.redis.RedisInception;

@Slf4j
public class DownStreamServer {

    EventLoopGroup bossGroup = new NioEventLoopGroup(1);

    EventLoopGroup workerGroup = new NioEventLoopGroup();

    RedisInception redisInception=new RedisInception();

    ChannelFuture f = null;

    DownStreamServerProperty property = null;

    public void start(DownStreamServerProperty downStreamServerProperty) {
        property = downStreamServerProperty;
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) {
                            ChannelPipeline pipeline = ch.pipeline();
//                            pipeline.addLast(new LoggingHandler());
                            pipeline.addLast(new RedisDecoder());
                            pipeline.addLast(new RedisEncoder());
                            pipeline.addLast(new DownStreamServerHandler(redisInception));
                        }
                    });
            f = b.bind(property.server, property.port);
            log.info("REDIS DEMON 启动  {} {} ", property.server, property.port);
            f.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }



}

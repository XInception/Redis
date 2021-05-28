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
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.xinc.redis.RedisInception;
import org.xinc.redis.upstream.AutoReconnectHandler;
import org.xinc.redis.upstream.UpstreamClientProperty;

@Slf4j
public class DownStreamServer {

    static EventLoopGroup bossGroup = new NioEventLoopGroup(1);

    static EventLoopGroup workerGroup = new NioEventLoopGroup();

    RedisInception redisInception=null;

    ChannelFuture f = null;

    DownStreamServerProperty property = null;

    public void start(DownStreamServerProperty downStreamServerProperty) {
        redisInception=new RedisInception(downStreamServerProperty);
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
                            pipeline.addLast(new DownStreamServerHandler(redisInception,property));
                        }
                    });
            f = b.bind(property.server, property.port);
            log.info("REDIS Initializer Successfully started  {} {} ", property.server, property.port);
            f.sync().channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }



}

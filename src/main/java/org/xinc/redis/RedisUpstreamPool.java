package org.xinc.redis;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.BaseKeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.xinc.redis.upstream.UpstreamClient;
import org.xinc.redis.upstream.UpstreamClientProperty;

import java.util.Map;

@Slf4j
public class RedisUpstreamPool extends BaseKeyedPooledObjectFactory<Map<String, Object>, UpstreamClient> {

    @Override
    public UpstreamClient create(Map<String, Object> stringObjectMap) throws Exception {
        log.info("获取客户端");

        return new UpstreamClient(new UpstreamClientProperty( Main.commandLine.getOptionValue("c","./application-server.properties")),(Channel) stringObjectMap.get("downStream"));
    }

    @Override
    public PooledObject<UpstreamClient> wrap(UpstreamClient upstreamClient) {
        return new DefaultPooledObject<>(upstreamClient);
    }
}

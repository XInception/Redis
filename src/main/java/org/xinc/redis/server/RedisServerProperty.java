package org.xinc.redis.server;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Admin
 */
public class RedisServerProperty {

    String server;
    Integer port;

    public RedisServerProperty(String s) throws IOException {
        this.loadProperty(s);
    }

    public void loadProperty(InputStream stream) throws IOException {
        Properties properties = new Properties();
        properties.load(stream);
        this.server = properties.getProperty("app.redis.server");
        this.port = Integer.parseInt(properties.getProperty("app.redis.port"));
    }

    public void loadProperty(String path) throws IOException {
        loadProperty(this.getClass().getResourceAsStream(path));
    }
}

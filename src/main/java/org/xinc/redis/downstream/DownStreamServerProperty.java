package org.xinc.redis.downstream;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Admin
 */
public class DownStreamServerProperty {

    String server;
    Integer port;

    public DownStreamServerProperty(String s) throws IOException {
        this.loadProperty(s);
    }

    public void loadProperty(InputStream stream) throws IOException {
        Properties properties = new Properties();
        properties.load(stream);
        this.server = properties.getProperty("app.redis.downstream.server");
        this.port = Integer.parseInt(properties.getProperty("app.redis.downstream.port"));
    }

    public void loadProperty(String path) throws IOException {
        loadProperty(this.getClass().getResourceAsStream(path));
    }
}

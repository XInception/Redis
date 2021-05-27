package org.xinc.redis.upstream;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Admin
 */
public class UpstreamClientProperty {

    String server;
    Integer port;

    public UpstreamClientProperty(String s) throws IOException {
        this.loadProperty(s);
    }

    public void loadProperty(InputStream stream) throws IOException {
        Properties properties = new Properties();
        properties.load(stream);
        this.server=properties.getProperty("app.redis.upstream.server");
        this.port=Integer.parseInt(properties.getProperty("app.redis.upstream.port"));
    }

    public void loadProperty(String path) throws IOException {
        loadProperty( new FileInputStream(path));
    }
}

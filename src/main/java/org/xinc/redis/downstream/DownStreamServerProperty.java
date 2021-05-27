package org.xinc.redis.downstream;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Admin
 */
public class DownStreamServerProperty extends Properties {

    String server;
    Integer port;

    public DownStreamServerProperty(String s) throws IOException {
        System.out.println("读取配置文件"+s);
        this.loadProperty(s);
    }

    public void loadProperty(InputStream stream) throws IOException {
        load(stream);
        this.server = this.getProperty("app.redis.downstream.server");
        this.port = Integer.parseInt(this.getProperty("app.redis.downstream.port"));
    }

    public void loadProperty(String path) throws IOException {
        loadProperty(new FileInputStream(path));
    }
}

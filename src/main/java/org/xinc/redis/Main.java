package org.xinc.redis;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.cli.*;
import org.xinc.redis.downstream.DownStreamServer;
import org.xinc.redis.downstream.DownStreamServerProperty;

import java.io.IOException;

@Slf4j
public class Main {

    public static CommandLine commandLine;

    private static DownStreamServer server;

    public static void main(String[] args) {


        Options options = new Options();
        CommandLineParser parser = new DefaultParser();

        options.addOption("c", "config", true, "设置配置文件");

        try {
            commandLine = parser.parse(options, args);
        } catch (ParseException exception) {
            exception.printStackTrace();
            log.info(exception.getMessage());
            return;
        }
        server = new DownStreamServer();
        try {
            server.start(new DownStreamServerProperty(commandLine.getOptionValue("c")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

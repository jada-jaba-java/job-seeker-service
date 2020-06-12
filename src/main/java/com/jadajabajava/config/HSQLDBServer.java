package com.jadajabajava.config;

import org.hsqldb.server.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "hsqldb.server.enabled")
public class HSQLDBServer {

    @Value("${hsqldb.server.host}")
    private String hsqlServerHost;

    @Value("${hsqldb.server.port}")
    private int hsqlServerPort;

    @Value("${hsqldb.server.dbname}")
    private String hsqlServerDbName;

    @Value("${hsqldb.server.dbpath}")
    private String hsqlServerDbPath;

    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server HSQLDatabaseServer() {
        Server server = new Server();
        server.setAddress(hsqlServerHost);
        server.setPort(hsqlServerPort);
        server.setDatabaseName(0, hsqlServerDbName);
        server.setDatabasePath(0, hsqlServerDbPath);
        return server;
    }
}

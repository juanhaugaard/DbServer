package org.tayrona.dbserver.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.tayrona.dbserver.config.ClientConfig;
import org.tayrona.dbserver.config.H2Configuration;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
@Component
public class AuditInitializer {
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private H2Configuration configuration;
    private Connection conn;

    @PostConstruct
    public void setup() throws SQLException {
        if (configuration != null && configuration.getClient() != null) {
            conn = dataSource.getConnection(username, password);
            Statement stat = conn.createStatement();
            ClientConfig clientConfig = configuration.getClient();
            for (String sql : clientConfig.getInitSql()) {
                stat.execute(sql);
            }
        }
    }
}

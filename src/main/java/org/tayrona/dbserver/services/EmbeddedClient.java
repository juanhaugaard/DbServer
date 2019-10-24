package org.tayrona.dbserver.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
@Component
public class EmbeddedClient implements Runnable {
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    @Autowired
    private DataSource dataSource;
    private Connection conn;

    private String[] sql = {
            "DROP TABLE TIMER IF EXISTS",
            "CREATE TABLE TIMER(ID INT PRIMARY KEY, TIME VARCHAR)",
            "MERGE INTO TIMER VALUES(1, LOCALTIME)"
    };

    @PostConstruct
    public void setup() throws SQLException {
        conn = dataSource.getConnection(username, password);
        Statement stat = conn.createStatement();
        stat.execute(sql[0]);
        stat.execute(sql[1]);
        log.info("Execute this a few times: SELECT TIME FROM TIMER");
        log.info("To stop this application (and the server), run: DROP TABLE TIMER");
        Thread thread = new Thread(this);
        thread.start();
    }

    @PreDestroy
    public void shutdown() {
        if (conn != null) {
            try {
                log.info("Client closing connection");
                conn.close();
            } catch (SQLException e) {
                log.error("Error: {}", e.toString());
            }
        }
    }

    @Override
    public void run() {
        try (Statement stat = conn.createStatement()) {
            while (true) {
                // runs forever, except if you drop the table remotely
                stat.execute(sql[2]);
                Thread.sleep(1000);
            }
        } catch (SQLException | InterruptedException e) {
            log.error("Error: {}", e.toString());
        }
    }
}

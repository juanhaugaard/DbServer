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
            "CREATE TABLE IF NOT EXISTS TIMER(ID IDENTITY PRIMARY KEY, AN_ID INT, S_TIME VARCHAR(64), A_TIME TIME, A_DATE DATE, DATE_TIME TIMESTAMP, A_DECIMAL DECIMAL, A_DOUBLE DOUBLE, A_REAL REAL, A_BIGINT BIGINT)",
            "MERGE INTO TIMER(ID, S_TIME) VALUES(1, LOCALTIME)",
            "INSERT INTO TIMER(S_TIME, AN_ID, A_TIME, A_DATE, DATE_TIME, A_DECIMAL, A_DOUBLE, A_REAL, A_BIGINT) VALUES(LOCALTIME, RANDOM()*100, CURRENT_TIME, CURRENT_DATE, CURRENT_TIMESTAMP, RANDOM()*100, RANDOM()*100, RANDOM()*100, RANDOM()*100)",
            "CREATE TRIGGER IF NOT EXISTS PUBLIC.INSERT_AUDIT AFTER INSERT ON PUBLIC.TIMER FOR EACH ROW CALL \"org.tayrona.dbserver.audit.InsertTrigger\";",
            "CREATE TRIGGER IF NOT EXISTS PUBLIC.UPDATE_AUDIT AFTER UPDATE ON PUBLIC.TIMER FOR EACH ROW CALL \"org.tayrona.dbserver.audit.UpdateTrigger\";",
            "CREATE TRIGGER IF NOT EXISTS PUBLIC.DELETE_AUDIT AFTER DELETE ON PUBLIC.TIMER FOR EACH ROW CALL \"org.tayrona.dbserver.audit.DeleteTrigger\";",
            "CREATE TRIGGER IF NOT EXISTS PUBLIC.SELECT_AUDIT BEFORE SELECT ON PUBLIC.TIMER CALL \"org.tayrona.dbserver.audit.SelectTrigger\";",
            "CREATE TRIGGER IF NOT EXISTS PUBLIC.ROLLBACK_AUDIT AFTER ROLLBACK ON PUBLIC.TIMER FOR EACH ROW CALL \"org.tayrona.dbserver.audit.RollbackTrigger\";"
    };

    @PostConstruct
    public void setup() throws SQLException {
        conn = dataSource.getConnection(username, password);
        Statement stat = conn.createStatement();
        // stat.execute(sql[0]); // do not drop the table
        stat.execute(sql[1]);
        stat.execute(sql[4]);
        stat.execute(sql[5]);
        stat.execute(sql[6]);
        stat.execute(sql[7]);
        stat.execute(sql[8]);
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
                stat.execute(sql[3]);
                Thread.sleep(1000);
            }
        } catch (SQLException | InterruptedException e) {
            log.error("Error: {}", e.toString());
        }
    }
}

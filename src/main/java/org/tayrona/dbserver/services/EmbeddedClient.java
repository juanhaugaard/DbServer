package org.tayrona.dbserver.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.SQLException;

@Slf4j
@Component
public class EmbeddedClient implements Runnable {
    private static final String CLASS_NAME = EmbeddedClient.class.getSimpleName();

    private Thread thread;

    private JdbcTemplate jdbcTemplate;

    public EmbeddedClient() {
        log.debug("{} constructed", CLASS_NAME);
    }

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
        dbSetup();
        threadSetup();
        log.info("{}.setup() - Execute this a few times: SELECT TIME FROM TIMER", CLASS_NAME);
        log.info("{}.setup() - To stop this application (and the server), run: DROP TABLE TIMER", CLASS_NAME);
    }

    private void threadSetup() {
        Thread thread = new Thread(this);
        thread.start();
    }

    private void dbSetup() throws SQLException {
        // jdbcTemplate.execute(sql[0]); // do not drop the table
        jdbcTemplate.execute(sql[1]);
        jdbcTemplate.execute(sql[4]);
        jdbcTemplate.execute(sql[5]);
        jdbcTemplate.execute(sql[6]);
        jdbcTemplate.execute(sql[7]);
        jdbcTemplate.execute(sql[8]);
    }

    @PreDestroy
    public void shutdown() {
        if (thread != null) {
            thread.interrupt();
        }
    }
    @Override
    public void run() {
        log.debug("{}.run() - start delay", CLASS_NAME);
        try {
            Thread.sleep(1000 * 60);
        } catch (InterruptedException ex) {
            log.error(ex.getMessage(), ex);
            return;
        }
        log.debug("{}.run() - running", CLASS_NAME);
        try {
            while (!Thread.interrupted()) {
                jdbcTemplate.execute(sql[3]);
                Thread.sleep(1);
            }
        } catch (DataAccessException | InterruptedException e) {
            log.error("{}.run() - Error: {}", CLASS_NAME, e.toString());
        }
    }

    @Autowired
    @Qualifier("JdbcTemplate")
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}

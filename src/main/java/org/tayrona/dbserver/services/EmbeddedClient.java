package org.tayrona.dbserver.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.tayrona.dbserver.config.H2Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class EmbeddedClient implements Runnable {
    private static final String CLASS_NAME = EmbeddedClient.class.getSimpleName();

    private H2Configuration h2Config;

    private Thread thread;

    private JdbcTemplate jdbcTemplate;

    public EmbeddedClient() {
        log.debug("{} constructed", CLASS_NAME);
    }

    @PostConstruct
    public void setup() throws SQLException {
        dbSetup();
        threadSetup();
        log.info("{}.setup() - Execute this a few times: SELECT TIME FROM TIMER", CLASS_NAME);
        log.info("{}.setup() - To stop this application (and the server), run: DROP TABLE TIMER", CLASS_NAME);
    }

    private void threadSetup() {
        Thread thread = new Thread(this);
//        thread.start();
    }

    private void dbSetup() throws SQLException {
        List<String> sql = h2Config.getClient().getInitSql();
        // jdbcTemplate.execute(sql.get(0)); // do not drop the table
        jdbcTemplate.execute(sql.get(1));
        jdbcTemplate.execute(sql.get(4));
        jdbcTemplate.execute(sql.get(5));
        jdbcTemplate.execute(sql.get(6));
        jdbcTemplate.execute(sql.get(7));
        jdbcTemplate.execute(sql.get(8));
        jdbcTemplate.execute(sql.get(9));
        jdbcTemplate.execute(sql.get(10));
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
            String sql = h2Config.getClient().getInitSql().get(3);
            while (!Thread.interrupted()) {
                jdbcTemplate.execute(sql);
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

    @Autowired
    public void setH2Config(H2Configuration h2Config) {
        this.h2Config = h2Config;
    }
}

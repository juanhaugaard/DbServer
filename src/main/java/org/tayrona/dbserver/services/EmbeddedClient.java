package org.tayrona.dbserver.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.DependsOn;
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
@DependsOn("eventAuditQueue")
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
        if (h2Config.getClient().getInterval() >= 0) {
            thread = new Thread(this, CLASS_NAME);
            thread.start();
        }
    }

    private void dbSetup() throws SQLException {
        List<String> sql = h2Config.getClient().getInitSql();
        // jdbcTemplate.execute(sql.get(0)); // do not drop the table
        jdbcTemplate.execute(sql.get(1));
        jdbcTemplate.execute(sql.get(4));
    }

    @PreDestroy
    public void shutdown() {
        if (thread != null) {
            log.debug("{}.shutdown() - client shutdown now", CLASS_NAME);
            thread.interrupt();
            thread = null;
        }
    }

    @Override
    public void run() {
        if (!initialDelay()) {
            return;
        }
        log.debug("{}.run() - running", CLASS_NAME);
        try {
            String sql = h2Config.getClient().getInitSql().get(3);
            while (!Thread.interrupted()) {
                jdbcTemplate.execute(sql);
                interval();
            }
        } catch (DataAccessException e1) {
            log.error("{}.run() - Error: {}", CLASS_NAME, e1.toString());
        } catch (InterruptedException e2) {
            log.info("{}.run() - info: {}", CLASS_NAME, e2.toString());
        }
    }

    private void interval() throws InterruptedException {
        if (h2Config.getClient().getInterval() == 0) {
            Thread.yield();
        } else {
            Thread.sleep(h2Config.getClient().getInterval());
        }
    }

    private boolean initialDelay() {
        log.debug("{}.initialDelay() - start delay", CLASS_NAME);
        long delay = h2Config.getClient().getInitialDelay();
        if (delay < 0) {
            return false;
        }
        try {
            if (delay > 0) {
                Thread.sleep(delay);
            } else {
                Thread.yield();
            }
        } catch (InterruptedException ex) {
            log.warn("{}.initialDelay() interrupted", CLASS_NAME);
            return false;
        }
        return true;
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

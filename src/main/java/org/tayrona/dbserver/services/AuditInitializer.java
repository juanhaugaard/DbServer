package org.tayrona.dbserver.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.tayrona.dbserver.config.ClientConfig;
import org.tayrona.dbserver.config.H2Configuration;

import javax.annotation.PostConstruct;
import java.sql.SQLException;

@Slf4j
@Component
public class AuditInitializer {
    private static final String CLASS_NAME = AuditInitializer.class.getSimpleName();

    private H2Configuration h2Config;

    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void setup() throws SQLException {
        log.debug("{}.setup()", CLASS_NAME);
        if (h2Config != null && h2Config.getClient() != null) {
            ClientConfig clientConfig = h2Config.getClient();
            for (String sql : clientConfig.getInitSql()) {
                log.debug("{}.setup() - executing: {}", CLASS_NAME, sql);
                jdbcTemplate.execute(sql);
            }
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

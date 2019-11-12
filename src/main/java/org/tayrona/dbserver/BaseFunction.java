package org.tayrona.dbserver;

import org.springframework.jdbc.core.JdbcTemplate;
import org.tayrona.dbserver.config.H2Configuration;

public abstract class BaseFunction {

    private static final Object lock = new Object();

    private static H2Configuration h2Config;

    protected static H2Configuration h2Config() {
        if (h2Config == null) {
            synchronized (lock) {
                if (h2Config == null) {
                    h2Config = (H2Configuration)Application.getApplicationContext().getBean("h2Configuration");
                }
            }
        }
        return h2Config;
    }

    protected static JdbcTemplate jdbcTemplate() {
        return (JdbcTemplate) Application.getApplicationContext().getBean("JdbcTemplate");
    }

    protected static void executeSqlStatement(final String fmt, final String tablename) {
        executeSqlStatement(String.format(fmt, tablename));
    }

    protected static void executeSqlStatement(final String sql) {
        jdbcTemplate().execute(sql);
    }
}

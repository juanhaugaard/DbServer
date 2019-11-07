package org.tayrona.dbserver.audit;

import org.springframework.jdbc.core.JdbcTemplate;
import org.tayrona.dbserver.Application;
import org.tayrona.dbserver.config.H2Configuration;

import java.util.List;

abstract class LogFunction {

    private static H2Configuration h2Config;

    private static final Object lock = new Object();

    static List<String> triggersCreate() {
        return h2Config().getAudit().getTriggerCreate();
    }

    static List<String> triggersDrop() {
        return h2Config().getAudit().getTriggerDrop();
    }

    static List<String> initAudit() {
        return h2Config().getAudit().getInitSql();
    }

    static H2Configuration h2Config() {
        if (h2Config == null) {
            synchronized (lock) {
                if (h2Config == null) {
                    h2Config = (H2Configuration)Application.getApplicationContext().getBean("H2Configuration");
                }
            }
        }
        return h2Config;
    }

    static void executeSqlStatement(final String fmt, final String tablename) {
        executeSqlStatement(String.format(fmt, tablename));
    }

    static void executeSqlStatement(final String sql) {
        jdbcTemplate().execute(sql);
    }

    protected static JdbcTemplate jdbcTemplate() {
        return (JdbcTemplate) Application.getApplicationContext().getBean("JdbcTemplate");
    }
}

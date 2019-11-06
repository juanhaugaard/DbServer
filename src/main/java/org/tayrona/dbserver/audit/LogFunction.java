package org.tayrona.dbserver.audit;

import org.springframework.jdbc.core.JdbcTemplate;
import org.tayrona.dbserver.Application;

public interface LogFunction {
    String[] triggersCreate = {
            "CREATE TRIGGER IF NOT EXISTS %s_INSERT_AUDIT AFTER INSERT ON PUBLIC.TIMER FOR EACH ROW CALL \"org.tayrona.dbserver.audit.InsertTrigger\"",
            "CREATE TRIGGER IF NOT EXISTS %s_UPDATE_AUDIT AFTER UPDATE ON PUBLIC.TIMER FOR EACH ROW CALL \"org.tayrona.dbserver.audit.UpdateTrigger\"",
            "CREATE TRIGGER IF NOT EXISTS %s_DELETE_AUDIT AFTER DELETE ON PUBLIC.TIMER FOR EACH ROW CALL \"org.tayrona.dbserver.audit.DeleteTrigger\"",
            "CREATE TRIGGER IF NOT EXISTS %s_SELECT_AUDIT BEFORE SELECT ON PUBLIC.TIMER CALL \"org.tayrona.dbserver.audit.SelectTrigger\"",
            "CREATE TRIGGER IF NOT EXISTS %s_ROLLBACK_INSERT_AUDIT AFTER ROLLBACK, INSERT ON PUBLIC.TIMER FOR EACH ROW CALL \"org.tayrona.dbserver.audit.RollbackTrigger\"",
            "CREATE TRIGGER IF NOT EXISTS %s_ROLLBACK_UPDATE_AUDIT AFTER ROLLBACK, UPDATE ON PUBLIC.TIMER FOR EACH ROW CALL \"org.tayrona.dbserver.audit.RollbackTrigger\"",
            "CREATE TRIGGER IF NOT EXISTS %s_ROLLBACK_DELETE_AUDIT AFTER ROLLBACK, DELETE ON PUBLIC.TIMER FOR EACH ROW CALL \"org.tayrona.dbserver.audit.RollbackTrigger\""
    };
    String[] triggersDrop = {
            "DROP TRIGGER IF EXISTS %s_INSERT_AUDIT",
            "DROP TRIGGER IF EXISTS %s_UPDATE_AUDIT",
            "DROP TRIGGER IF EXISTS %s_DELETE_AUDIT",
            "DROP TRIGGER IF EXISTS %s_SELECT_AUDIT",
            "DROP TRIGGER IF EXISTS %s_ROLLBACK_INSERT_AUDIT",
            "DROP TRIGGER IF EXISTS %s_ROLLBACK_UPDATE_AUDIT",
            "DROP TRIGGER IF EXISTS %s_ROLLBACK_DELETE_AUDIT"
    };

    static void executeSqlStatement(final String fmt, final String tablename) {
        String sql = String.format(fmt, tablename);
        jdbcTemplate().execute(sql);
    }

    static JdbcTemplate jdbcTemplate() {
        return (JdbcTemplate) Application.getApplicationContext().getBean("JdbcTemplate");
    }
}

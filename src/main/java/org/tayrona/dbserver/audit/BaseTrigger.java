package org.tayrona.dbserver.audit;

import lombok.extern.slf4j.Slf4j;
import org.h2.api.Trigger;

import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
public class BaseTrigger implements Trigger {
    protected static final String CLASS_NAME = BaseTrigger.class.getSimpleName();
    protected String schemaName, triggerName, tableName, catalog;

    /**
     * This method is called by the database engine once when initializing the
     * trigger. It is called when the trigger is created, as well as when the
     * database is opened. The type of operation is a bit field with the
     * appropriate flags set. As an example, if the trigger is of type INSERT
     * and UPDATE, then the parameter type is set to (INSERT | UPDATE).
     *
     * @param conn        a connection to the database (a system connection)
     * @param schemaName  the name of the schema
     * @param triggerName the name of the trigger used in the CREATE TRIGGER
     *                    statement
     * @param tableName   the name of the table
     * @param before      whether the fire method is called before or after the
     *                    operation is performed
     * @param type        the operation type: INSERT, UPDATE, DELETE, SELECT, or a
     */
    @Override
    public void init(Connection conn, String schemaName, String triggerName, String tableName, boolean before, int type) throws SQLException {
        this.schemaName = schemaName;
        this.triggerName = triggerName;
        this.tableName = tableName;
        this.catalog = conn.getCatalog();
        log.info("{}.init(catalog:{}, schema:{}, name:{}, table:{}, before:{}, type:{})", CLASS_NAME, conn.getCatalog(), schemaName, triggerName, tableName, before, type);
    }

    /**
     * This method is called for each triggered action. The method is called
     * immediately when the operation occurred (before it is committed). A
     * transaction rollback will also rollback the operations that were done
     * within the trigger, if the operations occurred within the same database.
     * If the trigger changes state outside the database, a rollback trigger
     * should be used.
     * <p>
     * The row arrays contain all columns of the table, in the same order
     * as defined in the table.
     * </p>
     * <p>
     * The trigger itself may change the data in the newRow array.
     * </p>
     *
     * @param conn   a connection to the database
     * @param oldRow the old row, or null if no old row is available (for
     *               INSERT)
     * @param newRow the new row, or null if no new row is available (for
     *               DELETE)
     * @throws SQLException if the operation must be undone
     */
    @Override
    public void fire(Connection conn, Object[] oldRow, Object[] newRow) throws SQLException {
        log.info("{}.fire(catalog:{}, schema:{}, name:{}, table:{})", CLASS_NAME, conn.getCatalog(), schemaName, triggerName, tableName);
        log.info("{}.fire() - old:{}, new:{}", CLASS_NAME, oldRow == null ? "null" : oldRow.length + " items", newRow == null ? "null" : newRow.length + " items");
        if (oldRow != null) {
            for (int i = 0; i < oldRow.length; i++) {
                log.info("{}.fire() -\told:{}- {}, {}", CLASS_NAME, i, oldRow[i], oldRow[i].getClass().getName());
            }
        }
        if (newRow != null) {
            for (int i = 0; i < newRow.length; i++) {
                log.info("{}.fire() -\tnew:{}- {}, {}", CLASS_NAME, i, newRow[i], newRow[i].getClass().getName());
            }
        }
    }

    /**
     * This method is called when the database is closed.
     * If the method throws an exception, it will be logged, but
     * closing the database will continue.
     */
    @Override
    public void close() throws SQLException {
        log.info("{}.close()", CLASS_NAME);
    }

    /**
     * This method is called when the trigger is dropped.
     */
    @Override
    public void remove() throws SQLException {
        log.info("{}.remove()", CLASS_NAME);
    }
}

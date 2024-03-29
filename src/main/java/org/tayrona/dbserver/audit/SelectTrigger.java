package org.tayrona.dbserver.audit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.SQLException;

@Slf4j
public class SelectTrigger extends BaseTrigger {
    @Autowired
    private EventAuditQueue queue;

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
     * @param connection   a connection to the database
     * @param oldRow the old row, or null if no old row is available (for
     *               INSERT)
     * @param newRow the new row, or null if no new row is available (for
     *               DELETE)
     * @throws SQLException if the operation must be undone
     */
    @Override
    public void fire(Connection connection, Object[] oldRow, Object[] newRow) throws SQLException {
        logFire(oldRow, newRow);
    }
}

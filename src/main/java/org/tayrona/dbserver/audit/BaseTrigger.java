package org.tayrona.dbserver.audit;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.h2.api.Trigger;
import org.tayrona.dbserver.Constants;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class BaseTrigger implements Trigger {
    protected static EventAuditQueue eventAuditQueue;
    protected final String CLASS_NAME;
    protected String schemaName, triggerName, tableName, catalog, action;
    protected boolean before;
    protected List<String> columns;
    private static ObjectMapper objectMapper;

    BaseTrigger() {
        CLASS_NAME = this.getClass().getSimpleName();
        log.debug("{} constructed", CLASS_NAME);
    }

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
    public void init(Connection conn, String schemaName, String triggerName, String tableName,
                     boolean before, int type) throws SQLException {
        this.schemaName = schemaName;
        this.triggerName = triggerName;
        this.tableName = tableName;
        this.catalog = conn.getCatalog();
        this.before = before;
        this.columns = extractColumns(conn);
        this.action = calcActions(type);
        log.info("{}.init(catalog:{}, schema:{}, name:{}, table:{}, before:{}, type:{})",
                CLASS_NAME, catalog, schemaName, triggerName, tableName, before, type);
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
        EventAuditQueue eventAuditQueue = getEventAuditQueue();
        if (eventAuditQueue == null) {
            log.warn("{}.fire(...) - EventAuditQueue is null!", CLASS_NAME);
        } else {
            try {
                String userName = conn.getMetaData().getUserName();
                ObjectNode payload = calcJsonObject(oldRow, newRow);
                String jsonPayload = getObjectMapper().writeValueAsString(payload);
                EventQueueItem item = new EventQueueItem(catalog, schemaName, tableName, action, userName, jsonPayload);
                log.debug("{}.fire(...) - queuing item: {}", CLASS_NAME, item);
                eventAuditQueue.putItem(item);
            } catch (JsonProcessingException e) {
                log.error("{}", e.getMessage());
                throw new SQLException(e.getMessage(), e);
            }
        }
    }

    /**
     * This method is called when the database is closed.
     * If the method throws an exception, it will be logged, but
     * closing the database will continue.
     */
    @Override
    public void close() {
        log.info("{}.close()", CLASS_NAME);
    }

    /**
     * This method is called when the trigger is dropped.
     */
    @Override
    public void remove() {
        log.info("{}.remove()", CLASS_NAME);
    }

    protected void logFire(Connection conn, Object[] oldRow, Object[] newRow) throws SQLException {
        ObjectNode payload = calcJsonObject(oldRow, newRow);
        try {
            String jsonPayload = getObjectMapper().writeValueAsString(payload);
            log.info("{}.logFire(action: {}, catalog:{}, schema:{}, name:{}, table:{}, old:{}, new:{}, JSON: {})",
                    CLASS_NAME, this.action, this.catalog, schemaName, triggerName, tableName,
                    oldRow == null ? "null" : oldRow.length + " items", newRow == null ? "null" : newRow.length + " items",
                    jsonPayload);
        } catch (JsonProcessingException e) {
            log.error("{}", e.getMessage());
            throw new SQLException(e.getMessage(), e);
        }
    }

    protected ObjectNode calcJsonObject(Object[] oldRow, Object[] newRow) {
        ObjectNode ret = getObjectMapper().getNodeFactory().objectNode();
        if (oldRow != null) {
            ObjectNode oldRowJson = ret.putObject("oldRow");
            for (int i = 0; i < oldRow.length; i++) {
                oldRowJson.putPOJO(this.columns.get(i), oldRow[i]);
            }
        }
        if (newRow != null) {
            ObjectNode newRowJson = ret.putObject("newRow");
            for (int i = 0; i < newRow.length; i++) {
                newRowJson.putPOJO(this.columns.get(i), newRow[i]);
            }
        }
        return ret;
    }

    private List<String> extractColumns(Connection conn) throws SQLException {
        List<String> ret = new ArrayList<>();
        ResultSet dbSchema = conn.getMetaData().getColumns(catalog, schemaName, tableName, null);
        if (!dbSchema.isFirst()) {
            dbSchema.next();
        }
        do {
            ret.add(dbSchema.getString(4));
            dbSchema.next();
        } while (!dbSchema.isAfterLast());
        return ret;
    }

    private String calcActions(int type) {
        List<String> actions = new ArrayList<>();
        if ((type & Trigger.INSERT) != 0) {
            actions.add("INSERT");
        }
        if ((type & Trigger.UPDATE) != 0) {
            actions.add("UPDATE");
        }
        if ((type & Trigger.DELETE) != 0) {
            actions.add("DELETE");
        }
        if ((type & Trigger.SELECT) != 0) {
            actions.add("SELECT");
        }
        return String.join(" | ", actions);
    }

    private EventAuditQueue getEventAuditQueue() {
        if (BaseTrigger.eventAuditQueue == null) {
            BaseTrigger.eventAuditQueue = EventAuditQueue.get();
        }
        return BaseTrigger.eventAuditQueue;
    }

    public static ObjectMapper getObjectMapper() {
        if (objectMapper == null) {
            DateFormat fmt = new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT);
            objectMapper = new ObjectMapper().disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS).setDateFormat(fmt);
        }
        return objectMapper;
    }

    public static void setObjectMapper(ObjectMapper objectMapper) {
        BaseTrigger.objectMapper = objectMapper;
    }
}

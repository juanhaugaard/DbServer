package org.tayrona.dbserver.audit;

import lombok.Data;
import org.springframework.boot.configurationprocessor.json.JSONObject;

@Data
public class QueueItem {
    private String schemaName;
    private String tableName;
    private String action;
    private JSONObject payload;

    public QueueItem(String schemaName, String tableName, String action, JSONObject payload) {
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.action = action;
        this.payload = payload;
    }
}

package org.tayrona.dbserver.audit;

import lombok.Data;
import org.springframework.boot.configurationprocessor.json.JSONObject;

@Data
public class EventQueueItem {
    private String catalogName;
    private String schemaName;
    private String tableName;
    private String action;
    private JSONObject payload;

    public EventQueueItem(String catalogName, String schemaName, String tableName, String action, JSONObject payload) {
        this.catalogName = catalogName;
        this.schemaName = schemaName;
        this.tableName = tableName;
        this.action = action;
        this.payload = payload;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("catalogName='").append(catalogName).append('\'');
        sb.append(", schemaName='").append(schemaName).append('\'');
        sb.append(", tableName='").append(tableName).append('\'');
        sb.append(", action='").append(action).append('\'');
        sb.append(", payload=").append(payload);
        sb.append('}');
        return sb.toString();
    }
}

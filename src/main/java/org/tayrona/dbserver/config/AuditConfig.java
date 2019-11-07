package org.tayrona.dbserver.config;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class AuditConfig {
    @NotBlank
    private String url;
    private long initialDelay = 1000;
    private long queueLatency = 300;
    private long shutdownDelay = 500;
    private List<String> initSql;
    private List<String> triggerCreate;
    private List<String> triggerDrop;
}

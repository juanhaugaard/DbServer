package org.tayrona.dbserver.config;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class AuditConfig {
    private String catalog;
    @NotBlank
    private String url;
    private long queueLatency = 500;
    private long shutdownDelay = 0;
    private List<String> initSql;
    private List<String> triggerCreate;
    private List<String> triggerDrop;
}

package org.tayrona.dbserver.config;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class AuditConfig {
    @NotBlank
    private String url;
    private List<String> initSql;
    private List<String> triggerCreate;
    private List<String> triggerDrop;
}

package org.tayrona.dbserver.config;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class ClientConfig {
    @NotBlank
    private String url;
    private List<String> initSql;
}

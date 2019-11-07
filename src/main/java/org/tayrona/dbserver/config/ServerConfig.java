package org.tayrona.dbserver.config;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ServerConfig {
    @NotBlank
    private String options;
    private long shutdownDelay = 1000;
}

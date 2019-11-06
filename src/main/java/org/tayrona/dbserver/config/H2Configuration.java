package org.tayrona.dbserver.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.validation.constraints.NotNull;

@Data
@Configuration
@ConfigurationProperties(prefix = "h2")
public class H2Configuration {
    @NotNull
    private ServerConfig server;

    @NotNull
    private AuditConfig audit;

    @NotNull
    private ClientConfig client;
}

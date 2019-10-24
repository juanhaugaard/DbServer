package org.tayrona.dbserver.config;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ClientConfig {
    @NotBlank
    private String url;
}

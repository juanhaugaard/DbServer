package org.tayrona.dbserver.config;

import lombok.Data;

import java.util.List;

@Data
public class ClientConfig {
    private List<String> initSql;
}

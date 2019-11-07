package org.tayrona.dbserver.config;

import lombok.Data;

import java.util.List;

@Data
public class ClientConfig {
    private long interval=-1;
    private long initialDelay = 1000;
    private List<String> initSql;
}

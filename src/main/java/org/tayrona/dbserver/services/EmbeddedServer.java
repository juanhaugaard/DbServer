package org.tayrona.dbserver.services;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.h2.tools.Server;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tayrona.dbserver.config.H2Configuration;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.SQLException;

@Data
@Slf4j
@Service
public class EmbeddedServer {

    private H2Configuration h2Config;

    private static Server server;

    @PostConstruct
    public void initialize() throws SQLException {
        // start the server, allows to access the database remotely
        log.info("Using server options: '{}'", h2Config.getServer().getOptions());
        String[] args = h2Config.getServer().getOptions().split(" ");
        server = new Server();
        server.runTool(args);
    }

    @PreDestroy
    public void shutdown() {
        if (server != null) {
            log.info("EmbeddedServer stopping server");
            server.stop();
        }
    }

    @Autowired
    public void setH2Config(H2Configuration h2Config) {
        this.h2Config = h2Config;
    }
}


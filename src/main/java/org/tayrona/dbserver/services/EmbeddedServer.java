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
public class EmbeddedServer{

    private static final String CLASS_NAME = EmbeddedServer.class.getSimpleName();

    private H2Configuration h2Config;

    private static Server server;

    @PostConstruct
    public void initialize() throws SQLException {
        // start the server, allows to access the database remotely
        log.info("{}.initialize() - Using server options: '{}'", CLASS_NAME, h2Config.getServer().getOptions());
        String[] args = h2Config.getServer().getOptions().split(" ");
        server = new Server();
        server.runTool(args);
    }

    @PreDestroy
    public void shutdown() {
        if (server != null) {
            log.info("{}.shutdown", CLASS_NAME);
            delay(h2Config.getServer().getShutdownDelay());
            log.info("{}.shutdown server shutdown Now", CLASS_NAME);
            server.stop();
            server = null;
        }
    }

    private void delay(long millisec) {
        if (millisec < 0){
            millisec = 0;
        }
        log.debug("{}.delay({})", CLASS_NAME, millisec);
        try {
            if (millisec > 0) {
                Thread.sleep(millisec);
            } else {
                Thread.yield();
            }
        } catch (InterruptedException e) {
            log.warn("{}.delay() interrupted", CLASS_NAME);
        }
    }

    @Autowired
    public void setH2Config(H2Configuration h2Config) {
        this.h2Config = h2Config;
    }
}


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
public class EmbeddedServer implements Runnable{

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
            log.info("{}.shutdown() - scheduling server shutdown", CLASS_NAME);
            new Thread(this).start();
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

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        if (server != null) {
            delay(h2Config.getServer().getShutdownDelay());
            log.info("{}.scheduled server shutdown", CLASS_NAME);
            server.stop();
            server = null;
        }
    }
}


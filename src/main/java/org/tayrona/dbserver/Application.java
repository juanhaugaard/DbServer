package org.tayrona.dbserver;

import lombok.extern.slf4j.Slf4j;
import org.h2.jdbcx.JdbcDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.h2.tools.Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.List;

import javax.sql.DataSource;

@Slf4j
@SpringBootApplication
public class Application implements EnvironmentAware, CommandLineRunner {
    @Value("${server.options:'web -tcp -pg -baseDir ./data -trace'}")
    private String OPT;
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    private String[] sql = {
            "DROP TABLE TIMER IF EXISTS",
            "CREATE TABLE TIMER(ID INT PRIMARY KEY, TIME VARCHAR)",
            "MERGE INTO TIMER VALUES(1, LOCALTIME)"
    };

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // start the server, allows to access the database remotely
        Server server = Server.createTcpServer("-tcpPort", "9081");
        server.start();
        log.info("Using server options: '{}'", OPT);
        // now use the database in your application in embedded mode
        Class.forName("org.h2.Driver");
        log.info("Getting connection for: {}, {}, {}", url, username, password);
//        Connection conn = DriverManager.getConnection(url, username, password);
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl(url);
        Connection conn = dataSource.getConnection(username, password);

        // some simple 'business usage'
        Statement stat = conn.createStatement();
        stat.execute(sql[0]);
        stat.execute(sql[1]);
        System.out.println("Execute this a few times: SELECT TIME FROM TIMER");
        System.out.println("To stop this application (and the server), run: DROP TABLE TIMER");
        try {
            while (true) {
                // runs forever, except if you drop the table remotely
                stat.execute(sql[2]);
                Thread.sleep(1000);
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.toString());
        }
        conn.close();

        // stop the server
        server.stop();
    }

    @Override
    public void setEnvironment(Environment environment) {
        reportProperties(environment);
    }
    private void reportProperties(Environment environment) {
        if (environment == null) {
            log.warn("Environment is null, skipping properties report");
        } else {
            List<String> properties = Arrays.asList(
                    "spring.application.name",
                    "spring.datasource.name",
                    "server.servlet.context-path",
                    "management.endpoints.web.exposure.include",
                    "logging.level.root",
                    "server.port",
                    "user.home",
                    "user.dir"
            );
            log.info("*** Start Properties Report ***");
            properties.stream()
                    .filter(name -> environment.getProperty(name) != null)
                    .map(name -> {
                        if ("jwt.sec.key".equalsIgnoreCase(name)) {
                            return "jwt.sec.key: is present";
                        } else {
                            return String.format("%s: %s", name, environment.getProperty(name));
                        }
                    })
                    .forEach(logMsg -> log.info("*** {}", logMsg));
            log.info("*** End Properties Report ***");
        }
    }
}

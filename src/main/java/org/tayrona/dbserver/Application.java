package org.tayrona.dbserver;

import lombok.extern.slf4j.Slf4j;
import org.h2.jdbcx.JdbcDataSource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.tayrona.dbserver.config.H2Configuration;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;

@Slf4j
@SpringBootApplication
public class Application implements EnvironmentAware {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public DataSource getDataSource(H2Configuration configuration) {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl(configuration.getClient().getUrl());
        return dataSource;
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
                    "h2.server.options",
                    "h2.client.url",
                    "spring.datasource.username",
                    "spring.datasource.password",
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

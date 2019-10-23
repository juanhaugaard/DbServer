package org.tayrona.dbserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.List;

@Slf4j
@SpringBootApplication
public class Application implements EnvironmentAware, CommandLineRunner {
    @Value("${server.options:web -tcp -pg -baseDir .\\data -trace}")
    private String OPT;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("Using server options: '{}'", OPT);
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
                    "server.servlet.context-path",
                    "spring.datasource.name",
                    "logging.level.root",
                    "user.dir",
                    "server.port"
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

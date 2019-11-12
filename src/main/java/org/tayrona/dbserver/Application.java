package org.tayrona.dbserver;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.h2.jdbcx.JdbcDataSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.tayrona.dbserver.config.H2Configuration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.sql.DataSource;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@EnableSwagger2
@SpringBootApplication
public class Application implements ApplicationContextAware {

    private static Environment environment;

    private static ApplicationContext applicationContext;

    private ObjectMapper objectMapper;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public DataSource getDataSource(H2Configuration configuration) {
        JdbcDataSource dataSource = new JdbcDataSource();
        dataSource.setUrl(configuration.getAudit().getUrl());
        return dataSource;
    }

    @Bean
    public RestTemplate restTemplate() {
        CloseableHttpClient httpClient = HttpClients.custom().setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        objectMapper.setDateFormat(new SimpleDateFormat(Constants.DEFAULT_DATE_FORMAT));
        jsonConverter.setObjectMapper(objectMapper);
        restTemplate.setMessageConverters(Collections.singletonList(jsonConverter));
        return restTemplate;
    }

    private void reportProperties(Environment environment) {
        if (environment == null) {
            log.warn("Environment is null, skipping properties report");
        } else {
            List<String> properties = Arrays.asList(
                    "spring.application.name",
                    "spring.datasource.name",
                    "h2.server.shutdown-delay",
                    "h2.audit.shutdown-delay",
                    "h2.server.options",
                    "h2.audit.url",
                    "h2.client.url",
                    "h2.client.interval",
                    "h2.client.initial-delay",
                    "h2.audit.initial-delay",
                    "h2.audit.queue-latency",
                    "spring.datasource.username",
                    "spring.datasource.password",
                    "server.servlet.context-path",
                    "management.endpoints.web.exposure.include",
                    "logging.level.org.tayrona.dbserver",
                    "logging.level.ROOT",
                    "server.port",
                    "user.home",
                    "user.dir"
            );
            log.info("*** Start Properties Report ***");
            properties.stream()
                    .filter(name -> environment.getProperty(name) != null)
                    .map(name -> String.format("%s: %s", name, environment.getProperty(name)))
                    .forEach(logMsg -> log.info("*** {}", logMsg));
            log.info("*** End Properties Report ***");
        }
    }

    @Bean
    public Docket swaggerApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.tayrona.dbserver.controllers"))
                .build()
                .apiInfo(getApiInfo());
    }

    private ApiInfo getApiInfo() {
        return new ApiInfo(
                "DB Event sourcing on H2 Database",
                "This page lists all the active endpoint details of Event sourcing on H2 Database",
                "1.0.0",
                "",
                new Contact("Juan Haugaard", "github.com/juanhaugaard/DbServer", "juanhaugaard@gmail.com"),
                "LICENSE",
                "/",
                Collections.emptyList()
        );
    }

    /**
     * Set the ApplicationContext that this object runs in.
     * Normally this call will be used to initialize the object.
     * <p>Invoked after population of normal bean properties but before an init callback such
     * as {@link InitializingBean#afterPropertiesSet()}
     */
    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        applicationContext = context;
        if (applicationContext != null) {
            setEnvironment(applicationContext.getEnvironment());
        }
    }

    @Autowired
    public void setObjectMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void setEnvironment(Environment environment) {
        reportProperties(environment);
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static Environment getEnvironment() {
        return environment;
    }
}

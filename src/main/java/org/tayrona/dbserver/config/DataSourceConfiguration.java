package org.tayrona.dbserver.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfiguration {
    private H2Configuration  h2Configuration;

    @Value("${spring.datasource.username}")
    private String userName;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.minimumPoolSize:2}")
    private Integer minimumPoolsize;

    @Value("${spring.datasource.maximumPoolSize:10}")
    private Integer maximumPoolsize;

    @Bean(name="DataSource")
    public DataSource getDataSource() {
        return makeDataSource( h2Configuration.getAudit().getUrl(), "HikariCP");
    }

    @Bean(name="NamedJdbcTemplate")
    public NamedParameterJdbcTemplate getNamedJdbcTemplate() { return new NamedParameterJdbcTemplate(getDataSource());}

    @Bean(name="JdbcTemplate")
    public JdbcTemplate getJdbcTemplate() { return new JdbcTemplate(getDataSource());}

    protected DataSource makeDataSource( String url,  String poolName) {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(org.h2.Driver.class.getName());
        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(userName);
        hikariConfig.setPassword(password);

        hikariConfig.setMaximumPoolSize(maximumPoolsize);
        hikariConfig.setConnectionTestQuery("SELECT 1");
        hikariConfig.setPoolName(poolName);

        hikariConfig.addDataSourceProperty("dataSource.cachePrepStmts", "true");
        hikariConfig.addDataSourceProperty("dataSource.prepStmtCacheSize", "25");
        hikariConfig.addDataSourceProperty("dataSource.prepStmtCacheSqlLimit", "512");
        hikariConfig.addDataSourceProperty("dataSource.useServerPrepStmts", "true");

        return new HikariDataSource(hikariConfig);
    }

    @Autowired
    public void setH2Configuration(H2Configuration h2Configuration) {
        this.h2Configuration = h2Configuration;
    }
}

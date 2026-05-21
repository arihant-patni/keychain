package com.drive.keychain.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.sql.DataSource;

/**
 * DataSource configuration that loads connection properties from classpath `db-config.properties`.
 * <p>
 * Use `-Dexternal.db.config.path=...` or Spring property overrides if you want to point to an external file.
 */
@Configuration
@PropertySource(value = "classpath:db-config.properties", ignoreResourceNotFound = true)
public class DataSourceConfig {

    @Value("${db.url:}")
    private String url;

    @Value("${db.username:}")
    private String username;

    @Value("${db.password:}")
    private String password;

    @Value("${db.driverClassName:}")
    private String driverClassName;

    @Value("${db.maximumPoolSize:10}")
    private int maximumPoolSize;

    @Bean
    public DataSource dataSource() {
        DataSourceBuilder<?> builder = DataSourceBuilder.create();
        if (driverClassName != null && !driverClassName.isBlank()) {
            builder.driverClassName(driverClassName);
        }
        if (url != null && !url.isBlank()) {
            builder.url(url);
        }
        if (username != null) {
            builder.username(username);
        }
        if (password != null) {
            builder.password(password);
        }

        // Use HikariDataSource
        HikariDataSource ds = (HikariDataSource) builder.type(HikariDataSource.class).build();
        ds.setMaximumPoolSize(maximumPoolSize);
        return ds;
    }
}


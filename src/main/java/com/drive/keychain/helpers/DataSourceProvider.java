package com.drive.keychain.helpers;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.Properties;

/**
 * Test helper to load DB properties from classpath and provide a DriverManagerDataSource.
 * Keeps the property loading in one place so tests remain minimal.
 */
public final class DataSourceProvider {

    private DataSourceProvider() {
    }

    public static DataSource getDataSource() {
        Properties props = new Properties();
        try (InputStream is = DataSourceProvider.class.getClassLoader().getResourceAsStream("db-config.properties")) {
            if (is == null) {
                throw new IllegalStateException("db-config.properties not found on classpath");
            }
            props.load(is);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load db-config.properties", e);
        }

        String url = props.getProperty("db.url");
        String user = props.getProperty("db.username");
        String pass = props.getProperty("db.password");
        String driver = props.getProperty("db.driverClassName");

        DriverManagerDataSource ds = new DriverManagerDataSource();
        if (driver != null && !driver.isBlank()) ds.setDriverClassName(driver);
        ds.setUrl(url);
        ds.setUsername(user);
        ds.setPassword(pass);
        return ds;
    }
}


package com.drive.keychain.helpers;

import com.drive.keychain.client.MysqlClient;
import com.drive.keychain.repository.JdbcTodoRepository;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Small factory to create JdbcTemplate, JdbcClient and repository instances for tests.
 */
public final class JdbcFactory {

    private JdbcFactory() {}

    public static JdbcTemplate createJdbcTemplate(DataSource ds) {
        return new JdbcTemplate(ds);
    }

    public static MysqlClient createJdbcClient(DataSource ds) {
        return new MysqlClient(createJdbcTemplate(ds));
    }

    public static JdbcTodoRepository createRepository(DataSource ds) {
        return new JdbcTodoRepository(createJdbcClient(ds));
    }
}


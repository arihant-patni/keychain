package com.drive.keychain.client;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Lightweight JDBC client wrapper around Spring's JdbcTemplate.
 * Placed alongside `HttpClient` so both remote clients live in the same package.
 */
@Component
public class MysqlClient {
    private final JdbcTemplate jdbcTemplate;

    public MysqlClient(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public <T> T queryForObject(String sql, RowMapper<T> rowMapper, Object... args) {
        return jdbcTemplate.queryForObject(sql, rowMapper, args);
    }

    public List<Map<String, Object>> queryForList(String sql, Object... args) {
        return jdbcTemplate.queryForList(sql, args);
    }

    public int update(String sql, Object... args) {
        return jdbcTemplate.update(sql, args);
    }

    public int[] batchUpdate(String sql, List<Object[]> batchArgs) {
        return jdbcTemplate.batchUpdate(sql, batchArgs);
    }

    public JdbcTemplate getJdbcTemplate() {
        return this.jdbcTemplate;
    }
}


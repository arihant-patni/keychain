package com.drive.keychain.repository;

import com.drive.keychain.client.MysqlClient;
import com.drive.keychain.model.SignUp;
import com.drive.keychain.model.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * JDBC-based repository for users entities.
 * Uses the project's JdbcClient (wrapper over JdbcTemplate) for DB access.
 */
@Repository
public class JdbcRepository {
    private final MysqlClient mysqlClient;

    public JdbcRepository(MysqlClient mysqlClient) {
        this.mysqlClient = mysqlClient;
    }

    private static final RowMapper<SignUp> TODO_ROW_MAPPER = new RowMapper<>() {
        @Override
        public SignUp mapRow(ResultSet rs, int rowNum) throws SQLException {
            String username = rs.getObject("username") != null ? rs.getString("id") : null;
            String email = rs.getString("email");
            return SignUp.builder()
                    .user(User.builder()
                            .username(username)
                            .email(email)
                            .build())
                    .build();
        }
    };

    public Optional<SignUp> findByEmail(String email) {
        String sql = "SELECT username, email FROM users WHERE email = ?";
        try {
            SignUp signUp = mysqlClient.queryForObject(sql, TODO_ROW_MAPPER, email);
            return Optional.ofNullable(signUp);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<SignUp> findAll() {
        String sql = "SELECT email, username, password FROM users";
        // Use jdbcTemplate via client for mapping
        return mysqlClient.getJdbcTemplate().query(sql, TODO_ROW_MAPPER);
    }

    public int save(SignUp t) {
        String sql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
        return mysqlClient.update(sql, t.getUser().getUsername(), t.getUser().getEmail(), t.getUser().getPassword());
    }

    public int update(SignUp t) {
        String sql = "UPDATE users SET email = ?, password = ? WHERE username = ?";
        return mysqlClient.update(sql, t.getUser().getUsername(), t.getUser().getEmail(), t.getUser().getPassword());
    }

    public int deleteById(String email) {
        String sql = "DELETE FROM users WHERE email = ?";
        return mysqlClient.update(sql, email);
    }
}


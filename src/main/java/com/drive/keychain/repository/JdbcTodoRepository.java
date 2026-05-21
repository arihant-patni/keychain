package com.drive.keychain.repository;

import com.drive.keychain.client.MysqlClient;
import com.drive.keychain.model.Todo;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * JDBC-based repository for Todo entities.
 * Uses the project's JdbcClient (wrapper over JdbcTemplate) for DB access.
 */
@Repository
public class JdbcTodoRepository {
    private final MysqlClient mysqlClient;

    public JdbcTodoRepository(MysqlClient mysqlClient) {
        this.mysqlClient = mysqlClient;
    }

    private static final RowMapper<Todo> TODO_ROW_MAPPER = new RowMapper<>() {
        @Override
        public Todo mapRow(ResultSet rs, int rowNum) throws SQLException {
            Integer id = rs.getObject("id") != null ? rs.getInt("id") : null;
            Integer userId = rs.getObject("user_id") != null ? rs.getInt("user_id") : null;
            String title = rs.getString("title");
            Boolean completed = rs.getObject("completed") != null ? rs.getBoolean("completed") : null;
            return Todo.builder()
                    .id(id)
                    .userId(userId)
                    .title(title)
                    .completed(completed)
                    .build();
        }
    };

    public Optional<Todo> findById(int id) {
        String sql = "SELECT id, user_id, title, completed FROM todos WHERE id = ?";
        try {
            Todo todo = mysqlClient.queryForObject(sql, TODO_ROW_MAPPER, id);
            return Optional.ofNullable(todo);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public List<Todo> findAll() {
        String sql = "SELECT id, user_id, title, completed FROM todos";
        // Use jdbcTemplate via client for mapping
        return mysqlClient.getJdbcTemplate().query(sql, TODO_ROW_MAPPER);
    }

    public int save(Todo t) {
        String sql = "INSERT INTO todos (id, user_id, title, completed) VALUES (?, ?, ?, ?)";
        return mysqlClient.update(sql, t.getId(), t.getUserId(), t.getTitle(), t.getCompleted());
    }

    public int update(Todo t) {
        String sql = "UPDATE todos SET user_id = ?, title = ?, completed = ? WHERE id = ?";
        return mysqlClient.update(sql, t.getUserId(), t.getTitle(), t.getCompleted(), t.getId());
    }

    public int deleteById(int id) {
        String sql = "DELETE FROM todos WHERE id = ?";
        return mysqlClient.update(sql, id);
    }
}


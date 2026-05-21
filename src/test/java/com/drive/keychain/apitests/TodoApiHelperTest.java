package com.drive.keychain.apitests;

import com.drive.keychain.client.HttpClient;
import com.drive.keychain.config.ExternalApiConfig;
import com.drive.keychain.model.Todo;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
// no logger needed in this test class
import com.drive.keychain.repository.JdbcTodoRepository;
import com.drive.keychain.helpers.DataSourceProvider;
import com.drive.keychain.helpers.JdbcFactory;
import javax.sql.DataSource;
// no DriverManagerDataSource import needed for this minimal unit test
// no external property loading; use in-memory DB for tests

import static org.assertj.core.api.Assertions.assertThat;
// other static imports not needed for DB test

/**
 * tests for TodoApi.
 * Uses Mockito to mock ExternalApiConfig and HttpClient dependencies.
 */
@DisplayName("TodoApiHelper Tests")
class TodoApiHelperTest {

    private final ExternalApiConfig externalApiConfig = new ExternalApiConfig();
    private final HttpClient httpClient = new HttpClient();


    private final TodoApiHelper todoApiHelper = new TodoApiHelper(externalApiConfig, httpClient);

    @BeforeEach
    void setUp() {
        externalApiConfig.load();
    }

    @Test
    @DisplayName("should fetch Todo by ID and map response correctly")
    void testFetchTodoById() {
        // Arrange
        Integer todoId = 1;

        // Act
        Todo todo = todoApiHelper.fetchTodoById(todoId);

        // Assert
        assertThat(todo).isNotNull();
        assertThat(todo.getUserId()).isEqualTo(1);
        assertThat(todo.getId()).isEqualTo(1);
        assertThat(todo.getTitle()).isEqualTo("delectus aut autem");
        assertThat(todo.getCompleted()).isFalse();
    }
   
    @Test
    @DisplayName("Should create Todo")
    void testCreateToDo() {
        Todo request = Todo.builder()
                .id(1)
                .userId(7)
                .title("Created")
                .completed(true)
                .build();

        System.out.println(new Gson().toJson(request));

        // Act
        Todo response = todoApiHelper.createTodo(request);
        assertThat(request.getId()).isEqualTo(response.getId());
        assertThat(request.getUserId()).isEqualTo(response.getUserId());
        assertThat(request.getTitle()).isEqualTo(response.getTitle());
        assertThat(request.getCompleted()).isEqualTo(response.getCompleted());
    }

    @Test
    public void getUrl() {
        String url = externalApiConfig.buildJsonPlaceholderTodoUrlById(1);
        assertThat(url).isNotNull();
        assertThat(url).isEqualTo("https://jsonplaceholder.typicode.com/todos/1");
    }

    @Test
    @DisplayName("fetch via real DB connection and compare with expected builder")
    void testDbFetchById() {
        // Use TestDataSourceProvider to centralize property loading and DataSource creation
        DataSource ds = DataSourceProvider.getDataSource();
        JdbcTodoRepository repo = JdbcFactory.createRepository(ds);

        // Act: fetch from real DB (assumes table and row with id=1 already exist)
        java.util.Optional<Todo> actualOpt = repo.findById(1);

        // Expected builder
        Todo expected = Todo.builder()
                .id(1)
                .userId(7)
                .title("db-inserted todo")
                .completed(false)
                .build();

        // Assert
        assertThat(actualOpt).isPresent();
        Todo actual = actualOpt.get();
        assertThat(actual).isEqualTo(expected);
    }
}



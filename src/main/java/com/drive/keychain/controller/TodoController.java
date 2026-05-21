package com.drive.keychain.controller;

import com.drive.keychain.apitests.TodoApiHelper;
import com.drive.keychain.client.HttpClient;
import com.drive.keychain.config.ExternalApiConfig;
import com.drive.keychain.model.Todo;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Todo operations.
 * Provides endpoints for creating, fetching, and managing todos.
 */
@RestController
public class TodoController {

    private final ExternalApiConfig externalApiConfig = new ExternalApiConfig();
    private final HttpClient httpClient = new HttpClient();

    private final TodoApiHelper todoApiHelper = new TodoApiHelper(externalApiConfig, httpClient);

    /**
     * get a existing Todo.
     *
     * @return the existing Todo object
     */
    @GetMapping("/get-todo")
    public Todo createTodo() {
        externalApiConfig.load();
        return todoApiHelper.fetchTodoById(1);
    }
}

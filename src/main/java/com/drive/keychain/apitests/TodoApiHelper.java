package com.drive.keychain.apitests;

import com.drive.keychain.client.HttpClient;
import com.drive.keychain.config.ExternalApiConfig;
import com.drive.keychain.model.Todo;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;

/**
 * Helper class for calling the JSONPlaceholder Todo API using HttpClient.
 * <p>
 * Provides convenient methods to fetch, create, update todos with automatic URL building
 * and response mapping to the Todo model.
 * </p>
 *
 * Typical usage (Spring-injected):
 * <pre>
 *   TodoApiHelper helper = new TodoApiHelper(externalApiConfig, httpClient);
 *   Todo todo = helper.fetchTodoById(1);
 * </pre>
 */
public class TodoApiHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(TodoApiHelper.class);

    private final ExternalApiConfig externalApiConfig;
    private final HttpClient httpClient;

    public TodoApiHelper(ExternalApiConfig externalApiConfig, HttpClient httpClient) {
        this.externalApiConfig = externalApiConfig;
        this.httpClient = httpClient;
    }

    /**
     * Fetches a single Todo by ID from the configured JSONPlaceholder API.
     *
     * @param todoId the ID of the todo to fetch (e.g., 1, 2, 3...)
     * @return populated Todo model with userId, id, title, completed fields
     * @throws IllegalStateException if the API URL is not configured
     * @throws RuntimeException if the HTTP call fails
     */
    public Todo fetchTodoById(Integer todoId) {
        validateTodoId(todoId);
        String url = externalApiConfig.buildJsonPlaceholderTodoUrlById(todoId);
        LOGGER.info("Fetching Todo from: {}", url);

        try {
            JsonObject response = httpClient.send(
                    url,
                    RequestMethod.GET,
                    null,  // body
                    "application/json",  // contentType
                    null,  // returnType
                    null,  // authorizationKey
                    true   // isHttps (JSONPlaceholder uses HTTPS)
            );

            return mapJsonToTodo(response);
        } catch (Exception e) {
            LOGGER.error("Failed to fetch Todo {} from {}: {}", todoId, url, e.getMessage(), e);
            throw new RuntimeException("Failed to fetch todo with ID " + todoId, e);
        }
    }

    public Todo createTodo(Todo todo) {

        String url = externalApiConfig.buildJsonPlaceholderPostTodoUrl();
        LOGGER.info("Fetching Todo from: {}", url);

        try {
            JsonObject response = httpClient.send(
                    url,
                    RequestMethod.POST,
                    null,  // body
                    "application/json",// contentType
                    objectToJsonString(todo),  // returnType
                    null,  // authorizationKey
                    true   // isHttps (JSONPlaceholder uses HTTPS)
            );

            return mapJsonToTodo(response);
        } catch (Exception e) {
            LOGGER.error("Failed to Create Todo : {}", url, e.getMessage(), e);
            throw new RuntimeException("Failed to create todo with ID " + todo.getId(), e);
        }
    }

    /**
     * Maps a GSON JsonObject response from the API to a Todo model.
     * <p>
     * Defensively reads each field, returning null for missing/unparseable values.
     * </p>
     *
     * @param jsonObject the GSON JsonObject from API response
     * @return populated Todo model
     */
    public Todo mapJsonToTodo(JsonObject jsonObject) {
        if (jsonObject == null || jsonObject.isJsonNull()) {
            throw new IllegalArgumentException("Cannot map null JsonObject to Todo");
        }

        try {
            return Todo.builder()
                    .userId(jsonObject.has("userId") ? jsonObject.get("userId").getAsInt() : null)
                    .id(jsonObject.has("id") ? jsonObject.get("id").getAsInt() : null)
                    .title(jsonObject.has("title") ? jsonObject.get("title").getAsString() : null)
                    .completed(jsonObject.has("completed") ? jsonObject.get("completed").getAsBoolean() : null)
                    .build();
        } catch (Exception e) {
            LOGGER.warn("Failed to fully map JsonObject to Todo, some fields may be null: {}", e.getMessage());
            return Todo.builder().build(); // Return empty Todo
        }
    }

    /**
     * Converts a Java object to its JSON string representation using GSON.
     *
     * @param object the Java object to convert
     * @return JSON string representation of the object
     */
    public String objectToJsonString(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    /**
     * Validates that the todoId is not null and is positive.
     *
     * @param todoId the ID to validate
     * @throws IllegalArgumentException if the ID is invalid
     */
    private void validateTodoId(Integer todoId) {
        if (todoId == null) {
            throw new IllegalArgumentException("todoId cannot be null");
        }
        if (todoId <= 0) {
            throw new IllegalArgumentException("todoId must be a positive integer, got: " + todoId);
        }
    }
}


package com.drive.keychain.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;
import java.util.Map;
import java.util.HashMap;
import java.util.Collections;

/**
 * Loads external API properties at runtime.
 * <p>
 * Behavior:
 * - If the system property / env property `external.config.path` is set (non-empty), the component will try
 *   to load the file from that absolute filesystem path.
 * - Otherwise it falls back to a classpath resource `external-apis.properties` (packaged in the JAR).
 *
 * Usage examples:
 * - Provide an external properties file at runtime:
 *   java -Dexternal.config.path="C:/configs/external-apis.properties" -jar keychain.jar
 * - Or rely on the classpath fallback included in `src/main/resources`.
 */
@Component
public class ExternalApiConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalApiConfig.class);
    private final Properties props = new Properties();
    private final String EXTERNAL_CONFIG_FILE_NAME= "external-apis.properties";

    // Can be set as a JVM system property or Spring property/environment variable
    @Value("${external.config.path:}")
    private String externalConfigPath;

    @PostConstruct
    public void load() {
        // Try external filesystem path if provided
        if (externalConfigPath != null && !externalConfigPath.isBlank()) {
            Path p = Path.of(externalConfigPath);
            if (Files.exists(p)) {
                try (InputStream is = new FileInputStream(p.toFile())) {
                    props.load(is);
                    LOGGER.info("Loaded external API properties from {}", p.toAbsolutePath());
                    return;
                } catch (IOException e) {
                    LOGGER.warn("Failed to load external properties from {}, falling back to classpath. Cause: {}", p.toAbsolutePath(), e.getMessage());
                }
            } else {
                LOGGER.warn("external.config.path is set to {} but file does not exist. Falling back to classpath.", externalConfigPath);
            }
        }

        // Fallback to classpath resource
        try (InputStream is = new ClassPathResource(EXTERNAL_CONFIG_FILE_NAME).getInputStream()) {
            props.load(is);
            LOGGER.info("Loaded external API properties from classpath resource external-apis.properties");
        } catch (IOException e) {
            LOGGER.error("Failed to load classpath external-apis.properties: {}", e.getMessage());
        }
    }

    /**
     * Get a property value by key.
     */
    public String get(String key) {
        return props.getProperty(key);
    }

    /**
     * Get the JSONPlaceholder base URL.
     */
    public String getJsonPlaceholderBaseUrl() {
        return props.getProperty("external.jsonplaceholder.base-url");
    }

    /**
     * Get the JSONPlaceholder todos API base path (without parameters).
     */
    public String getJsonPlaceholderTodosPath() {
        return props.getProperty("external.jsonplaceholder.todos.path");
    }

    /**
     * Expose properties as an immutable Map<String,String> for callers that prefer key/value access.
     */
    public Map<String, String> asMap() {
        Map<String,String> m = new HashMap<>();
        for (String name : props.stringPropertyNames()) {
            m.put(name, props.getProperty(name));
        }
        return Collections.unmodifiableMap(m);
    }

    /**
     * Build a URL by combining a base key and a path key, optionally replacing path placeholders.
     * Path placeholders use the form {name}. If placeholders are not present and a single param named "id" is
     * provided, the id will be appended to the path.
     *
     * @param baseKey property key for the base URL (e.g. external.jsonplaceholder.base-url)
     * @param pathKey property key for the path (e.g. external.jsonplaceholder.todos.path)
     * @param pathParams map of path parameter names to values used to replace placeholders
     * @return the constructed URL or null if required properties are missing
     */
    public String buildUrl(String baseKey, String pathKey, Map<String, String> pathParams) {
        String baseUrl = props.getProperty(baseKey);
        String path = props.getProperty(pathKey);
        if (baseUrl == null || baseUrl.isBlank() || path == null || path.isBlank()) {
            return null;
        }

        String resultPath = path;
        if (pathParams != null && !pathParams.isEmpty()) {
            for (Map.Entry<String,String> e : pathParams.entrySet()) {
                String placeholder = "{" + e.getKey() + "}";
                if (resultPath.contains(placeholder)) {
                    resultPath = resultPath.replace(placeholder, e.getValue());
                }
            }
        }

        // if path still contains a placeholder or no placeholder existed, handle common id append
        if (!resultPath.contains("{")) {
            // if no explicit placeholder was present but id param is provided, append
            final String checkPath = resultPath;
            boolean containsParamValue = pathParams != null && pathParams.values().stream().anyMatch(v -> checkPath.contains(v));
            if ((pathParams == null || !containsParamValue)
                    && pathParams != null && pathParams.containsKey("id") && !resultPath.endsWith("/")) {
                resultPath = resultPath + "/" + pathParams.get("id");
            }
        }

        return baseUrl + resultPath;
    }

    /** Convenience: build the JSONPlaceholder todo URL by id. */
    public String buildJsonPlaceholderTodoUrlById(Integer todoId) {
        if (todoId == null) return null;
        return buildUrl("external.jsonplaceholder.base-url","external.jsonplaceholder.todos.path", Map.of("id", String.valueOf(todoId)));
    }

    /** Convenience: build the JSONPlaceholder todo URL for Posting. */
    public String buildJsonPlaceholderPostTodoUrl() {
        return getJsonPlaceholderBaseUrl() + getJsonPlaceholderTodosPath();
    }

}

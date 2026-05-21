package com.drive.keychain.client;

import com.drive.keychain.util.ChainConstants;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * HTTP Client for making GET, POST, PUT requests to external APIs.
 * Handles both HTTP and HTTPS connections with configurable timeouts.
 * Returns responses as GSON JsonObject for easy parsing.
 */
@Component
public class HttpClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClient.class);
    private static final int CONNECT_TIMEOUT_MS = 60000;

    /**
     * Sends an HTTP request to the specified URL with the given parameters.
     *
     * @param source       The URL to send the request to. Must not be null.
     * @param requestMethod The HTTP method to use (GET, POST, PUT). Must not be null.
     * @param contentType  The content type of the request (optional for GET).
     * @param returnType   The expected return type (optional).
     * @param body         The body of the request (required for POST/PUT, ignored for GET).
     * @param authorizationKey The authorization key for the request (optional).
     * @param secure       Whether to use HTTPS.
     * @return The response as a JsonObject.
     * @throws IllegalArgumentException If required parameters are missing or invalid.
     * @throws IOException If an I/O error occurs during the request.
     * @throws JsonSyntaxException If the response cannot be parsed as JSON.
     * @throws RuntimeException If the HTTP response code indicates an error (not 200/201).
     */
    public JsonObject send(String source, RequestMethod requestMethod, String contentType, String returnType, String body,
                           String authorizationKey, boolean secure) throws IOException {

        // Validate required parameters
        if (source == null || source.isEmpty()) {
            throw new IllegalArgumentException("Source URL cannot be null or empty");
        }
        if (requestMethod == null) {
            throw new IllegalArgumentException("Request method cannot be null");
        }

        URL url = new URL(source);
        HttpURLConnection con = null;

        try {
            // Create connection with proper type casting
            if (secure) {
                con = (HttpsURLConnection) url.openConnection();
            } else {
                con = (HttpURLConnection) url.openConnection();
            }

            // Configure connection timeout
            con.setConnectTimeout(CONNECT_TIMEOUT_MS);
            con.setReadTimeout(CONNECT_TIMEOUT_MS);

            // Set optional request headers
            if (returnType != null && !returnType.isEmpty()) {
                con.setRequestProperty(ChainConstants.HTTP_REQUEST_ACCEPT, returnType);
            }

            if (authorizationKey != null && !authorizationKey.isEmpty()) {
                con.setRequestProperty(ChainConstants.HTTP_REQUEST_AUTHORIZATION, authorizationKey);
            }

            // Handle different HTTP methods
            switch (requestMethod) {
                case GET:
                    con.setRequestMethod("GET");
                    handleResponse(con);
                    break;

                case POST:
                    validateBodyParameter(body);
                    con.setDoOutput(true);
                    con.setRequestMethod(ChainConstants.HTTP_METHOD_POST);
                    if (contentType != null && !contentType.isEmpty()) {
                        con.setRequestProperty(ChainConstants.HTTP_REQUEST_CONTENT_TYPE, contentType);
                    }
                    writeRequestBody(con, body);
                    handleResponse(con);
                    break;

                case PUT:
                    validateBodyParameter(body);
                    con.setDoOutput(true);
                    con.setRequestMethod(ChainConstants.HTTP_METHOD_PUT);
                    if (contentType != null && !contentType.isEmpty()) {
                        con.setRequestProperty(ChainConstants.HTTP_REQUEST_CONTENT_TYPE, contentType);
                    }
                    writeRequestBody(con, body);
                    handleResponse(con);
                    break;

                default:
                    throw new IllegalArgumentException("Unsupported HTTP method: " + requestMethod);
            }

            // Read and parse response
            return readResponse(con);

        } finally {
            // Ensure connection is always closed
            if (con != null) {
                con.disconnect();
            }
        }
    }

    /**
     * Validates that body parameter is provided for methods that require it.
     */
    private void validateBodyParameter(String body) {
        if (body == null || body.isEmpty()) {
            throw new IllegalArgumentException("Request body cannot be null or empty for POST/PUT methods");
        }
    }

    /**
     * Writes the request body to the output stream.
     */
    private void writeRequestBody(HttpURLConnection con, String body) throws IOException {
        try (OutputStream os = con.getOutputStream()) {
            os.write(body.getBytes(StandardCharsets.UTF_8));
            os.flush();
        }
    }

    /**
     * Validates the HTTP response code.
     */
    private void handleResponse(HttpURLConnection con) throws IOException {
        int responseCode = con.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK && responseCode != HttpURLConnection.HTTP_CREATED) {
            String errorMessage = readErrorStream(con);
            LOGGER.error("HTTP error code: {}. Details: {}", responseCode, errorMessage);
            throw new RuntimeException("Failed: HTTP error code: " + responseCode + ". " + errorMessage);
        }
    }

    /**
     * Reads the error response from the connection.
     */
    private String readErrorStream(HttpURLConnection con) {
        try (InputStream errorStream = con.getErrorStream()) {
            if (errorStream != null) {
                return IOUtils.toString(errorStream, StandardCharsets.UTF_8);
            }
        } catch (IOException e) {
            LOGGER.warn("Failed to read error stream", e);
        }
        return "No error details available";
    }

    /**
     * Reads the response body and parses it as JSON.
     */
    private JsonObject readResponse(HttpURLConnection con) throws IOException {
        try (InputStream inputStream = con.getInputStream()) {
            String result = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            if (result == null || result.isEmpty()) {
                throw new IOException("Empty response body received");
            }
            return JsonParser.parseString(result).getAsJsonObject();
        } catch (JsonSyntaxException e) {
            throw new IOException("Failed to parse response as JSON", e);
        }
    }

}

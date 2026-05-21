package com.drive.keychain.apitests;

import com.drive.keychain.client.HttpClient;
import com.drive.keychain.config.ExternalApiConfig;
import com.drive.keychain.model.SignUp;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Helper class for performing sign up operations against the configured external API.
 * Encapsulates the logic for constructing the API URL, making the HTTP request, and parsing the response.
 */
public class SignUpHelper {
    private static final Logger LOGGER = LoggerFactory.getLogger(SignUpHelper.class);

    private final ExternalApiConfig externalApiConfig;
    private final HttpClient httpClient;

    public SignUpHelper(ExternalApiConfig externalApiConfig, HttpClient httpClient) {
        this.externalApiConfig = externalApiConfig;
        this.httpClient = httpClient;
    }

    /**
     * Performs a sign up operation by sending a POST request to the external API.
     *
     * @param signUp the SignUp object containing the user details to be signed up
     * @return the SignUp response from the API, parsed into a SignUp object
     * @throws RuntimeException if there is an error during the HTTP request or response parsing
     */
    public SignUp signUp(SignUp signUp) {
        String url = externalApiConfig.buildSignUpUrl();
        LOGGER.info("Fetching signUp from: {}", url);

        try {
            JsonObject response = httpClient.send(
                    url,
                    RequestMethod.POST,
                    "application/json",  // contentType
                    null,  // returnType
                    objectToJsonString(signUp),  // body
                    null,  // authorizationKey
                    true   // isHttps (JSONPlaceholder uses HTTPS)
            );

            return new Gson().fromJson(response, SignUp.class);
        } catch (Exception e) {
            LOGGER.error("Failed to do sign up from {}: {}", url, e.getMessage(), e);
            throw new RuntimeException("Failed to signup" , e);
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
}


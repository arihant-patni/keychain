package com.drive.keychain.controller;

import com.drive.keychain.apitests.SignUpHelper;
import com.drive.keychain.client.HttpClient;
import com.drive.keychain.config.ExternalApiConfig;
import com.drive.keychain.model.SignUp;
import com.drive.keychain.model.User;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Todo operations.
 * Provides endpoints for creating, fetching, and managing todos.
 */
@RestController
public class SignupController {

    private final ExternalApiConfig externalApiConfig = new ExternalApiConfig();
    private final HttpClient httpClient = new HttpClient();

    private final SignUpHelper signUpHelper = new SignUpHelper(externalApiConfig, httpClient);

    /**
     * get a existing Todo.
     *
     * @return the existing Todo object
     */
    @GetMapping("/get-signup")
    public SignUp createTodo() {
        externalApiConfig.load();
        return signUpHelper.signUp(SignUp.builder().user(User.builder()
                .password("passw0rd@123")
                .username("user123")
                .email("useraxrpj123@gmail.com").build()).build());
    }
}

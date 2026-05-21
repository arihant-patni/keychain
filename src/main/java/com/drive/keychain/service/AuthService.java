package com.drive.keychain.service;

import com.drive.keychain.apitests.SignUpHelper;
import com.drive.keychain.client.HttpClient;
import com.drive.keychain.config.ExternalApiConfig;
import com.drive.keychain.model.SignUp;
import com.drive.keychain.model.User;
import com.google.gson.Gson;

import java.io.FileReader;
import java.util.List;
import java.util.Random;

public class AuthService {
    private static final String USERS_FILE_PATH = "C:\\Users\\ariha\\Downloads\\keychain\\src\\main\\resources\\random-users-for-auth.json";
    private static String token;

    private final static ExternalApiConfig externalApiConfig = new ExternalApiConfig();
    private final static HttpClient httpClient = new HttpClient();

    private final static SignUpHelper signUpHelper = new SignUpHelper(externalApiConfig, httpClient);


    public static void init() throws Exception {
        // Load users from file
        Gson gson = new Gson();
        List<User> users = List.of(gson.fromJson(new FileReader(USERS_FILE_PATH), User[].class));

        // Pick a random user
        User randomUser = users.get(new Random().nextInt(users.size()));
        externalApiConfig.load();

        SignUp signUp = signUpHelper.signUp(SignUp.builder().user(randomUser).build());
        token = signUp.getUser().getToken();

    }

    public static String getAuthHeader() {
        if (token == null) throw new IllegalStateException("Token not initialized");
        return "Token " + token;
    }

}

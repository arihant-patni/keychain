package com.drive.keychain.apitests;

import com.drive.keychain.model.SignUp;
import com.drive.keychain.model.User;
import org.testng.annotations.DataProvider;

public class SignUpDataProvider {

    @DataProvider(name = "signup-provider")
    public static Object[][] signUpData() {

        SignUp request = SignUp.builder().user(User.builder()
                .password("passw0rd@123")
                .username("user123")
                .email("useraxrpj123@gmail.com").build()).build();

        return new Object[][] {
                {SignUp.builder().user(User.builder()
                        .password("passw0rd@123")
                        .username("user123")
                        .email("useraxrpj123@gmail.com").build()).build() },
        };
    }

}

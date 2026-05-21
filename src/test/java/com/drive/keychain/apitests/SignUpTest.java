package com.drive.keychain.apitests;

import com.drive.keychain.client.HttpClient;
import com.drive.keychain.config.ExternalApiConfig;
import com.drive.keychain.helpers.DataSourceProvider;
import com.drive.keychain.helpers.JdbcFactory;
import com.drive.keychain.model.SignUp;
import com.drive.keychain.model.User;
import com.drive.keychain.repository.JdbcRepository;
import com.drive.keychain.service.AuthService;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import javax.sql.DataSource;

import static org.assertj.core.api.Assertions.assertThat;

public class SignUpTest {

    private final ExternalApiConfig externalApiConfig = new ExternalApiConfig();
    private final HttpClient httpClient = new HttpClient();


    private final SignUpHelper signUpHelper = new SignUpHelper(externalApiConfig, httpClient);

    @BeforeTest
    void setUp() throws Exception {
        externalApiConfig.load();
        AuthService.init();
    }

    @Test(description = "Sign up", dataProvider = "signup-provider", dataProviderClass = SignUpDataProvider.class)
    void signUp(SignUp signUpRequest) {

        // Act
        SignUp signUpResponse = signUpHelper.login(signUpRequest);

        // Assert
        SoftAssert softAssert = new SoftAssert();
        softAssert.assertEquals(signUpResponse.getUser().getUsername(), signUpRequest.getUser().getUsername(), "Username should match");
        softAssert.assertEquals(signUpResponse.getUser().getEmail(), signUpRequest.getUser().getEmail(), "Email should match");
        //softAssert.assertEquals(signUpResponse.getUser().getPassword(), signUpRequest.getUser().getPassword(), "Password should match");
        softAssert.assertAll();

    }

    @Test(description = "Fetch SignUp by email from DB", dependsOnMethods = "signUp", enabled = false)
    void testDbFetchById() {
        // Use TestDataSourceProvider to centralize property loading and DataSource creation
        DataSource ds = DataSourceProvider.getDataSource();
        JdbcRepository repo = JdbcFactory.createRepository(ds);

        // Act: fetch from real DB (assumes table and row with id=1 already exist)
        java.util.Optional<SignUp> actualOpt = repo.findByEmail("test123@test.com");

        // Expected builder
        SignUp expected = SignUp.builder()
                .user(User.builder()
                        .username("username")
                        .email("test123@test.com")
                        .build())
                .build();

        // Assert
        assertThat(actualOpt).isPresent();
        SignUp actual = actualOpt.get();
        assertThat(actual).isEqualTo(expected);
    }
}

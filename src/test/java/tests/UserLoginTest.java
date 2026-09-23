package tests;

import client.StellarBurgersClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.User;
import org.junit.After;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

public class UserLoginTest {

    private StellarBurgersClient client = new StellarBurgersClient();
    private String accessToken;

    @Test
    @DisplayName("Вход под существующим пользователем")
    public void loginExistingUser() {
        // Создаём пользователя
        User user = new User(
                "login" + System.currentTimeMillis() + "@yandex.ru",
                "pass123",
                "LoginUser"
        );
        Response createResponse = client.createUser(user);
        accessToken = createResponse.then().extract().path("accessToken");

        // Логинимся
        Response loginResponse = client.loginUser(user);
        loginResponse.then()
                .statusCode(200)
                .body("success", is(true))
                .body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("Вход с неверным логином и паролем")
    public void loginWithWrongCredentials() {
        User user = new User(
                "wrong" + System.currentTimeMillis() + "@yandex.ru",
                "wrongpass",
                "WrongUser"
        );
        Response response = client.loginUser(user);

        response.then()
                .statusCode(401)
                .body("success", is(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            client.deleteUser(accessToken);
            accessToken = null;
        }
    }
}

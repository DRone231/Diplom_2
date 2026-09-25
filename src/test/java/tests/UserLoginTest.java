package tests;

import client.StellarBurgersClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

public class UserLoginTest {

    private StellarBurgersClient client = new StellarBurgersClient();
    private User existingUser;
    private String accessToken;

    @Before
    public void setUp() {
        // Создаём пользователя один раз перед всеми тестами, где он нужен
        existingUser = new User(
                "login" + System.currentTimeMillis() + "@yandex.ru",
                "pass123",
                "LoginUser"
        );
        Response createResponse = client.createUser(existingUser);
        accessToken = createResponse.then().extract().path("accessToken");
    }

    @Test
    @DisplayName("Вход под существующим пользователем")
    public void loginExistingUser() {
        Response loginResponse = client.loginUser(existingUser);
        loginResponse.then()
                .statusCode(200)
                .body("success", is(true))
                .body("accessToken", notNullValue());
    }

    @Test
    @DisplayName("Вход с неверным email")
    public void loginWithWrongEmail() {
        User wrongUser = new User(
                "wrong-email@yandex.ru",  // заведомо неверный email
                existingUser.getPassword(),
                existingUser.getName()
        );
        Response response = client.loginUser(wrongUser);

        response.then()
                .statusCode(401)
                .body("success", is(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Вход с неверным паролем")
    public void loginWithWrongPassword() {
        User wrongUser = new User(
                existingUser.getEmail(),
                "wrongpassword123",  // заведомо неверный пароль
                existingUser.getName()
        );
        Response response = client.loginUser(wrongUser);

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

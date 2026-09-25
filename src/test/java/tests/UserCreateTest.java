package tests;

import client.StellarBurgersClient;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.User;
import org.junit.After;
import org.junit.Test;

import static org.hamcrest.Matchers.*;

public class UserCreateTest {

    private StellarBurgersClient client = new StellarBurgersClient();
    private String accessToken;

    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Проверка, что можно создать пользователя с уникальными данными")
    public void createUniqueUser() {
        User user = new User(
                "test" + System.currentTimeMillis() + "@yandex.ru",
                "password123",
                "TestUser"
        );
        Response response = client.createUser(user);

        response.then()
                .statusCode(200)
                .body("success", is(true))
                .body("user.email", equalTo(user.getEmail().toLowerCase()))
                .body("accessToken", notNullValue())
                .body("refreshToken", notNullValue());

        accessToken = response.then().extract().path("accessToken");
    }

    @Test
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    @Description("Проверка, что нельзя создать пользователя с существующими данными")
    public void createDuplicateUser() {
        User user = new User(
                "dup" + System.currentTimeMillis() + "@yandex.ru",
                "password123",
                "DupUser"
        );
        Response first = client.createUser(user);
        accessToken = first.then().extract().path("accessToken");

        Response second = client.createUser(user);
        second.then()
                .statusCode(403)
                .body("success", is(false))
                .body("message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без email")
    @Description("Проверка, что нельзя создать пользователя без email")
    public void createUserWithoutEmail() {
        User user = new User(null, "password123", "NoEmailUser");
        Response response = client.createUser(user);

        response.then()
                .statusCode(403)
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без password")
    @Description("Проверка, что нельзя создать пользователя без password")
    public void createUserWithoutPassword() {
        User user = new User("no-pass@yandex.ru", null, "NoPassUser");
        Response response = client.createUser(user);

        response.then()
                .statusCode(403)
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без name")
    @Description("Проверка, что нельзя создать пользователя без name")
    public void createUserWithoutName() {
        User user = new User("no-name@yandex.ru", "password123", null);
        Response response = client.createUser(user);

        response.then()
                .statusCode(403)
                .body("success", is(false))
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            client.deleteUser(accessToken);
            accessToken = null;
        }
    }
}

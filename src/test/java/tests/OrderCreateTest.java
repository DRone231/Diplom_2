package tests;

import client.StellarBurgersClient;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import model.Order;
import model.User;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.*;

public class OrderCreateTest {

    private StellarBurgersClient client = new StellarBurgersClient();
    private String accessToken;
    private List<String> realIngredientIds;

    @Before
    public void setUp() {
        // Создаём пользователя для тестов с авторизацией
        User user = new User(
                "order" + System.currentTimeMillis() + "@yandex.ru",
                "pass123",
                "OrderUser"
        );
        Response createResponse = client.createUser(user);
        accessToken = createResponse.then().extract().path("accessToken");

        // Получаем реальные хеши ингредиентов с сервера
        Response ingredientsResponse = client.getIngredients();
        realIngredientIds = ingredientsResponse.then()
                .statusCode(200)
                .extract()
                .path("data._id");
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    public void createOrderWithAuth() {
        Order order = new Order(Arrays.asList(
                realIngredientIds.get(0),
                realIngredientIds.get(1)
        ));
        Response response = client.createOrder(order, accessToken);

        response.then()
                .statusCode(200)
                .body("success", is(true))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    public void createOrderWithoutAuth() {
        Order order = new Order(Arrays.asList(
                realIngredientIds.get(0),
                realIngredientIds.get(1)
        ));
        Response response = client.createOrder(order, null);

        response.then()
                .statusCode(200)
                .body("success", is(true))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа с ингредиентами")
    public void createOrderWithIngredients() {
        Order order = new Order(Arrays.asList(
                realIngredientIds.get(0),
                realIngredientIds.get(3),
                realIngredientIds.get(5)
        ));
        Response response = client.createOrder(order, accessToken);

        response.then()
                .statusCode(200)
                .body("success", is(true))
                .body("order.number", notNullValue());
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    public void createOrderWithoutIngredients() {
        Order order = new Order(Collections.emptyList());
        Response response = client.createOrder(order, accessToken);

        response.then()
                .statusCode(400)
                .body("success", is(false))
                .body("message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    public void createOrderWithInvalidHash() {
        Order order = new Order(Arrays.asList("invalidHash123", "anotherBadHash456"));
        Response response = client.createOrder(order, accessToken);

        response.then()
                .statusCode(500);
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            client.deleteUser(accessToken);
            accessToken = null;
        }
    }
}

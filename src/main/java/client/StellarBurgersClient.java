package client;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class StellarBurgersClient {

    private static final String BASE_URL = "https://stellarburgers.education-services.ru";

    private RequestSpecification spec() {
        return given()
                .baseUri(BASE_URL)
                .contentType("application/json")
                .filter(new AllureRestAssured());
    }

    // Создание пользователя
    public Response createUser(Object body) {
        return spec()
                .body(body)
                .when()
                .post("/api/auth/register");
    }

    // Логин пользователя
    public Response loginUser(Object body) {
        return spec()
                .body(body)
                .when()
                .post("/api/auth/login");
    }

    // Удаление пользователя
    public void deleteUser(String accessToken) {
        spec()
                .header("Authorization", accessToken)
                .when()
                .delete("/api/auth/user");
    }

    // Получение списка ингредиентов
    public Response getIngredients() {
        return spec().when().get("/api/ingredients");
    }

    // Создание заказа
    public Response createOrder(Object body, String accessToken) {
        RequestSpecification request = spec().body(body);
        if (accessToken != null && !accessToken.isEmpty()) {
            request.header("Authorization", accessToken);
        }
        return request.when().post("/api/orders");
    }
}

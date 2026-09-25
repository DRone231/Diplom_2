package client;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.qameta.allure.Step;

import static io.restassured.RestAssured.given;

public class StellarBurgersClient {

    private static final String BASE_URL = "https://stellarburgers.education-services.ru";

    private RequestSpecification spec() {
        return given()
                .baseUri(BASE_URL)
                .contentType("application/json")
                .filter(new AllureRestAssured());
    }

    @Step("Создание пользователя: POST /api/auth/register")
    public Response createUser(Object body) {
        return spec()
                .body(body)
                .when()
                .post("/api/auth/register");
    }

    @Step("Логин пользователя: POST /api/auth/login")
    public Response loginUser(Object body) {
        return spec()
                .body(body)
                .when()
                .post("/api/auth/login");
    }

    @Step("Удаление пользователя: DELETE /api/auth/user")
    public void deleteUser(String accessToken) {
        spec()
                .header("Authorization", accessToken)
                .when()
                .delete("/api/auth/user");
    }

    @Step("Получение списка ингредиентов: GET /api/ingredients")
    public Response getIngredients() {
        return spec().when().get("/api/ingredients");
    }

    @Step("Создание заказа: POST /api/orders (с токеном={hasToken})")
    public Response createOrder(Object body, String accessToken) {
        RequestSpecification request = spec().body(body);
        boolean hasToken = accessToken != null && !accessToken.isEmpty();
        if (hasToken) {
            request.header("Authorization", accessToken);
        }
        return request.when().post("/api/orders");
    }
}

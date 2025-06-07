package data.orderData;

import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.List;

import static data.orderData.DataForOrders.OrderContants.*;

public class OrderApi {
    private String token;
    private List<String> ingredients;

    public OrderApi() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Step("Получение данных об ингредиентах")
    public Response getAboutIngredients() {
        Response response = RestAssured.given()
                .header("Content-type", "application/json")
                .when()
                .get(GET_INGREDIENTS);
        this.ingredients = response.jsonPath().getList("data._id");
        return response;
    }

    @Step("Создание заказа")
    public Response createOrder() {
        String requestBody = String.format("{\"ingredients\": [\"%s\"]}", String.join("\", \"", ingredients));
        Response response = RestAssured.given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .body(requestBody)
                .when()
                .post(CREATE_ORDER);
        return response;
    }

    @Step("Создание заказа без ингредиентов")
    public Response createOrderWithoutIngredients() {
        String requestBody = String.format("{\"ingredients\": null}");
        Response response = RestAssured.given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .body(requestBody)
                .when()
                .post(CREATE_ORDER);
        return response;
    }

    @Step("Создание заказа с неввалидным хешом ингредиентов")
    public Response createOrderWithFakeIngredients() {
        String requestBody = "{\"ingredients\": [\"228\"]}";
        Response response = RestAssured.given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .body(requestBody)
                .when()
                .post(CREATE_ORDER);
        return response;
    }

    @Step("Получение заказов конкретного пользователя")
    public Response getOrders() {
        Response response = RestAssured.given()
                .header("Content-type", "application/json")
                .header("Authorization", token)
                .when()
                .get(CREATE_ORDER);
        return response;
    }
}
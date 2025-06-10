package tests.orderTests;

import data.orderData.OrderApi;
import data.userData.CreateUser;
import data.userData.UserApi;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static data.userData.DataForUsers.*;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class CreateOrderTest {
    private static String email;
    private static String password;
    private static String name;
    private static OrderApi orderApi;
    private static UserApi userApi;

    @BeforeClass
    public static void setUp() {
        orderApi = new OrderApi();
        email = generateRandomMail();
        password = generateRandomPass();
        name = generateRandomName();
        userApi = new UserApi();

        CreateUser user = new CreateUser(email, password, name);
        userApi.createUser(user);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией и ингредиентами")
    @Description("Тест проверяет создание заказа с авторизацией и ингредиентами")
    public void createOrderTest() {
        orderApi.getAboutIngredients();
        orderApi.setToken(userApi.getToken());
        Response response = orderApi.createOrder();
        response.then().statusCode(SC_OK)
                .and()
                .assertThat().body("success", equalTo(true),
                        "name", not(emptyOrNullString()),
                        "order.status", equalTo("done"),
                        "order.ingredients", not(emptyOrNullString()),
                        "order.owner.name", equalTo(name),
                        "order.owner.email", equalTo(email));
    }

    @Test
    @DisplayName("Создание заказа без авторизацией, с ингредиентами")
    @Description("Тест проверяет создание заказа без авторизации, с ингредиентами")
    public void createOrderWithoutAuthTest() {
        orderApi.getAboutIngredients();
        orderApi.setToken("");
        Response response = orderApi.createOrder();
        response.then().statusCode(SC_OK)
                .and()
                .assertThat().body("success", equalTo(true),
                        "name", not(emptyOrNullString()),
                        "order.number", not(emptyOrNullString()),
                        "order.owner.name", emptyOrNullString(),
                        "order.owner.email", emptyOrNullString());
    }

    @Test
    @DisplayName("Создание заказа с авторизацией, без ингредиентов")
    @Description("Тест проверяет создание заказа с авторизацией, без ингредиентов")
    public void createOrderWithoutIngredientsTest() {
        orderApi.setToken(userApi.getToken());
        Response response = orderApi.createOrderWithoutIngredients();
        response.then().statusCode(SC_BAD_REQUEST)
                .and()
                .assertThat().body("success", equalTo(false),
                        "message", equalTo("Ingredient ids must be provided"));
    }

    @Test
    @DisplayName("Создание заказа с авторизацией, c неправильным хешем ингредиентов")
    @Description("Тест проверяет создание заказа с авторизацией, c неправильным хешем ингредиентов")
    public void createOrderWithFakeIngredientsTest() {
        orderApi.setToken(userApi.getToken());
        Response response = orderApi.createOrderWithFakeIngredients();
        response.then().statusCode(SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    @DisplayName("Получение всех заказов пользователя")
    @Description("Тест проверяет получение всех заказов пользователя")
    public void getOrdersTest() {
        orderApi.setToken(userApi.getToken());
        Response response = orderApi.getOrders();
        response.then().statusCode(SC_OK)
                .and()
                .assertThat().body("success", equalTo(true),
                        "orders.number", hasSize(1),
                        "total", greaterThanOrEqualTo(1),
                        "totalToday", greaterThanOrEqualTo(1));
    }

    @Test
    @DisplayName("Получение всех заказов пользователя без авторизации")
    @Description("Тест проверяет НЕ получение всех заказов пользователя без авторизации")
    public void getOrdersWithoutTokenTest() {
        orderApi.setToken("");
        Response response = orderApi.getOrders();
        response.then().statusCode(SC_UNAUTHORIZED)
                .and().assertThat().body("success", equalTo(false),
                        "message", equalTo("You should be authorised"));
    }


    @AfterClass
    public static void tearDown() {
        userApi.deleteUser();
    }
}

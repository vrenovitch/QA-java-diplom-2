package tests.userTests;

import data.userData.CreateUser;
import data.userData.LoginUser;
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

public class LoginUserTest {
    private static String email;
    private static String password;
    private static String name;
    private static String fakeEmail;
    private static String fakePassword;
    private static UserApi userApi;

    @BeforeClass
    public static void setUp() {
        email = generateRandomMail();
        password = generateRandomPass();
        name = generateRandomName();
        fakeEmail = generateRandomMail();
        fakePassword = generateRandomPass();
        userApi = new UserApi();

        CreateUser user = new CreateUser(email, password, name);
        userApi.createUser(user);
    }

    @Test
    @DisplayName("Авторизация пользователя с корректными данными")
    @Description("Проверяем, что пользователь авторизуется с корректными данными и проверяем ответ")
    public void loginUserTest() {
        LoginUser loginUser = new LoginUser(email, password);

        Response response = userApi.loginUser(loginUser);
        response.then().statusCode(SC_OK)
                .and()
                .assertThat().body("success", equalTo(true),
                        "user.email", equalTo(email),
                        "user.name", equalTo(name));
    }

    @Test
    @DisplayName("Авторизация пользователя с некорректными данными")
    @Description("Проверяем, что пользователь не авторизуется с некорректными данными")
    public void loginUserWithFakeDataTest() {
        LoginUser loginUser = new LoginUser(fakeEmail, fakePassword);

        Response response = userApi.loginUser(loginUser);
        response.then().statusCode(SC_UNAUTHORIZED)
                .and()
                .assertThat().body("success", equalTo(false),
                        "message", equalTo("email or password are incorrect"));
    }

    @AfterClass
    public static void tearDown() {
        userApi.deleteUser();
    }
}

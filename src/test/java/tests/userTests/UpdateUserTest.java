package tests.userTests;

import data.userData.CreateUser;
import data.userData.UserApi;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import static data.userData.DataForUsers.*;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class UpdateUserTest {
    private static String email;
    private static String password;
    private static String name;
    private static String newEmail;
    private static String newPassword;
    private static String newName;
    private static UserApi userApi;

    @BeforeClass
    public static void setUp() {
        email = generateRandomMail();
        password = generateRandomPass();
        name = generateRandomName();
        newEmail = generateRandomMail();
        newPassword = generateRandomPass();
        newName = generateRandomName();
        userApi = new UserApi();

        CreateUser user = new CreateUser(email, password, name);
        userApi.createUser(user);
    }

    @Test
    @DisplayName("Обновление пользователя с авторизацией")
    public void updateUserTest() {
        CreateUser updateUser = new CreateUser(newEmail, newPassword, newName);

        Response response = userApi.updateUser(updateUser);
        response.then().statusCode(SC_OK)
                .and()
                .assertThat().body("success", equalTo(true),
                        "user.email", equalTo(newEmail),
                        "user.name", equalTo(newName));
    }

    @Test
    @DisplayName("Обновление пользователя без авторизацией")
    public void updateUserWithoutAuthTest() {
        CreateUser updateUser = new CreateUser(email, password, name);

        Response response = userApi.updateUserWithoutToken(updateUser);
        response.then().statusCode(SC_UNAUTHORIZED)
                .and()
                .assertThat().body("success", equalTo(false),
                        "message", equalTo("You should be authorised"));
    }

    @AfterClass
    public static void tearDown() {
        userApi.deleteUser();
    }
}

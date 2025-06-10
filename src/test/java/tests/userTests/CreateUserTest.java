package tests.userTests;

import data.userData.CreateUser;
import data.userData.UserApi;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static data.userData.DataForUsers.*;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;

public class CreateUserTest {
    private String email;
    private String password;
    private String name;
    private UserApi userApi;
    private boolean shouldDeleteUser = true;

    @Before
    public void setUp() {
        email = generateRandomMail();
        password = generateRandomPass();
        name = generateRandomName();
        userApi = new UserApi();
    }

    @Test
    @DisplayName("Создание пользователя")
    @Description("Создаем пользователя с рандомными данными и проверяем, что он успешно создан")
    public void createUserTest() {
        CreateUser createUser = new CreateUser(email, password, name);

        Response response = userApi.createUser(createUser);
        response.then().statusCode(SC_OK)
                .and()
                .assertThat().body("success", equalTo(true),
                        "user.email", equalTo(email),
                        "user.name", equalTo(name));
    }

    @Test
    @DisplayName("Создание пользователя с существующим email")
    @Description("Создаем пользователя с существующим email и проверка на ошибку")
    public void createUserWithExistEmailTest() {
        CreateUser createUser = new CreateUser(email, password, name);

        userApi.createUser(createUser).then().statusCode(SC_OK);

        Response response = userApi.createUser(createUser);
        response.then().statusCode(SC_FORBIDDEN)
                .and()
                .assertThat().body("success", equalTo(false),
                        "message", equalTo("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя с пустым email")
    @Description("Создаем пользователя с пустым email и проверка на ошибку")
    public void createUserWithEmptyEmailTest() {
        shouldDeleteUser = false;
        CreateUser createUser = new CreateUser("", password, name);

        Response response = userApi.createUser(createUser);
        response.then().statusCode(SC_FORBIDDEN)
                .and()
                .assertThat().body("success", equalTo(false),
                        "message", equalTo("Email, password and name are required fields"));
    }

    @After
    public void tearDown() {
        if (shouldDeleteUser) {
            userApi.deleteUser();
        }
    }
}

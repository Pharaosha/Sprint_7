import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class LoginToSystemTest {
    File json = new File("src/test/resources/Courier.json");
    File wrongJson = new File("src/test/resources/WrongCourierData.json");
    File CourierDataWithoutPassword = new File ("src/test/resources/CourierDataWithoutPassword.json");

    public static int getCourierId() {
        File json = new File("src/test/resources/Courier.json");

        Response response = given()
                .header("Content-type", "application/json")
                .body(json) // передаем JSON-файл напрямую
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(200)
                .extract()
                .response();

        return response.jsonPath().getInt("id");
    }

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }

    @AfterAll
    public static void deleteCourier() {
        int courierId = getCourierId(); // получаем ID курьера через login
        given()
                .header("Content-type", "application/json")
                .when()
                .delete("/api/v1/courier/" + courierId)
                .then()
                .statusCode(200); // если удаления нет, тест упадёт
    }



    @Test
    @DisplayName("Логин в систему и получение кода ответа от сервера")
    public void LoginAndCheckResponse(){
        createCourier();
        Response loginResponse = loginToSystem();
        checkStatusCode(loginResponse);
        checkId(loginResponse);
    }

    @Step("Отправить POST-запрос на создание курьера (endpoint: /api/v1/courier)")
    public Response createCourier() {
                return given()
                .header("Content-type", "application/json")
                .and()
                .body(json)
                .when()
                .post("/api/v1/courier");
    }

    @Step("Логин в систему")
    public Response loginToSystem(){
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(json)
                .when()
                .post("/api/v1/courier/login");
    }

    @Step("Проверка статус кода")
    public void checkStatusCode(Response response){
        response.then()
                .statusCode(200);
    }

    @Step("Успешный запрос возвращает id")
    public void checkId(Response response){
        response.then().body("id", notNullValue());
    }


    @Test
    @DisplayName("Логин в систему c неверным паролем/" +
            "если авторизоваться под несуществующим пользователем, запрос возвращает ошибку;")
    public void LoginWithWrongPassword(){
        Response wrongLoginResponse = loginToSystemWithWrongData();
        checkErrorLogin(wrongLoginResponse);
    }

            @Step("Cистема вернёт ошибку, если неправильно указать логин или пароль;")
            public Response loginToSystemWithWrongData () {
            return given()
                    .header("Content-type", "application/json")
                    .and()
                    .body(wrongJson)
                    .when()
                    .post("/api/v1/courier/login")
                    .then()
                    .extract()
                    .response();

        }

            @Step("Система возвращает ошибку при неверных данных — код 404 и сообщение")
            public void checkErrorLogin (Response response){
            response.then()
                    .statusCode(404)
                    .body("message", equalTo("Учетная запись не найдена"));
        }

    @Test
    @DisplayName("Логин в систему без поля пароля")
    public void LogintWithoutPassword(){
        Response LoginRequestWithoutPasswordResponse = sendLoginRequestWithoutPassword();
        checkMissingFieldError(LoginRequestWithoutPasswordResponse);


    }

    @Step("Отправка запроса логина без пароля")
    public Response sendLoginRequestWithoutPassword() {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(CourierDataWithoutPassword)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .extract()
                .response();
    }

    @Step("Проверка ошибки при отсутствии обязательного поля — код 400 и сообщение")
    public void checkMissingFieldError(Response response) {
        response.then()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для входа"));
    }
}

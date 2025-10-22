import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.*;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


public class CreateCourierTest {

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
    @DisplayName("Создание нового курьера и получение кода ответа от сервера")
    public void CreateNewCourierAndCheckResponse() {
        Response response = createCourier();
        checkCourierCreatedSuccessfully(response);
    }


    @Step("Отправить POST-запрос на создание курьера (endpoint: /api/v1/courier)")
    public Response createCourier() {
        File json = new File("src/test/resources/Courier.json");
        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(json)
                .when()
                .post("/api/v1/courier");
        return response;
    }

    @Step("Проверить, что курьер успешно создан (status code = 201, ok = true)")
    public void checkCourierCreatedSuccessfully(Response response) {
        response.then()
                .assertThat()
                .statusCode(201)
                .body("ok", equalTo(true));
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых курьеров")
    public void CreateSameCourierAndCheckResponse() {
        Response response = createCourier();
        checkTheSameCourierAndCheckResponse(response);
    }

    @Step("Проверить, что нельзя создать двух одинаковых курьеров")
    public void checkTheSameCourierAndCheckResponse(Response response) {
        response.then()
                .assertThat()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @DisplayName("Создание курьера без обязательного поля возвращает ошибку")
    public void createCourierWithoutRequiredFieldAndCheckResponse() {
        Response response = createCourierWithoutRequiredField();
        checkCourierCourierWithoutRequiredFieldAndCheckResponse(response);

    }

    @Step("Создание курьера без обязательного поля")
    public Response createCourierWithoutRequiredField() {
        File jsonMissingField = new File("src/test/resources/CourierMissingField.json");
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(jsonMissingField)
                .when()
                .post("/api/v1/courier");
    }

    @Step("Проверить, что нельзя нельзя создать курьера без обязательных полей")
    public void checkCourierCourierWithoutRequiredFieldAndCheckResponse(Response response) {
        response.then()
                .assertThat()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    }








import io.qameta.allure.Step;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class GetOrderListTest {

    @BeforeEach
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }

    @Test
    @DisplayName("Получение списка заказов, кода ответа от сервера и тела ответа")
    public void GetOrderListAndCheckResponse() {
        Response getOrderListResponse = getOrderList();
        checkStatusCode(getOrderListResponse);
        checkBody(getOrderListResponse);
    }

    @Step("Отправить GET-запрос на сервер (endpoint: /api/v1/orders)")
    public Response getOrderList() {
        return given()
                .get("/api/v1/orders");}

        @Step("Проверка статус кода")
        public void checkStatusCode(Response response){
            response.then()
                    .statusCode(200);
    }

    @Step("Проверка тела ответа")
    public void checkBody(Response response){
        response.then()
                .body("orders", notNullValue())
                .and()
                .body("orders", instanceOf(java.util.List.class))
                .and()
                .body("orders.size()", greaterThan(0));
    }

}
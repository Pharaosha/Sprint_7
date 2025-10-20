import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;


@RunWith(Parameterized.class)
public class MakeOrderAndCheckResponseTest {

    private final List<String> color;

    public MakeOrderAndCheckResponseTest(List<String> color) {
        this.color = color;
    }

    @Parameterized.Parameters(name = "Тест с цветами: {0}")
    public static Object[][] getColorData() {
        return new Object[][]{
                {Collections.singletonList("BLACK")},
                {Collections.singletonList("GREY")},
                {Arrays.asList("BLACK", "GREY")},
                {Collections.emptyList()}
        };
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }

    @Test
    public void makeOrderAndCheckResponse() {
        OrderData orderData = new OrderData(
                "Naruto",
                "Uchiha",
                "Konoha, 142 apt.",
                "4",
                "+7 800 355 35 35",
                5,
                "2020-06-06",
                "Saske, come back to Konoha",
                color
        );

        Response response = given()
                .header("Content-type", "application/json")
                .and()
                .body(orderData)
                .when()
                .post("/api/v1/orders");

        response.then().assertThat()
                .statusCode(201)
                .and()
                .body("track", notNullValue());
    }
}
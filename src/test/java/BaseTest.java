import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;

public class BaseTest {
    @BeforeAll
    public static void setupRestAssured() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }
}

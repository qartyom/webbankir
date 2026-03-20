package api.client;

import api.models.CreateUserRequest;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class UserApiClient {
    private final String baseUrl;

    public UserApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Response createUser(CreateUserRequest payload) {
        return given()
                .baseUri(baseUrl)
                .contentType(ContentType.JSON)
                .body(payload)
                .log().all()
                .when()
                .log().all()
                .post("/api/v1/users");
    }

    public Response getUserById(String userId) {
        return given()
                .baseUri(baseUrl)
                .pathParam("id", userId)
                .log().all()
                .when()
                .log().all()
                .get("/api/v1/users/{id}");
    }

    public Response deleteUser(int userId) {
        return given()
                .baseUri(baseUrl)
                .pathParam("id", userId)
                .log().all()
                .when()
                .log().all()
                .delete("/api/v1/users/{id}");
    }
}

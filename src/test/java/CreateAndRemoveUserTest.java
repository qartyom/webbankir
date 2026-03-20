import api.client.UserApiClient;
import api.generators.RandomData;
import api.generators.RandomModel;
import api.models.CreateUserRequest;
import com.codeborne.selenide.Selenide;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ui.pages.AdminPage;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateAndRemoveUserTest extends BaseUiTest {
    private static final UserApiClient apiClient = new UserApiClient("http://localhost:8080");
    private static CreateUserRequest tempUser;
    private static int tempUserId;
    private final AdminPage adminPage = new AdminPage();
    private Integer userId;

    @BeforeAll
    public static void addTempUser() {
        tempUser = RandomModel.genCreateUserRequest();
        Response response = apiClient.createUser(tempUser);
        assertThat(response.statusCode()).isEqualTo(201);
        tempUserId = response.jsonPath().getInt("id");
    }

    @AfterEach
    public void deleteUser() {
        if (userId != null) {
            Response response = apiClient.deleteUser(userId);
            assertThat(response.statusCode()).isEqualTo(200);
        }
    }

    @AfterAll
    public static void deleteTempUser() {
        Response response = apiClient.deleteUser(tempUserId);
        assertThat(response.statusCode()).isEqualTo(200);
    }

    // Стандартный тест (задание 2)
    @Test
    public void createUserAndCheckOnUiTest() {
        CreateUserRequest request = RandomModel.genCreateUserRequest();
        Response createUserResponse = apiClient.createUser(request);

        assertThat(createUserResponse.statusCode()).isEqualTo(201);
        userId = createUserResponse.jsonPath().getInt("id");

        adminPage.open();
        assertThat(adminPage.isUserPresent(request.getName())).isTrue();

        Response deleteUserResponse = apiClient.deleteUser(userId);
        assertThat(deleteUserResponse.statusCode()).isEqualTo(200);
        userId = null;

        Selenide.refresh();
        assertThat(adminPage.isUserPresent(request.getName())).isFalse();
    }

    public static Stream<Arguments> createUserRequestProvider() {
        return Stream.of(
                // Валидный пользователь
                Arguments.of(new CreateUserRequest(RandomData.genValidName(), RandomData.genValidEmail()), 201, true),
                // Пользователь с невалидным имейлом
                Arguments.of(new CreateUserRequest(RandomData.genValidName(), RandomData.genInvalidEmail()), 400, false),
                // Пользователь с дубликатом name
                Arguments.of(new CreateUserRequest(tempUser.getName(), RandomData.genValidEmail()), 400, false)
        );
    }

    // Параметризированный тест (задание 4B)
    @ParameterizedTest
    @MethodSource("createUserRequestProvider")
    public void parametrizedCreateUserAndCheckOnUiTest(CreateUserRequest request, int expectedStatusCode, boolean isPositiveTest) {
        Response createUserResponse = apiClient.createUser(request);
        assertThat(createUserResponse.statusCode()).isEqualTo(expectedStatusCode);
        if (createUserResponse.statusCode() == 201) {
            userId = createUserResponse.jsonPath().getInt("id");
        }

            adminPage.open();
            assertThat(adminPage.isUserPresent(request.getName())).isEqualTo(isPositiveTest);

        if (isPositiveTest) {
            Response deleteUserResponse = apiClient.deleteUser(userId);
            assertThat(deleteUserResponse.statusCode()).isEqualTo(200);
            userId = null;

            Selenide.refresh();
            assertThat(adminPage.isUserPresent(request.getName())).isFalse();
        }
    }
}

/*
p.s. На реальном проекте не стал бы совмещать такие сценарии (позитивные и негативные, ui + api) в один параметризированный
тест,но необходимо было выполнить условие задания. В данном примере из-за этого образовалось несколько костылей.
Спецификации запроса и ответа помогли бы здесь избежать дублирования кода, но чтобы реализовать это, пришлось бы api-клиент
переписывать полностью.
 */
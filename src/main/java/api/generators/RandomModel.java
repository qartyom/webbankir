package api.generators;

import api.models.CreateUserRequest;

public class RandomModel {

    // Имитируем генерацию случайного юзера
    public static CreateUserRequest genCreateUserRequest() {
        long currentTime = System.currentTimeMillis();

        return CreateUserRequest.builder()
                .name("Test_User_" + currentTime)
                .email("user_" + currentTime + "@mail.ru")
                .build();
    }
}

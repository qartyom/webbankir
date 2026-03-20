package api.generators;

// Имитация генерацию случайных данных
public class RandomData {
    public static String genValidEmail() {
        return "example@mail.ru";
    }

    public static String genInvalidEmail() {
        return "example@mail";
    }

    public static String genValidName() {
        return "Alex";
    }
}

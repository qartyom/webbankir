**Файл: `UserApiClient.java`**

```java
/*
        
 */
public class UserApiClient {

    private static UserApiClient instance;
    private final String baseUrl;
    // Поле timeout не используется в классе, вводит в заблуждение
    private int timeout = 5000;

    private UserApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    /* 
    Класс реализован как синглтон, но для http-клиента я бы 
    не использовал такой подход, т.к. если возникнет необходимость
    использовать другой url, то это не получится сделать.
    Еще момент: если в коде тестов будет использоваться вызов 
    getInstance(firstURL), а затем далее getInstance(secondURL),
    то это приведет к неочевидной проблеме, что при получении 
    инстанса с secondURL присвоение будет проигнорировано и на
    самом деле будет использоваться firstURL, что потом еще
    придется дебажить :)
    Как быстрый вариант - сделать обычный конструктор, если не
    хотим полностью переписывать клиент (я бы переписал).
     */
    public static UserApiClient getInstance(String baseUrl) {
        if (instance == null) {
            instance = new UserApiClient(baseUrl);
        }
        return instance;
    }

    /*
    Метод принимает Map в качестве тела запроса.
    Намного лучше использовать DTO в качестве аргумента.
    Это позволит избежать ошибок с некорректным названием ключей
    в json. DTO позволит проще формировать тело сообщения и 
    повысит читаемость кода.
     */
    public Response createUser(Map<String, Object> payload) {
        return given()
            .baseUri(baseUrl)
            .contentType(ContentType.JSON)
            .body(payload)
        /*
        Можно было бы добавить логирование запроса,
        например .log().all()
        но я бы реализовал собственный логгер.
         */
            .when()
         /*
         Хардкод эндпоинта. Дублирование в других методах.
         Для замены версии api нужно будет править каждый метод.
         Лучше вынести в отдельный enum с константами.
         */
            .post("/api/v1/users");
    }

    public Response getUserById(String userId) {
        return given()
            .baseUri(baseUrl)
            .pathParam("id", userId)
            .when()
            .get("/api/v1/users/{id}");
    }

    // Метод ничего не возвращает и не выполняет проверок
    public void deleteUser(String userId) {
        given()
            .baseUri(baseUrl)
            .when()
            .delete("/api/v1/users/" + userId);
    }
}
/*
Помимо всего - не используются спецификации запроса/ответа,
дублируется .baseUri(baseUrl) в каждом методе.

На обсуждение с командой вынес бы в первую очередь вопрос по
рефакторингу api-клиента. Вижу, что применение синглтона не дает
возможность создавать несколько разных клиентов, а также может
привести к ошибкам при написании тестов.
Также хотелось бы вынести на обсуждение необходимость использоавния
DTO для запросов и ответов.
И также предложил бы вынести хосты и пути в отдельный конфиг.
 */
```

**Файл: `BaseTest.java`**

```java
@ExtendWith(SeleniumExtension.class)
/*
Класс называется BaseTest, логично что от него будут наследоваться не только классы
с UI-тестами, но и с API-тестами. Получается, что при запуске каждого API-теста
будет инициализироваться WebDriver, что не нужно для API-тестов и приведет к их
замедлению и возможно к флаки-тестам.
Лучше было бы от BaseTest наследовать классы BaseApiTest и BaseUiTest и всю эту
логку оставить в BaseUiTest.
 */
public class BaseTest {

    protected WebDriver driver;
    protected UserApiClient apiClient;

    @BeforeEach
    void setUp() {
        /*
        Хардкод пути к chromedriver. В CI и на разных окружениях работать не будет.
        Лучше вынести в отдельный конфиг.
         */
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
        driver = new ChromeDriver();
        /*
        Используется неявное ожидание в 10 секунд. Задано глобально для всех элементов.
        Лучше использовать явные ожидания и задавать разные таймауты для элементов.
         */
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
        /*
        Хардкод хоста. Нельзя поменять хост, не переписывая код.
        Лучше хост вынести в конфиг.
         */
        apiClient = UserApiClient.getInstance("http://localhost:8080");
    }

    @AfterEach
    void tearDown() {
        driver.quit();
    }
}

```
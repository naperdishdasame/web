package ru.netology.webselenium;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;


import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CardOrderingTest {
    private WebDriver driver;

    @BeforeAll
    static void setUpAll() {
        WebDriverManager.chromedriver().setup();
    }

    @BeforeEach
    void setUp() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--headless");
        driver = new ChromeDriver(options);
        driver.get("http://localhost:9999");
    }

    @AfterEach
    void tearDown() {
        driver.quit();
        driver = null;
    }

    @ParameterizedTest

    void shouldCardForm(String name, String phone, String expected) {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys(name);
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys(phone);
        driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        driver.findElement(By.className("button")).click();

        String actual = driver.findElement(By.cssSelector("[data-test-id=order-success]")).getText().trim();

        assertEquals(expected, actual);
    }

    @Test
    void shouldCardFormInvalidName() {
        List<WebElement> elements = driver.findElements(By.className("input__control"));
        elements.get(0).sendKeys("Sam Fisher"); // Имя набрано латиницей
        elements.get(1).sendKeys("+79933843802");
        driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        driver.findElement(By.className("button")).click();

        String expected = "Имя и Фамилия указаные неверно. Допустимы только русские буквы, пробелы и дефисы.";
        String actual = driver.findElement(By.cssSelector("[data-test-id=name].input_invalid .input__sub")).getText().trim();

        assertEquals(expected, actual);
    }

    @Test
    void shouldCardFormInvalidPhone() {
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Сэм Фишер");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+79933843802"); // В номере телефона вместо 0 написано O
        driver.findElement(By.className("button")).click();

        String expected = "Телефон указан неверно. Должно быть 11 цифр, например, +79933843802.";
        String actual = driver.findElement(By.cssSelector("[data-test-id=phone].input_invalid .input__sub")).getText().trim();

        assertEquals(expected, actual);
    }

    @Test
    void shouldCardFormWithoutName() { // В заявке отсутствует имя
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+79933843802");
        driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        driver.findElement(By.className("button")).click();

        String expected = "Поле обязательно для заполнения";
        String actual = driver.findElement(By.cssSelector("[data-test-id=name].input_invalid .input__sub")).getText().trim();

        assertEquals(expected, actual);
    }

    @Test
    void shouldCardFormWithoutPhone() { // В заявке отсутствует номер телефона
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Сэм Фишер");
        driver.findElement(By.cssSelector("[data-test-id=agreement]")).click();
        driver.findElement(By.className("button")).click();

        String expected = "Поле обязательно для заполнения";
        String actual = driver.findElement(By.cssSelector("[data-test-id=phone].input_invalid .input__sub")).getText().trim();

        assertEquals(expected, actual);
    }

    @Test
    void shouldCardFormWithoutCheckbox() { // В заявке не проставлен чек-бокс
        driver.findElement(By.cssSelector("[data-test-id=name] input")).sendKeys("Сэм Фишер");
        driver.findElement(By.cssSelector("[data-test-id=phone] input")).sendKeys("+79933843802");
        driver.findElement(By.className("button")).click();

        String expected = "Я соглашаюсь с условиями обработки и использования моих персональных данных и разрешаю сделать запрос в бюро кредитных историй";
        String actual = driver.findElement(By.cssSelector("[data-test-id=agreement].input_invalid .checkbox__text")).getText().trim();

        assertEquals(expected, actual);
    }
}
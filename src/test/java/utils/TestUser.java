package utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Тестовые данные нового пользователя.
 * <p>
 * ShareLane удаляет все пользовательские данные каждый час в 00 минут, поэтому каждый тест,
 * которому нужен авторизованный пользователь, регистрирует свежий аккаунт с уникальным email.
 * <p>
 * Важно: после регистрации сайт заменяет имя, фамилию и email на сгенерированные значения,
 * поэтому для входа используется не тот email, который вводился в форме, а тот, который сайт
 * показал на странице подтверждения (см. {@link #loginEmail()}).
 */
public class TestUser {

    private static final DateTimeFormatter STAMP = DateTimeFormatter.ofPattern("MMddHHmmss");

    private final String firstName;
    private final String lastName;
    private final String registrationEmail;
    private final String zipCode;

    private String loginEmail;

    public TestUser(String firstName, String lastName, String registrationEmail, String zipCode) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.registrationEmail = registrationEmail;
        this.zipCode = zipCode;
    }

    /** Новый пользователь с уникальным email. */
    public static TestUser random() {
        String email = "qa_" + LocalDateTime.now().format(STAMP)
                + ThreadLocalRandom.current().nextInt(100, 999) + "@mail.com";
        return new TestUser("Ivan", "Petrov", email, "12345");
    }

    public String firstName() {
        return firstName;
    }

    public String lastName() {
        return lastName;
    }

    public String registrationEmail() {
        return registrationEmail;
    }

    public String zipCode() {
        return zipCode;
    }

    /** Email, который сайт выдал на странице подтверждения регистрации. */
    public String loginEmail() {
        return loginEmail;
    }

    public void setLoginEmail(String loginEmail) {
        this.loginEmail = loginEmail;
    }

    @Override
    public String toString() {
        return "TestUser{registrationEmail='" + registrationEmail + "', loginEmail='" + loginEmail + "'}";
    }
}

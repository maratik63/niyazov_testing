package utils;

import java.time.Duration;

/**
 * Централизованные константы для автотестов сайта https://www.saucedemo.com (Swag Labs):
 * базовый URL, таймаут явных ожиданий и демонстрационные учётные данные.
 */
public final class SauceDemoConfig {

    public static final String BASE_URL = "https://www.saucedemo.com";

    /** Единый таймаут для явных ожиданий (WebDriverWait) во всех Page Object. */
    public static final Duration EXPLICIT_TIMEOUT = Duration.ofSeconds(10);

    // Демонстрационные учётные записи Swag Labs (пароль единый для всех)
    public static final String STANDARD_USER = "standard_user";
    public static final String LOCKED_OUT_USER = "locked_out_user";
    public static final String PROBLEM_USER = "problem_user";
    public static final String PERFORMANCE_GLITCH_USER = "performance_glitch_user";
    public static final String PASSWORD = "secret_sauce";

    private SauceDemoConfig() {
    }
}

package tests;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

/**
 * Инициализация и утилизация драйвера для всех тестов.
 * <p>
 * Жизненный цикл: новый браузер на каждый тестовый метод ({@code @BeforeMethod}) и гарантированное
 * закрытие в {@code @AfterMethod(alwaysRun = true)} — тесты не зависят друг от друга, а драйвер
 * освобождается даже после падения теста.
 * <p>
 * Браузер задаётся параметром TestNG (testng.xml) или системным свойством Maven, которое имеет
 * приоритет: {@code mvn test -Dbrowser=firefox -Dheadless=true}.
 * Поддерживаются chrome (по умолчанию), firefox, edge.
 * <p>
 * Соединение: по умолчанию браузер идёт в сеть напрямую (локальный прокси разработчика может быть
 * выключен и тогда тесты падают с proxyConnectFailure). Если прокси нужен, запускайте с
 * {@code -Dproxy=system} — браузер возьмёт настройки прокси из системы.
 */
public abstract class BaseTest {

    /** Ограничение на загрузку страницы: страхует от «зависшего» ответа сайта. */
    private static final Duration PAGE_LOAD_TIMEOUT = Duration.ofSeconds(60);

    protected WebDriver driver;

    @Parameters({"browser", "headless"})
    @BeforeMethod(alwaysRun = true)
    public void setUp(@Optional("chrome") String browserParam,
                      @Optional("false") String headlessParam) {
        String browser = System.getProperty("browser", browserParam).toLowerCase();
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", headlessParam));

        driver = createDriver(browser, headless);
        driver.manage().timeouts().pageLoadTimeout(PAGE_LOAD_TIMEOUT);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        if (driver != null && !result.isSuccess()) {
            saveScreenshot(result);
        }
        if (driver != null) {
            driver.quit();
            driver = null;
        }
    }

    /** Запуск браузера с одной повторной попыткой: единичный сбой драйвера не должен ронять тест. */
    private WebDriver createDriver(String browser, boolean headless) {
        try {
            return startBrowser(browser, headless);
        } catch (WebDriverException firstAttempt) {
            System.out.println("Не удалось запустить браузер с первой попытки: " + firstAttempt.getMessage());
            return startBrowser(browser, headless);
        }
    }

    private WebDriver startBrowser(String browser, boolean headless) {
        return switch (browser) {
            case "firefox" -> {
                FirefoxOptions options = new FirefoxOptions();
                options.setPageLoadStrategy(PageLoadStrategy.EAGER);
                if (headless) {
                    options.addArguments("-headless");
                }
                if (isDirectConnection()) {
                    // network.proxy.type = 0 — подключаться напрямую, не используя системный прокси
                    options.addPreference("network.proxy.type", 0);
                }
                // Каждый тест получает свежий профиль, поэтому отключаем фоновые задачи Firefox
                // (телеметрия, обновления, Safebrowsing): они создают лишний трафик и замедляют прогон.
                options.addPreference("toolkit.telemetry.enabled", false);
                options.addPreference("toolkit.telemetry.unified", false);
                options.addPreference("datareporting.policy.dataSubmissionEnabled", false);
                options.addPreference("datareporting.healthreport.uploadEnabled", false);
                options.addPreference("app.update.enabled", false);
                options.addPreference("app.update.auto", false);
                options.addPreference("browser.safebrowsing.malware.enabled", false);
                options.addPreference("browser.safebrowsing.phishing.enabled", false);
                options.addPreference("extensions.pocket.enabled", false);
                options.addPreference("network.prefetch-next", false);
                options.addPreference("network.dns.disablePrefetch", true);
                options.addPreference("network.captive-portal-service.enabled", false);
                WebDriverManager.firefoxdriver().setup();
                yield new FirefoxDriver(options);
            }
            case "edge" -> {
                EdgeOptions options = new EdgeOptions();
                options.setPageLoadStrategy(PageLoadStrategy.EAGER);
                if (headless) {
                    options.addArguments("--headless=new");
                }
                if (isDirectConnection()) {
                    options.addArguments("--no-proxy-server");
                }
                options.setExperimentalOption("prefs", passwordManagerPrefs());
                options.addArguments("--disable-features=PasswordLeakDetection");
                WebDriverManager.edgedriver().setup();
                yield new EdgeDriver(options);
            }
            default -> {
                ChromeOptions options = new ChromeOptions();
                options.setPageLoadStrategy(PageLoadStrategy.EAGER);
                if (headless) {
                    options.addArguments("--headless=new");
                }
                if (isDirectConnection()) {
                    options.addArguments("--no-proxy-server");
                }
                // Облегчаем запуск: без первого запуска, фоновых сервисов и лишних проверок —
                // браузер стартует быстрее и реже падает при частых перезапусках на каждый тест.
                options.addArguments("--no-first-run", "--no-default-browser-check", "--disable-extensions",
                        "--disable-popup-blocking", "--disable-notifications", "--disable-background-networking",
                        "--disable-sync", "--disable-translate", "--disable-dev-shm-usage");
                // Chrome показывает модальное окно «пароль найден в утечке» (наши пароли — демо),
                // пока оно открыто, клики по странице не проходят: отключаем менеджер паролей.
                options.setExperimentalOption("prefs", passwordManagerPrefs());
                options.addArguments("--disable-features=PasswordLeakDetection");
                WebDriverManager.chromedriver().setup();
                yield new ChromeDriver(options);
            }
        };
    }

    private Map<String, Object> passwordManagerPrefs() {
        Map<String, Object> prefs = new HashMap<>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);
        return prefs;
    }

    /** Тесты по умолчанию идут напрямую; при -Dproxy=system используется системный прокси. */
    private boolean isDirectConnection() {
        return !"system".equalsIgnoreCase(System.getProperty("proxy", "direct"));
    }

    private void saveScreenshot(ITestResult result) {
        String testName = result.getTestClass().getRealClass().getSimpleName() + "_" + result.getName();
        try {
            Path dir = Paths.get("target", "screenshots");
            Files.createDirectories(dir);
            byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            Path file = dir.resolve(testName + ".png");
            Files.write(file, screenshot);
            System.out.println("Скриншот падения: " + file.toAbsolutePath());
        } catch (IOException | RuntimeException e) {
            System.out.println("Не удалось сохранить скриншот теста " + testName + ": " + e.getMessage());
        }
    }
}

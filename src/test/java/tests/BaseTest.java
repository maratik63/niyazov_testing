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
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;
import utils.ShareLaneConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Инициализация и закрытие браузера для тестов ShareLane.
 * <p>
 * Браузер поднимается один раз на класс, а перед каждым тестом очищаются cookie, поэтому тесты
 * не зависят друг от друга. Браузер задаётся параметром TestNG (testng.xml) или системным
 * свойством Maven, которое имеет приоритет: {@code mvn test -Dbrowser=chrome -Dheadless=true}.
 * По умолчанию — Firefox; поддерживаются firefox, chrome, edge.
 */
public abstract class BaseTest {

    protected WebDriver driver;

    @Parameters({"browser", "headless"})
    @BeforeClass(alwaysRun = true)
    public void setUp(@Optional("firefox") String browserParam,
                      @Optional("false") String headlessParam) {
        String browser = System.getProperty("browser", browserParam).toLowerCase();
        boolean headless = Boolean.parseBoolean(System.getProperty("headless", headlessParam));

        driver = createDriver(browser, headless);
        driver.manage().timeouts().pageLoadTimeout(ShareLaneConfig.PAGE_LOAD_TIMEOUT);
    }

    /** Каждый тест начинается без cookie: предыдущий пользователь не должен влиять на результат. */
    @BeforeMethod(alwaysRun = true)
    public void resetSession() {
        driver.manage().deleteAllCookies();
    }

    @AfterMethod(alwaysRun = true)
    public void saveScreenshotOnFailure(ITestResult result) {
        if (!result.isSuccess()) {
            saveScreenshot(result);
        }
    }

    @AfterClass(alwaysRun = true)
    public void tearDown() {
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
            case "chrome" -> {
                ChromeOptions options = new ChromeOptions();
                options.setPageLoadStrategy(PageLoadStrategy.EAGER);
                if (headless) {
                    options.addArguments("--headless=new");
                }
                WebDriverManager.chromedriver().setup();
                yield new ChromeDriver(options);
            }
            case "edge" -> {
                EdgeOptions options = new EdgeOptions();
                options.setPageLoadStrategy(PageLoadStrategy.EAGER);
                if (headless) {
                    options.addArguments("--headless=new");
                }
                WebDriverManager.edgedriver().setup();
                yield new EdgeDriver(options);
            }
            default -> {
                FirefoxOptions options = new FirefoxOptions();
                options.setPageLoadStrategy(PageLoadStrategy.EAGER);
                if (headless) {
                    options.addArguments("-headless");
                }
                WebDriverManager.firefoxdriver().setup();
                yield new FirefoxDriver(options);
            }
        };
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

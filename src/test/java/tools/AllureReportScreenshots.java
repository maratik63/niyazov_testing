package tools;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.Test;
import tests.BaseTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

/**
 * Служебный сценарий для отчёта по практике №14–16: снимает скриншоты страниц Allure-отчёта
 * (Dashboard, Behaviors, Suites, Environment, карточка упавшего теста).
 * <p>
 * Требует запущенного локального сервера с уже сгенерированным отчётом, например:
 * {@code python -m http.server 8765 --directory target/site/allure-maven-plugin}
 * <p>
 * Запуск: {@code mvn test -Dtest=AllureReportScreenshots -Dsurefire.suiteXmlFiles=}
 */
public class AllureReportScreenshots extends BaseTest {

    private static final String BASE_URL = System.getProperty("allureReport", "http://127.0.0.1:8765");
    private static final String FAILED_TEST_NAME = "Демонстрационное падение";
    private static final Path DIR = Paths.get("screenshots", "allure");

    private WebDriverWait wait;

    @Test(description = "Снимки страниц Allure-отчёта для материалов отчёта")
    public void captureAllureReport() throws IOException {
        Files.createDirectories(DIR);
        wait = new WebDriverWait(driver, Duration.ofSeconds(30));

        // Dashboard (Overview)
        driver.get(BASE_URL + "/index.html");
        waitForReport();
        takeScreenshot("01-allure-dashboard");

        // Блок Environment — внизу страницы Overview
        showEnvironment();

        // Behaviors (группировка по Epic/Feature/Story)
        driver.get(BASE_URL + "/index.html#/behaviors");
        waitForReport();
        takeScreenshot("02-allure-behaviors");

        // Suites (дерево сьютов и тестов)
        driver.get(BASE_URL + "/index.html#/suites");
        waitForReport();
        takeScreenshot("03-allure-suites");

        // Карточка упавшего теста со вложениями
        if (openFailedTestCard()) {
            takeScreenshot("05-allure-failed-test-card");
        }
    }

    private void waitForReport() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(
                By.cssSelector(".app__nav, .side-nav, .pane__title")));
    }

    /** Раскрывает и снимает блок Environment на странице Overview. */
    private void showEnvironment() {
        try {
            List<WebElement> headers = driver.findElements(By.xpath("//*[contains(text(), 'Environment')]"));
            if (headers.isEmpty()) {
                System.out.println("Блок Environment не найден на странице Overview");
                return;
            }
            WebElement header = headers.get(headers.size() - 1);
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", header);
            header.click();
            wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//*[contains(text(), 'java.version') or contains(text(), 'browser')]")));
            takeScreenshot("04-allure-environment");
        } catch (Exception e) {
            System.out.println("Не удалось снять блок Environment: " + e.getMessage());
        }
    }

    /**
     * Открывает карточку упавшего теста: фильтруем дерево Suites поиском, забираем deep-link
     * найденного теста и переходим по нему (карточка отчёта доступна по адресу
     * {@code #suites/<suiteUid>/<testUid>/}).
     */
    private boolean openFailedTestCard() {
        try {
            driver.get(BASE_URL + "/index.html#/suites");
            waitForReport();

            WebElement search = driver.findElement(By.cssSelector("input.search__input"));
            search.sendKeys(FAILED_TEST_NAME);
            wait.until(d -> !findFailedTestHref().isEmpty());

            String href = findFailedTestHref();
            driver.get(BASE_URL + "/index.html" + href);
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector(".test-result")));

            System.out.println("Карточка упавшего теста открыта: " + driver.getCurrentUrl());
            return true;
        } catch (Exception e) {
            System.out.println("Не удалось открыть карточку упавшего теста: " + e.getMessage());
            return false;
        }
    }

    /** Ссылка на лист дерева, в названии которого есть имя упавшего теста. */
    private String findFailedTestHref() {
        Object href = ((JavascriptExecutor) driver).executeScript(
                "var links = document.querySelectorAll('a.node__leaf');"
              + "for (var i = 0; i < links.length; i++) {"
              + "  if (links[i].innerText.indexOf('" + FAILED_TEST_NAME + "') >= 0) {"
              + "    return links[i].getAttribute('href');"
              + "  }"
              + "}"
              + "return '';");
        return href == null ? "" : href.toString();
    }

    private void takeScreenshot(String name) throws IOException {
        byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        Path file = DIR.resolve(name + ".png");
        Files.write(file, png);
        System.out.println("Скриншот: " + file.toAbsolutePath());
    }
}

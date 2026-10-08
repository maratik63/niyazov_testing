package tools;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.annotations.Test;
import tests.BaseTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Служебный сценарий для отчёта по практике: открывает в браузере HTML-отчёт Surefire
 * и сохраняет его скриншот в {@code screenshots/} (не входит в основной набор тестов).
 * <p>
 * Запуск: {@code mvn test -Dtest=SurefireReportScreenshot -Dsurefire.suiteXmlFiles= -Dreport=<путь к html>}
 */
public class SurefireReportScreenshot extends BaseTest {

    private static final String DEFAULT_REPORT = "target/surefire-reports/emailable-report.html";

    @Test(description = "Скриншот HTML-отчёта Surefire для материалов отчёта")
    public void captureSurefireReport() throws IOException {
        Path report = Paths.get(System.getProperty("report", DEFAULT_REPORT)).toAbsolutePath();
        Path output = Paths.get("screenshots", "08-surefire-report.png");

        Files.createDirectories(output.getParent());
        driver.get(report.toUri().toString());
        Files.write(output, ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES));
        System.out.println("Скриншот отчёта: " + output.toAbsolutePath());
    }
}

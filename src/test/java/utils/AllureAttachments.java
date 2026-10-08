package utils;

import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.ByteArrayInputStream;

/**
 * Диагностические вложения Allure: скриншот страницы, DOM-дамп и текстовые выдержки.
 * Прикладываются при падении теста (см. {@code BaseTest}), чтобы в отчёте сразу было видно состояние страницы.
 */
public final class AllureAttachments {

    private AllureAttachments() {
    }

    /** Скриншот текущего состояния страницы. */
    public static void screenshot(WebDriver driver, String name) {
        byte[] bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        Allure.addAttachment(name, new ByteArrayInputStream(bytes));
    }

    /** DOM-дамп страницы в момент падения. */
    public static void pageSource(WebDriver driver, String name) {
        Allure.addAttachment(name, "text/html", driver.getPageSource(), ".html");
    }

    /** Текстовое вложение (например, выдержка лога или результат проверки). */
    public static void text(String name, String content) {
        Allure.addAttachment(name, "text/plain", content, ".txt");
    }
}

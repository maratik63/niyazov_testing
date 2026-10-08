package tests;

import org.openqa.selenium.WebDriverException;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * Повтор теста при сбое инфраструктуры: браузер закрылся, сессия потеряна, драйвер не ответил.
 * <p>
 * Повторяются только сбои WebDriver. Падение проверки ({@link AssertionError}) не повторяется —
 * это настоящий дефект, и его нужно видеть, а не маскировать повтором.
 */
public class BrowserCrashRetryAnalyzer implements IRetryAnalyzer {

    private static final int MAX_RETRIES = 1;

    private int attempts;

    @Override
    public boolean retry(ITestResult result) {
        Throwable error = result.getThrowable();
        boolean infrastructureFailure = error instanceof WebDriverException
                && !(error instanceof AssertionError);

        if (!infrastructureFailure || attempts >= MAX_RETRIES) {
            return false;
        }

        attempts++;
        System.out.println("Повтор теста " + result.getName() + " после сбоя браузера: " + error.getMessage());
        return true;
    }
}

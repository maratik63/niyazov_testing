package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.ShareLaneConfig;

/**
 * Базовый класс для всех страниц ShareLane.
 * <p>
 * ShareLane — это CGI-приложение: каждый переход открывает новую страницу целиком.
 * Страницы отвечают медленно, поэтому все действия выполняются через явные ожидания,
 * а страница считается загруженной, когда появился один из ожидаемых элементов.
 */
public class BasePage {

    /** Сообщения приложения: <span class='error_message'> и <span class='confirmation_message'>. */
    protected static final By MESSAGE = By.cssSelector(".error_message, .confirmation_message");

    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, ShareLaneConfig.EXPLICIT_WAIT);
    }

    protected void waitForVisibility(By locator) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void waitForClickability(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    protected void waitForUrlContains(String urlFragment) {
        wait.until(ExpectedConditions.urlContains(urlFragment));
    }

    /**
     * Ожидание страницы по нескольким вариантам: CGI-скрипт ShareLane отвечает на один и тот же
     * запрос либо рабочей страницей, либо страницей с сообщением об ошибке.
     */
    protected void waitForAnyVisible(By... locators) {
        wait.until(d -> {
            for (By locator : locators) {
                for (WebElement element : d.findElements(locator)) {
                    try {
                        if (element.isDisplayed()) {
                            return true;
                        }
                    } catch (StaleElementReferenceException ignored) {
                        // страница успела обновиться — проверим на следующей итерации
                    }
                }
            }
            return false;
        });
    }

    protected void click(By locator) {
        waitForClickability(locator);
        driver.findElement(locator).click();
    }

    protected void type(By locator, String text) {
        waitForVisibility(locator);
        driver.findElement(locator).sendKeys(text);
    }

    protected String getText(By locator) {
        waitForVisibility(locator);
        return driver.findElement(locator).getText().trim();
    }

    /** Проверка наличия элемента без ожидания (нужна для пустых элементов, которые не видны). */
    protected boolean isElementPresent(By locator) {
        return !driver.findElements(locator).isEmpty();
    }

    /** Проверка видимости элемента без ожидания. */
    protected boolean isVisible(By locator) {
        for (WebElement element : driver.findElements(locator)) {
            try {
                if (element.isDisplayed()) {
                    return true;
                }
            } catch (StaleElementReferenceException ignored) {
                return false;
            }
        }
        return false;
    }

    protected void openPage(String url) {
        driver.get(url);
    }

    // ---------- сообщения приложения ----------

    /** Текст сообщения об ошибке или подтверждении; пустая строка, если сообщения нет. */
    public String getMessage() {
        return isElementPresent(MESSAGE) ? getText(MESSAGE) : "";
    }
}

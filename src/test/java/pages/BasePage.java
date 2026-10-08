package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Базовый класс для всех страниц (ShareLane, The Internet и Swag Labs).
 * <p>
 * Все три сайта — обычные многостраничные приложения: каждый переход открывает новую страницу
 * целиком. Действия выполняются через явные ожидания (неявные не используем), а страница
 * считается загруженной, когда появился один из ожидаемых элементов.
 */
public class BasePage {

    /** Таймаут явных ожиданий по умолчанию. */
    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(25);

    /** Таймаут проверки эффекта действия: страницы быстрые, ждать 25 секунд незачем. */
    private static final Duration EFFECT_TIMEOUT = Duration.ofSeconds(5);

    /** Сообщения ShareLane: <span class='error_message'> и <span class='confirmation_message'>. */
    protected static final By MESSAGE = By.cssSelector(".error_message, .confirmation_message");

    protected WebDriver driver;
    protected WebDriverWait wait;

    public BasePage(WebDriver driver) {
        this(driver, DEFAULT_TIMEOUT);
    }

    public BasePage(WebDriver driver, Duration explicitTimeout) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, explicitTimeout);
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

    /** Ожидание эффекта действия с коротким таймаутом. */
    protected void waitForEffect(Function<WebDriver, Boolean> effect) {
        new WebDriverWait(driver, EFFECT_TIMEOUT).until(effect);
    }

    /**
     * Ожидание страницы по нескольким вариантам: CGI-скрипты ShareLane отвечают на один и тот же
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

    /**
     * Клик с проверкой эффекта. Если страница успела перерисовать DOM и клик «потерялся»,
     * действие повторяется — всего до трёх попыток.
     *
     * @param effect условие, подтверждающее, что клик сработал (изменилось количество элементов,
     *               состояние чекбокса и т.п.)
     */
    protected void clickUntilEffect(By locator, Function<WebDriver, Boolean> effect) {
        for (int attempt = 1; attempt <= 3; attempt++) {
            click(locator);
            try {
                waitForEffect(effect);
                return;
            } catch (TimeoutException e) {
                if (attempt == 3) {
                    throw new TimeoutException(
                            "Клик по " + locator + " не привёл к ожидаемому эффекту за " + attempt + " попытки", e);
                }
            }
        }
    }

    protected void type(By locator, String text) {
        waitForVisibility(locator);
        driver.findElement(locator).sendKeys(text);
    }

    protected String getText(By locator) {
        waitForVisibility(locator);
        return driver.findElement(locator).getText().trim();
    }

    /** Тексты всех элементов, найденных по локатору (например, названия или цены товаров). */
    protected List<String> getAllTexts(By locator) {
        wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        return driver.findElements(locator).stream()
                .map(element -> element.getText().trim())
                .collect(Collectors.toList());
    }

    /** Элемент по индексу с ожиданием его появления (когда локатор подходит нескольким элементам). */
    protected WebElement elementAt(By locator, int index) {
        wait.until(d -> d.findElements(locator).size() > index);
        return driver.findElements(locator).get(index);
    }

    /** Количество элементов на странице без ожидания. */
    protected int countOf(By locator) {
        return driver.findElements(locator).size();
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

    // ---------- сообщения ShareLane ----------

    /** Текст сообщения об ошибке или подтверждении; пустая строка, если сообщения нет. */
    public String getMessage() {
        return isElementPresent(MESSAGE) ? getText(MESSAGE) : "";
    }
}

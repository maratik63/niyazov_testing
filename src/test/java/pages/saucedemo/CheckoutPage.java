package pages.saucedemo;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.BasePage;
import utils.SauceDemoConfig;

/** Шаг 1 оформления заказа Swag Labs (/checkout-step-one.html) — данные покупателя. */
public class CheckoutPage extends BasePage {

    private static final By FIRST_NAME_FIELD = By.id("first-name");
    private static final By LAST_NAME_FIELD = By.id("last-name");
    private static final By POSTAL_CODE_FIELD = By.id("postal-code");
    private static final By CONTINUE_BUTTON = By.id("continue");
    private static final By ERROR_MESSAGE = By.cssSelector("[data-test=error]");

    public CheckoutPage(WebDriver driver) {
        super(driver, SauceDemoConfig.EXPLICIT_TIMEOUT);
        waitForVisibility(FIRST_NAME_FIELD);
    }

    @Step("Заполнить данные покупателя: {firstName} {lastName}, индекс {postalCode}")
    public CheckoutPage fillCustomerInfo(String firstName, String lastName, String postalCode) {
        type(FIRST_NAME_FIELD, firstName);
        type(LAST_NAME_FIELD, lastName);
        type(POSTAL_CODE_FIELD, postalCode);
        return this;
    }

    /** Нажимает Continue без ожидания перехода — используется для проверки валидации. */
    @Step("Нажать Continue (без ожидания перехода — проверка валидации)")
    public CheckoutPage clickContinue() {
        click(CONTINUE_BUTTON);
        return this;
    }

    /** Переходит на шаг Overview: поля нужно заполнить заранее — {@link #fillCustomerInfo}. */
    @Step("Перейти к обзору заказа")
    public CheckoutOverviewPage continueToOverview() {
        clickUntilEffect(CONTINUE_BUTTON, d -> d.getCurrentUrl().contains("checkout-step-two.html"));
        return new CheckoutOverviewPage(driver);
    }

    @Step("Прочитать сообщение об ошибке оформления")
    public String getErrorMessage() {
        return getText(ERROR_MESSAGE);
    }
}

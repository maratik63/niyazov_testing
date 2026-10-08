package pages.saucedemo;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.BasePage;
import utils.SauceDemoConfig;

/** Финальная страница оформления заказа Swag Labs (/checkout-complete.html). */
public class CheckoutCompletePage extends BasePage {

    private static final By COMPLETE_HEADER = By.cssSelector("[data-test=complete-header]");
    private static final By COMPLETE_TEXT = By.cssSelector("[data-test=complete-text]");

    public CheckoutCompletePage(WebDriver driver) {
        super(driver, SauceDemoConfig.EXPLICIT_TIMEOUT);
        waitForVisibility(COMPLETE_HEADER);
    }

    /** Заголовок подтверждения — «Thank you for your order!». */
    @Step("Прочитать заголовок подтверждения заказа")
    public String getCompleteHeaderText() {
        return getText(COMPLETE_HEADER);
    }

    @Step("Прочитать текст подтверждения заказа")
    public String getCompleteText() {
        return getText(COMPLETE_TEXT);
    }
}

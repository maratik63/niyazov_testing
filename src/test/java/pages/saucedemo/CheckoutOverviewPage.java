package pages.saucedemo;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.BasePage;
import utils.SauceDemoConfig;

import java.util.List;

/** Шаг 2 оформления заказа Swag Labs (/checkout-step-two.html) — обзор заказа. */
public class CheckoutOverviewPage extends BasePage {

    private static final By INVENTORY_ITEM_NAME = By.cssSelector("[data-test=inventory-item-name]");
    private static final By SUBTOTAL_LABEL = By.cssSelector("[data-test=subtotal-label]");
    private static final By TOTAL_LABEL = By.cssSelector("[data-test=total-label]");
    private static final By FINISH_BUTTON = By.id("finish");

    public CheckoutOverviewPage(WebDriver driver) {
        super(driver, SauceDemoConfig.EXPLICIT_TIMEOUT);
        waitForVisibility(SUBTOTAL_LABEL);
    }

    @Step("Получить состав заказа на шаге Overview")
    public List<String> getItemNames() {
        return getAllTexts(INVENTORY_ITEM_NAME);
    }

    /** Сумма товаров из подписи вида «Item total: $29.99». */
    @Step("Прочитать сумму товаров (Item total)")
    public double getSubtotalAmount() {
        return parseAmount(getText(SUBTOTAL_LABEL));
    }

    /** Итоговая сумма с налогом из подписи вида «Total: $32.39». */
    @Step("Прочитать итоговую сумму с налогом (Total)")
    public double getTotalAmount() {
        return parseAmount(getText(TOTAL_LABEL));
    }

    @Step("Завершить оформление заказа (Finish)")
    public CheckoutCompletePage finishOrder() {
        clickUntilEffect(FINISH_BUTTON, d -> d.getCurrentUrl().contains("checkout-complete.html"));
        return new CheckoutCompletePage(driver);
    }

    private double parseAmount(String label) {
        return Double.parseDouble(label.replaceAll("[^0-9.]", ""));
    }
}

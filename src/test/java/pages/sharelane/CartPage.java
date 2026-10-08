package pages.sharelane;

import pages.BasePage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utils.ShareLaneConfig;

/**
 * Корзина (shopping_cart.py).
 * <p>
 * Таблица корзины: author | title | quantity | price | discount,% | discount,$ | total,$.
 * Товар хранится в cookie book_id, поэтому в корзине всегда не больше одной позиции.
 */
public class CartPage extends BasePage {

    private static final By QUANTITY_FIELD = By.name("q");
    private static final By CHECKOUT_BUTTON = By.cssSelector("input[value='Proceed to Checkout']");

    /** Строка с данными товара — следующая за строкой заголовков. */
    private static final String DATA_ROW = "//tr[td[normalize-space()='author']]/following-sibling::tr[1]/td";

    public CartPage(WebDriver driver) {
        super(driver);
    }

    public CartPage open() {
        openPage(ShareLaneConfig.SHOPPING_CART_PAGE);
        return waitForLoaded();
    }

    /** Корзина загружена, когда показана таблица с товаром или сообщение (пусто / нет входа). */
    public CartPage waitForLoaded() {
        waitForAnyVisible(QUANTITY_FIELD, MESSAGE);
        return this;
    }

    public boolean isLoginRequired() {
        return getMessage().equals("Oops, error. You must log in");
    }

    public String getTitle() {
        return cell(2);
    }

    public String getQuantity() {
        return driver.findElement(QUANTITY_FIELD).getAttribute("value");
    }

    public String getPrice() {
        return cell(4);
    }

    public String getTotal() {
        return cell(7);
    }

    public CheckoutPage proceedToCheckout() {
        click(CHECKOUT_BUTTON);
        return new CheckoutPage(driver).waitForLoaded();
    }

    private String cell(int index) {
        return getText(By.xpath(DATA_ROW + "[" + index + "]"));
    }
}

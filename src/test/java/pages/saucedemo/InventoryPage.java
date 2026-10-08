package pages.saucedemo;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import pages.BasePage;
import utils.SauceDemoConfig;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Каталог товаров Swag Labs (/inventory.html).
 * <p>
 * Кнопки товаров имеют стабильные id вида add-to-cart-{itemId} / remove-{itemId},
 * счётчик корзины — [data-test=shopping-cart-badge].
 */
public class InventoryPage extends BasePage {

    // Значения id товаров без префиксов add-to-cart- / remove-
    public static final String ITEM_BACKPACK = "sauce-labs-backpack";
    public static final String ITEM_BIKE_LIGHT = "sauce-labs-bike-light";

    private static final By PAGE_TITLE = By.cssSelector("[data-test=title]");
    private static final By INVENTORY_LIST = By.cssSelector("[data-test=inventory-list]");
    private static final By INVENTORY_ITEM_NAME = By.cssSelector("[data-test=inventory-item-name]");
    private static final By INVENTORY_ITEM_PRICE = By.cssSelector("[data-test=inventory-item-price]");
    private static final By CART_LINK = By.cssSelector("[data-test=shopping-cart-link]");
    private static final By CART_BADGE = By.cssSelector("[data-test=shopping-cart-badge]");
    private static final By SORT_CONTAINER = By.cssSelector("[data-test=product-sort-container]");

    public InventoryPage(WebDriver driver) {
        super(driver, SauceDemoConfig.EXPLICIT_TIMEOUT);
        waitForUrlContains("/inventory.html");
        waitForVisibility(INVENTORY_LIST);
    }

    @Step("Прочитать заголовок каталога")
    public String getPageTitle() {
        return getText(PAGE_TITLE);
    }

    /**
     * Добавляет товар в корзину и дожидается увеличения счётчика:
     * React обновляет бейдж асинхронно после клика.
     */
    @Step("Добавить товар «{itemId}» в корзину")
    public InventoryPage addItemToCart(String itemId) {
        int countBefore = readCartBadgeCount();
        clickUntilEffect(By.id("add-to-cart-" + itemId), d -> readCartBadgeCount() == countBefore + 1);
        return this;
    }

    /** Удаляет товар из корзины и дожидается уменьшения счётчика. */
    @Step("Удалить товар «{itemId}» из корзины")
    public InventoryPage removeItemFromCart(String itemId) {
        int countBefore = readCartBadgeCount();
        clickUntilEffect(By.id("remove-" + itemId), d -> readCartBadgeCount() == countBefore - 1);
        return this;
    }

    /** Количество товаров в корзине; при пустой корзине счётчика нет — 0. */
    @Step("Прочитать счётчик корзины")
    public int getCartBadgeCount() {
        return readCartBadgeCount();
    }

    @Step("Получить названия товаров каталога")
    public List<String> getItemNames() {
        return getAllTexts(INVENTORY_ITEM_NAME);
    }

    @Step("Получить цены товаров каталога")
    public List<Double> getItemPrices() {
        return getAllTexts(INVENTORY_ITEM_PRICE).stream()
                .map(price -> Double.parseDouble(price.replace("$", "")))
                .collect(Collectors.toList());
    }

    @Step("Отсортировать каталог: {option}")
    public InventoryPage sortBy(SortOption option) {
        waitForVisibility(SORT_CONTAINER);
        new Select(driver.findElement(SORT_CONTAINER)).selectByValue(option.getValue());
        return this;
    }

    @Step("Перейти в корзину")
    public CartPage openCart() {
        clickUntilEffect(CART_LINK, d -> d.getCurrentUrl().contains("cart.html"));
        return new CartPage(driver);
    }

    private int readCartBadgeCount() {
        List<WebElement> badges = driver.findElements(CART_BADGE);
        return badges.isEmpty() ? 0 : Integer.parseInt(badges.get(0).getText());
    }
}

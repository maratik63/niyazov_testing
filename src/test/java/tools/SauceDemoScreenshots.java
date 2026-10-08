package tools;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.annotations.Test;
import pages.saucedemo.CartPage;
import pages.saucedemo.CheckoutOverviewPage;
import pages.saucedemo.CheckoutPage;
import pages.saucedemo.InventoryPage;
import pages.saucedemo.LoginPage;
import tests.BaseTest;
import utils.SauceDemoConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Служебный сценарий для отчёта по практике №13: проходит ключевые экраны Swag Labs
 * и сохраняет скриншоты в каталог {@code screenshots/} (не входит в основной набор тестов).
 * <p>
 * Запуск: {@code mvn test -Dtest=SauceDemoScreenshots -Dsurefire.suiteXmlFiles=}
 */
public class SauceDemoScreenshots extends BaseTest {

    private static final Path SCREENSHOTS_DIR = Paths.get("screenshots");

    @Test(description = "Снимки ключевых экранов: ошибка входа, каталог, корзина, оформление, подтверждение")
    public void captureKeyScreens() throws IOException {
        Files.createDirectories(SCREENSHOTS_DIR);

        // 1. Негативная авторизация: сообщение о блокировке
        new LoginPage(driver).login(SauceDemoConfig.LOCKED_OUT_USER, SauceDemoConfig.PASSWORD);
        takeScreenshot("06-login-locked-out-error");

        // 2. Позитивная авторизация: каталог товаров
        driver.manage().deleteAllCookies();
        new LoginPage(driver).login(SauceDemoConfig.STANDARD_USER, SauceDemoConfig.PASSWORD);
        InventoryPage inventoryPage = new InventoryPage(driver);
        takeScreenshot("01-inventory-products");

        // 3. Корзина с добавленным товаром
        inventoryPage.addItemToCart(InventoryPage.ITEM_BACKPACK);
        CartPage cartPage = inventoryPage.openCart();
        takeScreenshot("02-cart");

        // 4. Шаг оформления заказа: данные покупателя
        CheckoutPage checkoutPage = cartPage.startCheckout();
        checkoutPage.fillCustomerInfo("Ivan", "Niyazov", "12345");
        takeScreenshot("03-checkout-info");

        // 5. Обзор заказа
        CheckoutOverviewPage overviewPage = checkoutPage.continueToOverview();
        takeScreenshot("04-checkout-overview");

        // 6. Заказ оформлен
        overviewPage.finishOrder();
        takeScreenshot("05-checkout-complete");
    }

    private void takeScreenshot(String name) throws IOException {
        byte[] png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        Path file = SCREENSHOTS_DIR.resolve(name + ".png");
        Files.write(file, png);
        System.out.println("Скриншот: " + file.toAbsolutePath());
    }
}

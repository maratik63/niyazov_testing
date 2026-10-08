package tests.saucedemo;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.saucedemo.CheckoutCompletePage;
import pages.saucedemo.CheckoutOverviewPage;
import pages.saucedemo.CheckoutPage;
import pages.saucedemo.InventoryPage;

import java.util.List;

/** Сценарии оформления заказа Swag Labs: полный цикл и валидация обязательных полей. */
public class CheckoutTests extends SauceDemoBaseTest {

    private static final String FIRST_NAME = "Ivan";
    private static final String LAST_NAME = "Niyazov";
    private static final String POSTAL_CODE = "12345";

    private static final String BACKPACK_NAME = "Sauce Labs Backpack";
    private static final double BACKPACK_PRICE = 29.99;

    @Test(description = "Полный цикл оформления заказа: данные покупателя, сверка суммы, подтверждение")
    public void testCompleteCheckoutFlow() {
        InventoryPage inventoryPage = loginAsStandardUser();
        inventoryPage.addItemToCart(InventoryPage.ITEM_BACKPACK);

        CheckoutPage checkoutPage = inventoryPage.openCart().startCheckout();
        checkoutPage.fillCustomerInfo(FIRST_NAME, LAST_NAME, POSTAL_CODE);
        CheckoutOverviewPage overviewPage = checkoutPage.continueToOverview();

        Assert.assertEquals(overviewPage.getItemNames(), List.of(BACKPACK_NAME),
                "На шаге Overview ожидался товар «Sauce Labs Backpack»");
        Assert.assertEquals(overviewPage.getSubtotalAmount(), BACKPACK_PRICE,
                "Ожидалась сумма товаров 29.99 на шаге Overview");

        CheckoutCompletePage completePage = overviewPage.finishOrder();
        Assert.assertEquals(completePage.getCompleteHeaderText(), "Thank you for your order!",
                "Ожидалось подтверждение оформления заказа");
    }

    @Test(description = "Валидация: пустое обязательное поле First Name не позволяет продолжить")
    public void testCheckoutValidationEmptyFirstName() {
        InventoryPage inventoryPage = loginAsStandardUser();
        inventoryPage.addItemToCart(InventoryPage.ITEM_BACKPACK);

        CheckoutPage checkoutPage = inventoryPage.openCart().startCheckout();
        checkoutPage.fillCustomerInfo("", LAST_NAME, POSTAL_CODE);
        checkoutPage.clickContinue();

        Assert.assertEquals(checkoutPage.getErrorMessage(), "Error: First Name is required",
                "Ожидалась ошибка обязательного поля First Name");
    }
}

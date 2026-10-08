package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.BalanceViewerPage;
import pages.CartPage;
import pages.CheckoutPage;
import pages.CreditCardGeneratorPage;
import pages.MainPage;
import pages.RegisterPage;
import utils.CreditCard;
import utils.ShareLaneConfig;
import utils.TestUser;

/**
 * Сквозные проверки ShareLane: регистрация, авторизация, корзина и оформление заказа.
 * <p>
 * Первый тест проходит весь основной сценарий покупки целиком — включая получение тестовой
 * карты и проверку того, что оплата списала деньги с её баланса. Второй проверяет,
 * что ключевые шаги корректно отказывают при неверных данных.
 */
public class ShareLaneTests extends BaseTest {

    private static final String CREDENTIALS_ERROR =
            "Oops, error. Email and/or password don't match our records";
    private static final String CARD_TYPE = "Visa";
    private static final String INVALID_CARD_NUMBER = "1234567890123456";

    @Test(description = "Полный сценарий покупки: регистрация → авторизация → корзина → оформление и оплата заказа")
    public void fullPurchaseFlow() {
        // 1. Регистрация: два шага формы, сайт создаёт аккаунт и показывает выданные данные
        RegisterPage registerPage = new MainPage(driver).open().openRegistration();
        TestUser user = TestUser.random();
        registerPage.register(user);

        Assert.assertTrue(registerPage.isAccountCreated(),
                "Аккаунт должен быть создан, ответ сайта: " + registerPage.getMessage());
        Assert.assertEquals(registerPage.getGeneratedPassword(), ShareLaneConfig.DEFAULT_PASSWORD,
                "Сайт выдаёт всем аккаунтам пароль 1111");
        String loginEmail = registerPage.getGeneratedEmail();
        Assert.assertTrue(loginEmail.contains("@"), "Сайт должен показать email созданного аккаунта");

        // 2. Авторизация выданными данными
        MainPage mainPage = new MainPage(driver).open()
                .login(loginEmail, ShareLaneConfig.DEFAULT_PASSWORD);
        Assert.assertTrue(mainPage.isUserLoggedIn(),
                "Ожидалось приветствие авторизованного пользователя, получено: " + mainPage.getUserGreeting());

        // 3. Блок получения карты для заказа: Test Portal → Credit Card Generator.
        // Реальной картой на ShareLane платить нельзя, поэтому тест выпускает виртуальную
        // и запоминает её баланс, чтобы затем проверить списание суммы заказа.
        CreditCard card = new CreditCardGeneratorPage(driver).open().generate(CARD_TYPE);
        Assert.assertFalse(card.number().isEmpty(), "Генератор должен выдать номер карты");

        BalanceViewerPage balanceViewer = new BalanceViewerPage(driver).open().showBalance(card.number());
        Assert.assertTrue(balanceViewer.isCardFound(), "Выпущенная карта должна находиться в БД");
        double balanceBefore = balanceViewer.getBalance();
        Assert.assertEquals(balanceBefore, card.balance(), 0.01,
                "Баланс из генератора карт должен совпадать с балансом в Balance Viewer");

        // 4. Добавление товара в корзину
        CartPage cartPage = mainPage.addBookToCart(ShareLaneConfig.BOOK_ID);
        Assert.assertEquals(cartPage.getTitle(), ShareLaneConfig.BOOK_TITLE,
                "В корзине должна быть добавленная книга");
        Assert.assertEquals(cartPage.getPrice(), ShareLaneConfig.BOOK_PRICE);
        Assert.assertEquals(cartPage.getQuantity(), "1", "Количество по умолчанию — 1");
        Assert.assertEquals(cartPage.getTotal(), ShareLaneConfig.BOOK_TOTAL,
                "Фактическая сумма заказа: сайт прибавляет скидку вместо вычитания");

        // 5. Оформление заказа: сверяем состав заказа и адрес доставки, затем платим картой
        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        Assert.assertTrue(checkoutPage.isPaymentFormDisplayed(), "Должна открыться форма оплаты");
        Assert.assertEquals(checkoutPage.getOrderTitle(), ShareLaneConfig.BOOK_TITLE,
                "В заказе должна быть та же книга, что и в корзине");
        Assert.assertEquals(checkoutPage.getOrderQuantity(), "1");
        Assert.assertEquals(checkoutPage.getOrderTotal(), ShareLaneConfig.BOOK_TOTAL,
                "Сумма к оплате должна совпадать с суммой корзины");
        Assert.assertEquals(checkoutPage.getShippingZipCode(), user.zipCode(),
                "Блок Shipping должен использовать данные аккаунта");

        // Сумму запоминаем до оплаты: после неё сайт показывает уже страницу подтверждения
        double orderTotal = Double.parseDouble(checkoutPage.getOrderTotal());

        checkoutPage.pay(card);
        Assert.assertTrue(checkoutPage.isOrderPlaced(),
                "Заказ должен быть оплачен, ответ сайта: " + checkoutPage.getMessage());
        Assert.assertFalse(checkoutPage.getOrderId().isEmpty(), "Должен быть показан номер заказа");

        // 6. Оплата списала сумму заказа с баланса карты
        double balanceAfter = new BalanceViewerPage(driver).open()
                .showBalance(card.number())
                .getBalance();
        Assert.assertEquals(balanceAfter, balanceBefore - orderTotal, 0.01,
                "Баланс карты должен уменьшиться на сумму заказа");
    }

    @Test(description = "Негативные проверки: доступ к корзине, авторизация, оплата")
    public void negativeScenarios() {
        // 1. Корзина недоступна без авторизации
        CartPage cartPage = new MainPage(driver).open().openCart();
        Assert.assertTrue(cartPage.isLoginRequired(),
                "Ожидалось требование авторизации, получено: " + cartPage.getMessage());

        // 2. Вход с неверным паролем
        RegisterPage registerPage = new MainPage(driver).open().openRegistration();
        TestUser user = TestUser.random();
        registerPage.register(user);
        String loginEmail = registerPage.getGeneratedEmail();

        MainPage mainPage = new MainPage(driver).open()
                .loginWithInvalidCredentials(loginEmail, "wrong_password");
        Assert.assertEquals(mainPage.getMessage(), CREDENTIALS_ERROR);
        Assert.assertFalse(mainPage.isUserLoggedIn(), "Пользователь не должен быть авторизован");

        // 3. Оплата несуществующей картой: заказ не создаётся
        mainPage = new MainPage(driver).open().login(loginEmail, ShareLaneConfig.DEFAULT_PASSWORD);
        CheckoutPage checkoutPage = mainPage.addBookToCart(ShareLaneConfig.BOOK_ID)
                .proceedToCheckout()
                .selectCardType(CARD_TYPE)
                .enterCardNumber(INVALID_CARD_NUMBER)
                .makePayment();

        Assert.assertTrue(checkoutPage.isPaymentRejected(),
                "Ожидался отказ в оплате, получено: " + checkoutPage.getMessage());
        Assert.assertFalse(checkoutPage.isOrderPlaced(), "Заказ не должен быть создан");
    }
}

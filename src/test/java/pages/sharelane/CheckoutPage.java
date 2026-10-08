package pages.sharelane;

import pages.BasePage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import utils.CreditCard;

/**
 * Оформление и оплата заказа (checkout.py).
 * <p>
 * Форма оплаты: тип карты, номер карты, срок действия (поля заблокированы), CVV2 (заблокировано)
 * и кнопка "Make Payment". После успешной оплаты показывается подтверждение
 * "Thank you for your order!!!" и номер заказа.
 * <p>
 * Особенности разметки, важные для тестов:
 * <ul>
 *     <li>у всех типов карт в списке одинаковое значение value='1' — тип карты не передаётся;</li>
 *     <li>поля "Card Number" и "CVV2" имеют одинаковое имя card_number, поэтому номер карты
 *     вводится в первый (активный) элемент;</li>
 *     <li>срок действия и CVV2 отключены (disabled) и в запрос не попадают.</li>
 * </ul>
 */
public class CheckoutPage extends BasePage {

    private static final By CARD_TYPE = By.name("card_type_id");
    private static final By CARD_NUMBER = By.cssSelector("input[name='card_number']:not([disabled])");
    private static final By MAKE_PAYMENT_BUTTON = By.cssSelector("input[value='Make Payment']");

    private static final By ORDER_SUCCESS = By.xpath("//font[contains(., 'Thank you for your order')]");
    private static final By ORDER_ID = By.xpath("//p[contains(., 'Order id')]/b");

    /** Строка с данными заказа в таблице "Book Info & Total". */
    private static final String ORDER_ROW = "//tr[td[normalize-space()='author']]/following-sibling::tr[1]/td";

    /** ZIP-код в блоке Shipping — сайт подставляет данные аккаунта. */
    private static final By SHIPPING_ZIP_CODE = By.xpath("//tr[td[normalize-space()='Zip code:']]/td[2]");

    public CheckoutPage(WebDriver driver) {
        super(driver);
    }

    /** Страница загружена, когда видна форма оплаты или сообщение (отказ в оплате / нужен вход). */
    public CheckoutPage waitForLoaded() {
        waitForAnyVisible(CARD_NUMBER, MESSAGE);
        return this;
    }

    public boolean isPaymentFormDisplayed() {
        return isVisible(CARD_NUMBER) && isVisible(MAKE_PAYMENT_BUTTON);
    }

    public CheckoutPage selectCardType(String visibleText) {
        new Select(driver.findElement(CARD_TYPE)).selectByVisibleText(visibleText);
        return this;
    }

    public CheckoutPage enterCardNumber(String cardNumber) {
        type(CARD_NUMBER, cardNumber);
        return this;
    }

    public CheckoutPage makePayment() {
        click(MAKE_PAYMENT_BUTTON);
        waitForAnyVisible(ORDER_SUCCESS, MESSAGE);
        return this;
    }

    /** Оплата тестовой картой: выбор типа карты, ввод номера и нажатие "Make Payment". */
    public CheckoutPage pay(CreditCard card) {
        return selectCardType(card.type())
                .enterCardNumber(card.number())
                .makePayment();
    }

    public boolean isOrderPlaced() {
        return isVisible(ORDER_SUCCESS);
    }

    public boolean isPaymentRejected() {
        return getMessage().equals("Oops, error. Invalid card number or not enough balance for purchase");
    }

    /** Номер созданного заказа со страницы подтверждения. */
    public String getOrderId() {
        waitForVisibility(ORDER_ID);
        return getText(ORDER_ID);
    }

    public String getOrderTitle() {
        return cell(2);
    }

    public String getOrderQuantity() {
        return cell(3);
    }

    /** Сумма к оплате (в том виде, в котором её показывает сайт, например "10.70"). */
    public String getOrderTotal() {
        return cell(4);
    }

    /** ZIP-код из блока Shipping — проверяем, что заказ использует данные аккаунта. */
    public String getShippingZipCode() {
        return getText(SHIPPING_ZIP_CODE);
    }

    private String cell(int index) {
        return getText(By.xpath(ORDER_ROW + "[" + index + "]"));
    }
}

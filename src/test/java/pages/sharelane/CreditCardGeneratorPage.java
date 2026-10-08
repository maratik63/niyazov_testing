package pages.sharelane;

import pages.BasePage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import utils.CreditCard;
import utils.ShareLaneConfig;

/**
 * Test Portal → Credit Card Generator (get_credit_card.py).
 * <p>
 * Блок получения карты для оплаты заказа: выбираем тип карты, нажимаем
 * "Generate Credit Card" и забираем из таблицы номер, срок действия, CVV2 и баланс.
 * Оплатить заказ можно только такой картой — реальные карты на сайте использовать нельзя.
 */
public class CreditCardGeneratorPage extends BasePage {

    private static final By CARD_TYPE_SELECT = By.name("type");
    private static final By GENERATE_BUTTON = By.cssSelector("input[value='Generate Credit Card']");

    /** Строка с данными выпущенной карты — следующая за строкой заголовков таблицы. */
    private static final String DATA_ROW = "//tr[td[normalize-space()='Number']]/following-sibling::tr[1]/td";
    private static final By RESULT_TYPE = By.xpath(DATA_ROW + "[1]");
    private static final By RESULT_NUMBER = By.xpath(DATA_ROW + "[2]//b");
    private static final By RESULT_EXPIRATION_MONTH = By.xpath(DATA_ROW + "[3]");
    private static final By RESULT_EXPIRATION_YEAR = By.xpath(DATA_ROW + "[4]");
    private static final By RESULT_CVV2 = By.xpath(DATA_ROW + "[5]");
    private static final By RESULT_BALANCE = By.xpath(DATA_ROW + "[6]");

    public CreditCardGeneratorPage(WebDriver driver) {
        super(driver);
    }

    public CreditCardGeneratorPage open() {
        openPage(ShareLaneConfig.CREDIT_CARD_GENERATOR_PAGE);
        waitForVisibility(GENERATE_BUTTON);
        return this;
    }

    /** Выпуск карты указанного типа: Visa, MasterCard или AmEx. */
    public CreditCard generate(String cardType) {
        new Select(driver.findElement(CARD_TYPE_SELECT)).selectByVisibleText(cardType);
        click(GENERATE_BUTTON);
        waitForVisibility(RESULT_NUMBER);
        return new CreditCard(
                getText(RESULT_TYPE),
                getText(RESULT_NUMBER),
                getText(RESULT_EXPIRATION_MONTH),
                getText(RESULT_EXPIRATION_YEAR),
                getText(RESULT_CVV2),
                parseAmount(getText(RESULT_BALANCE)));
    }

    private double parseAmount(String value) {
        return Double.parseDouble(value.replace("$", "").replace(",", "").trim());
    }
}

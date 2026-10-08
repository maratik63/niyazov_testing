package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utils.ShareLaneConfig;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Test Portal → Credit Card Balance Viewer (view_balance.py).
 * <p>
 * Показывает баланс карты: "Balance of card N is $1000.00". Используется для проверки,
 * что оплата заказа действительно списала деньги с карты.
 */
public class BalanceViewerPage extends BasePage {

    private static final By CARD_NUMBER_FIELD = By.name("card_number");
    private static final By SHOW_BALANCE_BUTTON = By.cssSelector("input[value='Show me the balance']");
    private static final By BODY = By.tagName("body");

    private static final By BALANCE_TEXT = By.xpath("//*[contains(text(), 'Balance of card')]");
    private static final By CARD_NOT_FOUND_TEXT = By.xpath("//*[contains(text(), 'Cannot find this card in DB')]");

    private static final Pattern BALANCE = Pattern.compile("\\$\\s*([\\d.]+)");

    public BalanceViewerPage(WebDriver driver) {
        super(driver);
    }

    public BalanceViewerPage open() {
        openPage(ShareLaneConfig.BALANCE_VIEWER_PAGE);
        waitForVisibility(CARD_NUMBER_FIELD);
        return this;
    }

    /** Запрос баланса по номеру карты. */
    public BalanceViewerPage showBalance(String cardNumber) {
        type(CARD_NUMBER_FIELD, cardNumber);
        click(SHOW_BALANCE_BUTTON);
        waitForAnyVisible(BALANCE_TEXT, CARD_NOT_FOUND_TEXT);
        return this;
    }

    public boolean isCardFound() {
        return !getText(BODY).contains("Cannot find this card in DB");
    }

    /** Баланс карты в долларах. */
    public double getBalance() {
        String text = getText(BODY);
        Matcher matcher = BALANCE.matcher(text);
        if (!matcher.find()) {
            throw new IllegalStateException("На странице баланса не найдена сумма: " + text);
        }
        return Double.parseDouble(matcher.group(1));
    }
}
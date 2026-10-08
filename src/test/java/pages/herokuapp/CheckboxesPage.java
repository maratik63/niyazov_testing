package pages.herokuapp;

import pages.BasePage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utils.HeroKuAppConfig;

/**
 * Checkboxes (https://the-internet.herokuapp.com/checkboxes).
 * <p>
 * Локаторы из задания: {@code input[type='checkbox']} — берём первый и второй чекбокс.
 * По умолчанию первый чекбокс не отмечен, второй отмечен.
 * Нумерация в методах как в задании: 1 — первый чекбокс, 2 — второй.
 */
public class CheckboxesPage extends BasePage {

    private static final By CHECKBOX_1 = By.cssSelector("input[type='checkbox']:nth-of-type(1)");
    private static final By CHECKBOX_2 = By.cssSelector("input[type='checkbox']:nth-of-type(2)");

    public CheckboxesPage(WebDriver driver) {
        super(driver);
        openPage(HeroKuAppConfig.CHECKBOXES_PAGE);
        waitForVisibility(CHECKBOX_1);
    }

    /** Состояние чекбокса: true — отмечен. */
    public boolean isCheckboxChecked(int checkboxNumber) {
        return elementAt(checkbox(checkboxNumber), 0).isSelected();
    }

    /** Клик по чекбоксу с ожиданием, что состояние действительно изменилось. */
    public CheckboxesPage clickCheckbox(int checkboxNumber) {
        By locator = checkbox(checkboxNumber);
        boolean before = isCheckboxChecked(checkboxNumber);
        clickUntilEffect(locator, d -> d.findElements(locator).get(0).isSelected() != before);
        return this;
    }

    public int getCheckboxesCount() {
        return countOf(CHECKBOX_1) + countOf(CHECKBOX_2);
    }

    private By checkbox(int checkboxNumber) {
        return checkboxNumber == 1 ? CHECKBOX_1 : CHECKBOX_2;
    }
}

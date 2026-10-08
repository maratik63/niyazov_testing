package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import utils.HeroKuAppConfig;

/**
 * Inputs (https://the-internet.herokuapp.com/inputs).
 * <p>
 * Локатор из задания: {@code By.tagName("input")}. Числовое поле принимает цифры, игнорирует
 * буквы, а стрелки вверх/вниз увеличивают и уменьшают значение на шаг (1).
 */
public class InputsPage extends BasePage {

    private static final By INPUT_FIELD = By.tagName("input");

    public InputsPage(WebDriver driver) {
        super(driver);
        openPage(HeroKuAppConfig.INPUTS_PAGE);
        waitForVisibility(INPUT_FIELD);
    }

    public InputsPage inputValue(String value) {
        type(INPUT_FIELD, value);
        return this;
    }

    public InputsPage pressArrowUp() {
        elementAt(INPUT_FIELD, 0).sendKeys(Keys.ARROW_UP);
        return this;
    }

    public InputsPage pressArrowDown() {
        elementAt(INPUT_FIELD, 0).sendKeys(Keys.ARROW_DOWN);
        return this;
    }

    /** Текущее значение поля. */
    public String getInputValue() {
        return elementAt(INPUT_FIELD, 0).getAttribute("value");
    }
}

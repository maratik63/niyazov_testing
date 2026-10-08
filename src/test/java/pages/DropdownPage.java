package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import utils.HeroKuAppConfig;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Dropdown (https://the-internet.herokuapp.com/dropdown).
 * <p>
 * Локатор из задания: {@code By.id("dropdown")}. В списке три элемента: подсказка
 * "Please select an option" (выбрана по умолчанию) и опции "Option 1", "Option 2".
 * Для работы со списком используется класс Select.
 */
public class DropdownPage extends BasePage {

    private static final By DROPDOWN = By.id("dropdown");

    public DropdownPage(WebDriver driver) {
        super(driver);
        openPage(HeroKuAppConfig.DROPDOWN_PAGE);
        waitForVisibility(DROPDOWN);
    }

    public DropdownPage selectOptionByVisibleText(String optionText) {
        dropdown().selectByVisibleText(optionText);
        return this;
    }

    public String getSelectedOptionText() {
        return dropdown().getFirstSelectedOption().getText().trim();
    }

    public int getDropdownOptionsCount() {
        return dropdown().getOptions().size();
    }

    /** Тексты всех опций списка — для проверки, что нужные опции присутствуют. */
    public List<String> getOptionTexts() {
        return dropdown().getOptions().stream()
                .map(WebElement::getText)
                .map(String::trim)
                .collect(Collectors.toList());
    }

    private Select dropdown() {
        return new Select(elementAt(DROPDOWN, 0));
    }
}

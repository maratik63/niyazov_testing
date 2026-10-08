package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.DropdownPage;

import java.util.List;

/**
 * Dropdown: наличие всех опций списка и выбор первой и второй опции.
 */
public class DropdownTests extends BaseTest {

    private static final String PLACEHOLDER = "Please select an option";
    private static final String FIRST_OPTION = "Option 1";
    private static final String SECOND_OPTION = "Option 2";

    @Test(description = "Проверка наличия опций в dropdown")
    public void testDropdownOptions() {
        DropdownPage page = new DropdownPage(driver);

        Assert.assertEquals(page.getDropdownOptionsCount(), 3, "В dropdown должно быть 3 опции");
        Assert.assertEquals(page.getOptionTexts(), List.of(PLACEHOLDER, FIRST_OPTION, SECOND_OPTION),
                "В списке должны быть подсказка и обе опции");
    }

    @Test(description = "Проверка выбора опций в dropdown")
    public void testSelectOption() {
        DropdownPage page = new DropdownPage(driver);

        page.selectOptionByVisibleText(FIRST_OPTION);
        Assert.assertEquals(page.getSelectedOptionText(), FIRST_OPTION, "Должна быть выбрана Option 1");

        page.selectOptionByVisibleText(SECOND_OPTION);
        Assert.assertEquals(page.getSelectedOptionText(), SECOND_OPTION, "Должна быть выбрана Option 2");
    }
}

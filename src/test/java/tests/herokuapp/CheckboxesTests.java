package tests.herokuapp;

import tests.BaseTest;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.herokuapp.CheckboxesPage;

/**
 * Checkboxes: состояние чекбоксов по умолчанию и их переключение.
 */
public class CheckboxesTests extends BaseTest {

    private static final int FIRST_CHECKBOX = 1;
    private static final int SECOND_CHECKBOX = 2;

    @Test(description = "Проверка начального состояния чекбоксов")
    public void testInitialCheckboxState() {
        CheckboxesPage page = new CheckboxesPage(driver);

        Assert.assertEquals(page.getCheckboxesCount(), 2, "На странице должно быть два чекбокса");
        Assert.assertFalse(page.isCheckboxChecked(FIRST_CHECKBOX),
                "Первый чекбокс должен быть unchecked по умолчанию");
        Assert.assertTrue(page.isCheckboxChecked(SECOND_CHECKBOX),
                "Второй чекбокс должен быть checked по умолчанию");
    }

    @Test(description = "Проверка переключения чекбоксов")
    public void testToggleCheckbox() {
        CheckboxesPage page = new CheckboxesPage(driver);

        page.clickCheckbox(FIRST_CHECKBOX);
        Assert.assertTrue(page.isCheckboxChecked(FIRST_CHECKBOX),
                "Первый чекбокс должен быть checked после клика");

        page.clickCheckbox(SECOND_CHECKBOX);
        Assert.assertFalse(page.isCheckboxChecked(SECOND_CHECKBOX),
                "Второй чекбокс должен быть unchecked после клика");
    }
}

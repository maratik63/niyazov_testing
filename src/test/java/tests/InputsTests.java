package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.InputsPage;

/**
 * Inputs: ввод цифровых и нецифровых значений и работа со стрелками вверх/вниз.
 */
public class InputsTests extends BaseTest {

    @Test(description = "Проверка ввода числовых значений")
    public void testNumericInput() {
        InputsPage page = new InputsPage(driver);

        page.inputValue("12345");

        Assert.assertEquals(page.getInputValue(), "12345", "В поле должно быть значение 12345");
    }

    @Test(description = "Проверка ввода нечисловых значений (негативный тест)")
    public void testNonNumericInput() {
        InputsPage page = new InputsPage(driver);

        page.inputValue("abc");

        Assert.assertNotEquals(page.getInputValue(), "abc", "Буквы не должны вводиться в поле");
    }

    @Test(description = "Проверка работы стрелок вверх и вниз")
    public void testArrowKeys() {
        InputsPage page = new InputsPage(driver);
        page.inputValue("10");

        page.pressArrowUp();
        Assert.assertEquals(page.getInputValue(), "11", "Значение должно увеличиться на 1 после стрелки вверх");

        page.pressArrowDown();
        Assert.assertEquals(page.getInputValue(), "10", "Значение должно уменьшиться на 1 после стрелки вниз");
    }
}

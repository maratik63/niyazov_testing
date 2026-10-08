package tests;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.AddRemoveElementsPage;

/**
 * Add/Remove Elements: добавление и удаление элементов, подсчёт кнопок Delete.
 */
public class AddRemoveElementsTests extends BaseTest {

    @Test(description = "Проверка добавления и удаления элементов")
    public void testAddAndRemoveElements() {
        AddRemoveElementsPage page = new AddRemoveElementsPage(driver);

        page.addElement();
        page.addElement();

        Assert.assertEquals(page.getDeleteButtonsCount(), 2,
                "Должно быть 2 кнопки Delete после добавления 2 элементов");

        page.deleteElement();

        Assert.assertEquals(page.getDeleteButtonsCount(), 1,
                "Должна остаться 1 кнопка Delete после удаления одного элемента");
    }

    @Test(description = "Проверка удаления всех элементов")
    public void testRemoveAllElements() {
        AddRemoveElementsPage page = new AddRemoveElementsPage(driver);

        page.addElement();
        page.addElement();
        page.addElement();

        page.deleteElement();
        page.deleteElement();
        page.deleteElement();

        Assert.assertEquals(page.getDeleteButtonsCount(), 0, "Все элементы должны быть удалены");
        Assert.assertFalse(page.isDeleteButtonDisplayed(), "Кнопка Delete не должна отображаться");
    }
}

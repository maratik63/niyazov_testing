package tests.saucedemo;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.saucedemo.InventoryPage;
import pages.saucedemo.SortOption;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Сценарии сортировки каталога Swag Labs: по названию и по цене. */
@Epic("Swag Labs")
@Feature("Каталог товаров")
@Story("Сортировка каталога")
@Owner("Ниязов")
public class SortingTests extends SauceDemoBaseTest {

    @Test(description = "Сортировка по названию A→Z")
    @Severity(SeverityLevel.NORMAL)
    public void testSortByNameAscending() {
        InventoryPage inventoryPage = loginAsStandardUser();

        List<String> expectedNames = new ArrayList<>(inventoryPage.getItemNames());
        expectedNames.sort(Comparator.naturalOrder());

        inventoryPage.sortBy(SortOption.NAME_ASC);

        Assert.assertEquals(inventoryPage.getItemNames(), expectedNames,
                "После сортировки A→Z товары должны быть упорядочены по алфавиту");
    }

    @Test(description = "Сортировка по названию Z→A")
    @Severity(SeverityLevel.MINOR)
    public void testSortByNameDescending() {
        InventoryPage inventoryPage = loginAsStandardUser();

        List<String> expectedNames = new ArrayList<>(inventoryPage.getItemNames());
        expectedNames.sort(Comparator.reverseOrder());

        inventoryPage.sortBy(SortOption.NAME_DESC);

        Assert.assertEquals(inventoryPage.getItemNames(), expectedNames,
                "После сортировки Z→A товары должны быть упорядочены по алфавиту в обратном порядке");
    }

    @Test(description = "Сортировка по цене Low→High")
    @Severity(SeverityLevel.NORMAL)
    public void testSortByPriceLowToHigh() {
        InventoryPage inventoryPage = loginAsStandardUser();

        List<Double> expectedPrices = new ArrayList<>(inventoryPage.getItemPrices());
        expectedPrices.sort(Comparator.naturalOrder());

        inventoryPage.sortBy(SortOption.PRICE_LOW_TO_HIGH);

        Assert.assertEquals(inventoryPage.getItemPrices(), expectedPrices,
                "После сортировки Low→High цены должны идти по возрастанию");
    }

    @Test(description = "Сортировка по цене High→Low")
    @Severity(SeverityLevel.MINOR)
    public void testSortByPriceHighToLow() {
        InventoryPage inventoryPage = loginAsStandardUser();

        List<Double> expectedPrices = new ArrayList<>(inventoryPage.getItemPrices());
        expectedPrices.sort(Comparator.reverseOrder());

        inventoryPage.sortBy(SortOption.PRICE_HIGH_TO_LOW);

        Assert.assertEquals(inventoryPage.getItemPrices(), expectedPrices,
                "После сортировки High→Low цены должны идти по убыванию");
    }
}

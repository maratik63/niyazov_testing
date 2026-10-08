package tools;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.saucedemo.InventoryPage;
import pages.saucedemo.LoginPage;
import tests.BaseTest;
import utils.AllureAttachments;
import utils.SauceDemoConfig;

/**
 * Служебный тест для отчёта по практике №14–16: намеренно падает, чтобы в Allure-отчёте была
 * карточка упавшего теста со скриншотом страницы и DOM-дампом (их прикладывает {@code BaseTest}).
 * <p>
 * В основной набор ({@code testng.xml}) не входит.
 * Запуск: {@code mvn test -Dtest=AllureFailureDemo -Dsurefire.suiteXmlFiles=}
 */
@Epic("Swag Labs")
@Feature("Демонстрация Allure")
@Story("Карточка упавшего теста")
@Owner("Ниязов")
public class AllureFailureDemo extends BaseTest {

    @Test(description = "Демонстрационное падение: заведомо неверное ожидание заголовка каталога")
    @Severity(SeverityLevel.MINOR)
    public void demoFailedTest() {
        new LoginPage(driver).login(SauceDemoConfig.STANDARD_USER, SauceDemoConfig.PASSWORD);
        InventoryPage inventoryPage = new InventoryPage(driver);

        AllureAttachments.text("Фактический заголовок каталога", inventoryPage.getPageTitle());

        Assert.assertEquals(inventoryPage.getPageTitle(), "Products (демонстрационное падение)",
                "Демонстрационное падение: в отчёте должны быть скриншот и DOM-дамп страницы");
    }
}

package tests.saucedemo;

import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Link;
import io.qameta.allure.Owner;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.testng.Assert;
import org.testng.annotations.Test;
import pages.saucedemo.InventoryPage;
import pages.saucedemo.LoginPage;
import tests.BaseTest;
import utils.SauceDemoConfig;

/** Сценарии авторизации Swag Labs (позитивные и негативные). */
@Epic("Swag Labs")
@Feature("Авторизация")
@Story("Вход в систему")
@Owner("Ниязов")
@Link(name = "Репозиторий проекта", url = "https://github.com/maratik63/niyazov_testing")
public class LoginTests extends BaseTest {

    @Test(description = "Позитивная авторизация standard_user: переход на /inventory.html и заголовок Products")
    @Severity(SeverityLevel.CRITICAL)
    public void testSuccessfulLogin() {
        new LoginPage(driver).login(SauceDemoConfig.STANDARD_USER, SauceDemoConfig.PASSWORD);

        InventoryPage inventoryPage = new InventoryPage(driver);

        Assert.assertTrue(driver.getCurrentUrl().contains("/inventory.html"),
                "Ожидался переход на /inventory.html, фактически URL: " + driver.getCurrentUrl());
        Assert.assertEquals(inventoryPage.getPageTitle(), "Products",
                "Ожидался заголовок каталога «Products»");
    }

    @Test(description = "Негативная авторизация locked_out_user: сообщение о блокировке")
    @Severity(SeverityLevel.NORMAL)
    public void testLockedOutUserLogin() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(SauceDemoConfig.LOCKED_OUT_USER, SauceDemoConfig.PASSWORD);

        Assert.assertEquals(loginPage.getErrorMessage(),
                "Epic sadface: Sorry, this user has been locked out.",
                "Ожидалось сообщение о блокировке пользователя");
        Assert.assertFalse(driver.getCurrentUrl().contains("/inventory.html"),
                "Заблокированный пользователь не должен попадать в каталог");
    }

    @Test(description = "Негативная авторизация с неверным паролем: сообщение о несоответствии данных")
    @Severity(SeverityLevel.NORMAL)
    public void testInvalidPasswordLogin() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(SauceDemoConfig.STANDARD_USER, "wrong_password");

        Assert.assertEquals(loginPage.getErrorMessage(),
                "Epic sadface: Username and password do not match any user in this service",
                "Ожидалось сообщение о несоответствии учётных данных");
    }
}

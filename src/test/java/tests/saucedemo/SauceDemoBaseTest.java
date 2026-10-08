package tests.saucedemo;

import pages.saucedemo.InventoryPage;
import pages.saucedemo.LoginPage;
import tests.BaseTest;
import utils.SauceDemoConfig;

/** Общая инфраструктура saucedemo-тестов: типовой вход под standard_user. */
public abstract class SauceDemoBaseTest extends BaseTest {

    protected InventoryPage loginAsStandardUser() {
        new LoginPage(driver).login(SauceDemoConfig.STANDARD_USER, SauceDemoConfig.PASSWORD);
        return new InventoryPage(driver);
    }
}

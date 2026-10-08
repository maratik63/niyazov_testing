package pages.saucedemo;

import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import pages.BasePage;
import utils.SauceDemoConfig;

/**
 * Страница авторизации Swag Labs (/).
 * <p>
 * Локаторы полей и кнопки — стабильные id-атрибуты: #user-name, #password, #login-button.
 */
public class LoginPage extends BasePage {

    private static final By USERNAME_FIELD = By.id("user-name");
    private static final By PASSWORD_FIELD = By.id("password");
    private static final By LOGIN_BUTTON = By.id("login-button");
    private static final By ERROR_MESSAGE = By.cssSelector("[data-test=error]");

    public LoginPage(WebDriver driver) {
        super(driver, SauceDemoConfig.EXPLICIT_TIMEOUT);
        openPage(SauceDemoConfig.BASE_URL);
        waitForVisibility(USERNAME_FIELD);
    }

    /**
     * Вход с переданными учётными данными. При успехе тест дальше создаёт InventoryPage —
     * его конструктор дожидается загрузки каталога.
     */
    @Step("Войти под пользователем {username}")
    public LoginPage login(String username, String password) {
        type(USERNAME_FIELD, username);
        type(PASSWORD_FIELD, password);
        click(LOGIN_BUTTON);
        return this;
    }

    /** Текст сообщения об ошибке входа («Epic sadface: …»). */
    @Step("Прочитать сообщение об ошибке входа")
    public String getErrorMessage() {
        return getText(ERROR_MESSAGE);
    }
}

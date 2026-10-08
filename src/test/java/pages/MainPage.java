package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import utils.ShareLaneConfig;

import java.util.List;

/**
 * Главная страница (main.py): вход, регистрация, товары, корзина.
 * <p>
 * Форма входа показывается только неавторизованному пользователю, а авторизованному —
 * приветствие "Hello &lt;имя&gt;". Признаком загруженной страницы выбрана ссылка на корзину:
 * она есть в шапке любой страницы сайта.
 */
public class MainPage extends BasePage {

    private static final By CART_LINK = By.linkText("Shopping Cart");
    private static final By LOGIN_EMAIL = By.name("email");
    private static final By LOGIN_PASSWORD = By.name("password");
    private static final By LOGIN_BUTTON = By.cssSelector("input[value='Login']");
    private static final By SIGN_UP_LINK = By.linkText("Sign up");
    private static final By USER_GREETING = By.cssSelector("span.user");
    private static final By ADD_TO_CART_LINK = By.cssSelector("a[href*='add_to_cart.py']");

    public MainPage(WebDriver driver) {
        super(driver);
    }

    public MainPage open() {
        openPage(ShareLaneConfig.MAIN_PAGE);
        waitForVisibility(CART_LINK);
        return this;
    }

    /** Вход с корректными данными: сайт показывает страницу-редирект и возвращает на main.py. */
    public MainPage login(String email, String password) {
        type(LOGIN_EMAIL, email);
        type(LOGIN_PASSWORD, password);
        click(LOGIN_BUTTON);
        wait.until(d -> {
            List<WebElement> greetings = d.findElements(USER_GREETING);
            return !greetings.isEmpty() && greetings.get(0).getText().startsWith("Hello");
        });
        return this;
    }

    /** Вход с неверными данными: остаёмся на странице входа с сообщением об ошибке. */
    public MainPage loginWithInvalidCredentials(String email, String password) {
        type(LOGIN_EMAIL, email);
        type(LOGIN_PASSWORD, password);
        click(LOGIN_BUTTON);
        waitForVisibility(MESSAGE);
        return this;
    }

    public RegisterPage openRegistration() {
        click(SIGN_UP_LINK);
        waitForUrlContains("register.py");
        return new RegisterPage(driver);
    }

    public CartPage openCart() {
        click(CART_LINK);
        waitForUrlContains("shopping_cart.py");
        return new CartPage(driver).waitForLoaded();
    }

    /** Добавление товара: карточка товара → "Add to Cart" → корзина. */
    public CartPage addBookToCart(int bookId) {
        openPage(ShareLaneConfig.showBookPage(bookId));
        waitForVisibility(ADD_TO_CART_LINK);
        click(ADD_TO_CART_LINK);
        waitForAnyVisible(MESSAGE, CART_LINK);
        return new CartPage(driver).open();
    }

    /**
     * Приветствие вида "Hello Ivan". У неавторизованного пользователя элемент есть, но пуст,
     * поэтому ожидание видимости здесь неприменимо.
     */
    public String getUserGreeting() {
        List<WebElement> greetings = driver.findElements(USER_GREETING);
        return greetings.isEmpty() ? "" : greetings.get(0).getText().trim();
    }

    public boolean isUserLoggedIn() {
        return getUserGreeting().startsWith("Hello");
    }
}

package pages.sharelane;

import pages.BasePage;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utils.ShareLaneConfig;
import utils.TestUser;

/**
 * Регистрация (register.py): два шага — ZIP-код, затем имя, фамилия, email и пароль.
 * <p>
 * Пароль, введённый пользователем, не сохраняется: сайт создаёт аккаунт с паролем {@code 1111}
 * и подменяет имя, фамилию и email на сгенерированные значения, которые показывает на странице
 * подтверждения.
 */
public class RegisterPage extends BasePage {

    private static final By ZIP_CODE_FIELD = By.name("zip_code");
    private static final By CONTINUE_BUTTON = By.cssSelector("input[value='Continue']");

    private static final By FIRST_NAME_FIELD = By.name("first_name");
    private static final By LAST_NAME_FIELD = By.name("last_name");
    private static final By EMAIL_FIELD = By.name("email");
    private static final By PASSWORD1_FIELD = By.name("password1");
    private static final By PASSWORD2_FIELD = By.name("password2");
    private static final By REGISTER_BUTTON = By.cssSelector("input[value='Register']");

    private static final By GENERATED_EMAIL = By.xpath("//tr[td[normalize-space()='Email']]/td/b");
    private static final By GENERATED_PASSWORD = By.xpath("//tr[td[normalize-space()='Password']]/td[2]");

    public RegisterPage(WebDriver driver) {
        super(driver);
    }

    public RegisterPage open() {
        openPage(ShareLaneConfig.REGISTER_PAGE);
        waitForVisibility(ZIP_CODE_FIELD);
        return this;
    }

    /** Регистрация пользователя: оба шага формы. */
    public RegisterPage register(TestUser user) {
        type(ZIP_CODE_FIELD, user.zipCode());
        click(CONTINUE_BUTTON);
        // Валидный ZIP открывает второй шаг, невалидный — сообщение об ошибке на первом.
        waitForAnyVisible(FIRST_NAME_FIELD, MESSAGE);

        type(FIRST_NAME_FIELD, user.firstName());
        type(LAST_NAME_FIELD, user.lastName());
        type(EMAIL_FIELD, user.registrationEmail());
        type(PASSWORD1_FIELD, ShareLaneConfig.REGISTRATION_PASSWORD);
        type(PASSWORD2_FIELD, ShareLaneConfig.REGISTRATION_PASSWORD);
        click(REGISTER_BUTTON);
        // Успех — страница подтверждения с выданным email, ошибка — та же форма и сообщение.
        waitForAnyVisible(GENERATED_EMAIL, MESSAGE);
        return this;
    }

    public boolean isAccountCreated() {
        return getMessage().equals("Account is created!");
    }

    /** Email, который сайт сгенерировал вместо введённого (им и нужно входить). */
    public String getGeneratedEmail() {
        waitForVisibility(GENERATED_EMAIL);
        return getText(GENERATED_EMAIL);
    }

    /** Пароль, выданный сайтом (всегда 1111). */
    public String getGeneratedPassword() {
        waitForVisibility(GENERATED_PASSWORD);
        return getText(GENERATED_PASSWORD);
    }
}

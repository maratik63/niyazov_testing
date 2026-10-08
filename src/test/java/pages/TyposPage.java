package pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import utils.HeroKuAppConfig;

/**
 * Typos (https://the-internet.herokuapp.com/typos).
 * <p>
 * Страница намеренно нестабильна: при каждой загрузке сайт случайно вносит опечатку
 * в слово "won't" (появляется "won,t"), поэтому тест проверяет допустимые варианты текста.
 */
public class TyposPage extends BasePage {

    private static final By PARAGRAPHS = By.tagName("p");

    public TyposPage(WebDriver driver) {
        super(driver);
        openPage(HeroKuAppConfig.TYPOS_PAGE);
        waitForVisibility(PARAGRAPHS);
    }

    /** Первый абзац — описание страницы, всегда одинаковый. */
    public String getFirstParagraphText() {
        return elementAt(PARAGRAPHS, 0).getText().trim();
    }

    /** Второй абзац — тот самый текст, в который сайт случайно вносит опечатку. */
    public String getSecondParagraphText() {
        return elementAt(PARAGRAPHS, 1).getText().trim();
    }
}

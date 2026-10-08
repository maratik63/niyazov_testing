package tests.herokuapp;

import tests.BaseTest;

import org.testng.Assert;
import org.testng.annotations.Test;
import pages.herokuapp.TyposPage;

/**
 * Typos: проверка орфографии абзацев.
 * <p>
 * Сайт намеренно вносит опечатку в слово "won't" случайным образом при каждой загрузке
 * ("other times you won,t"), поэтому для второго абзаца допустимы два варианта текста.
 */
public class TyposTests extends BaseTest {

    private static final String INTRO =
            "This example demonstrates a typo being introduced. It does it randomly on each page load.";
    private static final String PARAGRAPH_WITHOUT_TYPO =
            "Sometimes you'll see a typo, other times you won't.";
    private static final String PARAGRAPH_WITH_TYPO =
            "Sometimes you'll see a typo, other times you won,t.";

    @Test(description = "Первый абзац всегда написан без ошибок")
    public void introParagraphHasNoTypos() {
        TyposPage page = new TyposPage(driver);

        Assert.assertEquals(page.getFirstParagraphText(), INTRO,
                "Текст первого абзаца должен совпадать с ожидаемым");
    }

    @Test(description = "Второй абзац соответствует одному из допустимых вариантов (с опечаткой или без)")
    public void secondParagraphMatchesAllowedVariant() {
        TyposPage page = new TyposPage(driver);

        String text = page.getSecondParagraphText();
        boolean typoFound = text.equals(PARAGRAPH_WITH_TYPO);

        System.out.println("Второй абзац: " + text);
        System.out.println(typoFound ? "Сайт показал вариант с опечаткой «won,t»" : "Опечатки на этой загрузке нет");

        Assert.assertTrue(text.equals(PARAGRAPH_WITHOUT_TYPO) || typoFound,
                "Ожидался один из двух вариантов текста, получено: " + text);
    }
}

package pages.herokuapp;

import pages.BasePage;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import utils.HeroKuAppConfig;

import java.time.Duration;

/**
 * Add/Remove Elements (https://the-internet.herokuapp.com/add_remove_elements/).
 * <p>
 * Локаторы из задания: {@code //button[text()='Add Element']} и {@code //button[text()='Delete']}.
 * Кнопка "Add Element" добавляет элемент с кнопкой "Delete"; когда удалён последний элемент,
 * кнопок Delete на странице не остаётся.
 */
public class AddRemoveElementsPage extends BasePage {

    private static final By ADD_ELEMENT_BUTTON = By.xpath("//button[text()='Add Element']");
    private static final By DELETE_BUTTON = By.xpath("//button[text()='Delete']");

    /** Сколько раз пробуем перезагрузить страницу, если jQuery не подключилась. */
    private static final int LOAD_ATTEMPTS = 3;
    private static final Duration JQUERY_TIMEOUT = Duration.ofSeconds(10);

    public AddRemoveElementsPage(WebDriver driver) {
        super(driver);
        openWithJQuery();
    }

    /** Добавить элемент: ждём, что кнопок Delete стало больше. */
    public AddRemoveElementsPage addElement() {
        int before = getDeleteButtonsCount();
        clickUntilEffect(ADD_ELEMENT_BUTTON, d -> d.findElements(DELETE_BUTTON).size() > before);
        return this;
    }

    /** Удалить первый элемент: ждём, что кнопок Delete стало меньше. */
    public AddRemoveElementsPage deleteElement() {
        int before = getDeleteButtonsCount();
        clickUntilEffect(DELETE_BUTTON, d -> d.findElements(DELETE_BUTTON).size() < before);
        return this;
    }

    /** Количество кнопок Delete — столько элементов сейчас на странице. */
    public int getDeleteButtonsCount() {
        return countOf(DELETE_BUTTON);
    }

    public boolean isDeleteButtonDisplayed() {
        return isVisible(DELETE_BUTTON);
    }

    /**
     * Открывает страницу и проверяет, что jQuery подключилась: кнопки Add/Delete работают через неё
     * (библиотека отдаётся с того же хоста). Если скрипт не загрузился из-за сбоя сети,
     * страница перезагружается, иначе клики по кнопкам ничего не делают.
     */
    private void openWithJQuery() {
        WebDriverWait jQueryWait = new WebDriverWait(driver, JQUERY_TIMEOUT);
        for (int attempt = 1; attempt <= LOAD_ATTEMPTS; attempt++) {
            openPage(HeroKuAppConfig.ADD_REMOVE_ELEMENTS_PAGE);
            waitForVisibility(ADD_ELEMENT_BUTTON);
            try {
                jQueryWait.until(d -> "function".equals(
                        ((JavascriptExecutor) d).executeScript("return typeof window.jQuery")));
                return;
            } catch (TimeoutException e) {
                if (attempt == LOAD_ATTEMPTS) {
                    throw new TimeoutException("За " + LOAD_ATTEMPTS + " попытки страница открылась "
                            + "без jQuery — кнопки Add/Remove не работают", e);
                }
            }
        }
    }
}

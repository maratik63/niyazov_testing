package utils;

/**
 * Адреса страниц и тестовые данные сайта ShareLane (все CGI-скрипты лежат в /cgi-bin/).
 */
public final class ShareLaneConfig {

    public static final String BASE_URL = "https://sharelane.com/cgi-bin/";

    public static final String MAIN_PAGE = BASE_URL + "main.py";
    public static final String REGISTER_PAGE = BASE_URL + "register.py";
    public static final String SHOPPING_CART_PAGE = BASE_URL + "shopping_cart.py";
    public static final String CREDIT_CARD_GENERATOR_PAGE = BASE_URL + "get_credit_card.py";
    public static final String BALANCE_VIEWER_PAGE = BASE_URL + "view_balance.py";

    /** Товар, на котором построены проверки: "The Analects of Confucius" за $10.00. */
    public static final int BOOK_ID = 6;
    public static final String BOOK_TITLE = "The Analects of Confucius";
    public static final String BOOK_PRICE = "10.00";

    /**
     * Сумма в корзине за одну книгу. Дефект сайта: скидка не вычитается, а прибавляется
     * к стоимости (10.00 + 7 %), поэтому в проверке зафиксировано фактическое значение.
     */
    public static final String BOOK_TOTAL = "10.70";

    /** Пароль, который сайт выдаёт всем новым аккаунтам. */
    public static final String DEFAULT_PASSWORD = "1111";

    /** Пароль, который вводится в форме регистрации (в БД он не сохраняется). */
    public static final String REGISTRATION_PASSWORD = "qwerty123";

    private ShareLaneConfig() {
    }

    public static String showBookPage(int bookId) {
        return BASE_URL + "show_book.py?book_id=" + bookId;
    }
}

package pages.saucedemo;

/** Опции сортировки каталога Swag Labs (значения value выпадающего списка). */
public enum SortOption {
    NAME_ASC("az"),
    NAME_DESC("za"),
    PRICE_LOW_TO_HIGH("lohi"),
    PRICE_HIGH_TO_LOW("hilo");

    private final String value;

    SortOption(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

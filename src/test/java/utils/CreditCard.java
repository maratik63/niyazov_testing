package utils;

/**
 * Тестовая банковская карта, которую ShareLane выдаёт на странице
 * Test Portal → Credit Card Generator (get_credit_card.py).
 * <p>
 * Карты виртуальные и хранятся в БД сайта: у каждой есть баланс, который уменьшается
 * после оплаты заказа. Удаляются вместе с остальными данными каждый час в 00 минут.
 */
public class CreditCard {

    private final String type;
    private final String number;
    private final String expirationMonth;
    private final String expirationYear;
    private final String cvv2;
    private final double balance;

    public CreditCard(String type, String number, String expirationMonth,
                      String expirationYear, String cvv2, double balance) {
        this.type = type;
        this.number = number;
        this.expirationMonth = expirationMonth;
        this.expirationYear = expirationYear;
        this.cvv2 = cvv2;
        this.balance = balance;
    }

    public String type() {
        return type;
    }

    public String number() {
        return number;
    }

    public String expirationMonth() {
        return expirationMonth;
    }

    public String expirationYear() {
        return expirationYear;
    }

    public String cvv2() {
        return cvv2;
    }

    public double balance() {
        return balance;
    }

    @Override
    public String toString() {
        return type + " " + number + ", срок " + expirationMonth + "/" + expirationYear
                + ", CVV2 " + cvv2 + ", баланс $" + balance;
    }
}

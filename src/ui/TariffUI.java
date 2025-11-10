package ui;

public class TariffUI {

    /**
     * Метод перевіряє значення тарифу для валідації.
     * Тариф має перебувати в межах від 1.0 до 2.0 включно.
     *
     * @param tariffString Значення тарифу у вигляді рядка
     * @return true, якщо значення валідне
     * @throws IllegalArgumentException якщо тариф:
     *                                  - менший за 1.0
     *                                  - більший за 2.0
     *                                  - не є числовим
     */
    public boolean validateTariff(String tariffString) {
        try {
            double tariff = Double.parseDouble(tariffString);

            // Якщо тариф менший за мінімально допустиме значення
            if (tariff < 1.0) {
                throw new IllegalArgumentException("Тариф повинен бути більшим за 1");
            }

            // Якщо тариф більший за максимально допустиме значення
            if (tariff > 2.0) {
                throw new IllegalArgumentException("Тариф повинен бути меншим за 2");
            }

            return true; // Тариф валідний
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Тариф повинен бути числовим значенням", e);
        }
    }
}
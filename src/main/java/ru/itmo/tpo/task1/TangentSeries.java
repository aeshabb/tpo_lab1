package ru.itmo.tpo.task1;

/**
 * Вычисление tg(x) через разложение в степенной ряд (ряд Тейлора).
 *
 * tg(x) = Σ (от n=1 до ∞) [ B_{2n} * (-4)^n * (1 - 4^n) / (2n)! ] * x^{2n-1}
 *
 * Где B_{2n} — числа Бернулли.
 *
 * Используем известное разложение:
 * tan(x) = x + x^3/3 + 2x^5/15 + 17x^7/315 + 62x^9/2835 + ...
 *
 * Коэффициенты вычисляются через числа Бернулли.
 * Ряд сходится при |x| < π/2.
 */
public class TangentSeries {

    /**
     * Предвычисленные коэффициенты разложения tg(x) в ряд Тейлора.
     * tan(x) = sum_{n=0}^{N} coefficients[n] * x^(2n+1)
     *
     * Коэффициенты: 1, 1/3, 2/15, 17/315, 62/2835, 1382/155925,
     *               21844/6081075, 929569/638512875, ...
     */
    private static final double[] COEFFICIENTS = {
            1.0,                        // x
            1.0 / 3.0,                  // x^3 / 3
            2.0 / 15.0,                 // 2x^5 / 15
            17.0 / 315.0,              // 17x^7 / 315
            62.0 / 2835.0,             // 62x^9 / 2835
            1382.0 / 155925.0,         // 1382x^11 / 155925
            21844.0 / 6081075.0,       // 21844x^13 / 6081075
            929569.0 / 638512875.0,    // 929569x^15 / 638512875
            6404582.0 / 10854718875.0, // x^17
            443861162.0 / 1856156927625.0,  // x^19
            18888466084.0 / 194896477400625.0, // x^21
    };

    public static double compute(double x, int terms) {
        if (terms < 1 || terms > COEFFICIENTS.length) {
            throw new IllegalArgumentException(
                    "Количество членов ряда должно быть от 1 до " + COEFFICIENTS.length);
        }

        double normalized = normalizeAngle(x);

        if (Math.abs(normalized) >= Math.PI / 2 - 1e-10) {
            throw new ArithmeticException(
                    "Значение x слишком близко к π/2 + πn, tg(x) не определён");
        }

        double result = 0.0;
        double xPow = normalized; // x^(2n+1), начинаем с x^1

        for (int n = 0; n < terms; n++) {
            result += COEFFICIENTS[n] * xPow;
            xPow *= normalized * normalized;
        }

        return result;
    }

    public static double compute(double x) {
        return compute(x, COEFFICIENTS.length);
    }


    static double normalizeAngle(double x) {
        double result = x % Math.PI;
        if (result > Math.PI / 2) {
            result -= Math.PI;
        } else if (result < -Math.PI / 2) {
            result += Math.PI;
        }
        return result;
    }

    /**
     * Возвращает максимальное количество доступных членов ряда.
     */
    public static int getMaxTerms() {
        return COEFFICIENTS.length;
    }
}

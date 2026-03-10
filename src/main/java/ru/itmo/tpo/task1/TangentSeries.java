package ru.itmo.tpo.task1;

/**
 * Вычисление tg(x) как отношения рядов Тейлора для sin(x) и cos(x).
 *
 * sin(x) = x - x^3/3! + x^5/5! - ...
 * cos(x) = 1 - x^2/2! + x^4/4! - ...
 * tg(x) = sin(x) / cos(x)
 *
 * Ряды sin(x) и cos(x) вычисляются после нормализации угла к интервалу (-π/2, π/2).
 */
public class TangentSeries {

    private static final int MAX_TERMS = 20;
    private static final double UNDEFINED_EPSILON = 1e-10;

    public static double compute(double x, int terms) {
        if (terms < 1 || terms > MAX_TERMS) {
            throw new IllegalArgumentException(
                    "Количество членов ряда должно быть от 1 до " + MAX_TERMS);
        }

        double normalized = normalizeAngle(x);

        if (Math.abs(normalized) >= Math.PI / 2 - UNDEFINED_EPSILON) {
            throw new ArithmeticException(
                    "Значение x слишком близко к π/2 + πn, tg(x) не определён");
        }

        double sine = sineSeries(normalized, terms);
        double cosine = cosineSeries(normalized, terms);

        if (Math.abs(cosine) < UNDEFINED_EPSILON) {
            throw new ArithmeticException(
                    "Значение x слишком близко к π/2 + πn, tg(x) не определён");
        }

        return sine / cosine;
    }

    public static double compute(double x) {
        return compute(x, MAX_TERMS);
    }

    private static double sineSeries(double x, int terms) {
        double sum = 0.0;
        double term = x;

        for (int n = 0; n < terms; n++) {
            sum += term;
            term *= -x * x / ((2.0 * n + 2.0) * (2.0 * n + 3.0));
        }

        return sum;
    }

    private static double cosineSeries(double x, int terms) {
        double sum = 0.0;
        double term = 1.0;

        for (int n = 0; n < terms; n++) {
            sum += term;
            term *= -x * x / ((2.0 * n + 1.0) * (2.0 * n + 2.0));
        }

        return sum;
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
        return MAX_TERMS;
    }
}

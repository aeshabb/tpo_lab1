package ru.itmo.tpo.task1;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Модульные тесты для вычисления tg(x) через ряды Тейлора sin(x) и cos(x).
 *
 * Тестовое покрытие:
 * 1. Точные значения в характерных точках (0, π/4, π/6, π/3, и т.д.)
 * 2. Отрицательные значения (проверка нечётности функции)
 * 3. Граничные значения (около 0 и близко к ±π/2)
 * 4. Сходимость ряда (увеличение количества членов)
 * 5. Обработка ошибок (невалидные параметры)
 * 6. Периодичность функции
 */
@DisplayName("Тесты tg(x) через ряды Тейлора sin(x) и cos(x)")
class TangentSeriesTest {

    private static final double EPSILON = 1e-6;
    private static final double COARSE_EPSILON = 1e-3;
    private static final double NEAR_ASYMPTOTE_DELTA = 1e-3;

    @Nested
    @DisplayName("Значения в характерных точках")
    class CharacteristicPointsTest {

        @Test
        @DisplayName("tg(0) = 0")
        void testZero() {
            assertEquals(0.0, TangentSeries.compute(0.0));
        }

        @Test
        @DisplayName("tg(π/4) = 1")
        void testPiOver4() {
            assertEquals(1.0, TangentSeries.compute(Math.PI / 4), COARSE_EPSILON);
        }

        @Test
        @DisplayName("tg(-π/4) = -1")
        void testNegativePiOver4() {
            assertEquals(-1.0, TangentSeries.compute(-Math.PI / 4), COARSE_EPSILON);
        }

        @Test
        @DisplayName("tg(π/6) = 1/√3 ≈ 0.5774")
        void testPiOver6() {
            double expected = 1.0 / Math.sqrt(3);
            assertEquals(expected, TangentSeries.compute(Math.PI / 6), EPSILON);
        }

        @Test
        @DisplayName("tg(π/3) = √3 ≈ 1.7321")
        void testPiOver3() {
            double expected = Math.sqrt(3);
            assertEquals(expected, TangentSeries.compute(Math.PI / 3), COARSE_EPSILON);
        }

        @Test
        @DisplayName("tg(-π/3) = -√3 ≈ -1.7321")
        void testNegativePiOver3() {
            double expected = -Math.sqrt(3);
            assertEquals(expected, TangentSeries.compute(-Math.PI / 3), COARSE_EPSILON);
        }

        @Test
        @DisplayName("tg(-π/6) = -1/√3")
        void testNegativePiOver6() {
            double expected = -1.0 / Math.sqrt(3);
            assertEquals(expected, TangentSeries.compute(-Math.PI / 6), EPSILON);
        }
    }

    @Nested
    @DisplayName("Свойства функции tg(x)")
    class FunctionPropertiesTest {

        @Test
        @DisplayName("tg(x) — нечётная функция: tg(-x) = -tg(x)")
        void testOddFunction() {
            double[] testValues = {
                    1e-6,
                    Math.PI / 12,
                    Math.PI / 6,
                    Math.PI / 4,
                    Math.PI / 2 - NEAR_ASYMPTOTE_DELTA
            };
            for (double x : testValues) {
                double tanPositive = TangentSeries.compute(x);
                double tanNegative = TangentSeries.compute(-x);
                assertEquals(-tanPositive, tanNegative, EPSILON,
                        "tg(-" + x + ") должно быть равно -tg(" + x + ")");
            }
        }

        @ParameterizedTest
        @DisplayName("Малые значения: tg(x) ≈ x при x → 0")
        @ValueSource(doubles = {-0.01, 0.01})
        void testSmallValues(double x) {
            double result = TangentSeries.compute(x, 1);
            assertEquals(x, result, 1e-6,
                    "При малых x, tg(x) ≈ x (линейное приближение)");
        }
    }

    @Nested
    @DisplayName("Сходимость ряда")
    class ConvergenceTest {

        @Test
        @DisplayName("Увеличение количества членов повышает точность")
        void testConvergenceWithMoreTerms() {
            double x = Math.PI / 6;
            double expected = Math.tan(x);
            double prevError = Double.MAX_VALUE;

            for (int terms = 1; terms <= TangentSeries.getMaxTerms(); terms++) {
                double result = TangentSeries.compute(x, terms);
                double error = Math.abs(result - expected);
                assertTrue(error <= prevError + 1e-15,
                        "Ошибка должна уменьшаться (или не увеличиваться) при увеличении членов ряда." +
                                " terms=" + terms + ", error=" + error + ", prevError=" + prevError);
                prevError = error;
            }
        }

        @Test
        @DisplayName("Один член ряда: tg(x) ≈ x")
        void testOneTermApproximation() {
            double x = Math.PI / 12;
            double result = TangentSeries.compute(x, 1);
            assertEquals(x, result, 1e-10, "С одним членом ряда tg(x) ≈ x");
        }

        @Test
        @DisplayName("Два члена ряда: tg(x) ≈ x + x³/3")
        void testTwoTermApproximation() {
            double x = Math.PI / 12;
            double expected = (x - Math.pow(x, 3) / 6.0) / (1.0 - Math.pow(x, 2) / 2.0);
            double result = TangentSeries.compute(x, 2);
            assertEquals(expected, result, 1e-10);
        }

        @Test
        @DisplayName("Три члена ряда: tg(x) как отношение полиномов sin/cos")
        void testThreeTermApproximation() {
            double x = Math.PI / 12;
            double expected = (x - Math.pow(x, 3) / 6.0 + Math.pow(x, 5) / 120.0)
                    / (1.0 - Math.pow(x, 2) / 2.0 + Math.pow(x, 4) / 24.0);
            double result = TangentSeries.compute(x, 3);
            assertEquals(expected, result, 1e-10);
        }

    }

    @Nested
    @DisplayName("Граничные значения и обработка ошибок")
    class BoundaryAndErrorTest {

        @Test
        @DisplayName("Исключение при x = π/2 (tg не определён)")
        void testPiOver2ThrowsException() {
            assertThrows(ArithmeticException.class,
                    () -> TangentSeries.compute(Math.PI / 2));
        }

        @Test
        @DisplayName("Исключение при x = -π/2")
        void testNegativePiOver2ThrowsException() {
            assertThrows(ArithmeticException.class,
                    () -> TangentSeries.compute(-Math.PI / 2));
        }

        @Test
        @DisplayName("Исключение при x = 3π/2 (приводится к π/2)")
        void testThreePiOver2ThrowsException() {
            assertThrows(ArithmeticException.class,
                    () -> TangentSeries.compute(3 * Math.PI / 2));
        }

        @Test
        @DisplayName("Значение, близкое к π/2, ещё вычисляется")
        void testNearPiOver2StillComputes() {
            double x = Math.PI / 2 - NEAR_ASYMPTOTE_DELTA;
            assertEquals(Math.tan(x), TangentSeries.compute(x), 1e-2);
        }

        @Test
        @DisplayName("Исключение при невалидном количестве членов (0)")
        void testZeroTermsThrowsException() {
            assertThrows(IllegalArgumentException.class,
                    () -> TangentSeries.compute(0.5, 0));
        }

        @Test
        @DisplayName("Исключение при невалидном количестве членов (отрицательное)")
        void testNegativeTermsThrowsException() {
            assertThrows(IllegalArgumentException.class,
                    () -> TangentSeries.compute(0.5, -1));
        }

        @Test
        @DisplayName("Исключение при слишком большом количестве членов")
        void testTooManyTermsThrowsException() {
            assertThrows(IllegalArgumentException.class,
                    () -> TangentSeries.compute(0.5, TangentSeries.getMaxTerms() + 1));
        }

        @Test
        @DisplayName("Исключение при x = NaN")
        void testNaNThrowsException() {
            assertThrows(IllegalArgumentException.class,
                    () -> TangentSeries.compute(Double.NaN));
        }

        @Test
        @DisplayName("Исключение при x = +Infinity")
        void testPositiveInfinityThrowsException() {
            assertThrows(IllegalArgumentException.class,
                    () -> TangentSeries.compute(Double.POSITIVE_INFINITY));
        }
    }

    @Nested
    @DisplayName("Периодичность и нормализация угла")
    class PeriodicityTest {

        @Test
        @DisplayName("tg(x + π) = tg(x) — периодичность")
        void testPeriodicity() {
            double x = Math.PI / 6;
            double result1 = TangentSeries.compute(x);
            double result2 = TangentSeries.compute(x + Math.PI);
            assertEquals(result1, result2, COARSE_EPSILON,
                    "tg(x) должен иметь период π");
        }

        @Test
        @DisplayName("tg(x + 2π) = tg(x)")
        void testPeriodicity2Pi() {
            double x = Math.PI / 4;
            double result1 = TangentSeries.compute(x);
            double result2 = TangentSeries.compute(x + 2 * Math.PI);
            assertEquals(result1, result2, COARSE_EPSILON);
        }

        @Test
        @DisplayName("Нормализация большого положительного угла")
        void testLargePositiveAngle() {
            double x = 0.3 + 10 * Math.PI;
            double expected = Math.tan(0.3);
            assertEquals(expected, TangentSeries.compute(x), COARSE_EPSILON);
        }

        @Test
        @DisplayName("Нормализация большого отрицательного угла")
        void testLargeNegativeAngle() {
            double x = -0.3 - 10 * Math.PI;
            double expected = Math.tan(-0.3);
            assertEquals(expected, TangentSeries.compute(x), COARSE_EPSILON);
        }
    }
}

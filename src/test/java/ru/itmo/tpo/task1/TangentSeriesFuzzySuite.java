package ru.itmo.tpo.task1;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.SplittableRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Отдельный fuzzy-набор для tg(x)")
class TangentSeriesFuzzySuite {

    private static final int ITERATIONS = 5_000;
    private static final long SEED = 42L;
    private static final double MIN_X = -1_000.0;
    private static final double MAX_X = 1_000.0;
    private static final double ASYMPTOTE_GAP = 1e-4;

    @Test
    @DisplayName("Fuzzy: сравнение с Math.tan() на случайных допустимых значениях")
    void matchesMathTanOnRandomValues() {
        SplittableRandom random = new SplittableRandom(SEED);

        for (int i = 0; i < ITERATIONS; i++) {
            double x = generateInput(random);
            double expected = Math.tan(x);
            double actual = TangentSeries.compute(x);

            assertEquals(expected, actual, toleranceFor(x), "x=" + x);
        }
    }

    private static double generateInput(SplittableRandom random) {
        while (true) {
            double x = random.nextDouble(MIN_X, MAX_X);
            double distanceToAsymptote = Math.PI / 2 - Math.abs(TangentSeries.normalizeAngle(x));
            if (distanceToAsymptote > ASYMPTOTE_GAP) {
                return x;
            }
        }
    }

    private static double toleranceFor(double x) {
        double normalizedDistance = Math.PI / 2 - Math.abs(TangentSeries.normalizeAngle(x));
        return normalizedDistance < 1e-2 ? 1e-4 : 1e-6;
    }
}

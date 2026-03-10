package ru.itmo.tpo.task1;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.SplittableRandom;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Отдельный fuzzy-набор для tg(x)")
class TangentSeriesFuzzySuite {

    private static final int ITERATIONS = 2_000;
    private static final long SEED = 42L;

    @Test
    @DisplayName("Fuzzy: сравнение с Math.tan() на значимых областях")
    void matchesMathTanOnMeaningfulRanges() {
        SplittableRandom random = new SplittableRandom(SEED);

        for (int i = 0; i < ITERATIONS; i++) {
            double x = generateInput(random);
            double expected = Math.tan(x);
            double actual = TangentSeries.compute(x);

            assertEquals(expected, actual, toleranceFor(x), "x=" + x);
        }
    }

    private static double generateInput(SplittableRandom random) {
        int zone = random.nextInt(6);

        return switch (zone) {
            case 0 -> random.nextDouble(-1e-3, 1e-3);
            case 1 -> around(random, Math.PI / 6, 1e-3);
            case 2 -> around(random, Math.PI / 4, 1e-3);
            case 3 -> around(random, Math.PI / 3, 1e-3);
            case 4 -> nearAsymptote(random, 1.0);
            default -> nearAsymptote(random, -1.0);
        };
    }

    private static double around(SplittableRandom random, double center, double radius) {
        double signedCenter = random.nextBoolean() ? center : -center;
        int periodShift = random.nextInt(-3, 4);
        double offset = random.nextDouble(-radius, radius);
        return signedCenter + offset + periodShift * Math.PI;
    }

    private static double nearAsymptote(SplittableRandom random, double sign) {
        double delta = Math.pow(10.0, -random.nextDouble(2.0, 5.0));
        int periodShift = random.nextInt(-3, 4);
        return sign * (Math.PI / 2 - delta) + periodShift * Math.PI;
    }

    private static double toleranceFor(double x) {
        double normalizedDistance = Math.PI / 2 - Math.abs(TangentSeries.normalizeAngle(x));
        return normalizedDistance < 1e-3 ? 1e-2 : 1e-6;
    }
}

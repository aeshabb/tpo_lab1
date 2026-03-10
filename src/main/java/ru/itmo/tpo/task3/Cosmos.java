package ru.itmo.tpo.task3;

/**
 * Космос — почти бескрайние просторы.
 * Слова Артура путешествуют через космос.
 */
public class Cosmos {
    private final boolean almostBoundless;
    private final double spaceCurvatureFactor;

    public Cosmos() {
        this.almostBoundless = true;
        this.spaceCurvatureFactor = 42.0;
    }

    public boolean isAlmostBoundless() {
        return almostBoundless;
    }

    public double getSpaceCurvatureFactor() {
        return spaceCurvatureFactor;
    }

    /**
     * Эффективное расстояние с учётом искривления пространства.
     */
    public double calculateEffectiveDistance(Galaxy galaxy) {
        return galaxy.distanceFromOrigin() / spaceCurvatureFactor;
    }

    @Override
    public String toString() {
        return "Cosmos{almostBoundless=" + almostBoundless + "}";
    }
}

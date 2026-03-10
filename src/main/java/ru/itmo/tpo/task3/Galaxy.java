package ru.itmo.tpo.task3;

import java.util.ArrayList;
import java.util.List;

/**
 * Далёкая галактика, где обитают странные воинственные существа.
 */
public class Galaxy {
    private final String name;
    private final boolean distant; // далёкая
    private final List<WarlikeCreature> inhabitants;
    private InterstellarWar pendingWar;
    private final double x;
    private final double y;
    private final double z;

    public Galaxy(String name, boolean distant) {
        this(name, distant, distant ? 1200.0 : 0.0, distant ? 900.0 : 0.0, distant ? 400.0 : 0.0);
    }

    public Galaxy(String name, boolean distant, double x, double y, double z) {
        this.name = name;
        this.distant = distant;
        this.inhabitants = new ArrayList<>();
        this.pendingWar = null;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public String getName() {
        return name;
    }

    public boolean isDistant() {
        return distant;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    /**
     * Вычисляет евклидово расстояние от начала координат до галактики.
     */
    public double distanceFromOrigin() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public void addInhabitant(WarlikeCreature creature) {
        inhabitants.add(creature);
    }

    public List<WarlikeCreature> getInhabitants() {
        return new ArrayList<>(inhabitants);
    }

    public boolean hasWarlikeCreatures() {
        return !inhabitants.isEmpty();
    }

    /**
     * Устанавливает межзвёздную войну, на грани которой балансируют существа.
     */
    public void setPendingWar(InterstellarWar war) {
        this.pendingWar = war;
    }

    public InterstellarWar getPendingWar() {
        return pendingWar;
    }

    /**
     * Проверяет, балансирует ли галактика на грани ужасной межзвёздной войны.
     */
    public boolean isOnBrinkOfWar() {
        return pendingWar != null && !pendingWar.isStarted();
    }

    @Override
    public String toString() {
        return "Galaxy{name='" + name + "', distant=" + distant +
                ", inhabitants=" + inhabitants.size() + "}";
    }
}

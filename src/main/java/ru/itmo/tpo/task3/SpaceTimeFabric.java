package ru.itmo.tpo.task3;

/**
 * Ткань пространства-времени (Space-Time Fabric).
 * Может содержать случайные дыры.
 */
public class SpaceTimeFabric {

    /**
     * Случайная дыра в ткани пространства-времени.
     * Открывается в определённый момент и может переносить объекты.
     */
    private SpaceTimeHole currentHole;

    /**
     * Открывает случайную дыру в ткани пространства-времени.
     *
     * @param destination галактика назначения
     * @return открывшаяся дыра
     */
    public SpaceTimeHole openRandomHole(Galaxy destination) {
        this.currentHole = new SpaceTimeHole(destination);
        return currentHole;
    }

    /**
     * Закрывает текущую дыру.
     */
    public void closeHole() {
        if (currentHole != null) {
            currentHole.close();
            currentHole = null;
        }
    }

    public boolean hasOpenHole() {
        return currentHole != null && currentHole.isOpen();
    }

    public SpaceTimeHole getCurrentHole() {
        return currentHole;
    }
}

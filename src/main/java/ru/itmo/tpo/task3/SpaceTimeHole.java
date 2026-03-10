package ru.itmo.tpo.task3;

/**
 * Дыра в ткани пространства-времени.
 * Может переносить слова далеко во времени и пространстве.
 */
public class SpaceTimeHole {
    private final Galaxy destination;
    private boolean open;
    private static final double BASE_TRANSFER_TIME = 0.5;

    public SpaceTimeHole(Galaxy destination) {
        this.destination = destination;
        this.open = true;
    }

    public boolean isOpen() {
        return open;
    }

    public void close() {
        this.open = false;
    }

    /**
     * Переносит слова через дыру к месту назначения — далёкой галактике.
     */
    public void transport(Words words) {
        if (!open) {
            throw new IllegalStateException("Дыра в пространстве-времени уже закрыта");
        }
        words.transportTo(destination);
    }

    /**
     * Вычисляет длительность переноса слов через дыру.
     * Чем длиннее фраза, тем немного больше длительность.
     */
    public double calculateTransferTime(Words words) {
        return BASE_TRANSFER_TIME + words.getCharacterCount() / 100.0;
    }

    /**
     * Эффективная скорость переноса слов с учётом искривления пространства.
     */
    public double calculateEffectiveSpeed(Words words, Cosmos cosmos) {
        double distance = cosmos.calculateEffectiveDistance(destination);
        double time = calculateTransferTime(words);
        return distance / time;
    }

    public void transport(Words words, Cosmos cosmos) {
        transport(words);
        double time = calculateTransferTime(words);
        double speed = calculateEffectiveSpeed(words, cosmos);
        words.recordTravelMetrics(time, speed);
    }

    public Galaxy getDestination() {
        return destination;
    }

    @Override
    public String toString() {
        return "SpaceTimeHole{destination=" + destination + ", open=" + open + "}";
    }
}

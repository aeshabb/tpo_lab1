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
     * Переносит слова через дыру к месту назначения - далёкой галактике.
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
    public double calculateTransferTime(Words words, double characterTransferRate) {
        if (characterTransferRate <= 0) {
            throw new IllegalArgumentException("Скорость передачи символов должна быть положительной");
        }
        return BASE_TRANSFER_TIME + words.getCharacterCount() / characterTransferRate;
    }

    /**
     * Эффективная скорость переноса слов с учётом искривления пространства.
     */
    public double calculateEffectiveSpeed(Words words, Cosmos cosmos, double characterTransferRate) {
        double distance = cosmos.calculateEffectiveDistance(destination);
        double time = calculateTransferTime(words, characterTransferRate);
        return distance / time;
    }

    public void transport(Words words, Cosmos cosmos, double characterTransferRate) {
        transport(words);
        double time = calculateTransferTime(words, characterTransferRate);
        double speed = calculateEffectiveSpeed(words, cosmos, characterTransferRate);
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

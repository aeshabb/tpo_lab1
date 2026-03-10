package ru.itmo.tpo.task3;

/**
 * Дыра в ткани пространства-времени.
 * Может переносить слова далеко во времени и пространстве.
 */
public class SpaceTimeHole {
    private final Galaxy destination;
    private boolean open;

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

    public Galaxy getDestination() {
        return destination;
    }

    @Override
    public String toString() {
        return "SpaceTimeHole{destination=" + destination + ", open=" + open + "}";
    }
}

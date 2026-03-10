package ru.itmo.tpo.task3;

/**
 * Космос — почти бескрайние просторы.
 * Слова Артура путешествуют через космос.
 */
public class Cosmos {
    private final boolean almostBoundless;

    public Cosmos() {
        this.almostBoundless = true;
    }

    public boolean isAlmostBoundless() {
        return almostBoundless;
    }

    @Override
    public String toString() {
        return "Cosmos{almostBoundless=" + almostBoundless + "}";
    }
}

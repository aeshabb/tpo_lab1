package ru.itmo.tpo.task3;

/**
 * Персонаж (участник событий).
 * Артур — произносит фразу.
 */
public class Person {
    private final String name;
    private String currentPhrase;
    private LifeStyle lifeStyle;

    public Person(String name) {
        this.name = name;
        this.lifeStyle = new LifeStyle(LifeStyle.Quality.NORMAL);
    }

    public String getName() {
        return name;
    }

    public String getCurrentPhrase() {
        return currentPhrase;
    }

    /**
     * Произносит фразу. Это событие может спровоцировать открытие дыры
     * в пространстве-времени.
     */
    public Words sayPhrase(String phrase) {
        this.currentPhrase = phrase;
        return new Words(phrase, this);
    }

    public LifeStyle getLifeStyle() {
        return lifeStyle;
    }

    public void setLifeStyle(LifeStyle lifeStyle) {
        this.lifeStyle = lifeStyle;
    }

    @Override
    public String toString() {
        return "Person{name='" + name + "'}";
    }
}

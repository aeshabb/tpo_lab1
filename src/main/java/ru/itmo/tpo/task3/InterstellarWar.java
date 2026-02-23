package ru.itmo.tpo.task3;

/**
 * Ужасная межзвёздная война.
 * Существа балансируют на её грани.
 */
public class InterstellarWar {
    private final String description;
    private boolean terrible; // ужасная
    private boolean started;

    public InterstellarWar(String description) {
        this.description = description;
        this.terrible = true;
        this.started = false;
    }

    public String getDescription() {
        return description;
    }

    public boolean isTerrible() {
        return terrible;
    }

    public void setTerrible(boolean terrible) {
        this.terrible = terrible;
    }

    public boolean isStarted() {
        return started;
    }

    /**
     * Начинает войну.
     */
    public void start() {
        this.started = true;
    }

    @Override
    public String toString() {
        return "InterstellarWar{description='" + description +
                "', terrible=" + terrible + ", started=" + started + "}";
    }
}

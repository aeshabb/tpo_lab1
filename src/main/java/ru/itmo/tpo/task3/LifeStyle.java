package ru.itmo.tpo.task3;

/**
 * Образ жизни персонажа.
 * "А у меня, кажется, большие проблемы с образом жизни"
 */
public class LifeStyle {

    public enum Quality {
        NORMAL,
        PROBLEMATIC,
        CRITICAL
    }

    private Quality quality;
    private String description;

    public LifeStyle(Quality quality) {
        this.quality = quality;
        this.description = describeQuality(quality);
    }

    private String describeQuality(Quality quality) {
        return switch (quality) {
            case NORMAL -> "обычный образ жизни";
            case PROBLEMATIC -> "большие проблемы с образом жизни";
            case CRITICAL -> "критические проблемы с образом жизни";
        };
    }

    public boolean hasProblems() {
        return quality != Quality.NORMAL;
    }

    public Quality getQuality() {
        return quality;
    }

    public void setQuality(Quality quality) {
        this.quality = quality;
        this.description = describeQuality(quality);
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return "LifeStyle{quality=" + quality + ", description='" + description + "'}";
    }
}

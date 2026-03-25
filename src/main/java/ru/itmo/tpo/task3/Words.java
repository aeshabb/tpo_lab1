package ru.itmo.tpo.task3;

/**
 * Произнесённые слова, которые могут путешествовать через пространство-время.
 */
public class Words {
    private final String content;
    private final Person speaker;
    private boolean transported;
    private Galaxy destination;
    private double travelTime;
    private double effectiveSpeed;


    public Words(String content, Person speaker) {
        this.content = content;
        this.speaker = speaker;
        this.transported = false;
        this.destination = null;
        this.travelTime = 0.0;
        this.effectiveSpeed = 0.0;
    }

    public String getContent() {
        return content;
    }

    public Person getSpeaker() {
        return speaker;
    }

    public boolean isTransported() {
        return transported;
    }

    /**
     * Переносит слова в указанную галактику через дыру в пространстве-времени.
     */
    public void transportTo(Galaxy destination) {
        this.transported = true;
        this.destination = destination;
    }

    public void recordTravelMetrics(double travelTime, double effectiveSpeed) {
        this.travelTime = travelTime;
        this.effectiveSpeed = effectiveSpeed;
    }

    public Galaxy getDestination() {
        return destination;
    }

    public double getTravelTime() {
        return travelTime;
    }

    public double getEffectiveSpeed() {
        return effectiveSpeed;
    }

    public int getCharacterCount() {
        return content.length();
    }

    @Override
    public String toString() {
        return "Words{content='" + content + "', speaker=" + speaker.getName() +
                ", transported=" + transported + "}";
    }
}

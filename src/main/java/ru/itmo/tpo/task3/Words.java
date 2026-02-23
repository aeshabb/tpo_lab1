package ru.itmo.tpo.task3;

/**
 * Произнесённые слова, которые могут путешествовать через пространство-время.
 */
public class Words {
    private final String content;
    private final Person speaker;
    private boolean transported;
    private Galaxy destination;

    public Words(String content, Person speaker) {
        this.content = content;
        this.speaker = speaker;
        this.transported = false;
        this.destination = null;
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
     *
     * @param destination целевая галактика
     */
    public void transportTo(Galaxy destination) {
        this.transported = true;
        this.destination = destination;
    }

    public Galaxy getDestination() {
        return destination;
    }

    @Override
    public String toString() {
        return "Words{content='" + content + "', speaker=" + speaker.getName() +
                ", transported=" + transported + "}";
    }
}

package ru.itmo.tpo.task3;

/**
 * Странное воинственное существо, обитающее в далёкой галактике.
 * Балансирует на грани ужасной межзвёздной войны.
 */
public class WarlikeCreature {
    private final String species;
    private boolean strange;
    private boolean warlike;
    private WarReadiness warReadiness;

    public enum WarReadiness {
        PEACEFUL,
        TENSE,
        ON_BRINK_OF_WAR,
        AT_WAR
    }

    public WarlikeCreature(String species) {
        this.species = species;
        this.strange = true;
        this.warlike = true;
        this.warReadiness = WarReadiness.ON_BRINK_OF_WAR;
    }

    public String getSpecies() {
        return species;
    }

    public boolean isStrange() {
        return strange;
    }

    public void setStrange(boolean strange) {
        this.strange = strange;
    }

    public boolean isWarlike() {
        return warlike;
    }

    public void setWarlike(boolean warlike) {
        this.warlike = warlike;
    }

    public WarReadiness getWarReadiness() {
        return warReadiness;
    }

    public void setWarReadiness(WarReadiness warReadiness) {
        this.warReadiness = warReadiness;
    }

    /**
     * Проверяет, балансирует ли существо на грани войны.
     */
    public boolean isOnBrinkOfWar() {
        return warReadiness == WarReadiness.ON_BRINK_OF_WAR;
    }

    @Override
    public String toString() {
        return "WarlikeCreature{species='" + species + "', strange=" + strange +
                ", warlike=" + warlike + ", readiness=" + warReadiness + "}";
    }
}

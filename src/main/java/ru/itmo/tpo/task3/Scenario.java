package ru.itmo.tpo.task3;

/**
 * Сценарий:
 * "В тот самый момент, когда Артур произнёс 'А у меня, кажется, большие проблемы
 * с образом жизни', в ткани пространства-времени открылась случайная дыра и
 * перенесла его слова далеко-далеко во времени через почти бескрайние просторы
 * космоса в далёкую галактику, где странные воинственные существа балансировали
 * на грани ужасной межзвёздной войны."
 *
 * Этот класс объединяет все элементы доменной модели и позволяет
 * выполнить описанный сценарий.
 */
public class Scenario {
    private final Person arthur;
    private final SpaceTimeFabric spaceTimeFabric;
    private final Cosmos cosmos;
    private final Galaxy distantGalaxy;
    private final InterstellarWar war;

    private Words spokenWords;
    private boolean scenarioExecuted;

    public Scenario() {
        // Инициализация всех элементов сценария
        this.arthur = new Person("Артур");
        this.arthur.setLifeStyle(new LifeStyle(LifeStyle.Quality.PROBLEMATIC));

        this.spaceTimeFabric = new SpaceTimeFabric();
        this.cosmos = new Cosmos();

        this.distantGalaxy = new Galaxy("Далёкая галактика", true);
        this.war = new InterstellarWar("ужасная межзвёздная война");

        // Населяем галактику странными воинственными существами
        WarlikeCreature creature1 = new WarlikeCreature("Неизвестный вид А");
        WarlikeCreature creature2 = new WarlikeCreature("Неизвестный вид Б");
        distantGalaxy.addInhabitant(creature1);
        distantGalaxy.addInhabitant(creature2);
        distantGalaxy.setPendingWar(war);

        this.scenarioExecuted = false;
    }

    public Words execute() {
        // 1. Артур произносит фразу
        spokenWords = arthur.sayPhrase(
                "А у меня, кажется, большие проблемы с образом жизни");

        // 2. В ткани пространства-времени открывается случайная дыра
        SpaceTimeHole hole = spaceTimeFabric.openRandomHole(distantGalaxy);

        // 3. Дыра переносит слова далеко-далеко во времени через космос
        hole.transport(spokenWords, cosmos);

        // 4. Дыра закрывается
        spaceTimeFabric.closeHole();

        this.scenarioExecuted = true;
        return spokenWords;
    }

    public Person getArthur() {
        return arthur;
    }

    public SpaceTimeFabric getSpaceTimeFabric() {
        return spaceTimeFabric;
    }

    public Cosmos getCosmos() {
        return cosmos;
    }

    public Galaxy getDistantGalaxy() {
        return distantGalaxy;
    }

    public InterstellarWar getWar() {
        return war;
    }

    public Words getSpokenWords() {
        return spokenWords;
    }

    public boolean isScenarioExecuted() {
        return scenarioExecuted;
    }
}

package ru.itmo.tpo.task3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Тесты доменной модели")
class DomainModelTest {

    @Nested
    @DisplayName("Person")
    class PersonTest {

        private Person arthur;

        @BeforeEach
        void setUp() {
            arthur = new Person("Артур");
        }

        @Test
        @DisplayName("Создание персонажа")
        void testCreation() {
            assertAll(
                    () -> assertEquals("Артур", arthur.getName()),
                    () -> assertNull(arthur.getCurrentPhrase()),
                    () -> assertEquals(LifeStyle.Quality.NORMAL, arthur.getLifeStyle().getQuality())
            );
        }

        @Test
        @DisplayName("Персонаж произносит фразу")
        void testSayPhrase() {
            String phrase = "А у меня, кажется, большие проблемы с образом жизни";

            Words words = arthur.sayPhrase(phrase);

            assertAll(
                    () -> assertEquals(phrase, words.getContent()),
                    () -> assertEquals(arthur, words.getSpeaker()),
                    () -> assertEquals(phrase, arthur.getCurrentPhrase())
            );
        }
    }

    @Nested
    @DisplayName("LifeStyle")
    class LifeStyleTest {

        @Test
        @DisplayName("Нормальный образ жизни")
        void testNormalLifeStyle() {
            LifeStyle lifeStyle = new LifeStyle(LifeStyle.Quality.NORMAL);

            assertAll(
                    () -> assertFalse(lifeStyle.hasProblems()),
                    () -> assertEquals("обычный образ жизни", lifeStyle.getDescription())
            );
        }

        @Test
        @DisplayName("Проблемный образ жизни")
        void testProblematicLifeStyle() {
            LifeStyle lifeStyle = new LifeStyle(LifeStyle.Quality.PROBLEMATIC);

            assertAll(
                    () -> assertTrue(lifeStyle.hasProblems()),
                    () -> assertEquals("большие проблемы с образом жизни", lifeStyle.getDescription())
            );
        }

        @Test
        @DisplayName("Изменение качества обновляет описание")
        void testChangeQuality() {
            LifeStyle lifeStyle = new LifeStyle(LifeStyle.Quality.NORMAL);

            lifeStyle.setQuality(LifeStyle.Quality.CRITICAL);

            assertAll(
                    () -> assertTrue(lifeStyle.hasProblems()),
                    () -> assertEquals("критические проблемы с образом жизни", lifeStyle.getDescription())
            );
        }
    }

    @Nested
    @DisplayName("Words")
    class WordsTest {

        @Test
        @DisplayName("Создание слов")
        void testCreation() {
            Person speaker = new Person("Артур");
            Words words = new Words("тестовая фраза", speaker);

            assertAll(
                    () -> assertEquals("тестовая фраза", words.getContent()),
                    () -> assertEquals(speaker, words.getSpeaker()),
                    () -> assertFalse(words.isTransported()),
                    () -> assertNull(words.getDestination())
            );
        }

        @Test
        @DisplayName("Перенос слов и сохранение метрик")
        void testTransportAndMetrics() {
            Words words = new Words("фраза", new Person("Артур"));
            Galaxy galaxy = new Galaxy("Далекая галактика", true);

            words.transportTo(galaxy);
            words.recordTravelMetrics(1.25, 33.5);

            assertAll(
                    () -> assertTrue(words.isTransported()),
                    () -> assertEquals(galaxy, words.getDestination()),
                    () -> assertEquals(1.25, words.getTravelTime(), 1e-9),
                    () -> assertEquals(33.5, words.getEffectiveSpeed(), 1e-9)
            );
        }
    }

    @Nested
    @DisplayName("SpaceTimeFabric")
    class SpaceTimeFabricTest {

        @Test
        @DisplayName("Открытие случайной дыры")
        void testOpenHole() {
            SpaceTimeFabric fabric = new SpaceTimeFabric();
            Galaxy galaxy = new Galaxy("Далекая галактика", true);

            SpaceTimeHole hole = fabric.openRandomHole(galaxy);

            assertAll(
                    () -> assertNotNull(hole),
                    () -> assertTrue(hole.isOpen()),
                    () -> assertTrue(fabric.hasOpenHole()),
                    () -> assertEquals(galaxy, hole.getDestination())
            );
        }

        @Test
        @DisplayName("Закрытие дыры безопасно при любом состоянии")
        void testCloseHole() {
            SpaceTimeFabric fabric = new SpaceTimeFabric();
            Galaxy galaxy = new Galaxy("Далекая галактика", true);
            fabric.openRandomHole(galaxy);

            fabric.closeHole();

            assertAll(
                    () -> assertFalse(fabric.hasOpenHole()),
                    () -> assertDoesNotThrow(fabric::closeHole)
            );
        }
    }

    @Nested
    @DisplayName("SpaceTimeHole")
    class SpaceTimeHoleTest {

        @Test
        @DisplayName("Перенос слов через открытую дыру")
        void testTransportWords() {
            Galaxy galaxy = new Galaxy("Далекая галактика", true);
            SpaceTimeHole hole = new SpaceTimeHole(galaxy);
            Words words = new Words("фраза", new Person("Артур"));

            hole.transport(words);

            assertAll(
                    () -> assertTrue(words.isTransported()),
                    () -> assertEquals(galaxy, words.getDestination())
            );
        }

        @ParameterizedTest(name = "Длина фразы {0} символов")
        @ValueSource(ints = {0, 50, 100, 150})
        @DisplayName("Время переноса зависит от длины фразы (Анализ граничных значений и классов эквивалентности)")
        void testCalculateTransferTimeEquivalence(int length) {
            SpaceTimeHole hole = new SpaceTimeHole(new Galaxy("Далекая галактика", true));
            String content = "a".repeat(length);
            Words words = new Words(content, new Person("Артур"));

            double transferRate = 100.0;
            double expectedTime = 0.5 + length / transferRate;
            assertEquals(expectedTime, hole.calculateTransferTime(words, transferRate), 1e-9);
        }

        @Test
        @DisplayName("Отрицательная скорость переноса символов вызывает исключение")
        void testCalculateTransferTimeInvalidRate() {
            SpaceTimeHole hole = new SpaceTimeHole(new Galaxy("Далекая галактика", true));
            Words words = new Words("фраза", new Person("Артур"));

            assertThrows(IllegalArgumentException.class, () -> hole.calculateTransferTime(words, 0.0));
            assertThrows(IllegalArgumentException.class, () -> hole.calculateTransferTime(words, -50.0));
        }

        @Test
        @DisplayName("Эффективная скорость учитывает космос")
        void testCalculateEffectiveSpeed() {
            Galaxy galaxy = new Galaxy("Тестовая галактика", true, 300.0, 400.0, 0.0);
            Cosmos cosmos = new Cosmos();
            SpaceTimeHole hole = new SpaceTimeHole(galaxy);
            Words words = new Words("фраза", new Person("Артур"));
            double rate = 100.0;

            double expectedDistance = galaxy.distanceFromOrigin() / cosmos.getSpaceCurvatureFactor();
            double expectedTime = hole.calculateTransferTime(words, rate);

            assertEquals(expectedDistance / expectedTime, hole.calculateEffectiveSpeed(words, cosmos, rate), 1e-9);
        }

        @Test
        @DisplayName("Проверка гиперболической зависимости: EffectiveSpeed = k / TransferTime (v = k/x)")
        void testEffectiveSpeedIsInverseProportionalToTime() {
            Galaxy galaxy = new Galaxy("Тестовая галактика", true, 300.0, 400.0, 0.0);
            Cosmos cosmos = new Cosmos();
            SpaceTimeHole hole = new SpaceTimeHole(galaxy);

            double k = cosmos.calculateEffectiveDistance(galaxy);

            Words shortWords = new Words("", new Person("Артур"));
            double smallX = hole.calculateTransferTime(shortWords, 100.0);
            double speedAtSmallX = hole.calculateEffectiveSpeed(shortWords, cosmos, 100.0);

            String massiveContent = "a".repeat(1000000);
            Words longWords = new Words(massiveContent, new Person("Артур"));
            double largeX = hole.calculateTransferTime(longWords, 0.1);
            double speedAtLargeX = hole.calculateEffectiveSpeed(longWords, cosmos, 0.1);

            assertAll(
                    () -> assertEquals(k / smallX, speedAtSmallX, 1e-9, "Отклонение от k/x на малых значениях x"),
                    () -> assertEquals(k / largeX, speedAtLargeX, 1e-9, "Отклонение от k/x на больших значениях x")
            );
        }

        @Test
        @DisplayName("Перенос с космосом сохраняет метрики")
        void testTransportWordsWithMetrics() {
            Galaxy galaxy = new Galaxy("Тестовая галактика", true, 300.0, 400.0, 0.0);
            Cosmos cosmos = new Cosmos();
            SpaceTimeHole hole = new SpaceTimeHole(galaxy);
            Words words = new Words("фраза", new Person("Артур"));

            hole.transport(words, cosmos, 100.0);

            assertAll(
                    () -> assertTrue(words.isTransported()),
                    () -> assertEquals(galaxy, words.getDestination()),
                    () -> assertTrue(words.getTravelTime() > 0.0),
                    () -> assertTrue(words.getEffectiveSpeed() > 0.0)
            );
        }

        @Test
        @DisplayName("Нельзя переносить через закрытую дыру")
        void testTransportThroughClosedHole() {
            SpaceTimeHole hole = new SpaceTimeHole(new Galaxy("Далекая галактика", true));
            hole.close();

            assertThrows(IllegalStateException.class,
                    () -> hole.transport(new Words("фраза", new Person("Артур"))));
        }
    }

    @Nested
    @DisplayName("Galaxy")
    class GalaxyTest {

        @Test
        @DisplayName("Добавление воинственных существ")
        void testAddInhabitants() {
            Galaxy galaxy = new Galaxy("Далекая галактика", true);
            WarlikeCreature creature = new WarlikeCreature("Вид А");

            galaxy.addInhabitant(creature);

            assertAll(
                    () -> assertTrue(galaxy.hasWarlikeCreatures()),
                    () -> assertEquals(1, galaxy.getInhabitants().size())
            );
        }

        @Test
        @DisplayName("Галактика на грани войны")
        void testOnBrinkOfWar() {
            Galaxy galaxy = new Galaxy("Далекая галактика", true);
            galaxy.setPendingWar(new InterstellarWar("ужасная межзвездная война"));

            assertTrue(galaxy.isOnBrinkOfWar());
        }

        @Test
        @DisplayName("После начала войны галактика уже не на грани")
        void testNotOnBrinkOfWarWhenStarted() {
            Galaxy galaxy = new Galaxy("Далекая галактика", true);
            InterstellarWar war = new InterstellarWar("ужасная межзвездная война");
            war.start();
            galaxy.setPendingWar(war);

            assertFalse(galaxy.isOnBrinkOfWar());
        }

        @Test
        @DisplayName("Расстояние до галактики вычисляется по координатам")
        void testDistanceFromOrigin() {
            Galaxy galaxy = new Galaxy("Тестовая галактика", true, 3.0, 4.0, 12.0);

            assertEquals(13.0, galaxy.distanceFromOrigin(), 1e-9);
        }
    }

    @Nested
    @DisplayName("WarlikeCreature")
    class WarlikeCreatureTest {

        @Test
        @DisplayName("Создание странного воинственного существа")
        void testCreation() {
            WarlikeCreature creature = new WarlikeCreature("Неизвестный вид");

            assertAll(
                    () -> assertTrue(creature.isStrange()),
                    () -> assertTrue(creature.isWarlike()),
                    () -> assertTrue(creature.isOnBrinkOfWar())
            );
        }

        @Test
        @DisplayName("Изменение готовности к войне")
        void testChangeWarReadiness() {
            WarlikeCreature creature = new WarlikeCreature("Вид А");
            creature.setWarReadiness(WarlikeCreature.WarReadiness.AT_WAR);

            assertAll(
                    () -> assertFalse(creature.isOnBrinkOfWar()),
                    () -> assertEquals(WarlikeCreature.WarReadiness.AT_WAR, creature.getWarReadiness())
            );
        }
    }

    @Nested
    @DisplayName("InterstellarWar")
    class InterstellarWarTest {

        @Test
        @DisplayName("Создание ужасной межзвездной войны")
        void testCreation() {
            InterstellarWar war = new InterstellarWar("ужасная межзвездная война");

            assertAll(
                    () -> assertEquals("ужасная межзвездная война", war.getDescription()),
                    () -> assertTrue(war.isTerrible()),
                    () -> assertFalse(war.isStarted())
            );
        }

        @Test
        @DisplayName("Война начинается")
        void testStart() {
            InterstellarWar war = new InterstellarWar("война");
            war.start();

            assertTrue(war.isStarted());
        }
    }

    @Nested
    @DisplayName("Cosmos")
    class CosmosTest {

        @Test
        @DisplayName("Эффективное расстояние уменьшается из-за искривления пространства")
        void testEffectiveDistanceCalculation() {
            Cosmos cosmos = new Cosmos();
            Galaxy galaxy = new Galaxy("Тестовая галактика", true, 420.0, 0.0, 0.0);

            assertEquals(10.0, cosmos.calculateEffectiveDistance(galaxy), 1e-9);
        }
    }

    @Nested
    @DisplayName("Scenario")
    class ScenarioTest {

        @Test
        @DisplayName("Начальное состояние сценария")
        void testInitialState() {
            Scenario scenario = new Scenario();

            assertAll(
                    () -> assertEquals("Артур", scenario.getArthur().getName()),
                    () -> assertTrue(scenario.getArthur().getLifeStyle().hasProblems()),
                    () -> assertTrue(scenario.getDistantGalaxy().isDistant()),
                    () -> assertTrue(scenario.getDistantGalaxy().hasWarlikeCreatures()),
                    () -> assertTrue(scenario.getDistantGalaxy().isOnBrinkOfWar()),
                    () -> assertFalse(scenario.isScenarioExecuted()),
                    () -> assertNull(scenario.getSpokenWords())
            );
        }

        @Test
        @DisplayName("Выполнение полного сценария")
        void testExecuteScenario() {
            Scenario scenario = new Scenario();

            Words words = scenario.execute();

            assertAll(
                    () -> assertNotNull(words),
                    () -> assertEquals("А у меня, кажется, большие проблемы с образом жизни", words.getContent()),
                    () -> assertEquals(scenario.getArthur(), words.getSpeaker()),
                    () -> assertTrue(words.isTransported()),
                    () -> assertEquals(scenario.getDistantGalaxy(), words.getDestination()),
                    () -> assertTrue(words.getTravelTime() > 0.0),
                    () -> assertTrue(words.getEffectiveSpeed() > 0.0),
                    () -> assertFalse(scenario.getSpaceTimeFabric().hasOpenHole()),
                    () -> assertTrue(scenario.isScenarioExecuted())
            );
        }

        @Test
        @DisplayName("Галактика населена странными воинственными существами")
        void testGalaxyInhabitants() {
            Scenario scenario = new Scenario();

            for (WarlikeCreature creature : scenario.getDistantGalaxy().getInhabitants()) {
                assertAll(
                        () -> assertTrue(creature.isStrange()),
                        () -> assertTrue(creature.isWarlike()),
                        () -> assertTrue(creature.isOnBrinkOfWar())
                );
            }
        }
    }
}

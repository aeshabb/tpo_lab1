package ru.itmo.tpo.task3;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тестовое покрытие доменной модели.
 *
 * Предметная область:
 * "В тот самый момент, когда Артур произнёс 'А у меня, кажется, большие проблемы
 * с образом жизни', в ткани пространства-времени открылась случайная дыра и
 * перенесла его слова далеко-далеко во времени через почти бескрайние просторы
 * космоса в далёкую галактику, где странные воинственные существа балансировали
 * на грани ужасной межзвёздной войны."
 */
@DisplayName("Тесты доменной модели")
class DomainModelTest {

    @Nested
    @DisplayName("Person (Персонаж)")
    class PersonTest {

        private Person arthur;

        @BeforeEach
        void setUp() {
            arthur = new Person("Артур");
        }

        @Test
        @DisplayName("Создание персонажа с именем")
        void testCreation() {
            assertEquals("Артур", arthur.getName());
            assertNull(arthur.getCurrentPhrase());
        }

        @Test
        @DisplayName("Персонаж произносит фразу и возвращает Words")
        void testSayPhrase() {
            String phrase = "А у меня, кажется, большие проблемы с образом жизни";
            Words words = arthur.sayPhrase(phrase);

            assertNotNull(words);
            assertEquals(phrase, words.getContent());
            assertEquals(arthur, words.getSpeaker());
            assertEquals(phrase, arthur.getCurrentPhrase());
        }

        @Test
        @DisplayName("У персонажа есть образ жизни по умолчанию (NORMAL)")
        void testDefaultLifeStyle() {
            assertNotNull(arthur.getLifeStyle());
            assertEquals(LifeStyle.Quality.NORMAL, arthur.getLifeStyle().getQuality());
        }

        @Test
        @DisplayName("Можно установить проблемный образ жизни")
        void testSetProblematicLifeStyle() {
            arthur.setLifeStyle(new LifeStyle(LifeStyle.Quality.PROBLEMATIC));
            assertTrue(arthur.getLifeStyle().hasProblems());
            assertEquals(LifeStyle.Quality.PROBLEMATIC, arthur.getLifeStyle().getQuality());
        }
    }

    @Nested
    @DisplayName("LifeStyle")
    class LifeStyleTest {

        @Test
        @DisplayName("Нормальный образ жизни - нет проблем")
        void testNormalLifeStyle() {
            LifeStyle lifeStyle = new LifeStyle(LifeStyle.Quality.NORMAL);
            assertFalse(lifeStyle.hasProblems());
            assertEquals("обычный образ жизни", lifeStyle.getDescription());
        }

        @Test
        @DisplayName("Проблемный образ жизни - есть проблемы")
        void testProblematicLifeStyle() {
            LifeStyle lifeStyle = new LifeStyle(LifeStyle.Quality.PROBLEMATIC);
            assertTrue(lifeStyle.hasProblems());
            assertEquals("большие проблемы с образом жизни", lifeStyle.getDescription());
        }

        @Test
        @DisplayName("Критический образ жизни - есть проблемы")
        void testCriticalLifeStyle() {
            LifeStyle lifeStyle = new LifeStyle(LifeStyle.Quality.CRITICAL);
            assertTrue(lifeStyle.hasProblems());
        }

        @Test
        @DisplayName("Изменение качества образа жизни обновляет описание")
        void testChangeQuality() {
            LifeStyle lifeStyle = new LifeStyle(LifeStyle.Quality.NORMAL);
            assertFalse(lifeStyle.hasProblems());

            lifeStyle.setQuality(LifeStyle.Quality.PROBLEMATIC);
            assertTrue(lifeStyle.hasProblems());
            assertEquals("большие проблемы с образом жизни", lifeStyle.getDescription());
        }
    }

    @Nested
    @DisplayName("Words")
    class WordsTest {

        @Test
        @DisplayName("Создание слов с содержанием и автором")
        void testCreation() {
            Person speaker = new Person("Артур");
            Words words = new Words("тестовая фраза", speaker);

            assertEquals("тестовая фраза", words.getContent());
            assertEquals(speaker, words.getSpeaker());
            assertFalse(words.isTransported());
            assertNull(words.getDestination());
        }

        @Test
        @DisplayName("Перенос слов в галактику")
        void testTransport() {
            Person speaker = new Person("Артур");
            Words words = new Words("фраза", speaker);
            Galaxy galaxy = new Galaxy("Далёкая галактика", true);

            words.transportTo(galaxy);

            assertTrue(words.isTransported());
            assertEquals(galaxy, words.getDestination());
        }

        @Test
        @DisplayName("Подсчёт количества символов в словах")
        void testCharacterCount() {
            Words words = new Words("фраза", new Person("Артур"));
            assertEquals(5, words.getCharacterCount());
        }

        @Test
        @DisplayName("Сохранение метрик переноса")
        void testRecordTravelMetrics() {
            Words words = new Words("фраза", new Person("Артур"));
            words.recordTravelMetrics(1.25, 33.5);

            assertEquals(1.25, words.getTravelTime(), 1e-9);
            assertEquals(33.5, words.getEffectiveSpeed(), 1e-9);
        }
    }

    @Nested
    @DisplayName("SpaceTimeFabric (Ткань пространства-времени)")
    class SpaceTimeFabricTest {

        @Test
        @DisplayName("Изначально нет дыр")
        void testInitialState() {
            SpaceTimeFabric fabric = new SpaceTimeFabric();
            assertFalse(fabric.hasOpenHole());
            assertNull(fabric.getCurrentHole());
        }

        @Test
        @DisplayName("Открытие случайной дыры")
        void testOpenHole() {
            SpaceTimeFabric fabric = new SpaceTimeFabric();
            Galaxy galaxy = new Galaxy("Далёкая галактика", true);

            SpaceTimeHole hole = fabric.openRandomHole(galaxy);

            assertNotNull(hole);
            assertTrue(hole.isOpen());
            assertTrue(fabric.hasOpenHole());
            assertEquals(galaxy, hole.getDestination());
        }

        @Test
        @DisplayName("Закрытие дыры")
        void testCloseHole() {
            SpaceTimeFabric fabric = new SpaceTimeFabric();
            Galaxy galaxy = new Galaxy("Далёкая галактика", true);
            fabric.openRandomHole(galaxy);

            fabric.closeHole();

            assertFalse(fabric.hasOpenHole());
        }

        @Test
        @DisplayName("Закрытие при отсутствии дыры - без ошибки")
        void testCloseWhenNoHole() {
            SpaceTimeFabric fabric = new SpaceTimeFabric();
            assertDoesNotThrow(fabric::closeHole);
        }
    }

    @Nested
    @DisplayName("SpaceTimeHole (Дыра в пространстве-времени)")
    class SpaceTimeHoleTest {

        @Test
        @DisplayName("Создание открытой дыры")
        void testCreation() {
            Galaxy galaxy = new Galaxy("Далёкая галактика", true);
            SpaceTimeHole hole = new SpaceTimeHole(galaxy);

            assertTrue(hole.isOpen());
            assertEquals(galaxy, hole.getDestination());
        }

        @Test
        @DisplayName("Перенос слов через дыру")
        void testTransportWords() {
            Galaxy galaxy = new Galaxy("Далёкая галактика", true);
            SpaceTimeHole hole = new SpaceTimeHole(galaxy);
            Person speaker = new Person("Артур");
            Words words = new Words("фраза", speaker);

            hole.transport(words);

            assertTrue(words.isTransported());
            assertEquals(galaxy, words.getDestination());
        }

        @Test
        @DisplayName("Вычисление времени переноса зависит от длины фразы")
        void testCalculateTransferTime() {
            SpaceTimeHole hole = new SpaceTimeHole(new Galaxy("Далёкая галактика", true));
            Words shortWords = new Words("привет", new Person("Артур"));
            Words longWords = new Words("Очень длинная фраза для проверки времени переноса", new Person("Артур"));

            double shortTime = hole.calculateTransferTime(shortWords);
            double longTime = hole.calculateTransferTime(longWords);

            assertTrue(longTime > shortTime);
            assertEquals(0.5 + shortWords.getCharacterCount() / 100.0, shortTime, 1e-9);
        }

        @Test
        @DisplayName("Эффективная скорость переноса вычисляется по формуле distance / time")
        void testCalculateEffectiveSpeed() {
            Galaxy galaxy = new Galaxy("Тестовая галактика", true, 300.0, 400.0, 0.0);
            Cosmos cosmos = new Cosmos();
            SpaceTimeHole hole = new SpaceTimeHole(galaxy);
            Words words = new Words("фраза", new Person("Артур"));

            double expectedDistance = galaxy.distanceFromOrigin() / cosmos.getSpaceCurvatureFactor();
            double expectedTime = hole.calculateTransferTime(words);
            double expectedSpeed = expectedDistance / expectedTime;

            assertEquals(expectedSpeed, hole.calculateEffectiveSpeed(words, cosmos), 1e-9);
        }

        @Test
        @DisplayName("Перенос через дыру с космосом сохраняет метрики")
        void testTransportWordsWithMetrics() {
            Galaxy galaxy = new Galaxy("Тестовая галактика", true, 300.0, 400.0, 0.0);
            Cosmos cosmos = new Cosmos();
            SpaceTimeHole hole = new SpaceTimeHole(galaxy);
            Words words = new Words("фраза", new Person("Артур"));

            hole.transport(words, cosmos);

            assertTrue(words.isTransported());
            assertEquals(galaxy, words.getDestination());
            assertTrue(words.getTravelTime() > 0.0);
            assertTrue(words.getEffectiveSpeed() > 0.0);
        }

        @Test
        @DisplayName("Нельзя переносить через закрытую дыру")
        void testTransportThroughClosedHole() {
            Galaxy galaxy = new Galaxy("Далёкая галактика", true);
            SpaceTimeHole hole = new SpaceTimeHole(galaxy);
            hole.close();

            Person speaker = new Person("Артур");
            Words words = new Words("фраза", speaker);

            assertThrows(IllegalStateException.class, () -> hole.transport(words));
        }

        @Test
        @DisplayName("Закрытие дыры")
        void testClose() {
            Galaxy galaxy = new Galaxy("Далёкая галактика", true);
            SpaceTimeHole hole = new SpaceTimeHole(galaxy);

            hole.close();

            assertFalse(hole.isOpen());
        }
    }

    @Nested
    @DisplayName("Galaxy (Далёкая галактика)")
    class GalaxyTest {

        @Test
        @DisplayName("Создание далёкой галактики")
        void testCreation() {
            Galaxy galaxy = new Galaxy("Далёкая галактика", true);
            assertEquals("Далёкая галактика", galaxy.getName());
            assertTrue(galaxy.isDistant());
            assertFalse(galaxy.hasWarlikeCreatures());
        }

        @Test
        @DisplayName("Добавление воинственных существ")
        void testAddInhabitants() {
            Galaxy galaxy = new Galaxy("Далёкая галактика", true);
            WarlikeCreature creature = new WarlikeCreature("Вид А");

            galaxy.addInhabitant(creature);

            assertTrue(galaxy.hasWarlikeCreatures());
            assertEquals(1, galaxy.getInhabitants().size());
            assertEquals(creature, galaxy.getInhabitants().get(0));
        }

        @Test
        @DisplayName("Галактика на грани войны")
        void testOnBrinkOfWar() {
            Galaxy galaxy = new Galaxy("Далёкая галактика", true);
            InterstellarWar war = new InterstellarWar("ужасная межзвёздная война");
            galaxy.setPendingWar(war);

            assertTrue(galaxy.isOnBrinkOfWar());
        }

        @Test
        @DisplayName("Галактика не на грани войны (война уже началась)")
        void testNotOnBrinkOfWarWhenStarted() {
            Galaxy galaxy = new Galaxy("Далёкая галактика", true);
            InterstellarWar war = new InterstellarWar("ужасная межзвёздная война");
            war.start();
            galaxy.setPendingWar(war);

            assertFalse(galaxy.isOnBrinkOfWar());
        }

        @Test
        @DisplayName("Галактика без войны")
        void testNoWar() {
            Galaxy galaxy = new Galaxy("Далёкая галактика", true);
            assertFalse(galaxy.isOnBrinkOfWar());
        }

        @Test
        @DisplayName("getInhabitants возвращает копию списка")
        void testGetInhabitantsReturnsCopy() {
            Galaxy galaxy = new Galaxy("Галактика", true);
            galaxy.addInhabitant(new WarlikeCreature("Вид А"));

            var inhabitants = galaxy.getInhabitants();
            inhabitants.clear(); // очистка копии

            assertEquals(1, galaxy.getInhabitants().size()); // оригинал не изменился
        }

        @Test
        @DisplayName("Расстояние до галактики вычисляется по 3D-координатам")
        void testDistanceFromOrigin() {
            Galaxy galaxy = new Galaxy("Тестовая галактика", true, 3.0, 4.0, 12.0);
            assertEquals(13.0, galaxy.distanceFromOrigin(), 1e-9);
        }
    }

    @Nested
    @DisplayName("WarlikeCreature (Воинственные существа)")
    class WarlikeCreatureTest {

        @Test
        @DisplayName("Создание странного воинственного существа")
        void testCreation() {
            WarlikeCreature creature = new WarlikeCreature("Неизвестный вид");
            assertTrue(creature.isStrange());
            assertTrue(creature.isWarlike());
            assertTrue(creature.isOnBrinkOfWar());
            assertEquals("Неизвестный вид", creature.getSpecies());
        }

        @Test
        @DisplayName("Существо балансирует на грани войны")
        void testOnBrinkOfWar() {
            WarlikeCreature creature = new WarlikeCreature("Вид А");
            assertEquals(WarlikeCreature.WarReadiness.ON_BRINK_OF_WAR,
                    creature.getWarReadiness());
            assertTrue(creature.isOnBrinkOfWar());
        }

        @Test
        @DisplayName("Изменение состояния готовности к войне")
        void testChangeWarReadiness() {
            WarlikeCreature creature = new WarlikeCreature("Вид А");

            creature.setWarReadiness(WarlikeCreature.WarReadiness.AT_WAR);
            assertFalse(creature.isOnBrinkOfWar());
            assertEquals(WarlikeCreature.WarReadiness.AT_WAR, creature.getWarReadiness());

            creature.setWarReadiness(WarlikeCreature.WarReadiness.PEACEFUL);
            assertFalse(creature.isOnBrinkOfWar());
        }

        @Test
        @DisplayName("Существо может стать не странным")
        void testSetStrange() {
            WarlikeCreature creature = new WarlikeCreature("Вид А");
            creature.setStrange(false);
            assertFalse(creature.isStrange());
        }

        @Test
        @DisplayName("Существо может стать мирным")
        void testSetNotWarlike() {
            WarlikeCreature creature = new WarlikeCreature("Вид А");
            creature.setWarlike(false);
            assertFalse(creature.isWarlike());
        }
    }

    @Nested
    @DisplayName("InterstellarWar (Межзвёздная война)")
    class InterstellarWarTest {

        @Test
        @DisplayName("Создание ужасной межзвёздной войны")
        void testCreation() {
            InterstellarWar war = new InterstellarWar("ужасная межзвёздная война");
            assertEquals("ужасная межзвёздная война", war.getDescription());
            assertTrue(war.isTerrible());
            assertFalse(war.isStarted());
        }

        @Test
        @DisplayName("Война начинается")
        void testStart() {
            InterstellarWar war = new InterstellarWar("война");
            war.start();
            assertTrue(war.isStarted());
        }

        @Test
        @DisplayName("Война может быть не ужасной")
        void testNotTerrible() {
            InterstellarWar war = new InterstellarWar("мелкий конфликт");
            war.setTerrible(false);
            assertFalse(war.isTerrible());
        }
    }

    @Nested
    @DisplayName("Cosmos (Космос)")
    class CosmosTest {

        @Test
        @DisplayName("Космос почти бескрайний")
        void testAlmostBoundless() {
            Cosmos cosmos = new Cosmos();
            assertTrue(cosmos.isAlmostBoundless());
        }

        @Test
        @DisplayName("Эффективное расстояние уменьшается из-за искривления пространства")
        void testEffectiveDistanceCalculation() {
            Cosmos cosmos = new Cosmos();
            Galaxy galaxy = new Galaxy("Тестовая галактика", true, 420.0, 0.0, 0.0);

            assertEquals(10.0, cosmos.calculateEffectiveDistance(galaxy), 1e-9);
        }
    }

    @Nested
    @DisplayName("Scenario (Полный сценарий)")
    class ScenarioTest {

        @Test
        @DisplayName("Начальное состояние сценария")
        void testInitialState() {
            Scenario scenario = new Scenario();

            assertEquals("Артур", scenario.getArthur().getName());
            assertTrue(scenario.getArthur().getLifeStyle().hasProblems());
            assertTrue(scenario.getDistantGalaxy().isDistant());
            assertTrue(scenario.getDistantGalaxy().hasWarlikeCreatures());
            assertTrue(scenario.getDistantGalaxy().isOnBrinkOfWar());
            assertTrue(scenario.getWar().isTerrible());
            assertFalse(scenario.getWar().isStarted());
            assertFalse(scenario.isScenarioExecuted());
            assertNull(scenario.getSpokenWords());
        }

        @Test
        @DisplayName("Выполнение полного сценария")
        void testExecuteScenario() {
            Scenario scenario = new Scenario();

            Words words = scenario.execute();

            // 1. Артур произнёс фразу
            assertNotNull(words);
            assertEquals("А у меня, кажется, большие проблемы с образом жизни",
                    words.getContent());
            assertEquals(scenario.getArthur(), words.getSpeaker());

            // 2. Слова перенесены в далёкую галактику
            assertTrue(words.isTransported());
            assertEquals(scenario.getDistantGalaxy(), words.getDestination());
            assertTrue(words.getTravelTime() > 0.0);
            assertTrue(words.getEffectiveSpeed() > 0.0);

            // 3. Дыра закрылась
            assertFalse(scenario.getSpaceTimeFabric().hasOpenHole());

            // 4. Сценарий выполнен
            assertTrue(scenario.isScenarioExecuted());
        }

        @Test
        @DisplayName("Галактика населена странными воинственными существами")
        void testGalaxyInhabitants() {
            Scenario scenario = new Scenario();

            var inhabitants = scenario.getDistantGalaxy().getInhabitants();
            assertEquals(2, inhabitants.size());

            for (var creature : inhabitants) {
                assertTrue(creature.isStrange(), "Существо должно быть странным");
                assertTrue(creature.isWarlike(), "Существо должно быть воинственным");
                assertTrue(creature.isOnBrinkOfWar(),
                        "Существо должно балансировать на грани войны");
            }
        }

        @Test
        @DisplayName("Космос доступен в сценарии")
        void testCosmosInScenario() {
            Scenario scenario = new Scenario();
            assertNotNull(scenario.getCosmos());
            assertTrue(scenario.getCosmos().isAlmostBoundless());
        }

        @Test
        @DisplayName("Война ужасная и ещё не началась до выполнения сценария")
        void testWarState() {
            Scenario scenario = new Scenario();

            assertTrue(scenario.getWar().isTerrible());
            assertFalse(scenario.getWar().isStarted());
            assertTrue(scenario.getDistantGalaxy().isOnBrinkOfWar());
        }
    }
}

package ru.itmo.tpo.task2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Модульные тесты для хеш-таблицы с закрытой адресацией.
 *
 * Тестирование алгоритма включает:
 * 1. Проверку корректности операций (insert, search, delete)
 * 2. Проверку прохождения характерных точек алгоритма
 * 3. Тестирование коллизий (несколько ключей в одной корзине)
 * 4. Граничные случаи
 *
 * Характерные точки алгоритма:
 * A: HASH_COMPUTED           — хеш вычислен
 * B: BUCKET_EMPTY            — корзина пуста
 * C: BUCKET_NOT_EMPTY        — корзина не пуста
 * D: KEY_FOUND               — ключ найден в цепочке
 * E: KEY_NOT_FOUND           — ключ не найден в цепочке
 * F: ENTRY_INSERTED          — элемент вставлен
 * G: ENTRY_UPDATED           — элемент обновлён
 * H: ENTRY_DELETED           — элемент удалён
 * I: SEARCH_RESULT_RETURNED  — результат поиска возвращён
 */
@DisplayName("Тесты хеш-таблицы с закрытой адресацией")
class HashTableTest {

    private HashTable hashTable;
    private List<HashTable.TracePoint> traceLog;

    @BeforeEach
    void setUp() {
        hashTable = new HashTable(7);
        traceLog = new ArrayList<>();
        hashTable.setTraceListener(traceLog::add);
    }

    @Nested
    @DisplayName("Операция INSERT")
    class InsertTest {

        @Test
        @DisplayName("Вставка в пустую таблицу")
        void testInsertIntoEmptyTable() {
            hashTable.insert(10, 100);
            assertEquals(1, hashTable.getSize());
            assertEquals(100, hashTable.search(10));
        }

        @Test
        @DisplayName("Вставка нескольких элементов без коллизий")
        void testInsertWithoutCollisions() {
            hashTable.insert(1, 10);
            hashTable.insert(2, 20);
            hashTable.insert(3, 30);
            assertEquals(3, hashTable.getSize());
            assertEquals(10, hashTable.search(1));
            assertEquals(20, hashTable.search(2));
            assertEquals(30, hashTable.search(3));
        }

        @Test
        @DisplayName("Вставка элементов с коллизией (одинаковый хеш)")
        void testInsertWithCollision() {
            // При capacity=7: ключи 1 и 8 дают одинаковый хеш (1 % 7 = 1, 8 % 7 = 1)
            hashTable.insert(1, 10);
            hashTable.insert(8, 80);
            assertEquals(2, hashTable.getSize());
            assertEquals(10, hashTable.search(1));
            assertEquals(80, hashTable.search(8));
            // Оба должны быть в одной корзине
            assertEquals(2, hashTable.getBucketSize(1));
        }

        @Test
        @DisplayName("Обновление значения при повторной вставке")
        void testInsertUpdateExisting() {
            hashTable.insert(5, 50);
            hashTable.insert(5, 500);
            assertEquals(1, hashTable.getSize()); // размер не изменился
            assertEquals(500, hashTable.search(5)); // значение обновлено
        }

        @Test
        @DisplayName("Вставка с отрицательным ключом")
        void testInsertNegativeKey() {
            hashTable.insert(-3, 30);
            assertEquals(1, hashTable.getSize());
            assertEquals(30, hashTable.search(-3));
        }

        @Test
        @DisplayName("Вставка ключа 0")
        void testInsertZeroKey() {
            hashTable.insert(0, 999);
            assertEquals(999, hashTable.search(0));
        }
    }

    @Nested
    @DisplayName("Операция SEARCH")
    class SearchTest {

        @Test
        @DisplayName("Поиск в пустой таблице")
        void testSearchInEmptyTable() {
            assertNull(hashTable.search(1));
        }

        @Test
        @DisplayName("Поиск существующего элемента")
        void testSearchExistingKey() {
            hashTable.insert(5, 50);
            assertEquals(50, hashTable.search(5));
        }

        @Test
        @DisplayName("Поиск несуществующего элемента")
        void testSearchNonExistingKey() {
            hashTable.insert(5, 50);
            assertNull(hashTable.search(12));
        }

        @Test
        @DisplayName("Поиск с коллизией — нужный элемент в цепочке")
        void testSearchInChain() {
            hashTable.insert(1, 10);
            hashTable.insert(8, 80);
            hashTable.insert(15, 150);
            // Все в одной корзине (bucket 1)
            assertEquals(10, hashTable.search(1));
            assertEquals(80, hashTable.search(8));
            assertEquals(150, hashTable.search(15));
        }

        @Test
        @DisplayName("Поиск несуществующего ключа в непустой корзине")
        void testSearchMissingKeyInNonEmptyBucket() {
            hashTable.insert(1, 10);
            // Ключ 22 имеет хеш 22 % 7 = 1, та же корзина, но ключа нет
            assertNull(hashTable.search(22));
        }
    }

    @Nested
    @DisplayName("Операция DELETE")
    class DeleteTest {

        @Test
        @DisplayName("Удаление из пустой таблицы")
        void testDeleteFromEmptyTable() {
            assertFalse(hashTable.delete(1));
        }

        @Test
        @DisplayName("Удаление существующего элемента")
        void testDeleteExistingKey() {
            hashTable.insert(5, 50);
            assertTrue(hashTable.delete(5));
            assertEquals(0, hashTable.getSize());
            assertNull(hashTable.search(5));
        }

        @Test
        @DisplayName("Удаление несуществующего элемента")
        void testDeleteNonExistingKey() {
            hashTable.insert(5, 50);
            assertFalse(hashTable.delete(12));
            assertEquals(1, hashTable.getSize());
        }

        @Test
        @DisplayName("Удаление элемента из цепочки (коллизия)")
        void testDeleteFromChain() {
            hashTable.insert(1, 10);
            hashTable.insert(8, 80);
            hashTable.insert(15, 150);

            assertTrue(hashTable.delete(8));
            assertEquals(2, hashTable.getSize());
            assertNull(hashTable.search(8));
            assertEquals(10, hashTable.search(1));
            assertEquals(150, hashTable.search(15));
        }

        @Test
        @DisplayName("Удаление последнего элемента в корзине очищает корзину")
        void testDeleteLastInBucket() {
            hashTable.insert(1, 10);
            hashTable.delete(1);
            assertEquals(0, hashTable.getBucketSize(1));
        }

        @Test
        @DisplayName("Удаление несуществующего ключа в непустой корзине")
        void testDeleteMissingKeyInNonEmptyBucket() {
            hashTable.insert(1, 10);
            // 22 % 7 = 1, та же корзина
            assertFalse(hashTable.delete(22));
            assertEquals(1, hashTable.getSize());
        }
    }

    // =================== Тесты характерных точек алгоритма ===================

    @Nested
    @DisplayName("Прохождение характерных точек — INSERT")
    class InsertTraceTest {

        @Test
        @DisplayName("INSERT в пустую корзину: A → B → F")
        void testInsertEmptyBucketTrace() {
            traceLog.clear();
            hashTable.insert(10, 100);

            List<HashTable.TracePoint> expected = List.of(
                    HashTable.TracePoint.HASH_COMPUTED,
                    HashTable.TracePoint.BUCKET_EMPTY,
                    HashTable.TracePoint.ENTRY_INSERTED
            );
            assertEquals(expected, traceLog,
                    "Вставка в пустую корзину: HASH_COMPUTED → BUCKET_EMPTY → ENTRY_INSERTED");
        }

        @Test
        @DisplayName("INSERT с коллизией (новый ключ): A → C → E → F")
        void testInsertCollisionNewKeyTrace() {
            hashTable.insert(1, 10);
            traceLog.clear();

            hashTable.insert(8, 80); // 8 % 7 = 1, коллизия

            List<HashTable.TracePoint> expected = List.of(
                    HashTable.TracePoint.HASH_COMPUTED,
                    HashTable.TracePoint.BUCKET_NOT_EMPTY,
                    HashTable.TracePoint.KEY_NOT_FOUND,
                    HashTable.TracePoint.ENTRY_INSERTED
            );
            assertEquals(expected, traceLog,
                    "Вставка с коллизией (новый ключ): HASH_COMPUTED → BUCKET_NOT_EMPTY → KEY_NOT_FOUND → ENTRY_INSERTED");
        }

        @Test
        @DisplayName("INSERT с обновлением (ключ существует): A → C → D → G")
        void testInsertUpdateTrace() {
            hashTable.insert(5, 50);
            traceLog.clear();

            hashTable.insert(5, 500);

            List<HashTable.TracePoint> expected = List.of(
                    HashTable.TracePoint.HASH_COMPUTED,
                    HashTable.TracePoint.BUCKET_NOT_EMPTY,
                    HashTable.TracePoint.KEY_FOUND,
                    HashTable.TracePoint.ENTRY_UPDATED
            );
            assertEquals(expected, traceLog,
                    "Обновление существующего ключа: HASH_COMPUTED → BUCKET_NOT_EMPTY → KEY_FOUND → ENTRY_UPDATED");
        }
    }

    @Nested
    @DisplayName("Прохождение характерных точек — SEARCH")
    class SearchTraceTest {

        @Test
        @DisplayName("SEARCH в пустой корзине: A → B → I")
        void testSearchEmptyBucketTrace() {
            traceLog.clear();
            hashTable.search(10);

            List<HashTable.TracePoint> expected = List.of(
                    HashTable.TracePoint.HASH_COMPUTED,
                    HashTable.TracePoint.BUCKET_EMPTY,
                    HashTable.TracePoint.SEARCH_RESULT_RETURNED
            );
            assertEquals(expected, traceLog,
                    "Поиск в пустой корзине: HASH_COMPUTED → BUCKET_EMPTY → SEARCH_RESULT_RETURNED");
        }

        @Test
        @DisplayName("SEARCH — ключ найден: A → C → D → I")
        void testSearchFoundTrace() {
            hashTable.insert(5, 50);
            traceLog.clear();

            hashTable.search(5);

            List<HashTable.TracePoint> expected = List.of(
                    HashTable.TracePoint.HASH_COMPUTED,
                    HashTable.TracePoint.BUCKET_NOT_EMPTY,
                    HashTable.TracePoint.KEY_FOUND,
                    HashTable.TracePoint.SEARCH_RESULT_RETURNED
            );
            assertEquals(expected, traceLog,
                    "Поиск найденного ключа: HASH_COMPUTED → BUCKET_NOT_EMPTY → KEY_FOUND → SEARCH_RESULT_RETURNED");
        }

        @Test
        @DisplayName("SEARCH — ключ не найден в непустой корзине: A → C → E → I")
        void testSearchNotFoundTrace() {
            hashTable.insert(1, 10);
            traceLog.clear();

            hashTable.search(22); // 22 % 7 = 1, корзина не пуста, но ключа нет

            List<HashTable.TracePoint> expected = List.of(
                    HashTable.TracePoint.HASH_COMPUTED,
                    HashTable.TracePoint.BUCKET_NOT_EMPTY,
                    HashTable.TracePoint.KEY_NOT_FOUND,
                    HashTable.TracePoint.SEARCH_RESULT_RETURNED
            );
            assertEquals(expected, traceLog,
                    "Поиск несуществующего ключа: HASH_COMPUTED → BUCKET_NOT_EMPTY → KEY_NOT_FOUND → SEARCH_RESULT_RETURNED");
        }
    }

    @Nested
    @DisplayName("Прохождение характерных точек — DELETE")
    class DeleteTraceTest {

        @Test
        @DisplayName("DELETE из пустой корзины: A → B → E")
        void testDeleteEmptyBucketTrace() {
            traceLog.clear();
            hashTable.delete(10);

            List<HashTable.TracePoint> expected = List.of(
                    HashTable.TracePoint.HASH_COMPUTED,
                    HashTable.TracePoint.BUCKET_EMPTY,
                    HashTable.TracePoint.KEY_NOT_FOUND
            );
            assertEquals(expected, traceLog,
                    "Удаление из пустой корзины: HASH_COMPUTED → BUCKET_EMPTY → KEY_NOT_FOUND");
        }

        @Test
        @DisplayName("DELETE — ключ найден: A → C → D → H")
        void testDeleteFoundTrace() {
            hashTable.insert(5, 50);
            traceLog.clear();

            hashTable.delete(5);

            List<HashTable.TracePoint> expected = List.of(
                    HashTable.TracePoint.HASH_COMPUTED,
                    HashTable.TracePoint.BUCKET_NOT_EMPTY,
                    HashTable.TracePoint.KEY_FOUND,
                    HashTable.TracePoint.ENTRY_DELETED
            );
            assertEquals(expected, traceLog,
                    "Удаление найденного ключа: HASH_COMPUTED → BUCKET_NOT_EMPTY → KEY_FOUND → ENTRY_DELETED");
        }

        @Test
        @DisplayName("DELETE — ключ не найден в непустой корзине: A → C → E")
        void testDeleteNotFoundInChainTrace() {
            hashTable.insert(1, 10);
            traceLog.clear();

            hashTable.delete(22); // 22 % 7 = 1, корзина не пуста, но ключа нет

            List<HashTable.TracePoint> expected = List.of(
                    HashTable.TracePoint.HASH_COMPUTED,
                    HashTable.TracePoint.BUCKET_NOT_EMPTY,
                    HashTable.TracePoint.KEY_NOT_FOUND
            );
            assertEquals(expected, traceLog,
                    "Удаление из непустой корзины (ключ не найден): HASH_COMPUTED → BUCKET_NOT_EMPTY → KEY_NOT_FOUND");
        }
    }

    @Nested
    @DisplayName("Хеш-функция")
    class HashFunctionTest {

        @Test
        @DisplayName("Хеш положительного числа")
        void testHashPositive() {
            assertEquals(3, hashTable.hash(10)); // 10 % 7 = 3
        }

        @Test
        @DisplayName("Хеш нуля")
        void testHashZero() {
            assertEquals(0, hashTable.hash(0));
        }

        @Test
        @DisplayName("Хеш отрицательного числа (корректный floorMod)")
        void testHashNegative() {
            // Math.floorMod(-3, 7) = 4 (не -3 % 7 = -3)
            assertEquals(4, hashTable.hash(-3));
        }

        @Test
        @DisplayName("Коллизия: ключи с одинаковым хешем")
        void testCollisionKeys() {
            assertEquals(hashTable.hash(1), hashTable.hash(8));
            assertEquals(hashTable.hash(1), hashTable.hash(15));
        }
    }

    @Nested
    @DisplayName("Граничные случаи")
    class BoundaryTest {

        @Test
        @DisplayName("Ёмкость 1 — все в одной корзине")
        void testCapacityOne() {
            HashTable ht = new HashTable(1);
            ht.insert(1, 10);
            ht.insert(2, 20);
            ht.insert(3, 30);
            assertEquals(3, ht.getSize());
            assertEquals(10, ht.search(1));
            assertEquals(20, ht.search(2));
            assertEquals(30, ht.search(3));
        }

        @Test
        @DisplayName("Невалидная ёмкость (0)")
        void testInvalidCapacityZero() {
            assertThrows(IllegalArgumentException.class, () -> new HashTable(0));
        }

        @Test
        @DisplayName("Невалидная ёмкость (отрицательная)")
        void testInvalidCapacityNegative() {
            assertThrows(IllegalArgumentException.class, () -> new HashTable(-5));
        }

        @Test
        @DisplayName("Невалидный индекс корзины")
        void testInvalidBucketIndex() {
            assertThrows(IndexOutOfBoundsException.class,
                    () -> hashTable.getBucketSize(-1));
            assertThrows(IndexOutOfBoundsException.class,
                    () -> hashTable.getBucketSize(7));
        }

        @Test
        @DisplayName("Очистка таблицы")
        void testClear() {
            hashTable.insert(1, 10);
            hashTable.insert(2, 20);
            hashTable.clear();
            assertEquals(0, hashTable.getSize());
            assertNull(hashTable.search(1));
            assertNull(hashTable.search(2));
        }

        @Test
        @DisplayName("containsKey")
        void testContainsKey() {
            hashTable.insert(5, 50);
            assertTrue(hashTable.containsKey(5));
            assertFalse(hashTable.containsKey(10));
        }

    }

    @Nested
    @DisplayName("Комплексные сценарии с трассировкой")
    class ComplexTraceTest {

        @Test
        @DisplayName("Сценарий: вставка → поиск → обновление → удаление")
        void testFullLifecycleTrace() {
            // Вставка нового элемента
            traceLog.clear();
            hashTable.insert(5, 50);
            assertEquals(List.of(
                    HashTable.TracePoint.HASH_COMPUTED,
                    HashTable.TracePoint.BUCKET_EMPTY,
                    HashTable.TracePoint.ENTRY_INSERTED
            ), traceLog);

            // Поиск существующего элемента
            traceLog.clear();
            Integer result = hashTable.search(5);
            assertEquals(50, result);
            assertEquals(List.of(
                    HashTable.TracePoint.HASH_COMPUTED,
                    HashTable.TracePoint.BUCKET_NOT_EMPTY,
                    HashTable.TracePoint.KEY_FOUND,
                    HashTable.TracePoint.SEARCH_RESULT_RETURNED
            ), traceLog);

            // Обновление значения
            traceLog.clear();
            hashTable.insert(5, 500);
            assertEquals(List.of(
                    HashTable.TracePoint.HASH_COMPUTED,
                    HashTable.TracePoint.BUCKET_NOT_EMPTY,
                    HashTable.TracePoint.KEY_FOUND,
                    HashTable.TracePoint.ENTRY_UPDATED
            ), traceLog);

            // Удаление элемента
            traceLog.clear();
            assertTrue(hashTable.delete(5));
            assertEquals(List.of(
                    HashTable.TracePoint.HASH_COMPUTED,
                    HashTable.TracePoint.BUCKET_NOT_EMPTY,
                    HashTable.TracePoint.KEY_FOUND,
                    HashTable.TracePoint.ENTRY_DELETED
            ), traceLog);

            // Поиск удалённого элемента
            traceLog.clear();
            assertNull(hashTable.search(5));
            assertEquals(List.of(
                    HashTable.TracePoint.HASH_COMPUTED,
                    HashTable.TracePoint.BUCKET_EMPTY,
                    HashTable.TracePoint.SEARCH_RESULT_RETURNED
            ), traceLog);
        }

        @Test
        @DisplayName("Сценарий: цепочка из 3 элементов — поиск каждого")
        void testChainSearchTrace() {
            // Вставляем 3 элемента в одну корзину: 1, 8, 15 (все % 7 = 1)
            hashTable.insert(1, 10);
            hashTable.insert(8, 80);
            hashTable.insert(15, 150);

            // Поиск первого элемента в цепочке (ключ 1)
            traceLog.clear();
            assertEquals(10, hashTable.search(1));
            assertEquals(List.of(
                    HashTable.TracePoint.HASH_COMPUTED,
                    HashTable.TracePoint.BUCKET_NOT_EMPTY,
                    HashTable.TracePoint.KEY_FOUND,
                    HashTable.TracePoint.SEARCH_RESULT_RETURNED
            ), traceLog, "Поиск первого элемента в цепочке");

            // Поиск несуществующего ключа в непустой корзине (ключ 22, 22 % 7 = 1)
            traceLog.clear();
            assertNull(hashTable.search(22));
            assertEquals(List.of(
                    HashTable.TracePoint.HASH_COMPUTED,
                    HashTable.TracePoint.BUCKET_NOT_EMPTY,
                    HashTable.TracePoint.KEY_NOT_FOUND,
                    HashTable.TracePoint.SEARCH_RESULT_RETURNED
            ), traceLog, "Поиск несуществующего ключа в занятой корзине");
        }
    }
}

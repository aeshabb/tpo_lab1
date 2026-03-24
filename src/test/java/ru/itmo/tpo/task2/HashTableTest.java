package ru.itmo.tpo.task2;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("Минимальные тесты хеш-таблицы с закрытой адресацией")
class HashTableTest {

    private HashTable hashTable;
    private List<HashTable.TracePoint> traceLog;

    @BeforeEach
    void setUp() {
        hashTable = new HashTable(7);
        traceLog = new ArrayList<>();
        hashTable.setTraceListener(traceLog::add);
    }

    @Test
    @DisplayName("INSERT в пустой слот: A -> B -> F")
    void insertIntoEmptySlot() {
        hashTable.insert(10, 100);

        assertEquals(List.of(
                HashTable.TracePoint.HASH_COMPUTED,
                HashTable.TracePoint.BUCKET_EMPTY,
                HashTable.TracePoint.ENTRY_INSERTED
        ), traceLog);
        assertEquals(1, hashTable.getSize());
        assertEquals(100, hashTable.search(10));
    }

    @Test
    @DisplayName("INSERT при коллизии нового ключа: A -> C -> E -> F")
    void insertWithCollision() {
        hashTable.insert(1, 10);
        traceLog.clear();

        hashTable.insert(8, 80);

        assertEquals(List.of(
                HashTable.TracePoint.HASH_COMPUTED,
                HashTable.TracePoint.BUCKET_NOT_EMPTY,
                HashTable.TracePoint.KEY_NOT_FOUND,
                HashTable.TracePoint.ENTRY_INSERTED
        ), traceLog);
        assertEquals(2, hashTable.getSize());
        assertEquals(80, hashTable.search(8));
        assertEquals(1, hashTable.getBucketSize(2));
    }

    @Test
    @DisplayName("INSERT обновляет существующий ключ: A -> C -> D -> G")
    void updateExistingKey() {
        hashTable.insert(5, 50);
        traceLog.clear();

        hashTable.insert(5, 500);

        assertEquals(List.of(
                HashTable.TracePoint.HASH_COMPUTED,
                HashTable.TracePoint.BUCKET_NOT_EMPTY,
                HashTable.TracePoint.KEY_FOUND,
                HashTable.TracePoint.ENTRY_UPDATED
        ), traceLog);
        assertEquals(1, hashTable.getSize());
        assertEquals(500, hashTable.search(5));
    }

    @Test
    @DisplayName("SEARCH в пустом слоте: A -> B -> I")
    void searchInEmptySlot() {
        assertNull(hashTable.search(1));
        assertEquals(List.of(
                HashTable.TracePoint.HASH_COMPUTED,
                HashTable.TracePoint.BUCKET_EMPTY,
                HashTable.TracePoint.SEARCH_RESULT_RETURNED
        ), traceLog);
    }

    @Test
    @DisplayName("SEARCH найденного ключа: A -> C -> D -> I")
    void searchExistingKey() {
        hashTable.insert(1, 10);
        hashTable.insert(8, 80);
        traceLog.clear();

        assertEquals(80, hashTable.search(8));
        assertEquals(List.of(
                HashTable.TracePoint.HASH_COMPUTED,
                HashTable.TracePoint.BUCKET_NOT_EMPTY,
                HashTable.TracePoint.KEY_FOUND,
                HashTable.TracePoint.SEARCH_RESULT_RETURNED
        ), traceLog);
    }

    @Test
    @DisplayName("SEARCH отсутствующего ключа после коллизии: A -> C -> E -> I")
    void searchMissingKeyInProbeSequence() {
        hashTable.insert(1, 10);
        hashTable.insert(8, 80);
        traceLog.clear();

        assertNull(hashTable.search(15));
        assertEquals(List.of(
                HashTable.TracePoint.HASH_COMPUTED,
                HashTable.TracePoint.BUCKET_NOT_EMPTY,
                HashTable.TracePoint.KEY_NOT_FOUND,
                HashTable.TracePoint.SEARCH_RESULT_RETURNED
        ), traceLog);
    }

    @Test
    @DisplayName("DELETE найденного ключа: A -> C -> D -> H")
    void deleteExistingKey() {
        hashTable.insert(1, 10);
        hashTable.insert(8, 80);
        traceLog.clear();

        assertTrue(hashTable.delete(8));
        assertEquals(List.of(
                HashTable.TracePoint.HASH_COMPUTED,
                HashTable.TracePoint.BUCKET_NOT_EMPTY,
                HashTable.TracePoint.KEY_FOUND,
                HashTable.TracePoint.ENTRY_DELETED
        ), traceLog);
        assertNull(hashTable.search(8));
    }

    @Test
    @DisplayName("DELETE из пустого слота: A -> B -> E")
    void deleteFromEmptySlot() {
        assertFalse(hashTable.delete(3));
        assertEquals(List.of(
                HashTable.TracePoint.HASH_COMPUTED,
                HashTable.TracePoint.BUCKET_EMPTY,
                HashTable.TracePoint.KEY_NOT_FOUND
        ), traceLog);
    }

    @Test
    @DisplayName("DELETE отсутствующего ключа в последовательности проб: A -> C -> E")
    void deleteMissingKeyInProbeSequence() {
        hashTable.insert(1, 10);
        hashTable.insert(8, 80);
        traceLog.clear();

        assertFalse(hashTable.delete(15));
        assertEquals(List.of(
                HashTable.TracePoint.HASH_COMPUTED,
                HashTable.TracePoint.BUCKET_NOT_EMPTY,
                HashTable.TracePoint.KEY_NOT_FOUND
        ), traceLog);
    }

    @Test
    @DisplayName("Удаленный слот повторно используется при вставке")
    void reusesDeletedSlot() {
        hashTable.insert(1, 10);
        hashTable.insert(8, 80);
        assertTrue(hashTable.delete(1));

        hashTable.insert(15, 150);

        assertEquals(2, hashTable.getSize());
        assertEquals(150, hashTable.search(15));
        assertEquals(1, hashTable.getBucketSize(1));
    }

    @Test
    @DisplayName("Граничные случаи: невалидная емкость")
    void handlesBoundaryCases() {
        assertThrows(IllegalArgumentException.class, () -> new HashTable(0));
    }

    @Test
    @DisplayName("Граничные случаи: отрицательный ключ")
    void negativeKey() {
        assertEquals(4, hashTable.hash(-3));
    }
}

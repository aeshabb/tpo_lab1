package ru.itmo.tpo.task2;

import java.util.LinkedList;

/**
 * Хеш-таблица с закрытой адресацией (метод цепочек / open hashing).
 * Каждый слот таблицы содержит связанный список элементов.
 *
 * Поддерживает операции: вставка (insert), поиск (search), удаление (delete).
 *
 * Визуализация: https://www.cs.usfca.edu/~galles/visualization/OpenHash.html
 */
public class HashTable {

    /**
     * Запись хеш-таблицы: ключ-значение.
     */
    public static class Entry {
        private final int key;
        private int value;

        public Entry(int key, int value) {
            this.key = key;
            this.value = value;
        }

        public int getKey() {
            return key;
        }

        public int getValue() {
            return value;
        }

        public void setValue(int value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "(" + key + "=" + value + ")";
        }
    }

    // Характерные точки алгоритма (для тестирования прохождения)
    public enum TracePoint {
        HASH_COMPUTED,           // A: Хеш вычислен
        BUCKET_EMPTY,            // B: Корзина пуста
        BUCKET_NOT_EMPTY,        // C: Корзина не пуста
        KEY_FOUND,               // D: Ключ найден в цепочке
        KEY_NOT_FOUND,           // E: Ключ не найден в цепочке
        ENTRY_INSERTED,          // F: Элемент вставлен
        ENTRY_UPDATED,           // G: Элемент обновлён
        ENTRY_DELETED,           // H: Элемент удалён
        SEARCH_RESULT_RETURNED   // I: Результат поиска возвращён
    }

    /**
     * Интерфейс для отслеживания прохождения характерных точек.
     */
    public interface TraceListener {
        void onTracePoint(TracePoint point);
    }

    private final LinkedList<Entry>[] table;
    private final int capacity;
    private int size;
    private TraceListener traceListener;

    /**
     * Создаёт хеш-таблицу с указанной ёмкостью.
     *
     * @param capacity количество корзин (buckets)
     * @throws IllegalArgumentException если capacity <= 0
     */
    @SuppressWarnings("unchecked")
    public HashTable(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Ёмкость должна быть положительной: " + capacity);
        }
        this.capacity = capacity;
        this.table = new LinkedList[capacity];
        this.size = 0;
    }

    /**
     * Устанавливает слушателя для отслеживания характерных точек алгоритма.
     */
    public void setTraceListener(TraceListener listener) {
        this.traceListener = listener;
    }

    private void trace(TracePoint point) {
        if (traceListener != null) {
            traceListener.onTracePoint(point);
        }
    }

    /**
     * Вычисляет хеш для ключа.
     * hash(key) = key mod capacity
     *
     * @param key ключ
     * @return индекс корзины
     */
    int hash(int key) {
        return Math.floorMod(key, capacity);
    }

    /**
     * Вставляет пару ключ-значение в хеш-таблицу.
     * Если ключ уже существует, обновляет значение.
     *
     * Алгоритм:
     * 1. Вычислить хеш → индекс корзины         [HASH_COMPUTED]
     * 2. Если корзина пуста → создать список      [BUCKET_EMPTY]
     *    Иначе → искать ключ в цепочке            [BUCKET_NOT_EMPTY]
     * 3. Если ключ найден → обновить значение     [KEY_FOUND, ENTRY_UPDATED]
     *    Если не найден → добавить в конец         [KEY_NOT_FOUND, ENTRY_INSERTED]
     *
     * @param key   ключ
     * @param value значение
     */
    public void insert(int key, int value) {
        int index = hash(key);
        trace(TracePoint.HASH_COMPUTED);

        if (table[index] == null) {
            trace(TracePoint.BUCKET_EMPTY);
            table[index] = new LinkedList<>();
            table[index].add(new Entry(key, value));
            size++;
            trace(TracePoint.ENTRY_INSERTED);
        } else {
            trace(TracePoint.BUCKET_NOT_EMPTY);
            for (Entry entry : table[index]) {
                if (entry.getKey() == key) {
                    trace(TracePoint.KEY_FOUND);
                    entry.setValue(value);
                    trace(TracePoint.ENTRY_UPDATED);
                    return;
                }
            }
            trace(TracePoint.KEY_NOT_FOUND);
            table[index].add(new Entry(key, value));
            size++;
            trace(TracePoint.ENTRY_INSERTED);
        }
    }

    /**
     * Ищет значение по ключу в хеш-таблице.
     *
     * Алгоритм:
     * 1. Вычислить хеш → индекс корзины         [HASH_COMPUTED]
     * 2. Если корзина пуста → вернуть null        [BUCKET_EMPTY, SEARCH_RESULT_RETURNED]
     *    Иначе → искать ключ в цепочке            [BUCKET_NOT_EMPTY]
     * 3. Если найден → вернуть значение           [KEY_FOUND, SEARCH_RESULT_RETURNED]
     *    Если не найден → вернуть null             [KEY_NOT_FOUND, SEARCH_RESULT_RETURNED]
     *
     * @param key ключ для поиска
     * @return значение или null, если ключ не найден
     */
    public Integer search(int key) {
        int index = hash(key);
        trace(TracePoint.HASH_COMPUTED);

        if (table[index] == null) {
            trace(TracePoint.BUCKET_EMPTY);
            trace(TracePoint.SEARCH_RESULT_RETURNED);
            return null;
        }

        trace(TracePoint.BUCKET_NOT_EMPTY);
        for (Entry entry : table[index]) {
            if (entry.getKey() == key) {
                trace(TracePoint.KEY_FOUND);
                trace(TracePoint.SEARCH_RESULT_RETURNED);
                return entry.getValue();
            }
        }

        trace(TracePoint.KEY_NOT_FOUND);
        trace(TracePoint.SEARCH_RESULT_RETURNED);
        return null;
    }

    /**
     * Удаляет элемент по ключу из хеш-таблицы.
     *
     * Алгоритм:
     * 1. Вычислить хеш → индекс корзины         [HASH_COMPUTED]
     * 2. Если корзина пуста → элемент не найден   [BUCKET_EMPTY, KEY_NOT_FOUND]
     *    Иначе → искать ключ в цепочке            [BUCKET_NOT_EMPTY]
     * 3. Если найден → удалить элемент            [KEY_FOUND, ENTRY_DELETED]
     *    Если не найден → ничего не делать         [KEY_NOT_FOUND]
     *
     * @param key ключ для удаления
     * @return true если элемент был удалён, false если не найден
     */
    public boolean delete(int key) {
        int index = hash(key);
        trace(TracePoint.HASH_COMPUTED);

        if (table[index] == null) {
            trace(TracePoint.BUCKET_EMPTY);
            trace(TracePoint.KEY_NOT_FOUND);
            return false;
        }

        trace(TracePoint.BUCKET_NOT_EMPTY);
        var iterator = table[index].iterator();
        while (iterator.hasNext()) {
            Entry entry = iterator.next();
            if (entry.getKey() == key) {
                trace(TracePoint.KEY_FOUND);
                iterator.remove();
                size--;
                if (table[index].isEmpty()) {
                    table[index] = null;
                }
                trace(TracePoint.ENTRY_DELETED);
                return true;
            }
        }

        trace(TracePoint.KEY_NOT_FOUND);
        return false;
    }

    /**
     * @return текущее количество элементов в таблице
     */
    public int getSize() {
        return size;
    }

    /**
     * @return ёмкость (количество корзин) таблицы
     */
    public int getCapacity() {
        return capacity;
    }

    /**
     * Проверяет, содержит ли таблица указанный ключ.
     */
    public boolean containsKey(int key) {
        return search(key) != null;
    }

    /**
     * Возвращает количество элементов в корзине по индексу.
     *
     * @param index индекс корзины
     * @return количество элементов в цепочке
     */
    public int getBucketSize(int index) {
        if (index < 0 || index >= capacity) {
            throw new IndexOutOfBoundsException("Индекс корзины вне диапазона: " + index);
        }
        return table[index] == null ? 0 : table[index].size();
    }

    /**
     * Очищает хеш-таблицу.
     */
    public void clear() {
        for (int i = 0; i < capacity; i++) {
            table[i] = null;
        }
        size = 0;
    }
}

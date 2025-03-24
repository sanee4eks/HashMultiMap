package ru.vsu.cs.demo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class HashMultiMapTest {

    private HashMultiMap<String, String> map;

    @BeforeEach
    void setUp() {
        map = new HashMultiMap<>();
    }

    // Тесты базовых операций
    @Test
    void testPutAndGet() {
        map.put("key1", "value1");
        map.put("key1", "value2");
        map.put("key2", "value3");

        Collection<String> values1 = map.get("key1");
        assertEquals(2, values1.size());
        assertTrue(values1.contains("value1"));
        assertTrue(values1.contains("value2"));

        Collection<String> values2 = map.get("key2");
        assertEquals(1, values2.size());
        assertTrue(values2.contains("value3"));

        Collection<String> values3 = map.get("nonexistent");
        assertTrue(values3.isEmpty());
    }

    @Test
    void testPutWithNullKey() {
        map.put(null, "nullValue1");
        map.put(null, "nullValue2");

        Collection<String> values = map.get(null);
        assertEquals(2, values.size());
        assertTrue(values.contains("nullValue1"));
        assertTrue(values.contains("nullValue2"));
    }

    @Test
    void testPutWithNullValue() {
        map.put("key", null);
        Collection<String> values = map.get("key");
        assertEquals(1, values.size());
        assertTrue(values.contains(null));
    }

    @Test
    void testRemoveKeyValue() {
        map.put("key", "value1");
        map.put("key", "value2");

        assertTrue(map.remove("key", "value1"));
        Collection<String> values = map.get("key");
        assertEquals(1, values.size());
        assertTrue(values.contains("value2"));

        assertFalse(map.remove("key", "nonexistent"));
        assertFalse(map.remove("nonexistent", "value"));
    }

    @Test
    void testRemoveKey() {
        map.put("key1", "value1");
        map.put("key1", "value2");
        map.put("key2", "value3");

        assertTrue(map.removeKey("key1"));
        assertFalse(map.containsKey("key1"));
        assertTrue(map.containsKey("key2"));

        assertFalse(map.removeKey("nonexistent"));
    }

    @Test
    void testClear() {
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.clear();

        assertTrue(map.isEmpty());
        assertEquals(0, map.size());
    }

    // Тесты проверки состояний
    @Test
    void testIsEmpty() {
        assertTrue(map.isEmpty());
        map.put("key", "value");
        assertFalse(map.isEmpty());
        map.remove("key", "value");
        assertTrue(map.isEmpty());
    }

    @Test
    void testContainsKey() {
        assertFalse(map.containsKey("key"));
        map.put("key", "value");
        assertTrue(map.containsKey("key"));
        assertFalse(map.containsKey("nonexistent"));
    }

    @Test
    void testContainsValue() {
        assertFalse(map.containsValue("value"));
        map.put("key", "value");
        assertTrue(map.containsValue("value"));
        assertFalse(map.containsValue("nonexistent"));
    }

    @Test
    void testContainsValueWithNull() {
        map.put("key", null);
        assertTrue(map.containsValue(null));
    }

    // Тесты коллекций
    @Test
    void testKeySet() {
        map.put("key1", "value1");
        map.put("key2", "value2");
        map.put("key1", "value3");

        Set<String> keys = map.keySet();
        assertEquals(2, keys.size());
        assertTrue(keys.contains("key1"));
        assertTrue(keys.contains("key2"));
    }

    @Test
    void testEntrySet() {
        map.put("key1", "value1");
        map.put("key1", "value2");
        map.put("key2", "value3");

        Set<Map.Entry<String, Collection<String>>> entries = map.entrySet();
        assertEquals(2, entries.size());

        for (Map.Entry<String, Collection<String>> entry : entries) {
            if (entry.getKey().equals("key1")) {
                assertEquals(2, entry.getValue().size());
                assertTrue(entry.getValue().contains("value1"));
                assertTrue(entry.getValue().contains("value2"));
            } else if (entry.getKey().equals("key2")) {
                assertEquals(1, entry.getValue().size());
                assertTrue(entry.getValue().contains("value3"));
            } else {
                Assertions.fail("Unexpected key in entry set");
            }
        }
    }

    // Тесты массовых операций
    @Test
    void testPutAllFromHashMultiMap() {
        HashMultiMap<String, String> other = new HashMultiMap<>();
        other.put("key1", "value1");
        other.put("key1", "value2");
        other.put("key2", "value3");

        map.putAll(other);

        assertEquals(2, map.size());
        assertEquals(2, map.get("key1").size());
        assertEquals(1, map.get("key2").size());
    }

    @Test
    void testPutAllFromJavaMap() {
        Map<String, Collection<String>> other = new HashMap<>();
        other.put("key1", Arrays.asList("value1", "value2"));
        other.put("key2", Collections.singletonList("value3"));

        map.putAll(other);

        assertEquals(2, map.size());
        assertEquals(2, map.get("key1").size());
        assertEquals(1, map.get("key2").size());
    }

    @Test
    void testReplaceAllValues() {
        map.put("key", "value1");
        map.put("key", "value2");

        map.replaceAllValues("key", Arrays.asList("newValue1", "newValue2"));

        Collection<String> values = map.get("key");
        assertEquals(2, values.size());
        assertTrue(values.contains("newValue1"));
        assertTrue(values.contains("newValue2"));
    }

    @Test
    void testReplaceAllValuesForNonexistentKey() {
        assertFalse(map.containsKey("key"));

        map.replaceAllValues("key", Arrays.asList("newValue1", "newValue2"));

        Collection<String> values = map.get("key");
        assertEquals(2, values.size(), "Should contain exactly 2 values");
        assertTrue(values.contains("newValue1"));
        assertTrue(values.contains("newValue2"));
    }
    @Test
    void testAddAllValues() {
        map.put("key", "value1");
        map.addAllValues("key", Arrays.asList("value2", "value3"));

        Collection<String> values = map.get("key");
        assertEquals(3, values.size());
        assertTrue(values.contains("value1"));
        assertTrue(values.contains("value2"));
        assertTrue(values.contains("value3"));
    }

    @Test
    void testAddAllValuesForNonexistentKey() {
        // Убедимся, что ключа нет перед тестом
        assertFalse(map.containsKey("key"));

        map.addAllValues("key", Arrays.asList("value1", "value2"));

        Collection<String> values = map.get("key");
        assertEquals(2, values.size(), "Should contain exactly 2 values");
        assertTrue(values.contains("value1"));
        assertTrue(values.contains("value2"));
    }

    // Тесты на граничные случаи
    @Test
    void testResize() {
        // Проверяем, что resize не ломает структуру данных
        for (int i = 0; i < 1000; i++) {
            map.put("key" + i, "value" + i);
        }

        assertEquals(1000, map.size());
        for (int i = 0; i < 1000; i++) {
            assertTrue(map.containsKey("key" + i));
            assertEquals(1, map.get("key" + i).size());
            assertTrue(map.get("key" + i).contains("value" + i));
        }
    }

    @Test
    void testToString() {
        map.put("key1", "value1");
        map.put("key1", "value2");
        map.put("key2", "value3");

        String str = map.toString();
        assertTrue(str.contains("key1 → [value1, value2]") || str.contains("key1 → [value2, value1]"));
        assertTrue(str.contains("key2 → [value3]"));
    }

    // Тесты на иммутабельность возвращаемых коллекций
    @Test
    void testGetReturnsUnmodifiableCollection() {
        map.put("key", "value");
        Collection<String> values = map.get("key");

        Assertions.assertThrows(UnsupportedOperationException.class, () -> values.add("newValue"));
    }

    @Test
    void testKeySetReturnsUnmodifiableSet() {
        map.put("key", "value");
        Set<String> keys = map.keySet();

        Assertions.assertThrows(UnsupportedOperationException.class, () -> keys.add("newKey"));
    }

    @Test
    void testEntrySetReturnsUnmodifiableSet() {
        map.put("key", "value");
        Set<Map.Entry<String, Collection<String>>> entries = map.entrySet();

        assertThrows(UnsupportedOperationException.class, () -> entries.add(null));

        Map.Entry<String, Collection<String>> entry = entries.iterator().next();
        assertThrows(UnsupportedOperationException.class, () -> entry.setValue(new ArrayList<>()));
    }
}
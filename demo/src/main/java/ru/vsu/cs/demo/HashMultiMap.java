package ru.vsu.cs.demo;

import java.io.Serializable;
import java.util.*;

public class HashMultiMap<K, V> implements Serializable {
    private static final int INITIAL_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;

    private Entry<K, V>[] table;
    private int size;
    private int threshold;

    public HashMultiMap() {
        this.table = new Entry[INITIAL_CAPACITY];
        this.threshold = (int) (INITIAL_CAPACITY * LOAD_FACTOR);
    }

    private static class Entry<K, V> implements Serializable {
        final K key;
        final List<V> values;
        Entry<K, V> next;

        Entry(K key, V value, Entry<K, V> next) {
            this.key = key;
            this.values = new ArrayList<>();
            this.values.add(value);
            this.next = next;
        }
    }

    private int hash(Object key) {
        return (key == null) ? 0 : Math.abs(key.hashCode() % table.length);
    }

    public void put(K key, V value) {
        int index = hash(key);
        Entry<K, V> current = table[index];
        while (current != null) {
            if (Objects.equals(current.key, key)) {
                current.values.add(value);
                return;
            }
            current = current.next;
        }
        table[index] = new Entry<>(key, value, table[index]);
        size++;
        if (size > threshold) {
            resize();
        }
    }

    public Collection<V> get(K key) {
        int index = hash(key);
        Entry<K, V> current = table[index];
        while (current != null) {
            if (Objects.equals(current.key, key)) {
                return Collections.unmodifiableCollection(current.values);
            }
            current = current.next;
        }
        return Collections.emptyList();
    }

    public boolean remove(K key, V value) {
        int index = hash(key);
        Entry<K, V> current = table[index];
        Entry<K, V> prev = null;

        while (current != null) {
            if (Objects.equals(current.key, key)) {
                boolean removed = current.values.remove(value);
                if (current.values.isEmpty()) {
                    if (prev == null) {
                        table[index] = current.next;
                    } else {
                        prev.next = current.next;
                    }
                    size--;
                }
                return removed;
            }
            prev = current;
            current = current.next;
        }
        return false;
    }

    public boolean removeKey(K key) {
        int index = hash(key);
        Entry<K, V> current = table[index];
        Entry<K, V> prev = null;

        while (current != null) {
            if (Objects.equals(current.key, key)) {
                if (prev == null) {
                    table[index] = current.next;
                } else {
                    prev.next = current.next;
                }
                size--;
                return true;
            }
            prev = current;
            current = current.next;
        }
        return false;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public Set<K> keySet() {
        Set<K> keys = new HashSet<>();
        for (Entry<K, V> entry : table) {
            while (entry != null) {
                keys.add(entry.key);
                entry = entry.next;
            }
        }
        return Collections.unmodifiableSet(keys);
    }

    public boolean containsKey(K key) {
        int index = hash(key);
        Entry<K, V> current = table[index];
        while (current != null) {
            if (Objects.equals(current.key, key)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    public boolean containsValue(V value) {
        for (Entry<K, V> entry : table) {
            while (entry != null) {
                if (entry.values.contains(value)) {
                    return true;
                }
                entry = entry.next;
            }
        }
        return false;
    }

    public void clear() {
        Arrays.fill(table, null);
        size = 0;
    }

    private void resize() {
        int newCapacity = table.length * 2;
        Entry<K, V>[] newTable = new Entry[newCapacity];

        for (Entry<K, V> entry : table) {
            while (entry != null) {
                int newIndex = Math.abs(entry.key.hashCode() % newCapacity);

                Entry<K, V> nextEntry = entry.next;
                entry.next = newTable[newIndex];
                newTable[newIndex] = entry;

                entry = nextEntry;
            }
        }

        table = newTable;
        threshold *= 2;
    }

    public Set<Map.Entry<K, Collection<V>>> entrySet() {
        Set<Map.Entry<K, Collection<V>>> entrySet = new HashSet<>();
        for (Entry<K, V> entry : table) {
            while (entry != null) {
                entrySet.add(new AbstractMap.SimpleEntry<>(entry.key, Collections.unmodifiableCollection(entry.values)));
                entry = entry.next;
            }
        }
        return Collections.unmodifiableSet(entrySet);
    }

    public void putAll(HashMultiMap<K, V> other) {
        if (other != null) {
            for (Map.Entry<K, Collection<V>> entry : other.entrySet()) {
                K key = entry.getKey();
                Collection<V> values = entry.getValue();
                if (values != null) {
                    for (V value : values) {
                        put(key, value);
                    }
                }
            }
        }
    }

    public void putAll(Map<K, Collection<V>> other) {
        if (other != null) {
            for (Map.Entry<K, Collection<V>> entry : other.entrySet()) {
                K key = entry.getKey();
                Collection<V> values = entry.getValue();
                if (values != null) {
                    for (V value : values) {
                        put(key, value);
                    }
                }
            }
        }
    }

    public void replaceAllValues(K key, Collection<V> values) {
        int index = hash(key);
        Entry<K, V> current = table[index];
        while (current != null) {
            if (Objects.equals(current.key, key)) {
                current.values.clear();
                current.values.addAll(values);
                return;
            }
            current = current.next;
        }
        Entry<K, V> newEntry = new Entry<>(key, values.iterator().next(), table[index]);
        newEntry.values.addAll(values);
        table[index] = newEntry;
    }

    public void addAllValues(K key, Collection<V> values) {
        int index = hash(key);
        Entry<K, V> current = table[index];
        while (current != null) {
            if (Objects.equals(current.key, key)) {
                current.values.addAll(values);
                return;
            }
            current = current.next;
        }
        Entry<K, V> newEntry = new Entry<>(key, values.iterator().next(), table[index]);
        newEntry.values.addAll(values);
        table[index] = newEntry;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");

        for (Entry<K, V> bucket : table) {
            while (bucket != null) {
                sb.append(bucket.key).append(" → ").append(bucket.values).append(", ");
                bucket = bucket.next;
            }
        }

        if (sb.length() > 1) {
            sb.setLength(sb.length() - 2);
        }

        sb.append("}");
        return sb.toString();
    }
}

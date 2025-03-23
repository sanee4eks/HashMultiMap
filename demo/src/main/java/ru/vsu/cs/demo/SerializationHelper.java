package ru.vsu.cs.demo;

import java.io.*;


public class SerializationHelper {

    public static <K, V> void serialize(HashMultiMap<K, V> map, String filename) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(map);
        }
    }

    public static <K, V> HashMultiMap<K, V> deserialize(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            return (HashMultiMap<K, V>) in.readObject();
        }
    }
}

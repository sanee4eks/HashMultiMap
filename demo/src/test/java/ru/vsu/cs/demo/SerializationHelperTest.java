package ru.vsu.cs.demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

public class SerializationHelperTest {

    @Test
    void testSerializeAndDeserialize() throws IOException, ClassNotFoundException {
        HashMultiMap<String, String> original = new HashMultiMap<>();
        original.put("key1", "value1");
        original.put("key1", "value2");
        original.put("key2", "value3");

        String filename = "test_serialization.dat";
        SerializationHelper.serialize(original, filename);
        HashMultiMap<String, String> deserialized = SerializationHelper.deserialize(filename);

        assertEquals(original.size(), deserialized.size());
        assertEquals(original.get("key1").size(), deserialized.get("key1").size());
        assertTrue(deserialized.get("key1").contains("value1"));
        assertTrue(deserialized.get("key1").contains("value2"));
        assertEquals(original.get("key2").size(), deserialized.get("key2").size());
        assertTrue(deserialized.get("key2").contains("value3"));
    }
}
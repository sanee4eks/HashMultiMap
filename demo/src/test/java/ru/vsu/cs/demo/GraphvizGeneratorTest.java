package ru.vsu.cs.demo;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class GraphvizGeneratorTest {

    @Test
    void testToGraphviz() {
        GraphvizGenerator<String, String> generator = new GraphvizGenerator<>();
        generator.setGraphStyle("digraph G { rankdir=LR; node[shape=box];");

        HashMultiMap<String, String> map = new HashMultiMap<>();
        map.put("key1", "value1");
        map.put("key1", "value2");
        map.put("key2", "value3");

        String dot = generator.toGraphviz(map);

        assertNotNull(dot, "DOT string should not be null");
        assertTrue(dot.contains("digraph"), "Should contain digraph declaration");
        assertTrue(dot.contains("rankdir=LR"), "Should contain rankdir setting");
        assertTrue(dot.contains("node[shape=box]"), "Should contain node shape setting");

        assertTrue(dot.matches("(?s).*key1.*value1.*"), "Should contain key1->value1");
        assertTrue(dot.matches("(?s).*key1.*value2.*"), "Should contain key1->value2");
        assertTrue(dot.matches("(?s).*key2.*value3.*"), "Should contain key2->value3");
    }
}
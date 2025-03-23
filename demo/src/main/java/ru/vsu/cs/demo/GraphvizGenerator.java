package ru.vsu.cs.demo;


public class GraphvizGenerator<K, V> {

    private String graphvizStyle = "digraph G {\nrankdir=LR;\nnode[shape=box];\n";

    public String toGraphviz(HashMultiMap<K, V> map) {
        return toGraphviz(map, graphvizStyle);
    }

    public String toGraphviz(HashMultiMap<K, V> map, String style) {
        StringBuilder sb = new StringBuilder(style);
        for (K key : map.keySet()) {
            String nodeId = "key_" + key.hashCode();
            sb.append(String.format("  %s [label=\"%s\", style=filled, fillcolor=lightblue];\n",
                    nodeId, key));
            for (V value : map.get(key)) {
                String valueId = "value_" + value.hashCode();
                sb.append(String.format("  %s [label=\"%s\", style=filled, fillcolor=lightgreen];\n",
                        valueId, value));
                sb.append(String.format("  %s -> %s [arrowhead=empty];\n", nodeId, valueId));
            }
        }
        sb.append("}");
        return sb.toString();
    }

    public void setGraphStyle(String style) {
        this.graphvizStyle = style;
    }

    public String getStyle() {
        return graphvizStyle;
    }
}

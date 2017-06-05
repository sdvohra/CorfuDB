package org.corfudb.samples.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by shriyav on 5/25/17.
 */

public class Node {
    private String name;
    private ArrayList<Edge> edges;
    HashMap<String, Object> properties;

    public Node() {
        name = "";
        edges = new ArrayList<>();
    }

    public Node(String n) {
        name = n;
        edges = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public ArrayList<Edge> getEdges() {
        return edges;
    }

    public void setName(String n) {
        name = n;
    }

    public void addEdge(Edge e) {
        edges.add(e);
    }

    public Map<String, Object> getProperties() {
        return properties;
    }
}

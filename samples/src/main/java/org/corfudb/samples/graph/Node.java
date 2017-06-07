package org.corfudb.samples.graph;

import java.util.*;

/**
 * Created by shriyav on 5/25/17.
 */

public class Node {
    UUID id;
    String name;
    HashSet<UUID> edges;
    HashMap<String, Object> properties;

    public Node() {
        id = UUID.randomUUID();
        name = "";
        edges = new HashSet<>();
        properties = new HashMap<>();
    }

    public Node(UUID uuid) {
        id = uuid;
        name = "";
        edges = new HashSet<>();
        properties = new HashMap<>();
    }

    public Node(UUID uuid, String n) {
        id = uuid;
        name = n;
        edges = new HashSet<>();
        properties = new HashMap<>();
    }

    public Node(UUID uuid, String n, HashMap<String, Object> props) {
        id = uuid;
        name = n;
        edges = new HashSet<>();
        properties = props;
    }

    public UUID getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public HashSet<UUID> getEdges() {
        return edges;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setID(UUID uuid) {
        id = uuid;
    }

    public void setName(String n) {
        name = n;
    }

    public void addEdge(UUID e) {
        edges.add(e);
    }

    public void setProperties(HashMap<String, Object> props) {
        properties = props;
    }
}

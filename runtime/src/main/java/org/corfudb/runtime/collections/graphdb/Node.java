package org.corfudb.runtime.collections.graphdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by shriyav on 5/25/17.
 */

public class Node {
    UUID id;
    String name;
    ArrayList<UUID> edges; // sequential data structure for testing purposes
    Map<String, Object> properties;

    public Node(UUID uuid, String n) {
        id = uuid;
        name = n;
        edges = new ArrayList<>();
        properties = new HashMap<>();
    }

    public UUID getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    public ArrayList<UUID> getEdges() {
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

    public void addEdge(Node n) {
        edges.add(n.getID());
        n.getEdges().add(this.getID());
    }

    public void setProperties(HashMap<String, Object> props) {
        properties = props;
    }

    public void removeEdge(UUID e) { edges.remove(e); }
}

package org.corfudb.runtime.collections.graphdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A single data element in a GraphDB.
 *
 * @author shriyav
 */

public class Node {
    UUID id;
    String name;
    ArrayList<UUID> edges; // sequential data structure for testing purposes
    ArrayList<UUID> parents;
    ArrayList<UUID> children;
    Map<String, Object> properties;

    public Node(UUID uuid, String n) {
        id = uuid;
        name = n;
        //edges = new ArrayList<>();
        parents = new ArrayList<>();
        children = new ArrayList<>();
        properties = new HashMap<>();
    }

    public UUID getID() {
        return id;
    }

    public String getName() {
        return name;
    }

    //public ArrayList<UUID> getEdges() {
        //return edges;
    //}

    public ArrayList<UUID> getParents() {
        return parents;
    }

    public ArrayList<UUID> getChildren() {
        return children;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setID(UUID uuid) { id = uuid; }

    public void setName(String n) { name = n; }

    public void addEdge(Node n) {
        //edges.add(n.getID());
        //n.getEdges().add(this.getID());

        this.children.add(n.getID());
        n.getParents().add(this.getID());
    }

    public void setProperties(HashMap<String, Object> props) {
        properties = props;
    }

    public void removeEdge(Node n) {
        //edges.remove(e);
        this.children.remove(n.getID());
        n.getParents().remove(this.getID());
    }
}

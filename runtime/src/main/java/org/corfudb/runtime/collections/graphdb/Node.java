package org.corfudb.runtime.collections.graphdb;

import java.util.ArrayList;

/**
 * A single data element in a GraphDB.
 *
 * @author shriyav
 */

public class Node {
    private Object value;
    ArrayList<Node> inward;
    ArrayList<Node> outward;

    public Node(Object val) {
        value = val;
        inward = new ArrayList<>();
        outward = new ArrayList<>();
    }

    public Integer getID() { return value.hashCode(); }

    public Object getValue() {
        return value;
    }

    public ArrayList<Node> getInward() {
        return inward;
    }

    public ArrayList<Node> getOutward() {
        return outward;
    }

    public void addEdge(Node n) {
        this.outward.add(n);
        n.getInward().add(this);
    }

    public void removeEdge(Node n) {
        this.outward.remove(n);
        n.getInward().remove(this);
    }
}

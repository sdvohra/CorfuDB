package org.corfudb.runtime.collections.graphdb;

import java.util.ArrayList;
import java.util.List;

/**
 * A single data element in a GraphDB.
 *
 * @author shriyav
 */

public class Node {
    private Object value;
    List<Integer> inward;
    List<Integer> outward;

    public Node(Object val) {
        value = val;
        inward = new ArrayList<>();
        outward = new ArrayList<>();
    }

    public Integer getID() { return value.hashCode(); }

    public Object getValue() {
        return value;
    }

    public List<Integer> getInward() {
        return inward;
    }

    public List<Integer> getOutward() {
        return outward;
    }

    public void addEdge(Node n) {
        this.outward.add(n.getID());
        n.getInward().add(this.getID());
    }

    public void removeEdge(Node n) {
        this.outward.remove(n.getID());
        n.getInward().remove(this.getID());
    }
}

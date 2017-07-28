package org.corfudb.runtime.collections.graphdb;

import java.util.HashSet;
import java.util.Set;

/**
 * A single data element in a GraphDB.
 *
 * @author shriyav
 */

public class Node {
    private Object value;
    Set<Integer> inward;
    Set<Integer> outward;

    public Node(Object val) {
        value = val;
        inward = new HashSet<>();
        outward = new HashSet<>();
    }

    public Integer getID() {
        if (value instanceof Component) {
            return ((Component) value).getId().hashCode();
        }
        return value.hashCode();
    }

    public Object getValue() {
        return value;
    }

    public Set<Integer> getInward() {
        return inward;
    }

    public Set<Integer> getOutward() {
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

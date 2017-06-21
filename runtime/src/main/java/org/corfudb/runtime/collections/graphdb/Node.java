package org.corfudb.runtime.collections.graphdb;

import java.util.ArrayList;

/**
 * A single data element in a GraphDB.
 *
 * @author shriyav
 */

public class Node {
    Object value;
    ArrayList<Integer> parents;
    ArrayList<Integer> children;

    public Node(Object val) {
        value = val;
        parents = new ArrayList<>();
        children = new ArrayList<>();
    }

    public Integer getID() { return value.hashCode(); }

    public ArrayList<Integer> getParents() {
        return parents;
    }

    public ArrayList<Integer> getChildren() {
        return children;
    }

    public void addEdge(Node n) {
        this.children.add(n.getID());
        n.getParents().add(this.getID());
    }

    public void removeEdge(Node n) {
        this.children.remove(n.getID());
        n.getParents().remove(this.getID());
    }
}

package org.corfudb.runtime.collections.graphdb;

/**
 * Represents an edge between two Components in MapsGraphDB.
 *
 * @author shriyav
 */

public class Edge {
    Component from;
    Component to;

    public Edge(Component f, Component t) {
        from = f;
        to = t;
    }

    public Component getFrom() {
        return from;
    }

    public Component getTo() {
        return to;
    }

    @Override
    public int hashCode() {
        return from.hashCode()*2 + to.hashCode();
    }
}

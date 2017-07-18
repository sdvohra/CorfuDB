package org.corfudb.runtime.collections.graphdb;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

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
        Hasher hasher = Hashing.crc32c().newHasher();
        hasher.putLong(from.getId().getLeastSignificantBits());
        hasher.putLong(from.getId().getMostSignificantBits());
        hasher.putLong(to.getId().getLeastSignificantBits());
        hasher.putLong(to.getId().getMostSignificantBits());

        return hasher.hash().asInt();
    }
}

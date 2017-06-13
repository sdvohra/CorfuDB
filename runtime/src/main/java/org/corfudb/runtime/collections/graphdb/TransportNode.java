package org.corfudb.runtime.collections.graphdb;

import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author mdhawan
 * edited by shriyav
 */
@Data
@ToString
public class TransportNode extends Node {
    Set<UUID> transportZoneIds;

    public TransportNode(UUID uuid, String n) {
        super(uuid, n);
        transportZoneIds = new HashSet<>();
    }

    @Override
    public void addEdge(Node n) {
        super.addEdge(n);
        transportZoneIds.add(n.getID());
    }
}

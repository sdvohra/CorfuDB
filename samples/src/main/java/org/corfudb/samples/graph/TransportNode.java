package org.corfudb.samples.graph;

import lombok.Data;
import lombok.ToString;

import java.util.*;

/**
 * @author mdhawan
 * edited by shriyav
 */
@Data
@ToString
public class TransportNode extends Node {
    Set<UUID> transportZoneIds;

    public TransportNode() {
        super();
        transportZoneIds = new HashSet<>();
    }

    public TransportNode(UUID uuid) {
        super(uuid);
        transportZoneIds = new HashSet<>();
    }

    public TransportNode(UUID uuid, String n) {
        super(uuid, n);
        transportZoneIds = new HashSet<>();
    }

    public TransportNode(UUID uuid, String n, HashMap<String, Object> props) {
        super(uuid, n, props);
        transportZoneIds = new HashSet<>();
    }

    public void addTransportZoneID(UUID uuid) {
        transportZoneIds.add(uuid);
    }
}

package org.corfudb.runtime.collections.graphdb;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * A distinct L2 network created in the context of a transport zone.
 * A logical switch carries with it several feature properties that
 * are managed by a network profile.
 *
 * @author mdhawan
 * edited by shriyav
 */
@Data
@ToString
public class LogicalSwitch extends Node {
    // Transport zone which is the diameter of the network. A switch belongs to
    // one transport zone
    UUID tzId;
    List<UUID> profiles;

    public LogicalSwitch(UUID uuid, String n) {
        super(uuid, n);
        tzId = null;
        profiles = new ArrayList<>();
    }

    @Override
    public void addEdge(Node n) {
        super.addEdge(n);
        tzId = n.getID();
    }
}

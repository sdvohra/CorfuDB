package org.corfudb.runtime.collections.graphdb;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * @author mdhawan
 * edited by shriyav
 */
@Data
@ToString
public class LogicalSwitch extends Node {
    // Transport zone which is the diameter of the network. A switch belongs to
    // one transport zone
    UUID tzId; // will be modified when adding Edges
    List<UUID> profiles;

    public LogicalSwitch() {
        super();
        tzId = null;
        profiles = new ArrayList<>();
    }

    public LogicalSwitch(UUID uuid) {
        super(uuid);
        tzId = null;
        profiles = new ArrayList<>();
    }

    public LogicalSwitch(UUID uuid, String n) {
        super(uuid, n);
        tzId = null;
        profiles = new ArrayList<>();
    }

    public LogicalSwitch(UUID uuid, String n, HashMap<String, Object> props) {
        super(uuid, n, props);
        tzId = null;
        profiles = new ArrayList<>();
    }

    public void connectToTZ(UUID uuid) {
        tzId = uuid;
    }
}

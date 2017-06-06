package org.corfudb.samples.graph;

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
public class LogicalPort extends Node {
    UUID logicalSwitchId; // will be modified when adding Edges
    Attachment attachment;
    List<UUID> profiles;

    public LogicalPort() {
        super();
        logicalSwitchId = null;
        attachment = null;
        profiles = new ArrayList<>();
    }

    public LogicalPort(UUID uuid) {
        super(uuid);
        logicalSwitchId = null;
        attachment = null;
        profiles = new ArrayList<>();
    }

    public LogicalPort(UUID uuid, String n) {
        super(uuid, n);
        logicalSwitchId = null;
        attachment = null;
        profiles = new ArrayList<>();
    }

    public LogicalPort(UUID uuid, String n, HashMap<String, Object> props) {
        super(uuid, n, props);
        logicalSwitchId = null;
        attachment = null;
        profiles = new ArrayList<>();
    }

    public void connectToLS(UUID uuid) {
        logicalSwitchId = uuid;
    }
}
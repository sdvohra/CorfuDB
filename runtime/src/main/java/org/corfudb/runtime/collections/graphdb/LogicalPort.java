package org.corfudb.runtime.collections.graphdb;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * A logical port is an entity created in the context of a logical
 * switch. A port would represent a point where a VM attaches itself
 * to a switch.
 *
 * @author mdhawan
 * edited by shriyav
 */
@Data
@ToString
public class LogicalPort extends Node {
    UUID logicalSwitchId;
    Attachment attachment;
    List<UUID> profiles;

    public LogicalPort(UUID uuid, String n) {
        super(uuid, n);
        logicalSwitchId = null;
        attachment = null;
        profiles = new ArrayList<>();
    }

    @Override
    public void addEdge(Node n) {
        super.addEdge(n);
        logicalSwitchId = n.getID();
    }
}
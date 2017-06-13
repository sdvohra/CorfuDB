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
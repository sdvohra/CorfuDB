package org.corfudb.runtime.collections.graphdb;

import java.util.UUID;

/**
 * A container class for Transport Zones, Transport Nodes,
 * Logical Switches, and Logical Ports.
 *
 * @author shriyav
 */

public abstract class Component {
    public abstract UUID getId();
}

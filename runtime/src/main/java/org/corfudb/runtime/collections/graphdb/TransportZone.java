package org.corfudb.runtime.collections.graphdb;

import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.UUID;

/**
 * A transport zone is a select set of transport nodes that
 * define the max span of a network. It is the container
 * construct for a logical switch.
 *
 * @author mdhawan
 * edited by shriyav
 */
@Data
@ToString
public class TransportZone extends Node {
    public TransportZone(UUID uuid, String n) {
        super(uuid, n);
    }
}

package org.corfudb.runtime.collections.graphdb;

import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.UUID;

/**
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

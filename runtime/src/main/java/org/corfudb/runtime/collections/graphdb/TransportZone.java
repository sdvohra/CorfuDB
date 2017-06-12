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
    public TransportZone() {
        super();
    }

    public TransportZone(UUID uuid) {
        super(uuid);
    }

    public TransportZone(UUID uuid, String n) {
        super(uuid, n);
    }

    public TransportZone(UUID uuid, String n, HashMap<String, Object> props) {
        super(uuid, n, props);
    }

    public static void main(String[] args) {
        TransportZone tz = new TransportZone(UUID.randomUUID(), "hi");
        tz.getEdges();
    }

//    public Map<String, String> getProperties() {
//        return null; // consider writing a fn that will return <str, str> instead of <str, obj> ?
//    }

}

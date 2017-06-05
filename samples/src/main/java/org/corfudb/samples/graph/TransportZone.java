package org.corfudb.samples.graph;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.Map;
import java.util.UUID;

/**
 * @see L2Model.txt
 * @author mdhawan
 *
 */
@Data
@AllArgsConstructor
@ToString
public class TransportZone extends Node {
    UUID id;

//    public Map<String, String> getProperties() {
//        return null; // consider writing a fn that will return <str, str> instead of <str, obj> ?
//    }
}

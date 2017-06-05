package org.corfudb.samples.graph;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @see L2Model.txt
 * @author mdhawan
 *
 */
@Data
@AllArgsConstructor
@ToString
public class TransportNode extends Node {
    UUID id;
    Set<UUID> transportZoneIds;

    public void setProperties(HashMap<String, Object> props) {
        properties = props;
    }
}

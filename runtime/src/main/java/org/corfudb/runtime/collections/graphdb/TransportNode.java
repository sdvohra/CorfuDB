package org.corfudb.runtime.collections.graphdb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * It is a host (hypervisor) which has been prepared for
 * NSX connectivity and initialization.
 *
 * @author mdhawan
 * edited by shriyav
 */
@Data
@AllArgsConstructor
@ToString
public class TransportNode {
    UUID id;
    Set<UUID> transportZoneIds;
    String name;
    Map<String, Object> properties;
}

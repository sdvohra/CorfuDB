package org.corfudb.runtime.collections.graphdb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * A distinct L2 network created in the context of a transport zone.
 * A logical switch carries with it several feature properties that
 * are managed by a network profile.
 *
 * @author mdhawan
 * edited by shriyav
 */
@Data
@AllArgsConstructor
@ToString
public class LogicalSwitch {
    UUID id;
    String name;
    List<UUID> profiles;
    Map<String, Object> properties;
}

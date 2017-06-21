package org.corfudb.runtime.collections.graphdb;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;
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
@AllArgsConstructor
@ToString
public class LogicalPort {
    UUID id;
    String name;
    UUID logicalSwitchId;
    Attachment attachment;
    List<UUID> profiles;
    Map<String, Object> properties;
}
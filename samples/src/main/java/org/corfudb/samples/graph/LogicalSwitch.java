package org.corfudb.samples.graph;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.List;
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
public class LogicalSwitch extends Node {
    UUID id;
    // Transport zone which is the diameter of the network. A switch belongs to
    // one transport zone
    UUID tzId;
    List<UUID> profiles;
}

package org.corfudb.samples.graph;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Created by shriyav on 5/31/17.
 */
@Data
@AllArgsConstructor
@ToString
public class LogicalPort extends Node {
    UUID id;
    UUID logicalSwitchId;
    Attachment attachment;
    List<UUID> profiles;
}
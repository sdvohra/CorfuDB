package org.corfudb.runtime.collections.graphdb;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

/**
 * @author mdhawan
 */
@Data
@AllArgsConstructor
public class Attachment {
    UUID id;
    String type;
}

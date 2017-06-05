package org.corfudb.samples.graph;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

/**
 * @see L2Model.txt
 * @author mdhawan
 *
 */
@Data
@AllArgsConstructor
public class Attachment {
    UUID id;
    String type;
}

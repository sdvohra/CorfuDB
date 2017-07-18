package org.corfudb.runtime.collections.graphdb;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
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
public class LogicalPort extends Component {
    UUID id;
    String name;
    Attachment attachment;
    List<UUID> profiles;
    Map<String, Object> properties;

    @Override
    public int hashCode() {
        Hasher hasher = Hashing.crc32c().newHasher();
        hasher.putLong(id.getLeastSignificantBits());
        hasher.putLong(id.getMostSignificantBits());

        return hasher.hash().asInt();
    }
}
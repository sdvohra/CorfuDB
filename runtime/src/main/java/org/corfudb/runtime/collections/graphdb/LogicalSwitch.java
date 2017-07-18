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
public class LogicalSwitch extends Component {
    UUID id;
    String name;
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

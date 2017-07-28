package org.corfudb.runtime.collections.graphdb;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.Map;
import java.util.UUID;

/**
 * A transport zone is a select set of transport nodes that
 * define the max span of a network. It is the container
 * construct for a logical switch.
 *
 * @author mdhawan
 * edited by shriyav
 */
@Data
@AllArgsConstructor
@ToString
public class TransportZone extends Component {
    UUID id;
    String name;
    Map<String, String> properties;

    @Override
    public int hashCode() {
        Hasher hasher = Hashing.crc32c().newHasher();
        hasher.putLong(id.getLeastSignificantBits());
        hasher.putLong(id.getMostSignificantBits());

        return hasher.hash().asInt();
    }
}

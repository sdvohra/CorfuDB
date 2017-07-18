package org.corfudb.runtime.collections.graphdb;

import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * A transport node is a host (hypervisor) which has
 * been prepared for NSX connectivity and initialization.
 *
 * @author mdhawan
 * edited by shriyav
 */
@Data
@AllArgsConstructor
@ToString
public class TransportNode extends Component {
    UUID id;
    String name;
    Map<String, Object> properties;

    @Override
    public int hashCode() {
        Hasher hasher = Hashing.crc32c().newHasher();
        hasher.putLong(id.getLeastSignificantBits());
        hasher.putLong(id.getMostSignificantBits());

        return hasher.hash().asInt();
    }
}

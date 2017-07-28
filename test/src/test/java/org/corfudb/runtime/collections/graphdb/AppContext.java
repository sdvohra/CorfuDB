package org.corfudb.runtime.collections.graphdb;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Provides runtime context to tie the data access layer together.
 *
 * @author shriyav
 */
public interface AppContext {
    /** Creates new TransportZone in graph and returns ID of new Node in graph. */
    TransportZone createTransportZone(UUID uuid, String name, Map<String, String> props)
            throws NodeAlreadyExistsException;

    /** Creates new TransportNode in graph and returns ID of new Node in graph. */
    TransportNode createTransportNode(UUID uuid, String name, Map<String, Object> props)
            throws NodeAlreadyExistsException;

    /** Creates new LogicalSwitch in graph and returns ID of new Node in graph. */
    LogicalSwitch createLogicalSwitch(UUID uuid, String name, List<UUID> profs, Map<String, Object> props) throws NodeAlreadyExistsException;

    /** Creates new LogicalPort in graph and returns ID of new Node in graph. */
    LogicalPort createLogicalPort(UUID uuid, String name, Attachment attachment, List<UUID> profs,
                                  Map<String, Object> props) throws NodeAlreadyExistsException;

    /** Connects Transport Zone to Transport Node in graph. */
    void connectTZtoTN(TransportZone tz, TransportNode tn) throws Exception;

    /** Connects Transport Zone to Logical Switch in graph. */
    void connectTZtoLS(TransportZone tz, LogicalSwitch ls) throws Exception;

    /** Connects Logical Switch to Logical Port in graph. */
    void connectLStoLP(LogicalSwitch ls, LogicalPort lp) throws Exception;
}

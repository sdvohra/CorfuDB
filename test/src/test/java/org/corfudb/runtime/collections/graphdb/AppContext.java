package org.corfudb.runtime.collections.graphdb;

import org.corfudb.runtime.CorfuRuntime;
import org.corfudb.runtime.view.AbstractViewTest;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Provides runtime context to tie the data access layer together. Contains
 * the CorfuRuntime and a GraphDB. GraphDB can be constructed here.
 *
 * @author shriyav
 */
public class AppContext extends AbstractViewTest {
    GraphDB graph;

    public AppContext(CorfuRuntime rt, String graphName) {
        graph = new GraphDB(rt, graphName);
    }

    public GraphDB getGraph() {
        return graph;
    }

    /** Creates new TransportZone in graph and returns ID of new Node in graph. */
    public Integer createTransportZone(UUID uuid, String name, Map<String, String> props)
            throws NodeAlreadyExistsException {
        TransportZone tz = new TransportZone(uuid, name, props);
        return graph.addNode(tz);
    }

    /** Creates new TransportNode in graph and returns ID of new Node in graph. */
    public Integer createTransportNode(UUID uuid, String name, Set<UUID> tzIds,
                                   Map<String, Object> props) throws NodeAlreadyExistsException {
        TransportNode tn = new TransportNode(uuid, tzIds, name, props);
        return graph.addNode(tn);
    }

    /** Creates new LogicalSwitch in graph and returns ID of new Node in graph. */
    public Integer createLogicalSwitch(UUID uuid, String name, UUID tzUUID, List<UUID> profs,
                                    Map<String, Object> props) throws NodeAlreadyExistsException {
        LogicalSwitch ls = new LogicalSwitch(uuid, name, tzUUID, profs, props);
        return graph.addNode(ls);
    }

    /** Creates new LogicalPort in graph and returns ID of new Node in graph. */
    public Integer createLogicalPort(UUID uuid, String name, UUID lsUUID, Attachment attachment, List<UUID> profs,
                                 Map<String, Object> props) throws NodeAlreadyExistsException {
        LogicalPort lp = new LogicalPort(uuid, name, lsUUID, attachment, profs, props);
        return graph.addNode(lp);
    }

    public void connectTZtoTN(TransportZone tz, TransportNode tn) throws Exception {
        graph.connect(tz, tn);
    }

    public void connectTZtoLS(TransportZone tz, LogicalSwitch ls) throws Exception {
        graph.connect(tz, ls);
    }

    public void connectLStoLP(LogicalSwitch ls, LogicalPort lp) throws Exception {
        graph.connect(ls, lp);
    }
}

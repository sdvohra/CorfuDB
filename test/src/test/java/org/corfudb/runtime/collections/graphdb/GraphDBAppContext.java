package org.corfudb.runtime.collections.graphdb;

import org.corfudb.runtime.CorfuRuntime;
import org.corfudb.runtime.view.AbstractViewTest;

import java.util.*;

/**
 * Implements the AppContext interface using a native graph encoding.
 * Contains the CorfuRuntime and a GraphDB.
 *
 * @author shriyav
 */
public class GraphDBAppContext extends AbstractViewTest implements AppContext {
    GraphDB graph;

    public GraphDBAppContext(CorfuRuntime rt, String graphName) {
        graph = new GraphDB(rt, graphName);
    }

    public GraphDB getGraph() {
        return graph;
    }

    @Override
    public TransportZone createTransportZone(UUID uuid, String name, Map<String, String> props)
            throws NodeAlreadyExistsException {
        TransportZone tz = new TransportZone(uuid, name, props);
        graph.addNode(tz);
        return tz;
    }

    @Override
    public TransportNode createTransportNode(UUID uuid, String name, Map<String, Object> props)
            throws NodeAlreadyExistsException {
        TransportNode tn = new TransportNode(uuid, name, props);
        graph.addNode(tn);
        return tn;
    }

    @Override
    public LogicalSwitch createLogicalSwitch(UUID uuid, String name, List<UUID> profs,
                                    Map<String, Object> props) throws NodeAlreadyExistsException {
        LogicalSwitch ls = new LogicalSwitch(uuid, name, profs, props);
        graph.addNode(ls);
        return ls;
    }

    @Override
    public LogicalPort createLogicalPort(UUID uuid, String name, Attachment attachment, List<UUID> profs,
                                 Map<String, Object> props) throws NodeAlreadyExistsException {
        LogicalPort lp = new LogicalPort(uuid, name, attachment, profs, props);
        graph.addNode(lp);
        return lp;
    }

    @Override
    public void connectTZtoTN(TransportZone tz, TransportNode tn) throws Exception {
        graph.connect(tz, tn);
    }

    @Override
    public void connectTZtoLS(TransportZone tz, LogicalSwitch ls) throws Exception {
        graph.connect(tz, ls);
    }

    @Override
    public void connectLStoLP(LogicalSwitch ls, LogicalPort lp) throws Exception {
        graph.connect(ls, lp);
    }
}

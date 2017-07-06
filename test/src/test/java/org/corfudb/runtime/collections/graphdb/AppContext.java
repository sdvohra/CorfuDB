package org.corfudb.runtime.collections.graphdb;

import org.corfudb.runtime.CorfuRuntime;
import org.corfudb.runtime.view.AbstractViewTest;

import java.util.*;

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
    public TransportZone createTransportZone(UUID uuid, String name, Map<String, String> props)
            throws NodeAlreadyExistsException {
        TransportZone tz = new TransportZone(uuid, name, props);
        graph.addNode(tz);
        return tz;
    }

    /** Creates new TransportNode in graph and returns ID of new Node in graph. */
    public TransportNode createTransportNode(UUID uuid, String name, Map<String, Object> props)
            throws NodeAlreadyExistsException {
        TransportNode tn = new TransportNode(uuid, name, props);
        graph.addNode(tn);
        return tn;
    }

    /** Creates new LogicalSwitch in graph and returns ID of new Node in graph. */
    public LogicalSwitch createLogicalSwitch(UUID uuid, String name, List<UUID> profs,
                                    Map<String, Object> props) throws NodeAlreadyExistsException {
        LogicalSwitch ls = new LogicalSwitch(uuid, name, profs, props);
        graph.addNode(ls);
        return ls;
    }

    /** Creates new LogicalPort in graph and returns ID of new Node in graph. */
    public LogicalPort createLogicalPort(UUID uuid, String name, Attachment attachment, List<UUID> profs,
                                 Map<String, Object> props) throws NodeAlreadyExistsException {
        LogicalPort lp = new LogicalPort(uuid, name, attachment, profs, props);
        graph.addNode(lp);
        return lp;
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

    public ArrayList<Integer> getInward(Object obj) {
        return graph.getNode(obj).getInward();
    }

    public ArrayList<Integer> getOutward(Object obj) {
        return graph.getNode(obj).getInward();
    }
}

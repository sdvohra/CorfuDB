package org.corfudb.runtime.collections.graphdb;

import org.corfudb.runtime.CorfuRuntime;
import org.corfudb.runtime.view.AbstractViewTest;

import java.util.UUID;

/**
 * Created by shriyav on 6/7/17.
 */
public class AppContext extends AbstractViewTest {
    GraphDB graph;

    public AppContext(CorfuRuntime rt, String graphName) {
        graph = new GraphDB(rt, graphName);
    }

    public GraphDB getGraph() {
        return graph;
    }

    /** Creates new TransportZone and returns its UUID. */
    public UUID createTransportZone(String name) {
        UUID curr = UUID.randomUUID();
        try {
            graph.addNode(curr, new TransportZone(curr, name));
        } catch (Exception e) {
            return createTransportZone(name);
        }
        return curr;
    }

    /** Creates new TransportNode and returns its UUID. */
    public UUID createTransportNode(String name) {
        UUID curr = UUID.randomUUID();
        try {
            graph.addNode(curr, new TransportNode(curr, name));
        } catch (Exception e) {
            return createTransportNode(name);
        }
        return curr;
    }

    /** Creates new LogicalSwitch and returns its UUID. */
    public UUID createLogicalSwitch(String name) {
        UUID curr = UUID.randomUUID();
        try {
            graph.addNode(curr, new LogicalSwitch(curr, name));
        } catch (Exception e) {
            return createLogicalSwitch(name);
        }
        return curr;
    }

    /** Creates new LogicalPort and returns its UUID. */
    public UUID createLogicalPort(String name) {
        UUID curr = UUID.randomUUID();
        try {
            graph.addNode(curr, new LogicalPort(curr, name));
        } catch (Exception e) {
            return createLogicalPort(name);
        }
        return curr;
    }

    public void connectTransportNode(UUID tnID, UUID tzID) {
        graph.addEdge(tnID, tzID);
    }

    public void connectLogicalSwitch(UUID lsID, UUID tzID) {
        graph.addEdge(lsID, tzID);
    }

    public void connectLogicalPort(UUID lpID, UUID lsID) {
        graph.addEdge(lpID, lsID);
    }
}

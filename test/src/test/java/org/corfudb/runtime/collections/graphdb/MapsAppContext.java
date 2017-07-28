package org.corfudb.runtime.collections.graphdb;

import org.corfudb.runtime.CorfuRuntime;
import org.corfudb.runtime.view.AbstractViewTest;

import java.util.*;

/**
 * Implements the AppContext interface using maps.
 * Contains the CorfuRuntime.
 *
 * @author shriyav
 */
public class MapsAppContext extends AbstractViewTest implements AppContext {
    MapsGraphDB graph;

    public MapsAppContext(CorfuRuntime rt, String graphName) {
        graph = new MapsGraphDB(rt, graphName);
    }

    public MapsGraphDB getGraph() {
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
    public LogicalSwitch createLogicalSwitch(UUID uuid, String name, List<UUID> profs, Map<String, Object> props)
            throws NodeAlreadyExistsException {
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

    public Set<TransportNode> queryTZtoTN(TransportZone tz) {
        Set<TransportNode> TNs = new HashSet<>();

        for (Edge e : graph.getEdges().values()) {
            if (e.getFrom().equals(tz) && (e.getTo() instanceof TransportNode)) {
                TNs.add((TransportNode) e.getTo());
            }
        }
        return TNs;
    }

    public Set<LogicalSwitch> queryTZtoLS(TransportZone tz) {
        Set<LogicalSwitch> LSs = new HashSet<>();

        for (Edge e : graph.getEdges().values()) {
            if (e.getFrom().equals(tz) && (e.getTo() instanceof LogicalSwitch)) {
                LSs.add((LogicalSwitch) e.getTo());
            }
        }
        return LSs;
    }

    public Set<LogicalPort> queryLStoLP(LogicalSwitch ls) {
        Set<LogicalPort> LPs = new HashSet<>();

        for (Edge e : graph.getEdges().values()) {
            if (e.getFrom().equals(ls) && (e.getTo() instanceof LogicalPort)) {
                LPs.add((LogicalPort) e.getTo());
            }
        }
        return LPs;
    }

    public Set<LogicalPort> queryTZtoLP(TransportZone tz) {
        Set<LogicalPort> LPs = new HashSet<>();
        Set<LogicalSwitch> LSs = queryTZtoLS(tz);
        for (LogicalSwitch ls : LSs) {
            LPs.addAll(queryLStoLP(ls));
        }
        return LPs;
    }

    public Set<TransportZone> queryTNtoTZ(TransportNode tn) {
        Set<TransportZone> TZs = new HashSet<>();

        for (Edge e : graph.getEdges().values()) {
            if (e.getFrom().equals(tn) && (e.getTo() instanceof TransportZone)) {
                TZs.add((TransportZone) e.getTo());
            }
        }
        return TZs;
    }
}

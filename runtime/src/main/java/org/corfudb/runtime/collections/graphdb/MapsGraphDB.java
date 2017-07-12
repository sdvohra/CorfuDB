package org.corfudb.runtime.collections.graphdb;

import org.corfudb.runtime.CorfuRuntime;
import org.corfudb.runtime.collections.SMRMap;
import sun.rmi.transport.Transport;

import java.util.*;

/**
 * Implements Graph. Uses internal maps to represent
 * a directed graph.
 *
 * @author shriyav
 */

public class MapsGraphDB {
    CorfuRuntime rt;
    Map<UUID, TransportZone> transportZones;
    Map<UUID, TransportNode> transportNodes;
    Map<UUID, LogicalSwitch> logicalSwitches;
    Map<UUID, LogicalPort> logicalPorts;
    Map<Integer, Edge> edges;

    public MapsGraphDB(CorfuRuntime runtime, String name) {
        rt = runtime;
        transportZones = runtime.getObjectsView()
                .build()
                .setStreamName(name + "TZ")     // stream name
                .setType(SMRMap.class)   // object class backed by this stream
                .open();                 // instantiate the object!
        transportNodes = runtime.getObjectsView()
                .build()
                .setStreamName(name + "TN")     // stream name
                .setType(SMRMap.class)   // object class backed by this stream
                .open();                 // instantiate the object!
        logicalSwitches = runtime.getObjectsView()
                .build()
                .setStreamName(name + "LS")     // stream name
                .setType(SMRMap.class)   // object class backed by this stream
                .open();                 // instantiate the object!
        logicalPorts = runtime.getObjectsView()
                .build()
                .setStreamName(name + "LP")     // stream name
                .setType(SMRMap.class)   // object class backed by this stream
                .open();                 // instantiate the object!
        edges = runtime.getObjectsView()
                .build()
                .setStreamName(name + "Edges")     // stream name
                .setType(SMRMap.class)   // object class backed by this stream
                .open();                 // instantiate the object!
    }

    @Override
    public String toString() {
        return "This graph has " + getNumNodes() + " nodes.";
    }

    //public Map<Integer, Node> getNodes() { return nodes; }

    public void addNode(Component obj) throws NodeAlreadyExistsException {
        if (obj instanceof TransportZone) {
            if (transportZones.containsValue(obj)) {
                throw new NodeAlreadyExistsException();
            }
            transportZones.put(obj.getId(), (TransportZone) obj);
        } else if (obj instanceof TransportNode) {
            if (transportNodes.containsValue(obj)) {
                throw new NodeAlreadyExistsException();
            }
            transportNodes.put(obj.getId(), (TransportNode) obj);
        } else if (obj instanceof LogicalSwitch) {
            if (logicalSwitches.containsValue(obj)) {
                throw new NodeAlreadyExistsException();
            }
            logicalSwitches.put(obj.getId(), (LogicalSwitch) obj);
        } else if (obj instanceof LogicalPort) {
            if (logicalPorts.containsValue(obj)) {
                throw new NodeAlreadyExistsException();
            }
            logicalPorts.put(obj.getId(), (LogicalPort) obj);
        }
    }

    public Object getNode(UUID id) throws NodeDoesNotExistException {
        if (transportZones.get(id) != null) {
            return transportZones.get(id);
        } else if (transportNodes.get(id) != null) {
            return transportNodes.get(id);
        } else if (logicalSwitches.get(id) != null) {
            return logicalSwitches.get(id);
        } else if (logicalPorts.get(id) != null) {
            return logicalPorts.get(id);
        } else {
            throw new NodeDoesNotExistException();
        }
    }

    public void update(Component obj) throws NodeDoesNotExistException {
        if (obj instanceof TransportZone) {
            if (!transportZones.containsKey(obj.getId())) {
                throw new NodeDoesNotExistException();
            }
            transportZones.put(obj.getId(), (TransportZone) obj);
        } else if (obj instanceof TransportNode) {
            if (!transportNodes.containsKey(obj.getId())) {
                throw new NodeDoesNotExistException();
            }
            transportNodes.put(obj.getId(), (TransportNode) obj);
        } else if (obj instanceof LogicalSwitch) {
            if (!logicalSwitches.containsKey(obj.getId())) {
                throw new NodeDoesNotExistException();
            }
            logicalSwitches.put(obj.getId(), (LogicalSwitch) obj);
        } else if (obj instanceof LogicalPort) {
            if (!logicalPorts.containsKey(obj.getId())) {
                throw new NodeDoesNotExistException();
            }
            logicalPorts.put(obj.getId(), (LogicalPort) obj);
        }
    }

    private void removeUUID(Component comp) {
        for (Edge edge : edges.values()) {
            if (edge.getTo().equals(comp) || edge.getFrom().equals(comp)) {
                edges.remove(edge.hashCode());
            }
        }
    }

    public void removeNode(Component obj) throws NodeDoesNotExistException {
        if (obj instanceof TransportZone) {
            if (!transportZones.containsKey(obj.getId())) {
                throw new NodeDoesNotExistException();
            }
            transportZones.remove(obj.getId());
        } else if (obj instanceof TransportNode) {
            if (!transportNodes.containsKey(obj.getId())) {
                throw new NodeDoesNotExistException();
            }
            transportNodes.remove(obj.getId());
        } else if (obj instanceof LogicalSwitch) {
            if (!logicalSwitches.containsKey(obj.getId())) {
                throw new NodeDoesNotExistException();
            }
            logicalSwitches.remove(obj.getId());
        } else if (obj instanceof LogicalPort) {
            if (!logicalPorts.containsKey(obj.getId())) {
                throw new NodeDoesNotExistException();
            }
            logicalPorts.remove(obj.getId());
        }
        // Remove from edges
        removeUUID(obj);
    }

    public int getNumNodes() {
        return transportZones.size() + transportNodes.size()
                + logicalSwitches.size() + logicalPorts.size();
    }

    public void connect(Component outward, Component inward) throws NodeDoesNotExistException,
            EdgeAlreadyExistsException {
        if (outward instanceof TransportZone) {
            if (!transportZones.containsKey(outward.getId())) {
                throw new NodeDoesNotExistException();
            }
        } else if (outward instanceof TransportNode) {
            if (!transportNodes.containsKey(outward.getId())) {
                throw new NodeDoesNotExistException();
            }
        } else if (outward instanceof LogicalSwitch) {
            if (!logicalSwitches.containsKey(outward.getId())) {
                throw new NodeDoesNotExistException();
            }
        } else if (outward instanceof LogicalPort) {
            if (!logicalPorts.containsKey(outward.getId())) {
                throw new NodeDoesNotExistException();
            }
        }
        if (inward instanceof TransportZone) {
            if (!transportZones.containsKey(inward.getId())) {
                throw new NodeDoesNotExistException();
            }
        } else if (inward instanceof TransportNode) {
            if (!transportNodes.containsKey(inward.getId())) {
                throw new NodeDoesNotExistException();
            }
        } else if (inward instanceof LogicalSwitch) {
            if (!logicalSwitches.containsKey(inward.getId())) {
                throw new NodeDoesNotExistException();
            }
        } else if (inward instanceof LogicalPort) {
            if (!logicalPorts.containsKey(inward.getId())) {
                throw new NodeDoesNotExistException();
            }
        }
        Edge newEdge = new Edge(outward, inward);
        if (edges.containsKey(newEdge.hashCode())) {
            throw new EdgeAlreadyExistsException();
        }
        edges.put(newEdge.hashCode(), newEdge);
    }

    public void disconnect(Component outward, Component inward) throws NodeDoesNotExistException,
            EdgeDoesNotExistException {
        if (outward instanceof TransportZone) {
            if (!transportZones.containsKey(outward.getId())) {
                throw new NodeDoesNotExistException();
            }
        } else if (outward instanceof TransportNode) {
            if (!transportNodes.containsKey(outward.getId())) {
                throw new NodeDoesNotExistException();
            }
        } else if (outward instanceof LogicalSwitch) {
            if (!logicalSwitches.containsKey(outward.getId())) {
                throw new NodeDoesNotExistException();
            }
        } else if (outward instanceof LogicalPort) {
            if (!logicalPorts.containsKey(outward.getId())) {
                throw new NodeDoesNotExistException();
            }
        }
        if (inward instanceof TransportZone) {
            if (!transportZones.containsKey(inward.getId())) {
                throw new NodeDoesNotExistException();
            }
        } else if (inward instanceof TransportNode) {
            if (!transportNodes.containsKey(inward.getId())) {
                throw new NodeDoesNotExistException();
            }
        } else if (inward instanceof LogicalSwitch) {
            if (!logicalSwitches.containsKey(inward.getId())) {
                throw new NodeDoesNotExistException();
            }
        } else if (inward instanceof LogicalPort) {
            if (!logicalPorts.containsKey(inward.getId())) {
                throw new NodeDoesNotExistException();
            }
        }
        Edge tempEdge = new Edge(outward, inward);
        if (!edges.containsKey(tempEdge.hashCode())) {
            throw new EdgeDoesNotExistException();
        }
        edges.remove(tempEdge.hashCode());
    }

    //@Override
    public void clear() {
        transportZones.clear();
        transportNodes.clear();
        logicalSwitches.clear();
        logicalPorts.clear();
        edges.clear();
    }

    public Set<TransportNode> queryTZtoTN(TransportZone tz) {
        Set<TransportNode> TNs = new HashSet<>();

        for (Edge e : edges.values()) {
            if (e.getFrom().equals(tz) && (e.getTo() instanceof TransportNode)) {
                TNs.add((TransportNode) e.getTo());
            }
        }
        return TNs;
    }

    public Set<LogicalSwitch> queryTZtoLS(TransportZone tz) {
        Set<LogicalSwitch> LSs = new HashSet<>();

        for (Edge e : edges.values()) {
            if (e.getFrom().equals(tz) && (e.getTo() instanceof LogicalSwitch)) {
                LSs.add((LogicalSwitch) e.getTo());
            }
        }
        return LSs;
    }

    public Set<LogicalPort> queryLStoLP(LogicalSwitch ls) {
        Set<LogicalPort> LPs = new HashSet<>();

        for (Edge e : edges.values()) {
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

        for (Edge e : edges.values()) {
            if (e.getFrom().equals(tn) && (e.getTo() instanceof TransportZone)) {
                TZs.add((TransportZone) e.getTo());
            }
        }
        return TZs;
    }
}

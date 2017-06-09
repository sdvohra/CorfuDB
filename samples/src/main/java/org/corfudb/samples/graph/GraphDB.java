package org.corfudb.samples.graph;

import org.corfudb.runtime.CorfuRuntime;
import org.corfudb.runtime.collections.SMRMap;

import java.util.*;

/**
 * Created by shriyav on 5/25/17.
 */

public class GraphDB {
    private SMRMap<UUID, Node> nodes;
    CorfuRuntime rt;

    public GraphDB(CorfuRuntime runtime) {
        nodes = runtime.getObjectsView()
                .build()
                .setStreamName("A")     // stream name
                .setType(SMRMap.class)  // object class backed by this stream
                .open();                // instantiate the object!
        rt = runtime;
    }

    @Override
    public String toString() {
        return "This graph has " + getNumNodes() + " nodes.";
    }

    public SMRMap<UUID, Node> getNodes() { return nodes; }

    public Node getNode(UUID id) { return nodes.get(id); }

    public void addNode(UUID uuid) {
        nodes.put(uuid, new Node(uuid));
    }

    public void addNode(UUID uuid, String name) {
        nodes.put(uuid, new Node(uuid, name));
    }

    public void addNode(UUID uuid, String name, String type) {
        if (type.equals("Transport Zone")) {
            TransportZone tz = new TransportZone(uuid, name);
            nodes.put(uuid, tz);
        } else if (type.equals("Transport Node")) {
            TransportNode tn = new TransportNode(uuid, name);
            nodes.put(uuid, tn);
        } else if (type.equals("Logical Switch")) {
            LogicalSwitch ls = new LogicalSwitch(uuid, name);
            nodes.put(uuid, ls);
        } else if (type.equals("Logical Port")) {
            LogicalPort lp = new LogicalPort(uuid, name);
            nodes.put(uuid, lp);
        }
    }

    public void update(UUID uuid, Map<String, Object> properties) {
        nodes.get(uuid).getProperties().putAll(properties);
    }

    public void removeNode(UUID uuid) { nodes.remove(uuid); }

    public int getNumNodes() {
        return nodes.size();
    }

    public void addEdge(Node from, Node to) {
        if (from instanceof TransportNode) {
            ((TransportNode) from).addTransportZoneID(to.getID());
        } else if (from instanceof LogicalSwitch) {
            ((LogicalSwitch) from).connectToTZ(to.getID());
        } else if (from instanceof LogicalPort) {
            ((LogicalPort) from).connectToLS(to.getID());
        }

        from.addEdge(to.getID());
        to.addEdge(from.getID());
    }

    public void addEdge(UUID from, UUID to) {
        Node f = getNode(from);
        Node t = getNode(to);
        addEdge(f, t);
    }

    /** Returns an iterable of nodes of all nodes adjacent to v. */
    Iterable<UUID> adjacent(UUID v) {
        ArrayList<UUID> edgeList = nodes.get(v).getEdges();
        ArrayList<UUID> returnVal = new ArrayList<>();
        for (UUID e : edgeList) {
            returnVal.add(e);
        }
        return returnVal;
    }

    /** Clear entire graph: remove all nodes/edges. */
    void clear() {
        nodes.clear();
    }

    private ArrayList<UUID> dfsHelper(UUID f, String dfsType, ArrayList<UUID> ordered, ArrayList<UUID> seen) {
        Node n = nodes.get(f);
        if (n != null && !seen.contains(f)) {
            if (dfsType.equals("pre")) {
                ordered.add(f);
            }
            seen.add(f);
            for (UUID neighbor : n.getEdges()) {
                dfsHelper(neighbor, dfsType, ordered, seen);
            }
            if (dfsType.equals("post")) {
                ordered.add(f);
            }
        }
        return ordered;
    }

    ArrayList<UUID> preDFS(UUID first) {
        ArrayList<UUID> returnVal = dfsHelper(first, "pre", new ArrayList<UUID>(), new ArrayList<UUID>());
        return returnVal;
    }

    ArrayList<UUID> postDFS(UUID first) {
        ArrayList<UUID> returnVal = dfsHelper(first, "post", new ArrayList<UUID>(), new ArrayList<UUID>());
        return returnVal;
    }

    ArrayList<UUID> BFS(UUID first) {
        ArrayList<UUID> ordered = new ArrayList<>();
        ArrayList<UUID> fringe = new ArrayList<>();
        fringe.add(first);
        while (fringe.size() != 0) {
            UUID curr = fringe.remove(0);
            if (!ordered.contains(curr)) {
                ordered.add(curr);
                for (UUID neighbor : nodes.get(curr).getEdges()) {
                    fringe.add(neighbor);
                }
            }
        }
        return ordered;
    }
}

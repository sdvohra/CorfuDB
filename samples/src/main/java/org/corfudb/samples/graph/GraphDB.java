package org.corfudb.samples.graph;

import org.corfudb.runtime.CorfuRuntime;
import org.corfudb.runtime.collections.SMRMap;

import java.util.*;

/**
 * Created by shriyav on 5/25/17.
 */

public class GraphDB {
    private SMRMap<UUID, Node> vertices;
    CorfuRuntime rt;

    public GraphDB(CorfuRuntime runtime) {
        vertices = runtime.getObjectsView()
                .build()
                .setStreamName("A")     // stream name
                .setType(SMRMap.class)  // object class backed by this stream
                .open();                // instantiate the object!
        rt = runtime;
    }

    @Override
    public String toString() {
        return "This graph has " + getNumNodes() + " vertices.";
    }

    public SMRMap<UUID, Node> getVertices() { return vertices; }

    public Node getNode(UUID id) { return vertices.get(id); }

    public void addNode(UUID uuid) {
        vertices.put(uuid, new Node(uuid));
    }

    public void addNode(UUID uuid, String name) {
        vertices.put(uuid, new Node(uuid, name));
    }

    public void addNode(UUID uuid, String name, String type) {
        if (type.equals("Transport Zone")) {
            TransportZone tz = new TransportZone(uuid, name);
            vertices.put(uuid, tz);
        } else if (type.equals("Transport Node")) {
            TransportNode tn = new TransportNode(uuid, name);
            vertices.put(uuid, tn);
        } else if (type.equals("Logical Switch")) {
            LogicalSwitch ls = new LogicalSwitch(uuid, name);
            vertices.put(uuid, ls);
        } else if (type.equals("Logical Port")) {
            LogicalPort lp = new LogicalPort(uuid, name);
            vertices.put(uuid, lp);
        }
    }

    public void update(UUID uuid, Map<String, Object> properties) {
        vertices.get(uuid).getProperties().putAll(properties);
    }

    public void removeNode(UUID uuid) { vertices.remove(uuid); }

    public int getNumNodes() {
        return vertices.size();
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

    /** Returns an iterable of all vertex IDs in the graph. */
    Iterable<UUID> vertices() {
        return vertices.keySet();
    }

    /** Returns an iterable of nodes of all vertices adjacent to v. */
    Iterable<UUID> adjacent(UUID v) {
        HashSet<UUID> edgeList = vertices.get(v).getEdges();
        ArrayList<UUID> returnVal = new ArrayList<>();
        for (UUID e : edgeList) {
            returnVal.add(e);
        }
        return returnVal;
    }

    /** Clear entire graph: remove all nodes/edges. */
    void clear() {
        vertices.clear();
    }

    private ArrayList<UUID> dfsHelper(UUID f, String dfsType, ArrayList<UUID> ordered, ArrayList<UUID> seen) {
        Node n = vertices.get(f);
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
                for (UUID neighbor : vertices.get(curr).getEdges()) {
                    fringe.add(neighbor);
                }
            }
        }
        return ordered;
    }
}

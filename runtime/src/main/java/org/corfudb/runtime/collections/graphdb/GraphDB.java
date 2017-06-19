package org.corfudb.runtime.collections.graphdb;

import org.corfudb.runtime.CorfuRuntime;
import org.corfudb.runtime.collections.SMRMap;

import java.util.*;

/**
 * Implements GraphInterface. Uses adjacency lists to represent
 * an undirected graph of Nodes.
 *
 * @author shriyav
 */

public class GraphDB {
    private Map<UUID, Node> nodes;
    CorfuRuntime rt;

    public GraphDB(CorfuRuntime runtime, String name) {
        nodes = runtime.getObjectsView()
                .build()
                .setStreamName(name)     // stream name
                .setType(SMRMap.class)   // object class backed by this stream
                .open();                 // instantiate the object!
        rt = runtime;
    }

    @Override
    public String toString() {
        return "This graph has " + getNumNodes() + " nodes.";
    }

    public Map<UUID, Node> getNodes() { return nodes; }

    public Node getNode(UUID id) { return nodes.get(id); }

    public void addNode(UUID uuid, Node n) throws NodeAlreadyExistsException {
        // Error Handling
        if (getNode(uuid) != null) {
            throw new NodeAlreadyExistsException();
        }
        nodes.put(uuid, n);
    }

    public void update(UUID uuid, Node n) throws NodeDoesNotExistException {
        // Error Handling
        if (getNode(uuid) == null) {
            throw new NodeDoesNotExistException();
        }

        nodes.put(uuid, n);
    }

    public void removeNode(UUID uuid) throws NodeDoesNotExistException {
        // Error Handling
        if (getNode(uuid) == null) {
            throw new NodeDoesNotExistException();
        }

        for (UUID neighbor : adjacent(uuid)) {
            getNode(neighbor).removeEdge(nodes.get(uuid));
        }
        nodes.remove(uuid);
    }

    public int getNumNodes() {
        return nodes.size();
    }

    public void connect(Node from, Node to) throws NodeDoesNotExistException {
        // Error Handling
        if (getNode(from.getID()) == null || getNode(to.getID()) == null) {
            throw new NodeDoesNotExistException();
        }
        from.addEdge(to);
    }

    public void connect(UUID from, UUID to) throws NodeDoesNotExistException {
        // Error Handling
        if (getNode(from) == null || getNode(to) == null) {
            throw new NodeDoesNotExistException();
        }
        Node f = getNode(from);
        Node t = getNode(to);
        connect(f, t);
    }

    public void disconnect(Node from, Node to) throws NodeDoesNotExistException, EdgeDoesNotExistException {
        // Error Handling
        if (from == null || to == null) {
            throw new NodeDoesNotExistException();
        }
        if (!from.getChildren().contains(to) || !to.getParents().contains(from)) {
            throw new EdgeDoesNotExistException();
        }
        from.removeEdge(to);
    }

    public void disconnect(UUID from, UUID to) throws NodeDoesNotExistException, EdgeDoesNotExistException {
        // Error Handling
        if (getNode(from) == null || getNode(to) == null) {
            throw new NodeDoesNotExistException();
        }
        if (!getNode(from).getChildren().contains(to) || !getNode(to).getParents().contains(from)) {
            throw new EdgeDoesNotExistException();
        }
        Node f = getNode(from);
        Node t = getNode(to);
        disconnect(f, t);
    }

    /** Returns an iterable of nodes of all nodes adjacent to v. */
    public Iterable<UUID> adjacent(UUID v) throws NodeDoesNotExistException {
        // Error Handling
        if (getNode(v) == null) {
            throw new NodeDoesNotExistException();
        }

        ArrayList<UUID> edgeList = new ArrayList<>();
        edgeList.addAll(getNode(v).getParents());
        edgeList.addAll(getNode(v).getChildren());
        return edgeList;
    }

    /** Clear entire graphdb: remove all nodes/edges. */
    public void clear() {
        nodes.clear();
    }

    public ArrayList<UUID> preDFS(UUID first) throws NodeDoesNotExistException {
        // Error Handling
        if (getNode(first) == null) {
            throw new NodeDoesNotExistException();
        }

        ArrayList<UUID> returnVal = new ArrayList<>();

        Stack<UUID> stack = new Stack();
        stack.add(first);

        HashSet<UUID> visited = new HashSet<>();
        visited.add(first);

        while (!stack.isEmpty()) {
            UUID element = stack.pop();
            returnVal.add(element);

            ArrayList<UUID> neighbors = nodes.get(element).getChildren();
            for (int i = neighbors.size() - 1; i >= 0; i--) {
                UUID neighbor = neighbors.get(i);
                if (neighbor != null && !visited.contains(neighbor)) {
                    stack.add(neighbor);
                    visited.add(neighbor);
                }
            }
        }
        return returnVal;
    }

    public ArrayList<UUID> postDFS(UUID first) throws NodeDoesNotExistException {
        // Error Handling
        if (getNode(first) == null) {
            throw new NodeDoesNotExistException();
        }

        ArrayList<UUID> returnVal = new ArrayList<>();

        Stack<UUID> stack = new Stack();
        stack.add(first);

        HashSet<UUID> visited = new HashSet<>();
        visited.add(first);

        while (!stack.isEmpty()) {
            UUID element = stack.pop();
            returnVal.add(0, element);

            ArrayList<UUID> neighbors = nodes.get(element).getChildren();
            for (UUID neighbor : neighbors) {
                if (neighbor != null && !visited.contains(neighbor)) {
                    stack.add(neighbor);
                    visited.add(neighbor);
                }
            }
        }
        return returnVal;
    }

    public ArrayList<UUID> BFS(UUID first) throws NodeDoesNotExistException {
        // Error Handling
        if (getNode(first) == null) {
            throw new NodeDoesNotExistException();
        }

        ArrayList<UUID> ordered = new ArrayList<>();
        ArrayList<UUID> fringe = new ArrayList<>();
        fringe.add(first);
        while (fringe.size() != 0) {
            UUID curr = fringe.remove(0);
            if (!ordered.contains(curr)) {
                ordered.add(curr);
                for (UUID neighbor : nodes.get(curr).getChildren()) {
                    fringe.add(neighbor);
                }
            }
        }
        return ordered;
    }
}

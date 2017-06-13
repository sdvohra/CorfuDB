package org.corfudb.runtime.collections.graphdb;

import org.corfudb.runtime.CorfuRuntime;
import org.corfudb.runtime.collections.SMRMap;

import java.util.*;

/**
 * Created by shriyav on 5/25/17.
 */

public class GraphDB {
    private Map<UUID, Node> nodes;
    String name;
    CorfuRuntime rt;

    public GraphDB(CorfuRuntime runtime, String n) {
        nodes = runtime.getObjectsView()
                .build()
                .setStreamName("B")     // stream name
                .setType(SMRMap.class)  // object class backed by this stream
                .open();                // instantiate the object!
        name = n;
        rt = runtime;
    }

    @Override
    public String toString() {
        return "This graph has " + getNumNodes() + " nodes.";
    }

    public Map<UUID, Node> getNodes() { return nodes; }

    public Node getNode(UUID id) { return nodes.get(id); }

    public void addNode(UUID uuid, Node n) throws Exception {
        // Error Handling
        if (nodes.get(uuid) != null) {
            throw new Exception("NodeAlreadyExistsException");
        }
        nodes.put(uuid, n);
    }

    public void update(UUID uuid, Node n) throws Exception { // is this better?
        // Error Handling
        if (nodes.get(uuid) == null) {
            throw new Exception("NodeDoesNotExistException");
        }

        nodes.put(uuid, n);
        //nodes.get(uuid).getProperties().putAll(props);
    }

    public void removeNode(UUID uuid) throws Exception {
        // Error Handling
        if (nodes.get(uuid) == null) {
            throw new Exception("NodeDoesNotExistException");
        }

        for (UUID neighbor : nodes.get(uuid).getEdges()) {
            nodes.get(neighbor).removeEdge(uuid);
        }
        nodes.remove(uuid);
    }

    public int getNumNodes() {
        return nodes.size();
    }

    public void addEdge(Node from, Node to) {
        from.addEdge(to);
    }

    public void addEdge(UUID from, UUID to) {
        Node f = getNode(from);
        Node t = getNode(to);
        addEdge(f, t);
    }

    /** Returns an iterable of nodes of all nodes adjacent to v. */
    public Iterable<UUID> adjacent(UUID v) throws Exception {
        // Error Handling
        if (nodes.get(v) == null) {
            throw new Exception("NodeDoesNotExistException");
        }

        ArrayList<UUID> edgeList = nodes.get(v).getEdges();
        ArrayList<UUID> returnVal = new ArrayList<>();
        for (UUID e : edgeList) {
            returnVal.add(e);
        }
        return returnVal;
    }

    /** Clear entire graphdb: remove all nodes/edges. */
    public void clear() {
        nodes.clear();
    }

    public ArrayList<UUID> preDFS(UUID first) throws Exception {
        // Error Handling
        if (nodes.get(first) == null) {
            throw new Exception("NodeDoesNotExistException");
        }

        ArrayList<UUID> returnVal = new ArrayList<>();

        Stack<UUID> stack = new Stack();
        stack.add(first);

        HashSet<UUID> visited = new HashSet<>();
        visited.add(first);

        while (!stack.isEmpty()) {
            UUID element = stack.pop();
            returnVal.add(element);

            ArrayList<UUID> neighbors = nodes.get(element).getEdges();
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

    public ArrayList<UUID> postDFS(UUID first) throws Exception{
        // Error Handling
        if (nodes.get(first) == null) {
            throw new Exception("NodeDoesNotExistException");
        }

        ArrayList<UUID> returnVal = new ArrayList<>();

        Stack<UUID> stack = new Stack();
        stack.add(first);

        HashSet<UUID> visited = new HashSet<>();
        visited.add(first);

        while (!stack.isEmpty()) {
            UUID element = stack.pop();
            returnVal.add(0, element);

            ArrayList<UUID> neighbors = nodes.get(element).getEdges();
            for (UUID neighbor : neighbors) {
                if (neighbor != null && !visited.contains(neighbor)) {
                    stack.add(neighbor);
                    visited.add(neighbor);
                }
            }
        }
        return returnVal;
    }

    public ArrayList<UUID> BFS(UUID first) throws Exception {
        // Error Handling
        if (nodes.get(first) == null) {
            throw new Exception("NodeDoesNotExistException");
        }

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

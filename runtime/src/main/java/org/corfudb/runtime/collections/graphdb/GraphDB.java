package org.corfudb.runtime.collections.graphdb;

import org.corfudb.runtime.CorfuRuntime;
import org.corfudb.runtime.collections.SMRMap;

import java.util.*;

/**
 * Implements Graph. Uses adjacency lists to represent
 * an undirected graph of Nodes.
 *
 * @author shriyav
 */

public class GraphDB implements Graph {
    private Map<Integer, Node> nodes;
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

    public Map<Integer, Node> getNodes() { return nodes; }

    public void addNode(Object obj) throws NodeAlreadyExistsException {
        // Error Handling
        Node objNode = new Node(obj);
        if (getNode(objNode.getID()) != null) {
            throw new NodeAlreadyExistsException();
        }

        nodes.put(objNode.getID(), objNode);
    }

    private Node getNode(Integer id) { return nodes.get(id); }

    public Node getNode(Object obj) {
        Integer objID = obj.hashCode();
        return getNode(objID);
    }

    public void update(Object obj) throws NodeDoesNotExistException {
        // Error Handling
        Node objNode = new Node(obj);
        if (getNode(objNode.getID()) == null) {
            throw new NodeDoesNotExistException();
        }

        nodes.put(objNode.getID(), objNode);
    }

    public void removeNode(Object obj) throws NodeDoesNotExistException {
        Integer objID = obj.hashCode();
        // Error Handling
        if (getNode(objID) == null) {
            throw new NodeDoesNotExistException();
        }

        for (Object neighbor : adjacent(obj)) {
            getNode(neighbor).removeEdge(getNode(objID));
            nodes.put(neighbor.hashCode(), getNode(neighbor)); // for persistence
        }
        nodes.remove(objID);
    }

    public int getNumNodes() {
        return nodes.size();
    }

    public void connect(Object from, Object to) throws NodeDoesNotExistException,
            EdgeAlreadyExistsException {
        Node fromNode = getNode(from.hashCode());
        Node toNode = getNode(to.hashCode());
        // Error Handling
        if (fromNode == null || toNode == null) {
            throw new NodeDoesNotExistException();
        }
        if (fromNode.getOutward().contains(toNode) &&
                toNode.getInward().contains(fromNode)) {
            throw new EdgeAlreadyExistsException();
        }
        fromNode.addEdge(toNode);
        nodes.put(fromNode.getID(), fromNode);
        nodes.put(toNode.getID(), toNode);
    }

    public void disconnect(Object from, Object to) throws NodeDoesNotExistException,
            EdgeDoesNotExistException {
        Node fromNode = getNode(from.hashCode());
        Node toNode = getNode(to.hashCode());
        // Error Handling
        if (fromNode == null || toNode == null) {
            throw new NodeDoesNotExistException();
        }
        if (!fromNode.getOutward().contains(toNode) || !toNode.getInward().contains(fromNode)) {
            throw new EdgeDoesNotExistException();
        }
        fromNode.removeEdge(toNode);
        nodes.put(fromNode.getID(), fromNode);
        nodes.put(toNode.getID(), toNode);
    }

    @Override
    public void clear() {
        nodes.clear();
    }

    @Override
    public Iterable<Object> adjacent(Object obj) throws NodeDoesNotExistException {
        Integer objID = obj.hashCode();

        // Error Handling
        if (getNode(objID) == null) {
            throw new NodeDoesNotExistException();
        }

        ArrayList<Object> adjList = new ArrayList<>();
        for (Node inwardNode : getNode(objID).getInward()) {
            adjList.add(inwardNode.getValue());
        }
        for (Node outwardNode : getNode(objID).getOutward()) {
            adjList.add(outwardNode.getValue());
        }
        return adjList;
    }

    @Override
    public Iterable<Object> preDFS(Object obj) throws NodeDoesNotExistException {
        Integer firstID = obj.hashCode();

        // Error Handling
        if (getNode(firstID) == null) {
            throw new NodeDoesNotExistException();
        }

        ArrayList<Object> returnVal = new ArrayList<>();

        Stack<Integer> stack = new Stack(); // IDs to visit
        stack.add(firstID);

        HashSet<Integer> visited = new HashSet<>(); // IDs visited
        visited.add(firstID);

        while (!stack.isEmpty()) {
            Integer elementID = stack.pop();
            returnVal.add(getNode(elementID).getValue());

            ArrayList<Node> neighbors = getNode(elementID).getOutward();
            for (int i = neighbors.size() - 1; i >= 0; i--) {
                Node neighbor = neighbors.get(i);
                Integer neighborID = neighbor.getID();
                if (neighbor != null && !visited.contains(neighborID)) {
                    stack.add(neighborID);
                    visited.add(neighborID);
                }
            }
        }
        return returnVal;
    }

    @Override
    public Iterable<Object> postDFS(Object obj) throws NodeDoesNotExistException {
        Integer firstID = obj.hashCode();

        // Error Handling
        if (getNode(firstID) == null) {
            throw new NodeDoesNotExistException();
        }

        ArrayList<Object> returnVal = new ArrayList<>();

        Stack<Integer> stack = new Stack(); // IDs to visit
        stack.add(firstID);

        HashSet<Integer> visited = new HashSet<>(); // IDs visited
        visited.add(firstID);

        while (!stack.isEmpty()) {
            Integer elementID = stack.pop();
            returnVal.add(0, getNode(elementID).getValue());

            ArrayList<Node> neighbors = getNode(elementID).getOutward();
            for (int i = 0; i < neighbors.size(); i++) {
                Node neighbor = neighbors.get(i);
                Integer neighborID = neighbor.getID();
                if (neighbor != null && !visited.contains(neighborID)) {
                    stack.add(neighborID);
                    visited.add(neighborID);
                }
            }
        }
        return returnVal;
    }

    @Override
    public Iterable<Object> BFS(Object obj) throws NodeDoesNotExistException {
        Integer firstID = obj.hashCode();

        // Error Handling
        if (getNode(firstID) == null) {
            throw new NodeDoesNotExistException();
        }

        ArrayList<Object> ordered = new ArrayList<>();
        ArrayList<Integer> fringe = new ArrayList<>();
        fringe.add(firstID);
        while (fringe.size() != 0) {
            Integer curr = fringe.remove(0);
            if (!ordered.contains(getNode(curr).getValue())) {
                ordered.add(getNode(curr).getValue());
                for (Node neighbor : getNode(curr).getOutward()) {
                    fringe.add(neighbor.getID());
                }
            }
        }
        return ordered;
    }
}

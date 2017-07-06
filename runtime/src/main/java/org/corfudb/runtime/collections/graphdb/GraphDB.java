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

        for (Integer neighbor : adjacent(obj)) {
            getNode(neighbor).removeEdge(getNode(objID));
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
        if (fromNode.getOutward().contains(toNode.getID()) &&
                toNode.getInward().contains(fromNode.getID())) {
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
    public Iterable<Integer> adjacent(Object obj) throws NodeDoesNotExistException {
        Integer objID = obj.hashCode();

        // Error Handling
        if (getNode(objID) == null) {
            throw new NodeDoesNotExistException();
        }

        ArrayList<Integer> edgeList = new ArrayList<>();
        edgeList.addAll(getNode(objID).getInward());
        edgeList.addAll(getNode(objID).getOutward());
        return edgeList;
    }

    @Override
    public Iterable<Integer> preDFS(Object obj) throws NodeDoesNotExistException {
        Integer firstID = obj.hashCode();

        // Error Handling
        if (getNode(firstID) == null) {
            throw new NodeDoesNotExistException();
        }

        ArrayList<Integer> returnVal = new ArrayList<>();

        Stack<Integer> stack = new Stack();
        stack.add(firstID);

        HashSet<Integer> visited = new HashSet<>();
        visited.add(firstID);

        while (!stack.isEmpty()) {
            Integer element = stack.pop();
            returnVal.add(element);

            ArrayList<Integer> neighbors = getNode(element).getOutward();
            for (int i = neighbors.size() - 1; i >= 0; i--) {
                Integer neighbor = neighbors.get(i);
                if (neighbor != null && !visited.contains(neighbor)) {
                    stack.add(neighbor);
                    visited.add(neighbor);
                }
            }
        }
        return returnVal;
    }

    @Override
    public Iterable<Integer> postDFS(Object obj) throws NodeDoesNotExistException {
        Integer firstID = obj.hashCode();

        // Error Handling
        if (getNode(firstID) == null) {
            throw new NodeDoesNotExistException();
        }

        ArrayList<Integer> returnVal = new ArrayList<>();

        Stack<Integer> stack = new Stack();
        stack.add(firstID);

        HashSet<Integer> visited = new HashSet<>();
        visited.add(firstID);

        while (!stack.isEmpty()) {
            Integer element = stack.pop();
            returnVal.add(0, element);

            ArrayList<Integer> neighbors = getNode(element).getOutward();
            for (Integer neighbor : neighbors) {
                if (neighbor != null && !visited.contains(neighbor)) {
                    stack.add(neighbor);
                    visited.add(neighbor);
                }
            }
        }
        return returnVal;
    }

    @Override
    public Iterable<Integer> BFS(Object obj) throws NodeDoesNotExistException {
        Integer firstID = obj.hashCode();

        // Error Handling
        if (getNode(firstID) == null) {
            throw new NodeDoesNotExistException();
        }

        ArrayList<Integer> ordered = new ArrayList<>();
        ArrayList<Integer> fringe = new ArrayList<>();
        fringe.add(firstID);
        while (fringe.size() != 0) {
            Integer curr = fringe.remove(0);
            if (!ordered.contains(curr)) {
                ordered.add(curr);
                for (Integer neighbor : getNode(curr).getOutward()) {
                    fringe.add(neighbor);
                }
            }
        }
        return ordered;
    }
}

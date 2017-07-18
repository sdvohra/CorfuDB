package org.corfudb.runtime.collections.graphdb;

import org.corfudb.runtime.CorfuRuntime;
import org.corfudb.runtime.collections.SMRMap;

import java.util.*;

/**
 * Implements Graph. Uses adjacency lists to represent
 * a directed graph of Nodes.
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
            if (getNode(objNode.getID()).equals(obj)) {
                throw new NodeAlreadyExistsException();
            } else {
                System.out.println("NAE ERROR");
            }
        }

        nodes.put(objNode.getID(), objNode);
    }

    public Node getNode(Integer id) { return nodes.get(id); }

    public Integer getID(Object obj) {
        Node tempObj = new Node(obj);
        return tempObj.getID();
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
        Node objNode = new Node(obj);
        Integer objID = objNode.getID();
        // Error Handling
        if (getNode(objID) == null) {
            throw new NodeDoesNotExistException();
        }

        for (Object neighbor : adjacent(obj)) {
            Node neighborNode = new Node(neighbor);
            Integer neighborID = neighborNode.getID();
            getNode(neighborID).removeEdge(getNode(objID));
            nodes.put(neighborID, getNode(neighborID)); // for persistence
        }
        nodes.remove(objID);
    }

    public int getNumNodes() {
        return nodes.size();
    }

    public void connect(Object from, Object to) throws NodeDoesNotExistException,
            EdgeAlreadyExistsException {
        Integer fromNodeID = (new Node(from)).getID();
        Integer toNodeID = (new Node(to)).getID();
        Node fromNode = getNode(fromNodeID);
        Node toNode = getNode(toNodeID);

        // Error Handling
        if (fromNode == null || toNode == null) {
            throw new NodeDoesNotExistException();
        }
        if (fromNode.getOutward().contains(toNode) &&
                toNode.getInward().contains(fromNode)) {
            throw new EdgeAlreadyExistsException();
        }
        fromNode.addEdge(toNode);

        nodes.put(fromNodeID, fromNode);
        nodes.put(toNodeID, toNode);
    }

    public void disconnect(Object from, Object to) throws NodeDoesNotExistException,
            EdgeDoesNotExistException {
        Integer fromNodeID = (new Node(from)).getID();
        Integer toNodeID = (new Node(to)).getID();
        Node fromNode = getNode(fromNodeID);
        Node toNode = getNode(toNodeID);

        // Error Handling
        if (fromNode == null || toNode == null) {
            throw new NodeDoesNotExistException();
        }
        if (!fromNode.getOutward().contains(toNode) || !toNode.getInward().contains(fromNode)) {
            throw new EdgeDoesNotExistException();
        }
        fromNode.removeEdge(toNode);

        nodes.put(fromNodeID, fromNode);
        nodes.put(toNodeID, toNode);
    }

    @Override
    public void clear() {
        nodes.clear();
    }

    @Override
    public Iterable<Object> adjacent(Object obj) throws NodeDoesNotExistException {
        Node objNode = new Node(obj);
        Integer objID = objNode.getID();

        // Error Handling
        if (getNode(objID) == null) {
            throw new NodeDoesNotExistException();
        }

        ArrayList<Object> adjList = new ArrayList<>();
        for (Integer inwardNodeID : getNode(objID).getInward()) {
            adjList.add(getNode(inwardNodeID).getValue());
        }
        for (Integer outwardNodeID : getNode(objID).getOutward()) {
            adjList.add(getNode(outwardNodeID).getValue());
        }
        return adjList;
    }

    @Override
    public Iterable<Object> preDFS(Object obj) throws NodeDoesNotExistException {
//        Node objNode = new Node(obj);
//        Integer firstID = objNode.getID();
//
//        // Error Handling
//        if (getNode(firstID) == null) {
//            throw new NodeDoesNotExistException();
//        }
//
//        HashSet<Object> returnVal = new ArrayList<>();
//
//        Stack<Integer> stack = new Stack(); // IDs to visit
//        stack.add(firstID);
//
//        HashSet<Integer> visited = new HashSet<>(); // IDs visited
//        visited.add(firstID);
//
//        while (!stack.isEmpty()) {
//            Integer elementID = stack.pop();
//            returnVal.add(getNode(elementID).getValue());
//
//            Set<Integer> neighbors = getNode(elementID).getOutward();
//            for (Integer neighbor : neighbors) {
//                if (neighbor != null && !visited.contains(neighbor)) {
//                    stack.add(neighbor);
//                    visited.add(neighbor);
//                }
//            }
//        }
//        return returnVal;
        return null;
    }

    @Override
    public Iterable<Object> postDFS(Object obj) throws NodeDoesNotExistException {
//        Node objNode = new Node(obj);
//        Integer firstID = objNode.getID();
//
//        // Error Handling
//        if (getNode(firstID) == null) {
//            throw new NodeDoesNotExistException();
//        }
//
//        HashSet<Object> returnVal = new ArrayList<>();
//
//        Stack<Integer> stack = new Stack(); // IDs to visit
//        stack.add(firstID);
//
//        HashSet<Integer> visited = new HashSet<>(); // IDs visited
//        visited.add(firstID);
//
//        while (!stack.isEmpty()) {
//            Integer elementID = stack.pop();
//            returnVal.add(0, getNode(elementID).getValue());
//
//            List<Integer> neighbors = getNode(elementID).getOutward();
//            for (int i = 0; i < neighbors.size(); i++) {
//                Integer neighbor = neighbors.get(i);
//                if (neighbor != null && !visited.contains(neighbor)) {
//                    stack.add(neighbor);
//                    visited.add(neighbor);
//                }
//            }
//        }
//        return returnVal;
        return null;
    }

    @Override
    public Iterable<Object> BFS(Object obj) throws NodeDoesNotExistException {
//        Node objNode = new Node(obj);
//        Integer firstID = objNode.getID();
//
//        // Error Handling
//        if (getNode(firstID) == null) {
//            throw new NodeDoesNotExistException();
//        }
//
//        ArrayList<Object> ordered = new ArrayList<>();
//        ArrayList<Integer> fringe = new ArrayList<>();
//        fringe.add(firstID);
//        while (fringe.size() != 0) {
//            Integer curr = fringe.remove(0);
//            if (!ordered.contains(getNode(curr).getValue())) {
//                ordered.add(getNode(curr).getValue());
//                for (Integer neighbor : getNode(curr).getOutward()) {
//                    fringe.add(neighbor);
//                }
//            }
//        }
//        return ordered;
        return null;
    }
}

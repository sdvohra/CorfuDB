package org.corfudb.runtime.collections.graphdb;

/**
 * Interface class that represents an undirected graph.
 * Contains the following methods.
 *
 * @author shriyav
 */
public interface Graph {
    /** Clear entire graphdb: remove all nodes/edges. */
    void clear();

    /** Returns an iterable of Integer IDs for all nodes adjacent to obj. */
    Iterable<Integer> adjacent(Object obj) throws NodeDoesNotExistException;

    /** Returns an iterable of Integer IDs in pre-DFS order starting with obj. */
    Iterable<Integer> preDFS(Object obj) throws NodeDoesNotExistException;

    /** Returns an iterable of Integer IDs in post-DFS order starting with obj. */
    Iterable<Integer> postDFS(Object obj) throws NodeDoesNotExistException;

    /** Returns an iterable of Integer IDs in BFS order starting with obj. */
    Iterable<Integer> BFS(Object obj) throws NodeDoesNotExistException;
}

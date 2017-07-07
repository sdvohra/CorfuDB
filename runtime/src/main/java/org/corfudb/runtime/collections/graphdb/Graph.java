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

    /** Returns an iterable of Objects for all nodes adjacent to obj. */
    Iterable<Object> adjacent(Object obj) throws NodeDoesNotExistException;

    /** Returns an iterable of Objects in pre-DFS order starting with obj. */
    Iterable<Object> preDFS(Object obj) throws NodeDoesNotExistException;

    /** Returns an iterable of Objects in post-DFS order starting with obj. */
    Iterable<Object> postDFS(Object obj) throws NodeDoesNotExistException;

    /** Returns an iterable of Objects in BFS order starting with obj. */
    Iterable<Object> BFS(Object obj) throws NodeDoesNotExistException;
}

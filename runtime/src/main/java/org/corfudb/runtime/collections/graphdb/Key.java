package org.corfudb.runtime.collections.graphdb;

import java.util.UUID;

/**
 * Created by shriyav on 6/19/17.
 */
public class Key implements KeyInterface<Integer> {
    String id;

    Key(String uuid) {
        id = uuid;
    }

    @Override
    public Integer getID() {
        return id.hashCode();
    }
}

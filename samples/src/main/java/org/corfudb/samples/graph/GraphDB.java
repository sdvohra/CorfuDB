package org.corfudb.samples.graph;

import org.corfudb.runtime.CorfuRuntime;
import org.corfudb.runtime.collections.SMRMap;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by shriyav on 5/25/17.
 */

public class GraphDB {
    private SMRMap<UUID, Node> vertices;
    CorfuRuntime rt;

    private Tracer t;
    private boolean isTracing;

    public class Tracer {
        String cat;
        String name;
        int pid;
        long tid;
        long ts;
        String ph;
        String[] args;
        File log;

        public Tracer() {
            cat = "";
            name = "";
            pid = -1;
            tid = -1;
            ts = -1;
            ph = "";
            args = null;
            log = new File("performanceLog.json");
            try {
                if (!log.exists()) {
                    log.createNewFile();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void updateArgs(String c, String n, int p1, long t1, long t2, String p2, String[] a) {
            cat = c;
            name = n;
            pid = p1;
            tid = t1;
            ts = t2;
            ph = p2;
            args = a;
            writeToLog();
        }

        public void writeToLog() {
            try {
                FileWriter fw = new FileWriter(log.getAbsoluteFile(), true);
                BufferedWriter bw = new BufferedWriter(fw);

                bw.write("{");
                bw.write("\"cat\": " + "\"" + t.cat + "\",");
                bw.write("\"pid\": " + t.pid + ",");
                bw.write("\"tid\": " + t.tid + ",");
                bw.write("\"ts\": " + t.ts + ",");
                bw.write("\"ph\": " + "\""  + t.ph + "\",");
                bw.write("\"name\": " + "\""  + t.name + "\",");
                bw.write("\"args\": " + t.args + "},\n");

                bw.close();
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("Done");
        }
    }

    public void startMethodTrace() {
        isTracing = true;
    }

    public void endMethodTrace() {
        isTracing = false;
    }

    public GraphDB(CorfuRuntime runtime) {
        vertices = runtime.getObjectsView()
                .build()
                .setStreamName("A")     // stream name
                .setType(SMRMap.class)  // object class backed by this stream
                .open();                // instantiate the object!
        rt = runtime;
        t = new Tracer();
        isTracing = false;
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
        if (isTracing) {
            t.updateArgs("GraphDBTest", "adjacent", 1, Thread.currentThread().getId(),
                    System.currentTimeMillis(), "B", null);
        }

        // begin method
        HashSet<UUID> edgeList = vertices.get(v).getEdges();
        ArrayList<UUID> returnVal = new ArrayList<>();
        for (UUID e : edgeList) {
            returnVal.add(e);
        }
        // end method

        if (isTracing) {
            t.updateArgs("GraphDBTest", "adjacent", 1, Thread.currentThread().getId(),
                    System.currentTimeMillis(), "E", null);
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
        if (isTracing) {
            t.updateArgs("GraphDBTest", "preDFS", 1, Thread.currentThread().getId(),
                    System.currentTimeMillis(), "B", null);
        }

        // begin method
        ArrayList<UUID> returnVal = dfsHelper(first, "pre", new ArrayList<UUID>(), new ArrayList<UUID>());
        // end method

        if (isTracing) {
            t.updateArgs("GraphDBTest", "preDFS", 1, Thread.currentThread().getId(),
                    System.currentTimeMillis(), "E", null);
        }
        return returnVal;
    }

    ArrayList<UUID> postDFS(UUID first) {
        if (isTracing) {
            t.updateArgs("GraphDBTest", "postDFS", 1, Thread.currentThread().getId(),
                    System.currentTimeMillis(), "B", null);
        }

        // begin method
        ArrayList<UUID> returnVal = dfsHelper(first, "post", new ArrayList<UUID>(), new ArrayList<UUID>());
        // end method

        if (isTracing) {
            t.updateArgs("GraphDBTest", "postDFS", 1, Thread.currentThread().getId(),
                    System.currentTimeMillis(), "E", null);
        }
        return returnVal;
    }

    ArrayList<UUID> BFS(UUID first) {
        if (isTracing) {
            t.updateArgs("GraphDBTest", "BFS", 1, Thread.currentThread().getId(),
                    System.currentTimeMillis(), "B", null);
        }

        // begin method
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
        // end method

        if (isTracing) {
            t.updateArgs("GraphDBTest", "BFS", 1, Thread.currentThread().getId(),
                    System.currentTimeMillis(), "E", null);
        }
        return ordered;
    }
}

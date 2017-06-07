package org.corfudb.samples.graph;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import org.corfudb.runtime.CorfuRuntime;
import org.corfudb.runtime.collections.SMRMap;
import org.corfudb.util.GitRepositoryState;
import org.docopt.Docopt;
import org.slf4j.LoggerFactory;

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

    private static final String USAGE = "Usage: GraphDBLauncher [-c <conf>]\n"
            + "Options:\n"
            + " -c <conf>     Set the configuration host and port  [default: localhost:9999]\n";

    /**
     * Internally, the corfuRuntime interacts with the CorfuDB service over TCP/IP sockets.
     *
     * @param configurationString specifies the IP:port of the CorfuService
     *                            The configuration string has format "hostname:port", for example, "localhost:9090".
     * @return a CorfuRuntime object, with which Corfu applications perform all Corfu operations
     */
    private static CorfuRuntime getRuntimeAndConnect(String configurationString) {
        CorfuRuntime corfuRuntime = new CorfuRuntime(configurationString).connect();
        return corfuRuntime;
    }

    private static void testMethodsAndPerf(GraphDB d) {
//        for (int i = 0; i < 2000; i++) {
//            d.addNode("" + i);
//        }
//
//        for (int i = 0; i < 1999; i++) {
//            int temp = i + 1;
//            d.addEdge("" + i, "" + temp);
//            System.out.println(i);
//        }
//
//        d.startMethodTrace();
//        for (String friend : d.adjacent("2")) {
//            System.out.println(friend);
//        } // expect: ADEF
//        d.endMethodTrace();
//
//        d.startMethodTrace();
//        ArrayList<Node> pre = d.preDFS(d.getNode("0"));
//        for (Node item : pre) {
//            System.out.println(item.getName());
//        } // expect: ABDEHIFCGJ
//        d.endMethodTrace();
//
//        d.startMethodTrace();
//        ArrayList<Node> post = d.postDFS(d.getNode("0"));
//        for (Node item : post) {
//            System.out.println(item.getName());
//        } // expect: DHIEFBJGCA
//        d.endMethodTrace();
//
//        d.startMethodTrace();
//        ArrayList<Node> bfs = d.BFS(d.getNode("0"));
//        for (Node item : bfs) {
//            System.out.println(item.getName());
//        } // expect: CAGBJDEFHI
//        d.endMethodTrace();

        d.clear();
    }

    public static void main(String[] args) {
        // Enabling logging
        //Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        //root.setLevel(Level.TRACE);

        // Parse the options given, using docopt.
        Map<String, Object> opts =
                new Docopt(USAGE)
                        .withVersion(GitRepositoryState.getRepositoryState().describe)
                        .parse(args);
        String corfuConfigurationString = (String) opts.get("-c");

        /**
         * First, the application needs to instantiate a CorfuRuntime,
         * which is a Java object that contains all of the Corfu utilities exposed to applications.
         */
        CorfuRuntime runtime = getRuntimeAndConnect(corfuConfigurationString);

        GraphDB d = new GraphDB(runtime);
        //d.testMethodsAndPerf(d);

        // Create Transport Zone
        UUID TZ1 = UUID.randomUUID();
        d.addNode(TZ1, "TransportZone0", "Transport Zone");
        //props
        HashMap<String, Object> hm = new HashMap<>();
        hm.put("Description", "Transport Zone");
        hm.put("HostSwitch", "HostSwitch0");
        d.getNode(TZ1).setProperties(hm);

        // Create Transport Nodes
        UUID TN1 = null, TN2 = null, TN3 = null;
        for (int i = 0; i < 3; i++) {
            for (int j = 1; j < 3; j++) {
                UUID curr = UUID.randomUUID();
                if (i == 1 && j == 1) {
                    TN1 = curr;
                } else if (i == 1 && j == 2) {
                    TN2 = curr;
                } else if (i == 2 && j == 1) {
                    TN3 = curr;
                }
                d.addNode(curr, "TransportNode" + i + "." + j, "Transport Node");
                // props
                hm = new HashMap<>();
                hm.put("Description", "Transport Node");
                hm.put("HostSwitch", "HostSwitch" + i);
                d.getNode(curr).setProperties(hm);
            }
        }

        // Create Logical Switches
        UUID LS1 = null, LS2 = null;
        for (int i = 0; i < 4; i++) {
            for (int j = 1; j < 3; j++) {
                UUID curr = UUID.randomUUID();
                if (i == 3 && j == 1) {
                    LS1 = curr;
                } else if (i == 3 && j == 2) {
                    LS2 = curr;
                }
                d.addNode(curr, "LogicalSwitch" + i + "." + j, "Logical Switch");
                // props
                hm = new HashMap<>();
                hm.put("Description", "Logical Switch");
                hm.put("Property1", "Value1");
                hm.put("Property2", "Value2"); // hard-coded, should depend on # of profiles
                d.getNode(curr).setProperties(hm);
            }
        }

        // Create Logical Ports
        UUID LP1 = null, LP2 = null, LP3 = null;
        for (int i = 0; i < 3; i++) {
            for (int j = 1; j < 3; j++) {
                UUID curr = UUID.randomUUID();
                if (i == 0 && j == 2) {
                    LP1 = curr;
                } else if (i == 2 && j == 2) {
                    LP2 = curr;
                } else if (i == 1 && j == 2) {
                    LP3 = curr;
                }
                d.addNode(curr, "LogicalPort" + i + "." + j, "Logical Port");
                // props
                hm = new HashMap<>();
                hm.put("Description", "Logical Port");
                hm.put("Property1", "Value1");
                hm.put("Property2", "Value2"); // hard-coded, should depend on # of profiles
                d.getNode(curr).setProperties(hm);
            }
        }

        // Adding edges - order matters!
        d.addEdge(TN1, TZ1);
        d.addEdge(TN2, TZ1);
        d.addEdge(TN3, TZ1);
        d.addEdge(LS1, TZ1);
        d.addEdge(LS2, TZ1);
        d.addEdge(LP1, LS1); // addPort - wrap in API
        d.addEdge(LP3, LS2);
        d.addEdge(LP2, LS1);

        System.out.println(d);

        for (UUID friend : d.adjacent(TZ1)) {
            System.out.println(d.vertices.get(friend).getName());
        } // expect: 1.1, 1.2, 2.1
        System.out.println();

        ArrayList<UUID> pre = d.preDFS(TZ1);
        for (UUID item : pre) {
            System.out.println(d.vertices.get(item).getName());
        } // expect: 0, 1.1, 1.2, 2.1
        System.out.println();

        ArrayList<UUID> post = d.postDFS(TZ1);
        for (UUID item : post) {
            System.out.println(d.vertices.get(item).getName());
        } // expect: 1.1, 1.2, 2.1, 0
        System.out.println();

        ArrayList<UUID> bfs = d.BFS(TZ1);
        for (UUID item : bfs) {
            System.out.println(d.vertices.get(item).getName());
        } // expect: 0, 1.1, 1.2, 2.1
        System.out.println();
    }
}

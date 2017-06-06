package org.corfudb.samples.graph;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.google.common.collect.ImmutableList;
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
    private SMRMap<String, Node> vertices;
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

    public Node getNode(String name) { return vertices.get(name); }

    public void addNode(String name) { vertices.put(name, new Node(name)); }

    public void addNode(String name, String type, Object[] params) {
        if (type.equals("Transport Zone")) {
            TransportZone tz = new TransportZone((UUID) params[0]);
            tz.setName(name);
            vertices.put(name, tz);
        } else if (type.equals("Logical Switch")) {
            LogicalSwitch ls = new LogicalSwitch((UUID) params[0], (UUID) params[1], (ArrayList<UUID>) params[2]);
            ls.setName(name);
            vertices.put(name, ls);
        } else if (type.equals("Logical Port")) {
            LogicalPort lp = new LogicalPort((UUID) params[0], (UUID) params[1], (Attachment) params[2],
                    (ArrayList<UUID>) params[3]);
            lp.setName(name);
            vertices.put(name, lp);
        } else if (type.equals("Transport Node")) {
            TransportNode tn = new TransportNode((UUID) params[0], (HashSet<UUID>) params[1]);
            tn.setName(name);
            vertices.put(name, tn);
        }
    }

    public void update(String name, Map<String, Object> properties) {
        vertices.get(name).getProperties().putAll(properties);
    }

    public void removeNode(String name) { vertices.remove(name); }

    public int getNumNodes() {
        return vertices.size();
    }

    public Edge addEdge(Node from, Node to) {
        if (from instanceof TransportNode) {
            ((TransportNode) from).getTransportZoneIds().add(((TransportZone) to).id);
        } else if (from instanceof LogicalSwitch) {
            ((LogicalSwitch) from).setTzId(((TransportZone) to).id);
        } else if (from instanceof LogicalPort) {
            ((LogicalPort) from).setLogicalSwitchId(((LogicalSwitch) to).id);
        }

        Edge e = new Edge(from, to);
        from.addEdge(e);
        to.addEdge(e);
        return e;
    }

    public Edge addEdge(String from, String to) {
        Node f = getNode(from);
        Node t = getNode(to);
        return addEdge(f, t);
    }

    /** Returns an iterable of all vertex IDs in the graph. */
    Iterable<String> vertices() {
        return vertices.keySet();
    }

    /** Returns an iterable of names of all vertices adjacent to v. */
    Iterable<String> adjacent(String v) { // modify with pointers
        if (isTracing) {
            t.updateArgs("GraphDBTest", "adjacent", 1, Thread.currentThread().getId(),
                    System.currentTimeMillis(), "B", null);
        }

        // begin method
        ArrayList<Edge> edgeList = vertices.get(v).getEdges();
        ArrayList<String> returnVal = new ArrayList<>();
        for (Edge e : edgeList) {
            if (v.equals(e.getTo().getName())) {
                returnVal.add(e.getFrom().getName());
            } else {
                returnVal.add(e.getTo().getName());
            }
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

    /**
     *  Remove nodes with no connections from the graph.
     *  While this does not guarantee that any two nodes in the remaining graph are connected,
     *  we can reasonably assume this since typically roads are connected.
     */
    void clean() { // error in fn - it.remove() throws an error; everything else seems right.
        // Kept running into ConcurrentModificationException
        // Resolved with:
        // http://stackoverflow.com/questions/1884889/iterating-over-and-removing-from-a-map
        int counter = 0;
        for (Iterator<Map.Entry<String, Node>> it = vertices.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, Node> entry = it.next();
            if (entry.getValue().getEdges().size() == 0) {
                counter++;
                //it.remove();
            }
        }
        System.out.println(counter);
    }

    private ArrayList<Node> dfsHelper(Node n, String dfsType, ArrayList<Node> ordered, ArrayList<Node> seen) {
        if (n != null && !seen.contains(n)) {
            if (dfsType.equals("pre")) {
                ordered.add(n);
            }
            seen.add(n);
            for (Edge neighbor: n.getEdges()) {
                if (neighbor.getFrom().equals(n)) {
                    dfsHelper(neighbor.getTo(), dfsType, ordered, seen);
                } else {
                    dfsHelper(neighbor.getFrom(), dfsType, ordered, seen);
                }
            }
            if (dfsType.equals("post")) {
                ordered.add(n);
            }
        }
        return ordered;
    }

    ArrayList<Node> preDFS(Node f) {
        if (isTracing) {
            t.updateArgs("GraphDBTest", "preDFS", 1, Thread.currentThread().getId(),
                    System.currentTimeMillis(), "B", null);
        }

        // begin method
        ArrayList<Node> returnVal = dfsHelper(f, "pre", new ArrayList<Node>(), new ArrayList<Node>());
        // end method

        if (isTracing) {
            t.updateArgs("GraphDBTest", "preDFS", 1, Thread.currentThread().getId(),
                    System.currentTimeMillis(), "E", null);
        }
        return returnVal;
    }

    ArrayList<Node> postDFS(Node f) {
        if (isTracing) {
            t.updateArgs("GraphDBTest", "postDFS", 1, Thread.currentThread().getId(),
                    System.currentTimeMillis(), "B", null);
        }

        // begin method
        ArrayList<Node> returnVal = dfsHelper(f, "post", new ArrayList<Node>(), new ArrayList<Node>());
        // end method

        if (isTracing) {
            t.updateArgs("GraphDBTest", "postDFS", 1, Thread.currentThread().getId(),
                    System.currentTimeMillis(), "E", null);
        }
        return returnVal;
    }

    ArrayList<Node> BFS(Node f) {
        if (isTracing) {
            t.updateArgs("GraphDBTest", "BFS", 1, Thread.currentThread().getId(),
                    System.currentTimeMillis(), "B", null);
        }

        // begin method
        ArrayList<Node> ordered = new ArrayList<>();
        ArrayList<Node> fringe = new ArrayList<>();
        fringe.add(f);
        while (fringe.size() != 0) {
            Node curr = fringe.remove(0);
            if (!ordered.contains(curr)) {
                ordered.add(curr);
                for (Edge neighbor : curr.getEdges()) {
                    if (neighbor.getFrom().equals(curr)) {
                        fringe.add(neighbor.getTo());
                    } else {
                        fringe.add(neighbor.getFrom());
                    }
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
        for (int i = 0; i < 2000; i++) {
            d.addNode("" + i);
        }

        for (int i = 0; i < 1999; i++) {
            int temp = i + 1;
            d.addEdge("" + i, "" + temp);
            System.out.println(i);
        }

        d.startMethodTrace();
        for (String friend : d.adjacent("2")) {
            System.out.println(friend);
        } // expect: ADEF
        d.endMethodTrace();

        d.startMethodTrace();
        ArrayList<Node> pre = d.preDFS(d.getNode("0"));
        for (Node item : pre) {
            System.out.println(item.getName());
        } // expect: ABDEHIFCGJ
        d.endMethodTrace();

        d.startMethodTrace();
        ArrayList<Node> post = d.postDFS(d.getNode("0"));
        for (Node item : post) {
            System.out.println(item.getName());
        } // expect: DHIEFBJGCA
        d.endMethodTrace();

        d.startMethodTrace();
        ArrayList<Node> bfs = d.BFS(d.getNode("0"));
        for (Node item : bfs) {
            System.out.println(item.getName());
        } // expect: CAGBJDEFHI
        d.endMethodTrace();

        d.clear();
    }

    public static void main(String[] args) {
        // Enabling logging
        Logger root = (Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
        root.setLevel(Level.TRACE);

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
        Object[] p = new Object[1];
        p[0] = new UUID(-3121351451739669003L, -7836414342361877842L);
        d.addNode("TransportZone0", "Transport Zone", p);
        //props
        HashMap<String, Object> hm = new HashMap<>();
        hm.put("Description", "Transport Zone");
        hm.put("HostSwitch", "HostSwitch0");
        d.getNode("TransportZone0").properties = hm;

        // Create Transport Nodes
        for (int i = 0; i < 3; i++) {
            for (int j = 1; j < 3; j++) {
                p = new Object[3];
                p[0] = UUID.randomUUID(); //new UUID(-7646002813284697009L, -7442247082943576294L);
                HashSet<UUID> hs = new HashSet<>();
                //hs.add(new UUID(-3121351451739669003L, -7836414342361877842L));
                //hs.add(UUID.randomUUID());
                p[1] = hs;
                d.addNode("TransportNode" + i + "." + j, "Transport Node", p);
                // props
                hm = new HashMap<>();
                hm.put("Description", "Transport Node");
                hm.put("HostSwitch", "HostSwitch" + i);
                d.getNode("TransportNode" + i + "." + j).properties = hm;
            }
        }

        // Create Logical Switches
        for (int i = 0; i < 4; i++) {
            for (int j = 1; j < 3; j++) {
                p = new Object[3];
                p[0] = UUID.randomUUID(); //new UUID(-7646002813284697009L, -7442247082943576294L);
                p[1] = null;
                ArrayList<UUID> profs = new ArrayList<>();
                p[2] = profs;
                d.addNode("LogicalSwitch" + i + "." + j, "Logical Switch", p);
                // props
                hm = new HashMap<>();
                hm.put("Description", "Logical Switch");
                hm.put("Property1", "Value1");
                hm.put("Property2", "Value2"); // hard-coded, should depend on # of profiles
                d.getNode("LogicalSwitch" + i + "." + j).properties = hm;
            }
        }

        // Create Logical Ports
        for (int i = 0; i < 3; i++) {
            for (int j = 1; j < 3; j++) {
                p = new Object[4];
                p[0] = UUID.randomUUID(); //new UUID(-7646002813284697009L, -7442247082943576294L);
                p[1] = null;
                p[2] = null; // what is an attachment?
                ArrayList<UUID> profs = new ArrayList<>();
                p[3] = profs;
                d.addNode("LogicalPort" + i + "." + j, "Logical Port", p);
                // props
                hm = new HashMap<>();
                hm.put("Description", "Logical Port");
                hm.put("Property1", "Value1");
                hm.put("Property2", "Value2"); // hard-coded, should depend on # of profiles
                d.getNode("LogicalPort" + i + "." + j).properties = hm;
            }
        }

        // Adding edges - order matters!
        d.addEdge("TransportNode1.1", "TransportZone0");
        d.addEdge("TransportNode1.2", "TransportZone0");
        d.addEdge("TransportNode2.1", "TransportZone0");
        d.addEdge("LogicalSwitch3.1", "TransportZone0");
        d.addEdge("LogicalSwitch3.2", "TransportZone0");
        d.addEdge("LogicalPort0.2", "LogicalSwitch3.1");
        d.addEdge("LogicalPort1.2", "LogicalSwitch3.2");
        d.addEdge("LogicalPort2.2", "LogicalSwitch3.1");

        System.out.println(d);

        for (String friend : d.adjacent("TransportZone0")) {
            System.out.println(friend);
        } // expect: 1.1, 1.2, 2.1

        ArrayList<Node> pre = d.preDFS(d.getNode("TransportZone0"));
        for (Node item : pre) {
            System.out.println(item.getName());
        } // expect: 0, 1.1, 1.2, 2.1

        ArrayList<Node> post = d.postDFS(d.getNode("TransportZone0"));
        for (Node item : post) {
            System.out.println(item.getName());
        } // expect: 1.1, 1.2, 2.1, 0

        ArrayList<Node> bfs = d.BFS(d.getNode("TransportZone0"));
        for (Node item : bfs) {
            System.out.println(item.getName());
        } // expect: 0, 1.1, 1.2, 2.1

        d.clean();
        System.out.println(d);

        d.clear();
    }
}

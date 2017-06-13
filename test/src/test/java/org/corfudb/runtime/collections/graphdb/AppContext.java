package org.corfudb.runtime.collections.graphdb;

import org.corfudb.runtime.CorfuRuntime;
import org.corfudb.util.GitRepositoryState;
import org.docopt.Docopt;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

/**
 * Created by shriyav on 6/7/17.
 */
public class AppContext {
    GraphDB graph;

    public AppContext(CorfuRuntime rt, String graphName) {
        graph = new GraphDB(rt, graphName);
    }

    public GraphDB getGraph() {
        return graph;
    }

    /** Creates new TransportZone and returns its UUID. */
    public UUID createTransportZone(String name) {
        UUID curr = UUID.randomUUID();
        graph.addNode(curr, name, "Transport Zone");
        return curr;
    }

    /** Creates new TransportNode and returns its UUID. */
    public UUID createTransportNode(String name) {
        UUID curr = UUID.randomUUID();
        graph.addNode(curr, name, "Transport Node");
        return curr;
    }

    /** Creates new LogicalSwitch and returns its UUID. */
    public UUID createLogicalSwitch(String name) {
        UUID curr = UUID.randomUUID();
        graph.addNode(curr, name, "Logical Switch");
        return curr;
    }

    /** Creates new LogicalPort and returns its UUID. */
    public UUID createLogicalPort(String name) {
        UUID curr = UUID.randomUUID();
        graph.addNode(curr, name, "Logical Port");
        return curr;
    }

    public void connectTransportNode(UUID tnID, UUID tzID) {
        graph.addEdge(tnID, tzID);
    }

    public void connectLogicalSwitch(UUID lsID, UUID tzID) {
        graph.addEdge(lsID, tzID);
    }

    public void connectLogicalPort(UUID lpID, UUID lsID) {
        graph.addEdge(lpID, lsID);
    }

    private static final String USAGE = "Usage: AppContext [-c <conf>]\n"
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

    public static void main(String[] args) {
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

        AppContext myApp = new AppContext(runtime, "myGraph");

        // Create Transport Zone
        UUID TZ1 = myApp.createTransportZone("TransportZone0");
        // Create Transport Nodes
        UUID TN1 = myApp.createTransportNode("TransportNode1.1");
        UUID TN2 = myApp.createTransportNode("TransportNode1.2");
        UUID TN3 = myApp.createTransportNode("TransportNode2.1");
        // Create Logical Switches
        UUID LS1 = myApp.createLogicalSwitch("LogicalSwitch3.1");
        UUID LS2 = myApp.createLogicalSwitch("LogicalSwitch3.2");
        // Create Logical Ports
        UUID LP1 = myApp.createLogicalPort("LogicalPort0.2");
        UUID LP2 = myApp.createLogicalPort("LogicalPort2.2");
        UUID LP3 = myApp.createLogicalPort("LogicalPort1.2");

        // Connect the elements
        myApp.connectTransportNode(TN1, TZ1);
        myApp.connectTransportNode(TN2, TZ1);
        myApp.connectTransportNode(TN3, TZ1);
        myApp.connectLogicalSwitch(LS1, TZ1);
        myApp.connectLogicalSwitch(LS2, TZ1);
        myApp.connectLogicalPort(LP1, LS1);
        myApp.connectLogicalPort(LP2, LS1);
        myApp.connectLogicalPort(LP3, LS2);

        // Run graphdb methods!
        System.out.println(myApp.getGraph()); // should change each time it's run (b/c persistent)

        ArrayList<String> actual = new ArrayList<>();
        ArrayList<String> expected = new ArrayList<>();
        expected.add("TransportNode1.1");
        expected.add("TransportNode1.2");
        expected.add("TransportNode2.1");
        expected.add("LogicalSwitch3.1");
        expected.add("LogicalSwitch3.2");
        for (UUID friend : myApp.getGraph().adjacent(TZ1)) {
            actual.add(myApp.getGraph().getNodes().get(friend).getName());
        } // expect: TN1.1, TN1.2, TN2.1, LS3.1, LS3.2
        Assert.assertEquals(actual, expected);

        ArrayList<UUID> pre = myApp.getGraph().preDFS(TZ1);
        actual.clear();
        expected.clear();
        expected.add("TransportZone0");
        expected.add("TransportNode1.1");
        expected.add("TransportNode1.2");
        expected.add("TransportNode2.1");
        expected.add("LogicalSwitch3.1");
        expected.add("LogicalPort0.2");
        expected.add("LogicalPort2.2");
        expected.add("LogicalSwitch3.2");
        expected.add("LogicalPort1.2");
        for (UUID item : pre) {
            actual.add(myApp.getGraph().getNodes().get(item).getName());
        } // expect: TZ0, TN1.1, TN1.2, TN2.1, LS3.1, LP0.2, LP2.2, LS3.2, LP1.2
        Assert.assertEquals(actual, expected);

        ArrayList<UUID> post = myApp.getGraph().postDFS(TZ1);
        actual.clear();
        expected.clear();
        expected.add("TransportNode1.1");
        expected.add("TransportNode1.2");
        expected.add("TransportNode2.1");
        expected.add("LogicalPort0.2");
        expected.add("LogicalPort2.2");
        expected.add("LogicalSwitch3.1");
        expected.add("LogicalPort1.2");
        expected.add("LogicalSwitch3.2");
        expected.add("TransportZone0");
        for (UUID item : post) {
            actual.add(myApp.getGraph().getNodes().get(item).getName());
        } // expect: TN1.1, TN1.2, TN2.1, LP0.2, LP2.2, LS3.1, LP1.2, LS3.2, TZ0
        Assert.assertEquals(actual, expected);

        ArrayList<UUID> bfs = myApp.getGraph().BFS(TZ1);
        actual.clear();
        expected.clear();
        expected.add("TransportZone0");
        expected.add("TransportNode1.1");
        expected.add("TransportNode1.2");
        expected.add("TransportNode2.1");
        expected.add("LogicalSwitch3.1");
        expected.add("LogicalSwitch3.2");
        expected.add("LogicalPort0.2");
        expected.add("LogicalPort2.2");
        expected.add("LogicalPort1.2");
        for (UUID item : bfs) {
            actual.add(myApp.getGraph().getNodes().get(item).getName());
        } // expect: TZ0, TN1.1, TN1.2, TN2.1, LS3.1, LS3.2, LP0.2, LP2.2, LP1.2
        Assert.assertEquals(actual, expected);

        myApp.getGraph().clear();
        Assert.assertEquals(myApp.getGraph().getNumNodes(), 0);
    }

    //@Test
    public void myTest() {
        String[] args = {"-c", "localhost:9000"};
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

        AppContext myApp = new AppContext(runtime, "myGraph");

        // Create Transport Zone
        UUID TZ1 = myApp.createTransportZone("TransportZone0");
        // Create Transport Nodes
        UUID TN1 = myApp.createTransportNode("TransportNode1.1");
        UUID TN2 = myApp.createTransportNode("TransportNode1.2");
        UUID TN3 = myApp.createTransportNode("TransportNode2.1");
        // Create Logical Switches
        UUID LS1 = myApp.createLogicalSwitch("LogicalSwitch3.1");
        UUID LS2 = myApp.createLogicalSwitch("LogicalSwitch3.2");
        // Create Logical Ports
        UUID LP1 = myApp.createLogicalPort("LogicalPort0.2");
        UUID LP2 = myApp.createLogicalPort("LogicalPort2.2");
        UUID LP3 = myApp.createLogicalPort("LogicalPort1.2");

        // Connect the elements
        myApp.connectTransportNode(TN1, TZ1);
        myApp.connectTransportNode(TN2, TZ1);
        myApp.connectTransportNode(TN3, TZ1);
        myApp.connectLogicalSwitch(LS1, TZ1);
        myApp.connectLogicalSwitch(LS2, TZ1);
        myApp.connectLogicalPort(LP1, LS1);
        myApp.connectLogicalPort(LP2, LS1);
        myApp.connectLogicalPort(LP3, LS2);

        // Run graphdb methods!
        System.out.println(myApp.getGraph()); // should change each time it's run (b/c persistent)

        ArrayList<String> actual = new ArrayList<>();
        ArrayList<String> expected = new ArrayList<>();
        expected.add("TransportNode1.1");
        expected.add("TransportNode1.2");
        expected.add("TransportNode2.1");
        expected.add("LogicalSwitch3.1");
        expected.add("LogicalSwitch3.2");
        for (UUID friend : myApp.getGraph().adjacent(TZ1)) {
            actual.add(myApp.getGraph().getNodes().get(friend).getName());
        } // expect: TN1.1, TN1.2, TN2.1, LS3.1, LS3.2
        Assert.assertEquals(actual, expected);
        System.out.println();

        ArrayList<UUID> pre = myApp.getGraph().preDFS(TZ1);
        for (UUID item : pre) {
            System.out.println(myApp.getGraph().getNodes().get(item).getName());
        } // expect: TZ0, TN1.1, TN1.2, TN2.1, LS3.1, LP0.2, LP2.2, LS3.2, LP1.2
        System.out.println();

        ArrayList<UUID> post = myApp.getGraph().postDFS(TZ1);
        for (UUID item : post) {
            System.out.println(myApp.getGraph().getNodes().get(item).getName());
        } // expect: TN1.1, TN1.2, TN2.1, LP0.2, LP2.2, LS3.1, LP1.2, LS3.2, TZ0
        System.out.println();

        ArrayList<UUID> bfs = myApp.getGraph().BFS(TZ1);
        for (UUID item : bfs) {
            System.out.println(myApp.getGraph().getNodes().get(item).getName());
        } // expect: TZ0, TN1.1, TN1.2, TN2.1, LS3.1, LS3.2, LP0.2, LP2.2, LP1.2
        System.out.println();

        //myApp.getGraph().clear();
    }
}

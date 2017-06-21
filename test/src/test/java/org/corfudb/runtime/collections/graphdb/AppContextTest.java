package org.corfudb.runtime.collections.graphdb;

import org.corfudb.runtime.CorfuRuntime;
import org.corfudb.util.GitRepositoryState;
import org.docopt.Docopt;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;

/**
 * Creates an AppContext and verifies that GraphDB methods
 * work as expected.
 *
 * @author shriyav
 */
public class AppContextTest {
    private static final String USAGE = "Usage: AppContextTest [-c <conf>]\n"
            + "Options:\n"
            + " -c <conf>     Set the configuration host and port  [default: localhost:9999]\n";
    private static boolean setUpComplete = false;
    private static CorfuRuntime runtime;

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

    @Before
    public void setUp() {
        if (setUpComplete) {
            return;
        }
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
        runtime = getRuntimeAndConnect(corfuConfigurationString);
        setUpComplete = true;
    }

    @Test
    public void adjacentTest() {
        AppContext myApp = new AppContext(runtime, "myGraph");

        // Create Transport Zone
        Key TZ1 = myApp.createTransportZone("TransportZone0");
        // Create Transport Nodes
        Key TN1 = myApp.createTransportNode("TransportNode1.1");
        Key TN2 = myApp.createTransportNode("TransportNode1.2");
        Key TN3 = myApp.createTransportNode("TransportNode2.1");
        // Create Logical Switches
        Key LS1 = myApp.createLogicalSwitch("LogicalSwitch3.1");
        Key LS2 = myApp.createLogicalSwitch("LogicalSwitch3.2");
        // Create Logical Ports
        Key LP1 = myApp.createLogicalPort("LogicalPort0.2");
        Key LP2 = myApp.createLogicalPort("LogicalPort2.2");
        Key LP3 = myApp.createLogicalPort("LogicalPort1.2");

        // Connect the elements
        try {
            myApp.connectTransportNode(TN1, TZ1);
            myApp.connectTransportNode(TN2, TZ1);
            myApp.connectTransportNode(TN3, TZ1);
            myApp.connectLogicalSwitch(LS1, TZ1);
            myApp.connectLogicalSwitch(LS2, TZ1);
            myApp.connectLogicalPort(LP1, LS1);
            myApp.connectLogicalPort(LP2, LS1);
            myApp.connectLogicalPort(LP3, LS2);
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        }

        // Run graphdb methods!
        System.out.println(myApp.getGraph()); // should change each time it's run (b/c persistent)

        ArrayList<String> actual = new ArrayList<>();
        ArrayList<String> expected = new ArrayList<>();
        expected.add("TransportNode1.1");
        expected.add("TransportNode1.2");
        expected.add("TransportNode2.1");
        expected.add("LogicalSwitch3.1");
        expected.add("LogicalSwitch3.2");
        try {
            Iterable<Key> adj = myApp.getGraph().adjacent(TZ1);
            for (Key friend : adj) {
                actual.add(myApp.getGraph().getNodes().get(friend).getName());
            } // expect: TN1.1, TN1.2, TN2.1, LS3.1, LS3.2
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        }
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void preDFSTest() {
        AppContext myApp = new AppContext(runtime, "myGraph");

        // Create Transport Zone
        Key TZ1 = myApp.createTransportZone("TransportZone0");
        // Create Transport Nodes
        Key TN1 = myApp.createTransportNode("TransportNode1.1");
        Key TN2 = myApp.createTransportNode("TransportNode1.2");
        Key TN3 = myApp.createTransportNode("TransportNode2.1");
        // Create Logical Switches
        Key LS1 = myApp.createLogicalSwitch("LogicalSwitch3.1");
        Key LS2 = myApp.createLogicalSwitch("LogicalSwitch3.2");
        // Create Logical Ports
        Key LP1 = myApp.createLogicalPort("LogicalPort0.2");
        Key LP2 = myApp.createLogicalPort("LogicalPort2.2");
        Key LP3 = myApp.createLogicalPort("LogicalPort1.2");

        // Connect the elements
        try {
            myApp.connectTransportNode(TN1, TZ1);
            myApp.connectTransportNode(TN2, TZ1);
            myApp.connectTransportNode(TN3, TZ1);
            myApp.connectLogicalSwitch(LS1, TZ1);
            myApp.connectLogicalSwitch(LS2, TZ1);
            myApp.connectLogicalPort(LP1, LS1);
            myApp.connectLogicalPort(LP2, LS1);
            myApp.connectLogicalPort(LP3, LS2);
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        }

        // Run graphdb methods!
        System.out.println(myApp.getGraph()); // should change each time it's run (b/c persistent)

        ArrayList<String> actual = new ArrayList<>();
        ArrayList<String> expected = new ArrayList<>();

        try {
            Iterable<Key> pre = myApp.getGraph().preDFS(TZ1);
            expected.add("TransportZone0");
            expected.add("TransportNode1.1");
            expected.add("TransportNode1.2");
            expected.add("TransportNode2.1");
            expected.add("LogicalSwitch3.1");
            expected.add("LogicalPort0.2");
            expected.add("LogicalPort2.2");
            expected.add("LogicalSwitch3.2");
            expected.add("LogicalPort1.2");
            for (Key item : pre) {
                actual.add(myApp.getGraph().getNodes().get(item).getName());
            } // expect: TZ0, TN1.1, TN1.2, TN2.1, LS3.1, LP0.2, LP2.2, LS3.2, LP1.2
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        }
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void postDFSTest() {
        AppContext myApp = new AppContext(runtime, "myGraph");

        // Create Transport Zone
        Key TZ1 = myApp.createTransportZone("TransportZone0");
        // Create Transport Nodes
        Key TN1 = myApp.createTransportNode("TransportNode1.1");
        Key TN2 = myApp.createTransportNode("TransportNode1.2");
        Key TN3 = myApp.createTransportNode("TransportNode2.1");
        // Create Logical Switches
        Key LS1 = myApp.createLogicalSwitch("LogicalSwitch3.1");
        Key LS2 = myApp.createLogicalSwitch("LogicalSwitch3.2");
        // Create Logical Ports
        Key LP1 = myApp.createLogicalPort("LogicalPort0.2");
        Key LP2 = myApp.createLogicalPort("LogicalPort2.2");
        Key LP3 = myApp.createLogicalPort("LogicalPort1.2");

        // Connect the elements
        try {
            myApp.connectTransportNode(TN1, TZ1);
            myApp.connectTransportNode(TN2, TZ1);
            myApp.connectTransportNode(TN3, TZ1);
            myApp.connectLogicalSwitch(LS1, TZ1);
            myApp.connectLogicalSwitch(LS2, TZ1);
            myApp.connectLogicalPort(LP1, LS1);
            myApp.connectLogicalPort(LP2, LS1);
            myApp.connectLogicalPort(LP3, LS2);
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        }

        // Run graphdb methods!
        System.out.println(myApp.getGraph()); // should change each time it's run (b/c persistent)

        ArrayList<String> actual = new ArrayList<>();
        ArrayList<String> expected = new ArrayList<>();

        try {
            Iterable<Key> post = myApp.getGraph().postDFS(TZ1);
            expected.add("TransportNode1.1");
            expected.add("TransportNode1.2");
            expected.add("TransportNode2.1");
            expected.add("LogicalPort0.2");
            expected.add("LogicalPort2.2");
            expected.add("LogicalSwitch3.1");
            expected.add("LogicalPort1.2");
            expected.add("LogicalSwitch3.2");
            expected.add("TransportZone0");
            for (Key item : post) {
                actual.add(myApp.getGraph().getNodes().get(item).getName());
            } // expect: TN1.1, TN1.2, TN2.1, LP0.2, LP2.2, LS3.1, LP1.2, LS3.2, TZ0
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        }
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void bfsTest() {
        AppContext myApp = new AppContext(runtime, "myGraph");

        // Create Transport Zone
        Key TZ1 = myApp.createTransportZone("TransportZone0");
        // Create Transport Nodes
        Key TN1 = myApp.createTransportNode("TransportNode1.1");
        Key TN2 = myApp.createTransportNode("TransportNode1.2");
        Key TN3 = myApp.createTransportNode("TransportNode2.1");
        // Create Logical Switches
        Key LS1 = myApp.createLogicalSwitch("LogicalSwitch3.1");
        Key LS2 = myApp.createLogicalSwitch("LogicalSwitch3.2");
        // Create Logical Ports
        Key LP1 = myApp.createLogicalPort("LogicalPort0.2");
        Key LP2 = myApp.createLogicalPort("LogicalPort2.2");
        Key LP3 = myApp.createLogicalPort("LogicalPort1.2");

        // Connect the elements
        try {
            myApp.connectTransportNode(TN1, TZ1);
            myApp.connectTransportNode(TN2, TZ1);
            myApp.connectTransportNode(TN3, TZ1);
            myApp.connectLogicalSwitch(LS1, TZ1);
            myApp.connectLogicalSwitch(LS2, TZ1);
            myApp.connectLogicalPort(LP1, LS1);
            myApp.connectLogicalPort(LP2, LS1);
            myApp.connectLogicalPort(LP3, LS2);
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        }

        // Run graphdb methods!
        System.out.println(myApp.getGraph()); // should change each time it's run (b/c persistent)

        ArrayList<String> actual = new ArrayList<>();
        ArrayList<String> expected = new ArrayList<>();

        try {
            Iterable<Key> bfs = myApp.getGraph().BFS(TZ1);
            expected.add("TransportZone0");
            expected.add("TransportNode1.1");
            expected.add("TransportNode1.2");
            expected.add("TransportNode2.1");
            expected.add("LogicalSwitch3.1");
            expected.add("LogicalSwitch3.2");
            expected.add("LogicalPort0.2");
            expected.add("LogicalPort2.2");
            expected.add("LogicalPort1.2");
            for (Key item : bfs) {
                actual.add(myApp.getGraph().getNodes().get(item).getName());
            } // expect: TZ0, TN1.1, TN1.2, TN2.1, LS3.1, LS3.2, LP0.2, LP2.2, LP1.2
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        }
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void clearTest() {
        AppContext myApp = new AppContext(runtime, "myGraph");

        // Create Transport Zone
        Key TZ1 = myApp.createTransportZone("TransportZone0");
        // Create Transport Nodes
        Key TN1 = myApp.createTransportNode("TransportNode1.1");
        Key TN2 = myApp.createTransportNode("TransportNode1.2");
        Key TN3 = myApp.createTransportNode("TransportNode2.1");
        // Create Logical Switches
        Key LS1 = myApp.createLogicalSwitch("LogicalSwitch3.1");
        Key LS2 = myApp.createLogicalSwitch("LogicalSwitch3.2");
        // Create Logical Ports
        Key LP1 = myApp.createLogicalPort("LogicalPort0.2");
        Key LP2 = myApp.createLogicalPort("LogicalPort2.2");
        Key LP3 = myApp.createLogicalPort("LogicalPort1.2");

        // Connect the elements
        try {
            myApp.connectTransportNode(TN1, TZ1);
            myApp.connectTransportNode(TN2, TZ1);
            myApp.connectTransportNode(TN3, TZ1);
            myApp.connectLogicalSwitch(LS1, TZ1);
            myApp.connectLogicalSwitch(LS2, TZ1);
            myApp.connectLogicalPort(LP1, LS1);
            myApp.connectLogicalPort(LP2, LS1);
            myApp.connectLogicalPort(LP3, LS2);
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        }

        myApp.getGraph().clear();
        Assert.assertEquals(0, myApp.getGraph().getNumNodes());
    }

    @Test
    public void errorTest() {
        AppContext myApp = new AppContext(runtime, "myGraph");
        // Create Transport Zone
        Key TZ1 = myApp.createTransportZone("TransportZone0");

        Key randKey = new Key(UUID.randomUUID());
        while (myApp.getGraph().getNode(randKey) != null) {
            randKey = new Key(UUID.randomUUID());
        }
        final Key finalRandom = randKey;

        assertThatThrownBy(() -> myApp.getGraph().update(finalRandom, null))
                .isInstanceOf(NodeDoesNotExistException.class);

        assertThatThrownBy(() -> myApp.getGraph().removeNode(finalRandom))
                .isInstanceOf(NodeDoesNotExistException.class);

        assertThatThrownBy(() -> myApp.getGraph().connect(finalRandom, null))
                .isInstanceOf(NodeDoesNotExistException.class);

        assertThatThrownBy(() -> myApp.getGraph().adjacent(finalRandom))
                .isInstanceOf(NodeDoesNotExistException.class);

        assertThatThrownBy(() -> myApp.getGraph().preDFS(finalRandom))
                .isInstanceOf(NodeDoesNotExistException.class);

        assertThatThrownBy(() -> myApp.getGraph().postDFS(finalRandom))
                .isInstanceOf(NodeDoesNotExistException.class);

        assertThatThrownBy(() -> myApp.getGraph().BFS(finalRandom))
                .isInstanceOf(NodeDoesNotExistException.class);

        assertThatThrownBy(() -> myApp.getGraph().addNode(TZ1, null))
                .isInstanceOf(NodeAlreadyExistsException.class);
    }

    @Test
    public void deepGraphTest() {
        AppContext myApp = new AppContext(runtime, "deepGraph");

        ArrayList<Key> ids = new ArrayList<>();
        for (int i = 0; i < 5000; i++) {
            ids.add(myApp.createTransportNode("TN" + i));
        }
        for (int i = 0; i < 4999; i++) {
            try {
                myApp.connectTransportNode(ids.get(i), ids.get(i + 1));
            } catch (Exception e) {
                System.out.println(e);
            }
        }
        try {
            Iterable<Key> ordered = myApp.getGraph().preDFS(ids.get(0));
            for (Key item : ordered) {
                System.out.println(item);
            }
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}

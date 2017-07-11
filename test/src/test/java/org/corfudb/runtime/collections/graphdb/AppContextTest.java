package org.corfudb.runtime.collections.graphdb;

import org.corfudb.runtime.CorfuRuntime;
import org.corfudb.util.GitRepositoryState;
import org.docopt.Docopt;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
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
        AppContext myApp = new AppContext(runtime, "myAdjacentGraph");

        // Create the elements
        TransportZone TZ1 = null;
        TransportNode TN1 = null, TN2 = null, TN3 = null;
        LogicalSwitch LS1 = null, LS2 = null;
        LogicalPort LP1 = null, LP2 = null, LP3 = null;
        try {
            // Create Transport Zone
            TZ1 = myApp.createTransportZone(UUID.randomUUID(), "TZ1", null);
            // Create Transport Nodes
            TN1 = myApp.createTransportNode(UUID.randomUUID(), "TN1", null);
            TN2 = myApp.createTransportNode(UUID.randomUUID(), "TN2", null);
            TN3 = myApp.createTransportNode(UUID.randomUUID(), "TN3", null);
            // Create Logical Switches
            LS1 = myApp.createLogicalSwitch(UUID.randomUUID(), "LS1", null, null);
            LS2 = myApp.createLogicalSwitch(UUID.randomUUID(), "LS2", null, null);
            // Create Logical Ports
            LP1 = myApp.createLogicalPort(UUID.randomUUID(), "LP1", null, null, null);
            LP2 = myApp.createLogicalPort(UUID.randomUUID(), "LP2", null, null, null);
            LP3 = myApp.createLogicalPort(UUID.randomUUID(), "LP3", null, null, null);
        } catch(Exception e) {
            System.out.println("ERROR: " + e);
        }

        // Connect the elements
        try {
            myApp.connectTZtoTN(TZ1, TN1);
            myApp.connectTZtoTN(TZ1, TN2);
            myApp.connectTZtoTN(TZ1, TN3);
            myApp.connectTZtoLS(TZ1, LS1);
            myApp.connectTZtoLS(TZ1, LS2);
            myApp.connectLStoLP(LS1, LP1);
            myApp.connectLStoLP(LS1, LP2);
            myApp.connectLStoLP(LS2, LP3);
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        }

        ArrayList<Object> actual = new ArrayList<>();
        ArrayList<Object> expected = new ArrayList<>();
        expected.add(TN1);
        expected.add(TN2);
        expected.add(TN3);
        expected.add(LS1);
        expected.add(LS2);
        try {
            Iterable<Object> adj = myApp.getGraph().adjacent(TZ1);
            for (Object friend : adj) {
                actual.add(friend);
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        }
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void preDFSTest() {
        AppContext myApp = new AppContext(runtime, "myPreGraph");

        // Create the elements
        TransportZone TZ1 = null;
        TransportNode TN1 = null, TN2 = null, TN3 = null;
        LogicalSwitch LS1 = null, LS2 = null;
        LogicalPort LP1 = null, LP2 = null, LP3 = null;
        try {
            // Create Transport Zone
            TZ1 = myApp.createTransportZone(UUID.randomUUID(), "TZ1", null);
            // Create Transport Nodes
            TN1 = myApp.createTransportNode(UUID.randomUUID(), "TN1", null);
            TN2 = myApp.createTransportNode(UUID.randomUUID(), "TN2", null);
            TN3 = myApp.createTransportNode(UUID.randomUUID(), "TN3", null);
            // Create Logical Switches
            LS1 = myApp.createLogicalSwitch(UUID.randomUUID(), "LS1", null, null);
            LS2 = myApp.createLogicalSwitch(UUID.randomUUID(), "LS2", null, null);
            // Create Logical Ports
            LP1 = myApp.createLogicalPort(UUID.randomUUID(), "LP1", null, null, null);
            LP2 = myApp.createLogicalPort(UUID.randomUUID(), "LP2", null, null, null);
            LP3 = myApp.createLogicalPort(UUID.randomUUID(), "LP3", null, null, null);
        } catch(Exception e) {
            System.out.println("ERROR: " + e);
        }

        // Connect the elements
        try {
            myApp.connectTZtoTN(TZ1, TN1);
            myApp.connectTZtoTN(TZ1, TN2);
            myApp.connectTZtoTN(TZ1, TN3);
            myApp.connectTZtoLS(TZ1, LS1);
            myApp.connectTZtoLS(TZ1, LS2);
            myApp.connectLStoLP(LS1, LP1);
            myApp.connectLStoLP(LS1, LP2);
            myApp.connectLStoLP(LS2, LP3);
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        }

        ArrayList<Object> actual = new ArrayList<>();
        ArrayList<Object> expected = new ArrayList<>();

        try {
            Iterable<Object> pre = myApp.getGraph().preDFS(TZ1);
            expected.add(TZ1);
            expected.add(TN1);
            expected.add(TN2);
            expected.add(TN3);
            expected.add(LS1);
            expected.add(LP1);
            expected.add(LP2);
            expected.add(LS2);
            expected.add(LP3);
            for (Object item : pre) {
                actual.add(item);
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        }
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void postDFSTest() {
        AppContext myApp = new AppContext(runtime, "myPostGraph");

        // Create the elements
        TransportZone TZ1 = null;
        TransportNode TN1 = null, TN2 = null, TN3 = null;
        LogicalSwitch LS1 = null, LS2 = null;
        LogicalPort LP1 = null, LP2 = null, LP3 = null;
        try {
            // Create Transport Zone
            TZ1 = myApp.createTransportZone(UUID.randomUUID(), "TZ1", null);
            // Create Transport Nodes
            TN1 = myApp.createTransportNode(UUID.randomUUID(), "TN1", null);
            TN2 = myApp.createTransportNode(UUID.randomUUID(), "TN2", null);
            TN3 = myApp.createTransportNode(UUID.randomUUID(), "TN3", null);
            // Create Logical Switches
            LS1 = myApp.createLogicalSwitch(UUID.randomUUID(), "LS1", null, null);
            LS2 = myApp.createLogicalSwitch(UUID.randomUUID(), "LS2", null, null);
            // Create Logical Ports
            LP1 = myApp.createLogicalPort(UUID.randomUUID(), "LP1", null, null, null);
            LP2 = myApp.createLogicalPort(UUID.randomUUID(), "LP2", null, null, null);
            LP3 = myApp.createLogicalPort(UUID.randomUUID(), "LP3", null, null, null);
        } catch(Exception e) {
            System.out.println("ERROR: " + e);
        }

        // Connect the elements
        try {
            myApp.connectTZtoTN(TZ1, TN1);
            myApp.connectTZtoTN(TZ1, TN2);
            myApp.connectTZtoTN(TZ1, TN3);
            myApp.connectTZtoLS(TZ1, LS1);
            myApp.connectTZtoLS(TZ1, LS2);
            myApp.connectLStoLP(LS1, LP1);
            myApp.connectLStoLP(LS1, LP2);
            myApp.connectLStoLP(LS2, LP3);
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        }

        ArrayList<Object> actual = new ArrayList<>();
        ArrayList<Object> expected = new ArrayList<>();

        try {
            Iterable<Object> post = myApp.getGraph().postDFS(TZ1);
            expected.add(TN1);
            expected.add(TN2);
            expected.add(TN3);
            expected.add(LP1);
            expected.add(LP2);
            expected.add(LS1);
            expected.add(LP3);
            expected.add(LS2);
            expected.add(TZ1);
            for (Object item : post) {
                actual.add(item);
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        }
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void bfsTest() {
        AppContext myApp = new AppContext(runtime, "myBFSGraph");

        // Create the elements
        TransportZone TZ1 = null;
        TransportNode TN1 = null, TN2 = null, TN3 = null;
        LogicalSwitch LS1 = null, LS2 = null;
        LogicalPort LP1 = null, LP2 = null, LP3 = null;
        try {
            // Create Transport Zone
            TZ1 = myApp.createTransportZone(UUID.randomUUID(), "TZ1", null);
            // Create Transport Nodes
            TN1 = myApp.createTransportNode(UUID.randomUUID(), "TN1", null);
            TN2 = myApp.createTransportNode(UUID.randomUUID(), "TN2", null);
            TN3 = myApp.createTransportNode(UUID.randomUUID(), "TN3", null);
            // Create Logical Switches
            LS1 = myApp.createLogicalSwitch(UUID.randomUUID(), "LS1", null, null);
            LS2 = myApp.createLogicalSwitch(UUID.randomUUID(), "LS2", null, null);
            // Create Logical Ports
            LP1 = myApp.createLogicalPort(UUID.randomUUID(), "LP1", null, null, null);
            LP2 = myApp.createLogicalPort(UUID.randomUUID(), "LP2", null, null, null);
            LP3 = myApp.createLogicalPort(UUID.randomUUID(), "LP3", null, null, null);
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        }

        // Connect the elements
        try {
            myApp.connectTZtoTN(TZ1, TN1);
            myApp.connectTZtoTN(TZ1, TN2);
            myApp.connectTZtoTN(TZ1, TN3);
            myApp.connectTZtoLS(TZ1, LS1);
            myApp.connectTZtoLS(TZ1, LS2);
            myApp.connectLStoLP(LS1, LP1);
            myApp.connectLStoLP(LS1, LP2);
            myApp.connectLStoLP(LS2, LP3);
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        }

        ArrayList<Object> actual = new ArrayList<>();
        ArrayList<Object> expected = new ArrayList<>();

        try {
            Iterable<Object> bfs = myApp.getGraph().BFS(TZ1);
            expected.add(TZ1);
            expected.add(TN1);
            expected.add(TN2);
            expected.add(TN3);
            expected.add(LS1);
            expected.add(LS2);
            expected.add(LP1);
            expected.add(LP2);
            expected.add(LP3);
            for (Object item : bfs) {
                actual.add(item);
            } // expect: TN1.1, TN1.2, TN2.1, LP0.2, LP2.2, LS3.1, LP1.2, LS3.2, TZ0
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        }
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void clearTestIntegers() {
        AppContext myApp = new AppContext(runtime, "myIntegerGraph");

        // Create the elements
        try {
            myApp.getGraph().addNode("Hi");
            myApp.getGraph().addNode("Bonjour");
            myApp.getGraph().addNode("Namaste");
            myApp.getGraph().addNode("Hola");
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        }

        Assert.assertEquals(4, myApp.getGraph().getNumNodes());
        myApp.getGraph().clear();
        Assert.assertEquals(0, myApp.getGraph().getNumNodes());
    }

    @Test
    public void clearTest() {
        AppContext myApp = new AppContext(runtime, "myClearGraph");
        myApp.getGraph().clear();

        // Create the elements
        TransportZone TZ1 = null;
        TransportNode TN1 = null, TN2 = null, TN3 = null;
        LogicalSwitch LS1 = null, LS2 = null;
        LogicalPort LP1 = null, LP2 = null, LP3 = null;
        try {
            // Create Transport Zone
            TZ1 = myApp.createTransportZone(UUID.randomUUID(), "TZ1", new HashMap<>());
            // Create Transport Nodes
            TN1 = myApp.createTransportNode(UUID.randomUUID(), "TN1", new HashMap<>());
            TN2 = myApp.createTransportNode(UUID.randomUUID(), "TN2", new HashMap<>());
            TN3 = myApp.createTransportNode(UUID.randomUUID(), "TN3", new HashMap<>());
            // Create Logical Switches
            LS1 = myApp.createLogicalSwitch(UUID.randomUUID(), "LS1", new ArrayList<>(), new HashMap<>());
            LS2 = myApp.createLogicalSwitch(UUID.randomUUID(), "LS2", new ArrayList<>(), new HashMap<>());
            // Create Logical Ports
            LP1 = myApp.createLogicalPort(UUID.randomUUID(), "LP1", new Attachment(UUID.randomUUID(), "1"), new ArrayList<>(), new HashMap<>());
            LP2 = myApp.createLogicalPort(UUID.randomUUID(), "LP2", new Attachment(UUID.randomUUID(), "2"), new ArrayList<>(), new HashMap<>());
            LP3 = myApp.createLogicalPort(UUID.randomUUID(), "LP3", new Attachment(UUID.randomUUID(), "3"), new ArrayList<>(), new HashMap<>());
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        }

        // Connect the elements
        try {
            myApp.connectTZtoTN(TZ1, TN1);
            myApp.connectTZtoTN(TZ1, TN2);
            myApp.connectTZtoTN(TZ1, TN3);
            myApp.connectTZtoLS(TZ1, LS1);
            myApp.connectTZtoLS(TZ1, LS2);
            myApp.connectLStoLP(LS1, LP1);
            myApp.connectLStoLP(LS1, LP2);
            myApp.connectLStoLP(LS2, LP3);
        } catch (Exception e) {
            System.out.println("ERROR: " + e);
        }

        Assert.assertEquals(9, myApp.getGraph().getNumNodes());
        myApp.getGraph().clear();
        Assert.assertEquals(0, myApp.getGraph().getNumNodes());
    }

    @Test
    public void errorTest() throws NodeDoesNotExistException, NodeAlreadyExistsException {
        AppContext myApp = new AppContext(runtime, "myErrorGraph");
        // Create Transport Zone
        TransportZone TZ1 = myApp.createTransportZone(UUID.randomUUID(), "TZ1", null);
        String dangerous = "NotInGraph";

        assertThatThrownBy(() -> myApp.getGraph().update(dangerous))
                .isInstanceOf(NodeDoesNotExistException.class);

        assertThatThrownBy(() -> myApp.getGraph().removeNode(dangerous))
                .isInstanceOf(NodeDoesNotExistException.class);

        assertThatThrownBy(() -> myApp.getGraph().connect(TZ1, dangerous))
                .isInstanceOf(NodeDoesNotExistException.class);

        assertThatThrownBy(() -> myApp.getGraph().connect(dangerous, TZ1))
                .isInstanceOf(NodeDoesNotExistException.class);

        assertThatThrownBy(() -> myApp.getGraph().adjacent(dangerous))
                .isInstanceOf(NodeDoesNotExistException.class);

        assertThatThrownBy(() -> myApp.getGraph().preDFS(dangerous))
                .isInstanceOf(NodeDoesNotExistException.class);

        assertThatThrownBy(() -> myApp.getGraph().postDFS(dangerous))
                .isInstanceOf(NodeDoesNotExistException.class);

        assertThatThrownBy(() -> myApp.getGraph().BFS(dangerous))
                .isInstanceOf(NodeDoesNotExistException.class);

        assertThatThrownBy(() -> myApp.getGraph().addNode(TZ1))
                .isInstanceOf(NodeAlreadyExistsException.class);
    }

    @Test
    public void deepGraphTest() throws NodeAlreadyExistsException {
        AppContext myApp = new AppContext(runtime, "myDeepGraph");
        myApp.getGraph().clear();

        ArrayList<String> objects = new ArrayList<>();
        for (int i = 0; i < 5000; i++) {
            objects.add("hello" + String.valueOf(i));
            myApp.getGraph().addNode(objects.get(i));
        }
        for (int i = 0; i < 4999; i++) {
            try {
                myApp.getGraph().connect(objects.get(i), objects.get(i+1));
            } catch (Exception e) {
                Assert.fail("Error while connecting objects!");
            }
        }
        try {
            Iterable<Object> ordered = myApp.getGraph().preDFS(objects.get(0));
            for (Object item : ordered) {
                System.out.println(item);
            }
        } catch (Exception e) {
            Assert.fail("Error while performing preDFS/printing objects!");
        }
    }
}

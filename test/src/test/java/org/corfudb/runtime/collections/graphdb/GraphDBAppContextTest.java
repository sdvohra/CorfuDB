package org.corfudb.runtime.collections.graphdb;

import org.corfudb.runtime.CorfuRuntime;
import org.corfudb.util.GitRepositoryState;
import org.docopt.Docopt;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

import java.util.*;

/**
 * Creates a GraphDBAppContext and verifies that GraphDB methods
 * work as expected.
 *
 * @author shriyav
 */
public class GraphDBAppContextTest {
    private static final String USAGE = "Usage: GraphDBAppContextTest [-c <conf>]\n"
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
        GraphDBAppContext myApp = new GraphDBAppContext(runtime, "myAdjacentGraph");

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
            Assert.fail("ERROR: " + e);
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
            Assert.fail("ERROR: " + e);
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
            Assert.fail("ERROR: " + e);
        }
        Assert.assertTrue(expected.containsAll(actual) && actual.containsAll(expected));
    }

    @Test
    public void preDFSTest() {
        GraphDBAppContext myApp = new GraphDBAppContext(runtime, "myPreGraph");

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
            Assert.fail("ERROR: " + e);
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
            Assert.fail("ERROR: " + e);
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
            Assert.fail("ERROR: " + e);
        }

        Assert.assertTrue(expected.size() == actual.size());
        Assert.assertEquals(expected.get(0), actual.get(0));
        Assert.assertTrue(actual.containsAll(expected));
        int indexLS1 = actual.indexOf(LS1);
        if (!(((actual.indexOf(LP1) == indexLS1 + 1) || (actual.indexOf(LP1) == indexLS1 + 2)) &&
                ((actual.indexOf(LP2) == indexLS1 + 1) || (actual.indexOf(LP2) == indexLS1 + 2)))) {
            Assert.fail();
        }
        int indexLS2 = actual.indexOf(LS2);
        if (actual.indexOf(LP3) != indexLS2 + 1) {
            Assert.fail();
        }
    }

    @Test
    public void postDFSTest() {
        GraphDBAppContext myApp = new GraphDBAppContext(runtime, "myPostGraph");

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
            Assert.fail("ERROR: " + e);
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
            Assert.fail("ERROR: " + e);
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
            Assert.fail("ERROR: " + e);
        }

        Assert.assertTrue(expected.size() == actual.size());
        Assert.assertEquals(expected.get(expected.size()-1), actual.get(actual.size()-1));
        Assert.assertTrue(actual.containsAll(expected));
        int indexLS1 = actual.indexOf(LS1);
        if (!(((actual.indexOf(LP1) == indexLS1 - 1) || (actual.indexOf(LP1) == indexLS1 - 2)) &&
                ((actual.indexOf(LP2) == indexLS1 - 1) || (actual.indexOf(LP2) == indexLS1 - 2)))) {
            Assert.fail();
        }
        int indexLS2 = actual.indexOf(LS2);
        if (actual.indexOf(LP3) != indexLS2 - 1) {
            Assert.fail();
        }
    }

    @Test
    public void bfsTest() {
        GraphDBAppContext myApp = new GraphDBAppContext(runtime, "myBFSGraph");

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
            Assert.fail("ERROR: " + e);
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
            Assert.fail("ERROR: " + e);
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
            }
        } catch (Exception e) {
            Assert.fail("ERROR: " + e);
        }

        Assert.assertTrue(expected.size() == actual.size());
        Assert.assertEquals(expected.get(0), actual.get(0));
        Assert.assertTrue(actual.containsAll(expected));
        for (int i = 1; i < 6; i++) {
            Assert.assertTrue(actual.subList(1, 6).contains(expected.get(i)));
        }
    }

    @Test
    public void clearTestStrings() {
        GraphDBAppContext myApp = new GraphDBAppContext(runtime, "myStringGraph");

        // Create the elements
        try {
            myApp.getGraph().addNode("Hi");
            myApp.getGraph().addNode("Bonjour");
            myApp.getGraph().addNode("Namaste");
            myApp.getGraph().addNode("Hola");
        } catch (Exception e) {
            Assert.fail("ERROR: " + e);
        }

        Assert.assertEquals(4, myApp.getGraph().getNumNodes());
        myApp.getGraph().clear();
        Assert.assertEquals(0, myApp.getGraph().getNumNodes());
    }

    @Test
    public void clearTest() {
        GraphDBAppContext myApp = new GraphDBAppContext(runtime, "myClearGraph");
        myApp.getGraph().clear();

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
            Assert.fail("ERROR: " + e);
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
            Assert.fail("ERROR: " + e);
        }

        Assert.assertEquals(9, myApp.getGraph().getNumNodes());
        myApp.getGraph().clear();
        Assert.assertEquals(0, myApp.getGraph().getNumNodes());
    }

    @Test
    public void errorTest() throws NodeDoesNotExistException, NodeAlreadyExistsException {
        GraphDBAppContext myApp = new GraphDBAppContext(runtime, "myErrorGraph");
        // Create Transport Zone
        TransportZone TZ1 = myApp.createTransportZone(UUID.randomUUID(), "TZ1", null);
        String dangerous = "NotInGraph";

        assertThatThrownBy(() -> myApp.getGraph().update(dangerous, ""))
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
        GraphDBAppContext myApp = new GraphDBAppContext(runtime, "myDeepGraph");
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
            int counter = 0;
            for (Object item : ordered) {
                Assert.assertEquals(item, "hello" + counter);
                counter++;
            }
        } catch (Exception e) {
            Assert.fail("Error while performing preDFS/printing objects!");
        }
    }

    @Test
    public void queryTest() {
        GraphDBAppContext myApp = new GraphDBAppContext(runtime, "myQueryGraph");

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
            Assert.fail("ERROR: " + e);
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
            Assert.fail("ERROR: " + e);
        }

        Set<TransportNode> actual = myApp.queryTZtoTN(TZ1);
        Set<TransportNode> expected = new HashSet<>();
        expected.add(TN1);
        expected.add(TN2);
        expected.add(TN3);
        Assert.assertEquals(expected, actual);

        Set<LogicalSwitch> actualLS = myApp.queryTZtoLS(TZ1);
        Set<LogicalSwitch> expectedLS = new HashSet<>();
        expectedLS.add(LS1);
        expectedLS.add(LS2);
        Assert.assertEquals(expectedLS, actualLS);

        Set<LogicalPort> actualLP = myApp.queryLStoLP(LS1);
        Set<LogicalPort> expectedLP = new HashSet<>();
        expectedLP.add(LP1);
        expectedLP.add(LP2);
        Assert.assertEquals(expectedLP, actualLP);

        Set<LogicalPort> actual4 = myApp.queryTZtoLP(TZ1);
        Set<LogicalPort> expected4 = new HashSet<>();
        expected4.add(LP1);
        expected4.add(LP2);
        Assert.assertNotEquals(expected4, actual4);
        expected4.add(LP3);
        Assert.assertEquals(expected4, actual4);
    }

    @Test
    public void bigTest() throws Exception { // Large, randomly-generated graph
        GraphDBAppContext myApp = new GraphDBAppContext(runtime, "myBigGraph");
        myApp.getGraph().clear();
        Set<UUID> existing = new HashSet<>();

        // Create Transport Zones
        Map<String, TransportZone> transportZones = new HashMap<>();
        for (int i = 1; i <= 2000; i++) {
            UUID newID = UUID.randomUUID();
            while (existing.contains(newID)) {
                newID = UUID.randomUUID();
            }
            TransportZone currTZ = myApp.createTransportZone(newID, "TZ" + i, null);
            existing.add(newID);
            transportZones.put("TZ" + i, currTZ);
        }

        // Create Transport Nodes
        Map<String, TransportNode> transportNodes = new HashMap<>();
        for (int i = 1; i <= 4000; i++) {
            UUID newID = UUID.randomUUID();
            while (existing.contains(newID)) {
                newID = UUID.randomUUID();
            }
            TransportNode currTN = myApp.createTransportNode(newID, "TN" + i, null);
            existing.add(newID);
            transportNodes.put("TN" + i, currTN);
        }

        // Create Logical Switches
        Map<String, LogicalSwitch> logicalSwitches = new HashMap<>();
        for (int i = 1; i <= 4000; i++) {
            UUID newID = UUID.randomUUID();
            while (existing.contains(newID)) {
                newID = UUID.randomUUID();
            }
            LogicalSwitch currLS = myApp.createLogicalSwitch(newID, "LS" + i, null, null);
            existing.add(newID);
            logicalSwitches.put("LS" + i, currLS);
        }

        // Create Logical Ports
        Map<String, LogicalPort> logicalPorts = new HashMap<>();
        for (int i = 1; i <= 8000; i++) {
            UUID newID = UUID.randomUUID();
            while (existing.contains(newID)) {
                newID = UUID.randomUUID();
            }
            LogicalPort currLP = myApp.createLogicalPort(newID, "LS" + i, null,
                    null, null);
            existing.add(newID);
            logicalPorts.put("LP" + i, currLP);
        }

        // Randomize connections
        Random rand = new Random();
        int min = 1;
        int max;


        // Connect LS --> LP
        for (int i = 1; i <= logicalPorts.size(); i++) {
            max = logicalSwitches.size();
            for (int j = 0; j < 3; j++) {
                int randomNum = rand.nextInt((max - min) + 1) + min;
                try {
                    myApp.connectLStoLP(logicalSwitches.get("LS" + randomNum), logicalPorts.get("LP" + i));
                } catch (Exception e) {
                    continue;
                }
            }
        }

        // Connect TZ --> LS
        for (int i = 1; i <= logicalSwitches.size(); i++) {
            max = transportZones.size();
            int randomNum = rand.nextInt((max - min) + 1) + min;
            myApp.connectTZtoLS(transportZones.get("TZ" + randomNum), logicalSwitches.get("LS" + i));
        }

        // Connect TZ --> TN
        for (int i = 1; i <= transportNodes.size(); i++) {
            max = transportZones.size();
            for (int j = 0; j < 5; j++) {
                int randomNum = rand.nextInt((max - min) + 1) + min;
                try {
                    myApp.connectTZtoTN(transportZones.get("TZ" + randomNum), transportNodes.get("TN" + i));
                } catch (Exception e) {
                    continue;
                }
            }
        }

        // Query
        max = transportZones.size();
        int randomNum = rand.nextInt((max - min) + 1) + min;
        System.out.println(myApp.queryTZtoLP(transportZones.get("TZ" + randomNum)).size());
    }

    @Test
    public void threaded() throws Exception {
        GraphDBAppContext myApp = new GraphDBAppContext(runtime, "myThreadGraph");
        // Create the elements
        final TransportZone TZ1 = myApp.createTransportZone(UUID.randomUUID(), "TZ1", null);
        TransportNode TN1 = null, TN2 = null, TN3 = null;
        LogicalSwitch LS1 = null, LS2 = null;
        LogicalPort LP1 = null, LP2 = null, LP3 = null;
        try {
            // Create Transport Zone
            //TZ1 = myApp.createTransportZone(UUID.randomUUID(), "TZ1", null);
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
            Assert.fail("ERROR: " + e);
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
            Assert.fail("ERROR: " + e);
        }

        // Multithreading
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Started t1");
                    myApp.getGraph().BFS(TZ1);
                    Thread.sleep(2000);
                    System.out.println("Completed t1");
                } catch (Exception e) {
                    System.out.println("ERROR: " + e);
                }
            }
        });
        t1.start();

        Thread t2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Started t2");
                    myApp.getGraph().preDFS(TZ1);
                    Thread.sleep(1000);
                    System.out.println("Completed t2");
                } catch (Exception e) {
                    System.out.println("ERROR: " + e);
                }
            }
        });
        t2.start();

        Thread t3 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("Started t3");
                    myApp.getGraph().postDFS(TZ1);
                    System.out.println("Completed t3");
                } catch (Exception e) {
                    System.out.println("ERROR: " + e);
                }
            }
        });
        t3.start();
        t1.join();
        t2.join();
        t3.join();
    }
}

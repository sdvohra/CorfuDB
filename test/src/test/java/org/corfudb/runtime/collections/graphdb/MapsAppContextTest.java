package org.corfudb.runtime.collections.graphdb;

import org.corfudb.runtime.CorfuRuntime;
import org.corfudb.util.GitRepositoryState;
import org.docopt.Docopt;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Array;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Creates a MapsAppContext and verifies that methods work as expected.
 *
 * @author shriyav
 */
public class MapsAppContextTest {
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
    public void query() {
        MapsAppContext myApp = new MapsAppContext(runtime, "myQueryGraph");
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

        Set<TransportNode> actual = myApp.query1(TZ1);
        Set<TransportNode> expected = new HashSet<>();
        expected.add(TN1);
        expected.add(TN2);
        expected.add(TN3);
        Assert.assertEquals(expected, actual);

        Set<LogicalSwitch> actualLS = myApp.query2(TZ1);
        Set<LogicalSwitch> expectedLS = new HashSet<>();
        expectedLS.add(LS1);
        expectedLS.add(LS2);
        Assert.assertEquals(expectedLS, actualLS);

        Set<LogicalPort> actualLP = myApp.query3(LS1);
        Set<LogicalPort> expectedLP = new HashSet<>();
        expectedLP.add(LP1);
        expectedLP.add(LP2);
        Assert.assertEquals(expectedLP, actualLP);

        Set<LogicalPort> actual4 = myApp.query4(TZ1);
        Set<LogicalPort> expected4 = new HashSet<>();
        expected4.add(LP1);
        expected4.add(LP2);
        Assert.assertNotEquals(expected4, actual4);
        expected4.add(LP3);
        Assert.assertEquals(expected4, actual4);

    }

    @Test
    public void errorTest() throws NodeDoesNotExistException, NodeAlreadyExistsException {
        GraphDBAppContext myApp = new GraphDBAppContext(runtime, "myErrorGraph");
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
}

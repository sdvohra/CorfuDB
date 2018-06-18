package org.corfudb.runtime.collections;

import org.corfudb.runtime.CorfuRuntime;
import org.corfudb.runtime.object.transactions.TransactionType;
import org.junit.Test;
import org.corfudb.runtime.Tracer;

import java.util.Map;

/**
 * Created by sdvohra on 5/22/18.
 */
public class ProfilingTest {
    final int numIterations = 10000;
    //public CorfuRuntime r;

    @Test
    public void threadScale() throws Exception {

        CorfuRuntime rt = new CorfuRuntime("localhost:9000").connect();
        //rt.getParameters().setEnableMultiStreamQuery(true);
        //CorfuRuntime rt = r;
        Map<String, String> left = rt.getObjectsView().build().setStreamName("left").setType(SMRMap.class).open();
        Map<String, String> right = rt.getObjectsView().build().setStreamName("right").setType(SMRMap.class).open();
        Map<String, String> mid = rt.getObjectsView().build().setStreamName("mid").setType(SMRMap.class).open();

        int numThread = 8;
        final int numOps = 30000;


        Thread[] threads = new Thread[numThread];
        long s1 = System.currentTimeMillis();
        for (int x = 0; x < numThread; x++) {
            System.out.println(x);
            Runnable r = () -> {
                long avg = 0;

                for (int y = 0; y < numOps; y++) {
                    String leftK = Thread.currentThread().getName() + y + "left";
                    String rightK = Thread.currentThread().getName() + y + "right";
                    String midK = Thread.currentThread().getName() + y + "mid";

                    long a1 = System.nanoTime();
                    left.put(leftK, leftK);
                    right.put(rightK, rightK);
                    // rt.getObjectsView().TXEnd();


                    // rt.getObjectsView().TXBuild()
                    //.setType(TransactionType.WRITE_AFTER_WRITE)
                    //     .begin();
                    left.containsKey(leftK);

                    rt.getObjectsView().TXBuild()
                            .setType(TransactionType.WRITE_AFTER_WRITE)
                            .begin();
                    right.containsKey(rightK);
                    mid.containsKey(midK);
                    mid.put(midK, midK);
                    rt.getObjectsView().TXEnd();
                    long a2 = System.nanoTime();
                    avg = avg  + (a2 - a1);
                }
                System.out.println("avg " + ((avg/1_000_000.0)/numOps));
            };

            threads[x] = new Thread(r);
            threads[x].start();
        }

        for (int x = 0; x < numThread; x++) {
            threads[x].join();
        }

        long s2 = System.currentTimeMillis();

        System.out.println("total time ms" + (s2 - s1));
        System.out.println("throughput op/ms" + (numThread * numOps * 1.0) / (s2 - s1));
    }

    // @Test
    /*public void profilingTest() throws Exception {
        Map<Long, Long> testMap = instantiateCorfuObject(SMRMap.class,
                "profilingtest");

        // threading
/*        Thread[] threads = new Thread[4];
        // splits up the many "put" calls for various threads
        for (int i = 0; i < threads.length; i++) {
            Runnable run = () -> {
                for (long j = 0; j < (long) numIterations; j++) {
                    //r.getObjectsView().TXBegin();
                    testMap.put(j, j);
                    //r.getObjectsView().TXEnd();
                }
            };
            threads[i] = new Thread(run);

            threads[i].start();
        }

        for (int i = 0; i < threads.length; i++) {
            threads[i].join();
        }
*/
        // populate the map
        /*for (long i = 0; i < (long) numIterations; i++) {
            r.getObjectsView().TXBegin();
            testMap.put(i, i);
            r.getObjectsView().TXEnd();
        }


        assertThat(testMap.get(0L))
                .isEqualTo(0L);
        assertThat(testMap.size())
                .isEqualTo(numIterations);

        // remove elements from the map
        ArrayList<Long> removed = new ArrayList<>();
        for (long i = 0; i < (long) numIterations; i += 2) {
            testMap.remove(i);
            removed.add(i);
        }

        // re-populate the map
        for (long i : removed) {
            testMap.put(i, i);
        }

        assertThat(testMap.get(0L))
                .isEqualTo(0L);
        assertThat(testMap.size())
                .isEqualTo(numIterations);

    }*/
}

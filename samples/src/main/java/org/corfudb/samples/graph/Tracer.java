package org.corfudb.samples.graph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by shriyav on 6/7/17.
 * This class is useful for conducting performance analyses on various classes.
 *
 * To use, create a Tracer instance var in the class that you will be analyzing
 * and a boolean value isTracing that will be updated when "probing" methods.
 */
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
            bw.write("\"cat\": " + "\"" + cat + "\",");
            bw.write("\"pid\": " + pid + ",");
            bw.write("\"tid\": " + tid + ",");
            bw.write("\"ts\": " + ts + ",");
            bw.write("\"ph\": " + "\""  + ph + "\",");
            bw.write("\"name\": " + "\""  + name + "\",");
            bw.write("\"args\": " + args + "},\n");

            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Done");
    }

    public static void main (String[] args) {
        /**
         * Using this space to write about how to use the Tracer class...
         * At the beginning and end of each function, have something that looks like this:
         *
         * if (isTracing) {
                t.updateArgs("GraphDBTest", "adjacent", 1, Thread.currentThread().getId(),
                System.currentTimeMillis(), "B", null);
           }
           // begin method
           ...
           // end method
           if (isTracing) {
                t.updateArgs("GraphDBTest", "adjacent", 1, Thread.currentThread().getId(),
                System.currentTimeMillis(), "E", null);
           }
         *
         * This allows the data to be stored in a JSON file performanceLog.json in such a way
         * that it can be processed and analysed by the Chrome tool found at chrome:\\tracing.
         */
    }
}

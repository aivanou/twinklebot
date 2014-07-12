/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tbot.statistics;

import java.io.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author alex
 */
public class Statistics {
    
    public static final AtomicInteger fetcherTimes = new AtomicInteger(0);
    public static final AtomicInteger parserTimes = new AtomicInteger(0);
    public static final AtomicInteger validatorTimes = new AtomicInteger(0);
    public static final AtomicInteger sendDocumentsTimes = new AtomicInteger(0);
    public static final AtomicInteger count = new AtomicInteger(0);
    private final Map<Integer, Collection<String>> timeUrls;
    private final Map<Integer, Integer> timeCountUrls;
    private String filename;
    private String countFN;
    private final int MAX_SIZE = 10000;
    
    public Statistics(String filename, String countFilename) {
        this.timeUrls = new ConcurrentHashMap<Integer, Collection<String>>();
        this.timeCountUrls = new ConcurrentHashMap<Integer, Integer>();
        this.filename = filename;
        this.countFN = countFilename;
    }
    
    public synchronized void insertStatistics(int time, String url) {
        count.incrementAndGet();
        int t = this.genTime(time);
        if (!this.timeUrls.containsKey(t)) {
            Collection<String> urls = new ArrayList<String>();
            urls.add(url);
            this.timeUrls.put(t, urls);
        }
        if (!this.timeCountUrls.containsKey(t)) {
            this.timeCountUrls.put(t, 1);
            return;
        }
        this.timeCountUrls.put(t, this.timeCountUrls.get(t) + 1);
        this.timeUrls.get(t).add(url);
        synchronized (this) {
            if (Statistics.count.get() >= MAX_SIZE) {
                try {
                    this.writeCounts();
                    this.write();
                } catch (IOException ex) {
                    Logger.getLogger(Statistics.class.getName()).log(Level.SEVERE, null, ex);
                }
                Statistics.count.set(0);
            }
        }
    }
    
    private void writeCounts() throws IOException {
        File f = new File(countFN);
        f.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(f));
        for (Integer key : this.timeCountUrls.keySet()) {
            writer.write("time:  " + key + "  urls: " + this.timeCountUrls.get(key));
            writer.newLine();
        }
        writer.flush();
        writer.close();
    }
    
    private void write() throws IOException {
        File f = new File(filename);
        if (!f.exists()) {
            f.createNewFile();
        }
        BufferedWriter writer = new BufferedWriter(new FileWriter(f));
        for (Integer key : this.timeUrls.keySet()) {
            for (String url : this.timeUrls.get(key)) {
                writer.append("time:  " + key + "; url:  " + url);
                writer.newLine();
            }
        }
        writer.flush();
        writer.close();
        for (Integer key : this.timeUrls.keySet()) {
            this.timeUrls.get(key).clear();
        }
        this.timeUrls.clear();
    }
    
    private int genTime(int time) {
        return 1000 * (time / 1000);
    }
}

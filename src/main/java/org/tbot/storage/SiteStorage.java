package org.tbot.storage;

import org.tbot.loggin.LoggerCreator;
import org.tbot.objects.ObjectToCrawl;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import org.apache.log4j.Logger;

/**
 * Created by IntelliJ IDEA. User: alatysh Date: 5/21/12 Time: 11:27 AM To
 * change this template use File | Settings | File Templates.
 */
/**
 * This class save sites in file documents and give you methods for work with
 * storage.
 */
public class SiteStorage {

    static Logger serverLogger = LoggerCreator.getLogger();
    private int sizeGetDomainInGSClient;
    private Queue<ObjectToCrawl> queue;
    private String PATH = null;
    private boolean readFromGS = false;
    private boolean readFromFile = false;

    /**
     * Constructor
     */
    public SiteStorage(String filePath, int gsRequestSize, boolean readFromGS, boolean readFromFile) {
        this.readFromFile = readFromFile;
        this.readFromGS = readFromGS;
        this.queue = new ConcurrentLinkedQueue<ObjectToCrawl>();
        this.PATH = filePath;
        this.sizeGetDomainInGSClient = gsRequestSize;//Integer.valueOf(propStorage.getProperty("sizeGetDomainGSClient"));
    }

    /**
     * this methods is get one sites without queue
     *
     * @return object Domain
     * @throws InterruptedException - class exception
     */
    public synchronized ObjectToCrawl pollSite() {
        while (this.queue.size() == 0) {
            loadDomain(PATH);
        }
        return this.queue.poll();
    }

    private Queue<ObjectToCrawl> readFromFile(String filename) {
        SiteStorageState siteStorageState = new SiteStorageState(filename);
        siteStorageState.readFileBackUpSitesToQueue();
        return null;
    }

    private void loadDomain(String path) {
        if (this.readFromGS) {
            try {
                List<ObjectToCrawl> lst = new LinkedList<>();
                for (ObjectToCrawl item : lst) {
                    this.queue.add(item);
                }
                return;
            } catch (Exception ex) {
                java.util.logging.Logger.getLogger(SiteStorage.class.getName()).log(Level.SEVERE, null, ex);
                if (this.readFromFile) {
                    this.queue = this.readFromFile(path);
                }
                return;
            }
        }
        if (this.readFromFile) {
            this.queue = this.readFromFile(path);
        }
    }

    /**
     * this methods is reload(resave) site storage.
     *
     * @param siteStorageState - object SiteStorageState (inner class in this
     * class!)
     */
    public void restore(SiteStorageState siteStorageState) {
        this.queue.clear();
//        for (ObjectToCrawl item : siteStorageState.getState()) {
//            this.queue.add(item);
//        }
    }

    /**
     * this method is refresh queue in site storage state.
     *
     * @param queueSites - queue Domain (BlockingQueue<Domain>)
     * @return - object Site Storage State
     */
    public SiteStorageState refresh(Queue<ObjectToCrawl> queueSites) {
        SiteStorageState siteStorageState = new SiteStorageState(this.PATH);
//        siteStorageState.setState(queueSites);
        return siteStorageState;
    }
    /**
     * this inner class is load state for sites in file (back_up)
     */
}

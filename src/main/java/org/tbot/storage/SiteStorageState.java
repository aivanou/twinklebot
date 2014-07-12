package org.tbot.storage;

import org.tbot.objects.CrawlObjectType;
import org.tbot.objects.ObjectToCrawl;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import org.apache.log4j.Logger;

/**
 *
 */
public class SiteStorageState {

    Logger serverLoggerState = Logger.getLogger(SiteStorageState.class);
    private Queue<ObjectToCrawl> queue;
    private String PATH_FILE_SITES = "";

    /**
     * Constructor
     */
    public SiteStorageState(String path) {
        this.queue = new ConcurrentLinkedQueue<ObjectToCrawl>();
        PATH_FILE_SITES = path;
    }

    /**
     * this method get queue Domain
     *
     * @return BlockingQueue<Domain>
     */
    public Queue<ObjectToCrawl> getState() {
        return queue;
    }

    /**
     * this method is set state for queue sites and save in file. (back_up)
     *
     * @param domains - BlockingQueue<Domain>
     */
    public void setState(Queue<ObjectToCrawl> domains) {
        for (ObjectToCrawl item : domains) {

            this.queue.add(item);

        }
        this.writeQueueToFileBackUpSites();
    }

    /**
     * this method is set state for queue sites and save in file. (back_up)
     *
     * @param domains - BlockingQueue<Domain>
     */
    public void setState(List<ObjectToCrawl> domains) {
        for (ObjectToCrawl item : domains) {
            this.queue.add(item);
        }
        this.writeQueueToFileBackUpSites();
    }

    /**
     * this method is convert information in queue (Domain) in List<String>
     *
     * @param queue - BlockingQueue<Domain>
     * @return List<String>
     */
    private List<String> convertQueueInList(Queue<ObjectToCrawl> queue) {
//        List<String> res = new LinkedList<String>();
//        for (ObjectToCrawl item : queue) {
//            StringBuilder domainString = new StringBuilder();
//            domainString.append(item.getUrl()).append("!~!").append(item.getMaxCrawlPages()).append("!~!").append(item.getMaxDepth()).
//                    append("!~!").append(item.getType()).append("!~!").append(item.getContentHash()).
//                    append("!~!").append(item.getNextDepthStrategy().toString()).append("!~!").append(item.getDefaultBoost());
//            res.add(domainString.toString());
//        }
//        return res;
        return null;
    }

    /**
     * this method is convert information in List<String> to
     * BlockingQueue<Domain>
     *
     * @param sites List<String>
     * @return BlockingQueue<Domain>
     */
    private Queue<ObjectToCrawl> convertListStringToQueueDomain(List<String> sites) {
//        Queue<ObjectToCrawl> res = new ConcurrentLinkedQueue<ObjectToCrawl>();
//        for (String item : sites) {
//            String[] items = item.split("!~!");
//            if (items.length == 7) {
//                NextDepthInsertStrategy strategy = NextDepthInsertStrategy.getStrategy(items[5]);
//                if (strategy == null) {
//                    strategy = NextDepthInsertStrategy.OnlyCurrentDomainUrls;
//                }
//                float boost = 1.0f;
//                try {
//                    boost = Float.parseFloat(items[6]);
//                } catch (NumberFormatException ignore) {
//                }
//                ObjectToCrawl object = new ObjectToCrawl(items[0], Integer.valueOf(items[2]), Integer.valueOf(items[1]),
//                        Integer.valueOf(items[4]), CrawlObjectType.getTypeByName(items[3]), boost, strategy);
//                res.add(object);
//            }
//        }
//        return res;
        return null;
    }

    /**
     * this method is write in file site (back-up) information in queue
     * <Domain>. Used fileUtil module in Linkysearch/utils/
     */
    private void writeQueueToFileBackUpSites() {
        writeInFile(convertQueueInList(this.queue), PATH_FILE_SITES);
    }

    private boolean writeInFile(List<String> information, String fileName) {
        BufferedWriter fileWritter = null;
        Boolean res = null;
        File fileTextUrl = new File(fileName);
        if (!fileTextUrl.exists()) {
            try {
                fileTextUrl.createNewFile();
            } catch (IOException e) {
                e.printStackTrace(System.out);
            }
        }
        try {
            fileWritter = new BufferedWriter(new FileWriter(fileName, false));
            for (String proxy : information) {
                fileWritter.write(proxy);
                fileWritter.newLine();
            }
            fileWritter.flush();
            fileWritter.close();
            res = true;
        } catch (Exception e) {
            e.printStackTrace(System.out);
            res = false;
        } finally {
            if (fileWritter != null) {
                try {
                    fileWritter.close();
                } catch (IOException e) {
                    e.printStackTrace(System.out);
                }
            }
            return res;
        }
    }

    /**
     * this method is read with file (back_up) information and convertation in
     * queue<Domain>. Used fileUtil module in Linkysearch/utils/
     */
    public void readFileBackUpSitesToQueue() {
//        this.queue = convertListStringToQueueDomain(ReaderFile.readProxyInList(PATH_FILE_SITES));
    }
}

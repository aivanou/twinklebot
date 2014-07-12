package org.tbot.entity;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Vadim Martos Date: 2/16/12 Time: 7:04 AM
 */
public class RobotRules implements Serializable {

    private final Map<String, Boolean> entries;
    private long crawlDelay;

    public RobotRules() {
        this.entries = new HashMap<>();
        crawlDelay = 0l;
    }

    public RobotRules(Map<String, Boolean> entries, long crawlDelay) {
        this.entries = entries;
        this.crawlDelay = crawlDelay;
    }

    public long getCrawlDelay() {
        return crawlDelay;
    }

    public void setCrawlDelay(long crawlDelay) {
        this.crawlDelay = crawlDelay;
    }

    public void add(String path, boolean allow) {
        entries.put(path, allow);
    }

    public boolean isAllowed(String path) {
        return entries.containsKey(path) ? entries.get(path) : Boolean.TRUE;
    }

    public void clear() {
        entries.clear();
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tbot.pool;

/**
 *
 * @author alex
 */
public enum ObjectToCrawlProcessStatus {

    Processed("processed"),
    Started("started"),
    Finished("finished");
    private String status;

    private ObjectToCrawlProcessStatus(String status) {
        this.status = status;
    }

    public static ObjectToCrawlProcessStatus getType(String type) {
        for (ObjectToCrawlProcessStatus status : ObjectToCrawlProcessStatus.values()) {
            if (status.toString().trim().equals(type.trim())) {
                return status;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.status;
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tbot.conf;

import java.util.Properties;

/**
 *
 * @author alex
 */
public class SearchConfig extends Config {

    private String host = "localhost";
    private int port = 44555;
    private int bulkLoadSize = 250;

    public SearchConfig(Properties props) {
        if (props == null) {
            return;
        }
        this.host = this.loadStringProp(props, "search.host", host);
        this.port = this.loadIntProp(props, "search.port", port);
        this.bulkLoadSize = this.loadIntProp(props, "search.bulkLoadSize", bulkLoadSize);
    }

    public int getBulkLoadSize() {
        return bulkLoadSize;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}

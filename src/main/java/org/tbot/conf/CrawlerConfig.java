/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tbot.conf;

import java.util.Properties;
import org.apache.http.conn.scheme.SchemeRegistry;

/**
 *
 * @author alex
 */
public class CrawlerConfig {

    public final SchemeRegistry sr = new SchemeRegistry();
    public int CONNECTION_TIMEOUT = 5000;
    public int SOCKET_TIMEOUT = 1500;
    public int MAX_CONTENT_LENGTH = 16000000;
    public int CONNECTIONS_PER_HOST = 20;
    public int TOTAL_CONNECTIONS = 100;
    public int CONCURRENT_DOMAINS = 10;
    public int FETCH_POOL_SIZE = 120;
    public int GS_REQUEST_POOL_SIZE = 100;
    public int GS_RESPONSE_POOL_SIZE = 20;
    public String DATA_FOLDER = "data";

    public CrawlerConfig() {
    }

    public CrawlerConfig(Properties props) {
        CONNECTION_TIMEOUT = this.loadIntProp(props, "twinklebot.connectionTimeout", CONNECTION_TIMEOUT);
        SOCKET_TIMEOUT = this.loadIntProp(props, "twinklebot.socketTimeout", SOCKET_TIMEOUT);
        MAX_CONTENT_LENGTH = this.loadIntProp(props, "twinklebot.maxContentLength", MAX_CONTENT_LENGTH);
        CONNECTIONS_PER_HOST = this.loadIntProp(props, "twinklebot.connectionsPerHost", CONNECTIONS_PER_HOST);
        CONCURRENT_DOMAINS = this.loadIntProp(props, "twinklebot.concurrentDomains", CONCURRENT_DOMAINS);
        FETCH_POOL_SIZE = this.loadIntProp(props, "twinklebot.fetchPoolSize", FETCH_POOL_SIZE);
        GS_RESPONSE_POOL_SIZE = this.loadIntProp(props, "gs.responsePool", GS_RESPONSE_POOL_SIZE);
        GS_REQUEST_POOL_SIZE = this.loadIntProp(props, "gs.requestPool", GS_REQUEST_POOL_SIZE);
        DATA_FOLDER = this.loadStringProp(props, "twinklebot.dataFolder", DATA_FOLDER);
        TOTAL_CONNECTIONS = CONCURRENT_DOMAINS * CONNECTIONS_PER_HOST;
    }

    private String loadStringProp(Properties props, String key, String defaultValue) {
        if (props.containsKey(key)) {
            try {
                String value = props.getProperty(key);
                return value;
            } catch (Exception ex) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }

    private int loadIntProp(Properties props, String key, int defaultValue) {
        if (props.containsKey(key)) {
            try {
                int value = Integer.parseInt(props.getProperty(key));
                return value;
            } catch (NumberFormatException ex) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }
}

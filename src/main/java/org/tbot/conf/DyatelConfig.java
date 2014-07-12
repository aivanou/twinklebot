/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tbot.conf;

import java.util.Properties;

/**
 *
 * @author Vadim Martos @date Mar 23, 2012
 */
public final class DyatelConfig extends Config {

    private int connectTimeout = 1000;
    private int readTimeout = 1000;
    private String lang = "th";
    private int threads = 10;
    private String gsHost = "localhost";
    private int gsPort = 12345;
    private String configDir = "conf";
    private int requestSize = 50;
    //default config entity

    public DyatelConfig(Properties props) {
        connectTimeout = this.loadIntProp(props, "langDetector.connectTimeout", connectTimeout);
        readTimeout = this.loadIntProp(props, "langDetector.readTimeout", readTimeout);
        lang = this.loadStringProp(props, "langDetector.lang", lang);
        threads = this.loadIntProp(props, "langDetector.threads", readTimeout);
        gsHost = this.loadStringProp(props, "gs.host", gsHost);
        gsPort = this.loadIntProp(props, "gs.port", gsPort);
        configDir = this.loadStringProp(props, "configDir", configDir);
        requestSize = this.loadIntProp(props, "langDetector.requestSize", readTimeout);
    }

    public int getRequestSize() {
        return requestSize;
    }

    public String getConfigDir() {
        return configDir;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public String getGsHost() {
        return gsHost;
    }

    public int getGsPort() {
        return gsPort;
    }

    public String getLang() {
        return lang;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public int getThreads() {
        return threads;
    }
}

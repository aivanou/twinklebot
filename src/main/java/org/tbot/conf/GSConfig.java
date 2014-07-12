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
public class GSConfig extends Config {

    public final SchemeRegistry sr = new SchemeRegistry();
    public int PORT = 12345;
    public String HOST = "localhost";

    public GSConfig() {
    }

    public GSConfig(Properties props) {
        PORT = this.loadIntProp(props, "gs.port", PORT);
        HOST = this.loadStringProp(props, "gs.host", HOST);
    }
}

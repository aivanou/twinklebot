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
public abstract class Config {

    protected String loadStringProp(Properties props, String key, String defaultValue) {
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

    protected int loadIntProp(Properties props, String key, int defaultValue) {
        if (props.containsKey(key)) {
            try {
                int value = Integer.parseInt(props.getProperty(key));
                return value;
            } catch (Exception ex) {
                return defaultValue;
            }
        } else {
            return defaultValue;
        }
    }
}

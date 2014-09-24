package org.tbot.log;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 *
 */
public class LoggerCreator {

    private static Logger logger = null;
    private static String serviceName = "default service name";

    public static void setName(String name) {
        serviceName = name;
        logger = Logger.getLogger(serviceName);
    }

    public static void configure(String filename) {
        if (logger == null) {
            PropertyConfigurator.configure(filename);
        }
    }

    @SuppressWarnings("CallToThreadDumpStack")
    public static synchronized Logger getLogger() {
        if (logger == null) {
            logger = Logger.getLogger(serviceName);
        }
        return logger;
    }
}

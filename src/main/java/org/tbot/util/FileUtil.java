package org.tbot.util;

import java.io.File;

/**
 *
 */
public class FileUtil {

    public static void createIfNotExist(String dirPath) {
        File dir = new File(dirPath);
        if (dir.exists()) {
            return;
        }
        dir.mkdir();
    }

    public static void createOrClear(String dirPath) {
        File dir = new File(dirPath);
        dir.deleteOnExit();
        dir.mkdir();
    }

}

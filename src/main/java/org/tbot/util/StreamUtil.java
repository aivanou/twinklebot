/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tbot.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 *
 * @author vadim
 */
public class StreamUtil {

    public static final int BUFFER_SIZE = 2048;
    public static final int MAX_CONTENT_SIZE = 16000000;

    private StreamUtil() {
    }

    public static ByteBuffer streamToByteBuffer(InputStream stream) throws IOException {
        if (stream == null) {
            return null;
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[BUFFER_SIZE];
        int len;
        int totalLength = 0;
        try {
            while ((len = stream.read(buffer)) != -1) {
                output.write(buffer, 0, len);
                totalLength += len;
                if (totalLength >= MAX_CONTENT_SIZE) {
                    break;
                }
            }
        } finally {
            output.flush();
            output.close();
        }
        return ByteBuffer.wrap(output.toByteArray(), 0, output.size());
    }
}

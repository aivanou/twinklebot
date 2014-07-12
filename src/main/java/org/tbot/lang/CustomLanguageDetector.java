/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tbot.lang;

import org.tbot.conf.DyatelConfig;
import org.tbot.loggin.LoggerCreator;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 *
 * @author alex
 */
public class CustomLanguageDetector implements LangDetector {

    private final Logger logger = LoggerCreator.getLogger();
    private final List<String> ALPHABET = new ArrayList<String>();
    private final String GOOD_LANG;
    private final String POINT_LANG;
    private final String OTHER_LANG;
    private final String EX_LANG = "exception";
    private final int percent = 3;

    public CustomLanguageDetector(String alphabetPath, DyatelConfig config) {
        this.GOOD_LANG = config.getLang();
        this.POINT_LANG = String.format(".%s", GOOD_LANG);
        this.OTHER_LANG = String.format("not_%s", GOOD_LANG);
        BufferedReader reader = null;
        try {
            InputStream stream = new FileInputStream(alphabetPath);
            reader = new BufferedReader(new InputStreamReader(stream));
            String code;
            StringBuilder sb = new StringBuilder();
            while ((code = reader.readLine()) != null) {
                ALPHABET.add(code);
                sb.append(code);
            }
        } catch (IOException e) {
            logger.fatal(e.getMessage(), e);
            System.exit(1);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    logger.fatal(e.getMessage(), e);
                    System.exit(1);
                }
            }
        }
    }

    @Override
    public String detect(String host, String text) {
        if (text == null || text.isEmpty()) {
            return EX_LANG;
        }
        if (host.endsWith(POINT_LANG)) {
            return GOOD_LANG;
        }
        int len = text.length();
        int count = 0;
        for (int i = 0; i < text.length(); ++i) {
            if (ALPHABET.contains(text.substring(i, i + 1))) {
                ++count;
                if (count * 100 / len > percent) {
                    return GOOD_LANG;
                }
            }
        }
        return OTHER_LANG;
    }
}

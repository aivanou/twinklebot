/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tbot.lang;

import org.tbot.conf.DyatelConfig;
import org.tbot.loggin.LoggerCreator;
import org.apache.log4j.Logger;
import org.apache.tika.language.LanguageIdentifier;

/**
 *
 * @author vadim
 */
public class TikaLang implements LangDetector {

    private final Logger logger = LoggerCreator.getLogger();
    private final String GOOD_LANG;
    private final String POINT_LANG;
    private final String OTHER_LANG;
    private final String EX_LANG = "exception";

    public TikaLang(DyatelConfig config) {
        this.GOOD_LANG = config.getLang();
        this.POINT_LANG = String.format(".%s", GOOD_LANG);
        this.OTHER_LANG = String.format("not_%s", GOOD_LANG);
    }

    @Override
    public String detect(String host, String text) {
        if (text == null || text.isEmpty()) {
            return EX_LANG;
        } else if (host.endsWith(POINT_LANG)) {
            return GOOD_LANG;
        } else {
            LanguageIdentifier identifier = new LanguageIdentifier(text);
            return identifier.getLanguage();
        }
    }
}

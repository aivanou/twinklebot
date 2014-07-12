package org.tbot.state;

import org.tbot.entity.RobotRules;
import java.net.URL;

/**
 * @author Vadim Martos Date: 12/1/11 Time: 9:32 AM
 */
/**
 * this class return ExtendedRobotRules of a url
 */
public class RobotRulesFactory {

    private static String[] robots = null;
    private static RobotRulesParser rulesParser = null;

    private RobotRulesFactory() {
    }

    public static byte[] getContent(URL url) {
        if (rulesParser == null || robots == null) {
            init();
        }
        return rulesParser.getContent(url);
    }

    public static RobotRules getRobotRules(URL url) {
        if (rulesParser == null || robots == null) {
            init();
        }
        return rulesParser.parseRules(url);
    }

    private static void init() {
        robots = new String[]{"asdf-crawler"};
        rulesParser = new RobotRulesParser(robots);
    }
}

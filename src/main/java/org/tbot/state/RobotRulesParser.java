package org.tbot.state;

import org.tbot.entity.RobotRules;
import org.tbot.util.StreamUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author Vadim Martos Date: 12/2/11 Time: 9:59 AM
 */
public class RobotRulesParser {

    private static final String CHARACTER_ENCODING = "UTF-8";
    private static final int NO_PRECEDENCE = Integer.MAX_VALUE;
    private Map<String, Integer> robotNames = new Hashtable<String, Integer>();

    public RobotRulesParser(Map<String, Integer> robotNames) {
        this.robotNames = robotNames;
        // always make sure "*" is included
        if (!robotNames.containsKey("*")) {
            this.robotNames.put("*", new Integer(robotNames.size()));
        }
    }

    public RobotRulesParser(String[] robotNames) {
        for (int i = 0; i < robotNames.length; i++) {
            this.robotNames.put(robotNames[i].toLowerCase(), new Integer(i));
        }
        // always make sure "*" is included
        if (!this.robotNames.containsKey("*")) {
            this.robotNames.put("*", new Integer(robotNames.length));
        }
    }

    public RobotRules parseRules(byte[] robotContent) {
        if (robotContent == null) {
            return new RobotRules();
        }
        String content = new String(robotContent);
        StringTokenizer lineParser = new StringTokenizer(content, "\n\r");
        RobotRules bestRulesSoFar = null;
        int bestPrecedenceSoFar = NO_PRECEDENCE;
        RobotRules currentRules = new RobotRules();
        int currentPrecedence = NO_PRECEDENCE;
        boolean addRules = false;    // in stanza for our robot
        boolean doneAgents = false;  // detect multiple agent lines

        while (lineParser.hasMoreTokens()) {
            String line = lineParser.nextToken();
            // trim out comments and whitespace
            int hashPos = line.indexOf("#");
            if (hashPos >= 0) {
                line = line.substring(0, hashPos);
            }
            line = line.trim();
            if ((line.length() >= 11)
                    && (line.substring(0, 11).equalsIgnoreCase("User-agent:"))) {
                if (doneAgents) {
                    if (currentPrecedence < bestPrecedenceSoFar) {
                        bestPrecedenceSoFar = currentPrecedence;
                        bestRulesSoFar = currentRules;
                        currentPrecedence = NO_PRECEDENCE;
                        currentRules = new RobotRules();
                    }
                    addRules = false;
                }
                doneAgents = false;

                String agentNames = line.substring(line.indexOf(":") + 1);
                agentNames = agentNames.trim();
                StringTokenizer agentTokenizer = new StringTokenizer(agentNames);

                while (agentTokenizer.hasMoreTokens()) {
                    // for each agent listed, see if it's us:
                    String agentName = agentTokenizer.nextToken().toLowerCase();

                    Integer precedenceInt = (Integer) robotNames.get(agentName);

                    if (precedenceInt != null) {
                        int precedence = precedenceInt.intValue();
                        if ((precedence < currentPrecedence)
                                && (precedence < bestPrecedenceSoFar)) {
                            currentPrecedence = precedence;
                        }
                    }
                }

                if (currentPrecedence < bestPrecedenceSoFar) {
                    addRules = true;
                }

            } else if ((line.length() >= 9)
                    && (line.substring(0, 9).equalsIgnoreCase("Disallow:"))) {

                doneAgents = true;
                String path = line.substring(line.indexOf(":") + 1);
                path = path.trim();
                try {
                    path = URLDecoder.decode(path, CHARACTER_ENCODING);
                } catch (Exception e) {
                    /*
                     * if (LOG.isWarnEnabled()) { LOG.warn("error parsing robots
                     * rules- can't decode path: " + path); }
                     */
                }

                if (path.length() == 0) { // "empty rule"
                    if (addRules) {
                        currentRules.clear();
                    }
                } else {  // rule with path
                    if (addRules) {
                        currentRules.add(path, false);
                    }
                }

            } else if ((line.length() >= 6)
                    && (line.substring(0, 6).equalsIgnoreCase("Allow:"))) {

                doneAgents = true;
                String path = line.substring(line.indexOf(":") + 1);
                path = path.trim();

                if (path.length() == 0) {
                    // "empty rule"- treat same as empty disallow
                    if (addRules) {
                        currentRules.clear();
                    }
                } else {  // rule with path
                    if (addRules) {
                        currentRules.add(path, true);
                    }
                }
            } else if ((line.length() >= 12)
                    && (line.substring(0, 12).equalsIgnoreCase("Crawl-Delay:"))) {
                doneAgents = true;
                if (addRules) {
                    long crawlDelay = -1;
                    String delay = line.substring("Crawl-Delay:".length(), line.length()).trim();
                    if (delay.length() > 0) {
                        try {
                            crawlDelay = Long.parseLong(delay) * 1000; // sec to millisec
                        } catch (Exception e) {
                            //LOG.info("can not parse Crawl-Delay:" + e.toString());
                        }
                        currentRules.setCrawlDelay(crawlDelay);
                    }
                }
            }
        }

        if (currentPrecedence < bestPrecedenceSoFar) {
            bestPrecedenceSoFar = currentPrecedence;
            bestRulesSoFar = currentRules;
        }

        if (bestPrecedenceSoFar == NO_PRECEDENCE) {
            return new RobotRules();
        }
        return bestRulesSoFar;
    }

    public byte[] getContent(URL url) {
        InputStream stream = null;
        try {
            URL robots = new URL(url.getProtocol() + "://" + url.getHost().toLowerCase() + "/robots.txt");
            stream = robots.openStream();
            return new String(StreamUtil.streamToByteBuffer(stream).array()).trim().getBytes();
        } catch (Throwable e) {
            return null;
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    //nothing
                }
            }
        }
    }

    public RobotRules parseRules(URL url) {
        return parseRules(getContent(url));
    }
}

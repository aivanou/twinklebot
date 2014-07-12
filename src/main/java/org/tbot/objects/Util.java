package org.tbot.objects;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 */
public final class Util {

    public static ProtocolType getProtocol(String url) {
        url = url.trim();
        if (url.isEmpty()) {
            return ProtocolType.Unknown;
        }
        int ind = url.indexOf("://");
        if (ind == -1) {
            return ProtocolType.Unknown;
        }
        return ProtocolType.getType(url.substring(0, ind));
    }

    public static String getDomain(String url) {
        url = url.trim();
        if (url.isEmpty()) {
            return null;
        }
        String domain;
        int ind = url.indexOf("://");
        if (ind != -1) {
            domain = url.substring(ind + 3);
        } else {
            domain = url;
        }
        Pattern pattern = Pattern.compile("^(www\\.)?[^\\.]+\\.\\w+");
        Matcher matcher = pattern.matcher(domain);
        if (matcher.find()) {
            return domain.substring(matcher.start(), matcher.end());
        }
        return null;
    }

    public static String normalizeUrl(String domain, String url) {
        if (url.contains("://")) {
            return url;
        }
        if (url.contains("#")) {
            url = url.substring(0, url.indexOf("#"));
        }
        if (url.startsWith("//")) {
            return "http:" + url;
        }
        if (url.startsWith("..")) {
            return domain;
        }
        if (url.startsWith("/")) {
            return domain + url;
        }
        return domain + "/" + url;
    }

    public static void main(String[] args) {
        System.out.println(Util.getDomain("www.google.com"));
    }

}

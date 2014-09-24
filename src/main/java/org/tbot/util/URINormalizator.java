/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tbot.util;

import org.tbot.log.LoggerCreator;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;

/**
 *
 * @author vadim
 */
public class URINormalizator {

    private static final List<String> trunc;
    private static final Pattern pattern;
    private static final String disallowed = "", replacer = "";
    private static final Collection<String> truncExtensions;
    private static Logger logger;

    static {
        truncExtensions = new LinkedList<String>();
        truncExtensions.add("gif");
        truncExtensions.add("png");
        truncExtensions.add("jpg");
        truncExtensions.add("avi");
        truncExtensions.add("css");
        truncExtensions.add("mp3");

        trunc = new LinkedList<String>();
        trunc.add("index.php");
        StringBuilder builder = new StringBuilder();
        for (String string : trunc) {
            builder.append(string).append("*[/]*");
        }
        pattern = Pattern.compile(String.format("(.*)(%s)(.*)", builder.toString()));
    }

    private URINormalizator() {
    }

    public static URL normalize(URI uri, String charset) throws URISyntaxException, MalformedURLException, UnsupportedEncodingException {
        return null;
        //        //[scheme:][//authority][path][?query][#fragment]
//        String input = uri.toString();
//        String newURI = URLDecoder.decode(input, charset);
//        uri = new URI(newURI);
//        uri = uri.normalize();
//
//        String scheme = uri.getScheme();
//        if (scheme == null) {
//            return uri.toURL();
//        }
//        scheme = scheme.toLowerCase();
//        String authority = uri.getAuthority();
//        String path = uri.getPath();
//        if (path != null) {
//            Matcher matcher = pattern.matcher(path);
//            if (matcher.matches()) {
//                path = new StringBuilder(matcher.group(1)).append(matcher.group(3)).toString();
//            }
//            if (!path.endsWith("/")) {
//                path = new StringBuilder(path).append("/").toString();
//            }
//        } else {
//            path = "/";
//        }
//        scheme.replaceAll(disallowed, replacer);
//        authority.replaceAll(disallowed, replacer);
//        path.replaceAll(disallowed, replacer);
////        scheme.replaceAll(disallowed, replacer);
////        authority.replaceAll(disallowed, replacer);
////        path.replaceAll(disallowed, replacer);
//
////        scheme.replaceAll(disallowed, replacer);
////        authority.replaceAll(disallowed, replacer);
////        path.replaceAll(disallowed, replacer);
//        scheme.replaceAll(disallowed, replacer);
//        authority.replaceAll(disallowed, replacer);
//        path.replaceAll(disallowed, replacer);
//        URL outputUrl = new URI(scheme, authority, path, null, null).toURL();
//        String rurl = outputUrl.toString().endsWith("/") ? outputUrl.toString().substring(0, outputUrl.toString().length()) : outputUrl.toString();
//        //System.out.println("URL NORMALIZE: input: " + input.toString() + "\n"
//        //        + "URL NORMALIZE: output: " + rurl);
//        return new URL(rurl);

    }

    public static void main(String[] args) throws URISyntaxException, MalformedURLException, UnsupportedEncodingException {
        String log4jProps = System.getProperty("log4j");
        if (log4jProps == null) {
            log4jProps = "/home/alex/work/code/newTB/TBv2/src/main/resources/log4j.properties";
        }
        LoggerCreator.configure(log4jProps);
        LoggerCreator.setName("uri normalizer");
        logger = LoggerCreator.getLogger();
        URI tempUrl1 = new URI("http://docs.oracle.com/javase/1.4.2/docs/api/java/net/URI.html#toString()");
        URI tempUrl2 = new URI("http%3A%2F%2Flinky.co.th%2Fstatic%2Fcss_3d61e0c1798dd6218f1c599027afda24.css");
        URI tempUrl3 = new URI("https://linky.th/index.php/someoneelse");

        Collection<URI> uris = new ArrayList<URI>();
        uris.add(tempUrl1);
        uris.add(tempUrl2);
        uris.add(tempUrl3);
        for (URI uri : uris) {
            logger.info(URINormalizator.normalize(uri, "UTF-8"));
        }
        /*
         * URI uri = new
         * URI("http://VaDim.com/%d0%98%d0%b2%d0%b0%d0%bd%d0%be%d0%b2%20%d0%98%d0%b2%d0%b0%d0%bd%20%d0%b8%d0%b2%d0%b0%d0%bd%d0%be%d0%b2%d0%b8%d1%87?q=123123#qweqwe");
         * uri = new
         * URI("http://VadIm.com/InDex.php/%d0%98%d0%b2%d0%b0%d0%bd%d0%be%d0%b2%20%d0%98%d0%b2%d0%b0%d0%bd%20%d0%b8%d0%b2%d0%b0%d0%bd%d0%be%d0%b2%d0%b8%d1%87#asdasd");
         * //uri = new URI("http://VadIm.com/InDex.php/");
         * System.out.println(uri); URL url = normalize(uri, "UTF-8");
         * System.out.println(url);
         */
        int i = 10;
    }
}

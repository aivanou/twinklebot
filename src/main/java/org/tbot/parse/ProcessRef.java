package org.tbot.parse;

import java.util.logging.Level;
import org.apache.log4j.Logger;

import java.io.UnsupportedEncodingException;
import java.net.*;

/**
 * Created by IntelliJ IDEA. User: alatysh Date: 6/22/12 Time: 5:15 PM
 */
public class ProcessRef {

    protected static final Logger logger = Logger.getLogger(HtmlParser.class.getName());
    private static final String[] badHref = new String[]{"#"};

    public String removeEndSlash(String url) {
        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }

    private URI getUriFromStartOneSlash(String ref, String from) {
        StringBuilder urlAbsolute = new StringBuilder();
        try {
            URI uri = new URI(URLEncoder.encode(ref, "UTF8"));
            URL urlT;
            urlT = new URL(from);
            urlAbsolute.append(urlT.getProtocol()).append("://").append(urlT.getHost());
            URI fromUri;
            fromUri = new URI(String.format("%s/", urlAbsolute.toString()));
            String uriNormalization = uri.toString();
            while (uriNormalization.startsWith("%2F")) {
                uriNormalization = uriNormalization.substring(3);
            }
            uri = new URI(uriNormalization);
            return fromUri.resolve(uri);
        } catch (URISyntaxException e) {
            logger.trace(e.getMessage(), e);
        } catch (MalformedURLException | UnsupportedEncodingException e) {
            logger.trace(e.getMessage(), e);
        }
        return null;
    }

    private URI getUriFromStartTwoSlash(String ref, String from) {
        StringBuilder urlAbsolute = new StringBuilder();
        try {
            URL urlT;
            urlT = new URL(from);
            urlAbsolute.append(urlT.getProtocol()).append(":").append(ref);
            return new URI(urlAbsolute.toString());
        } catch (URISyntaxException e) {
            logger.trace(e.getMessage(), e);
        } catch (MalformedURLException e) {
            logger.trace(e.getMessage(), e);
        }
        return null;
    }

    public URI getUriInLink(String ref, String from) {
        URI uri = null;
        for (int i = 0, n = badHref.length; i < n; i++) {
            if (ref.startsWith(badHref[i])) {
                return null;
            }
        }
        if (!ref.startsWith("/") && !ref.startsWith("..") && !ref.startsWith("http")) {
            try {
                URL fu = new URL(from);
                ref = fu.getProtocol() + "://" + fu.getHost() + "/" + ref;
            } catch (MalformedURLException ex) {
                java.util.logging.Logger.getLogger(ProcessRef.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        try {
            if (ref.contains("/")) {
                if (ref.startsWith("//")) {
                    return getUriFromStartTwoSlash(ref, from);
                } else {
                    if (ref.startsWith("/")) {
                        return getUriFromStartOneSlash(ref, from);
                    } else {
                        if (!ref.startsWith("..")) {
                            uri = new URI(ref);
                        } else {
                            String[] params = ref.split("//");
                            String[] paramsRef = params[params.length - 1].split("/");
                            uri = new URI(getNormalRef(from, paramsRef));
                        }
                    }
                }
            }
        } catch (URISyntaxException e) {
            logger.trace(e.getMessage(), e);
        }
        if (uri == null) {
            return null;
        }
        String uriStr = uri.toString();
        for (String symb : badHref) {
            int start = uriStr.indexOf(symb);
            if (start == -1) {
                continue;
            }
            uriStr = uriStr.substring(0, start);
        }
        try {
            return new URI(uriStr);
        } catch (URISyntaxException ex) {
            return uri;
        }
    }

    private int relativeDepth(String argRef[]) {
        int size = 0;
        for (int i = 0, n = argRef.length; i < n; i++) {
            if (argRef[i].contains("..")) {
                ++size;
            }
        }
        return size;
    }

    private String getNormalRef(String from, String[] argRef) {
        StringBuilder stringBuilder = new StringBuilder();
        String[] argFrom = from.split("//");
        if (argFrom.length > 1) {
            stringBuilder.append(argFrom[0]).append("/");
            String[] pathFrom = argFrom[1].split("/");
            int sizePathRelative = relativeDepth(argRef);
            if (pathFrom.length > sizePathRelative) {
                for (int i = 0, n = pathFrom.length - sizePathRelative; i < n; i++) {
                    stringBuilder.append("/").append(pathFrom[i]);
                }
                for (int i = sizePathRelative, n = argRef.length; i < n; i++) {
                    stringBuilder.append("/").append(argRef[i]);
                }
            }
        }
        return stringBuilder.toString();
    }
}

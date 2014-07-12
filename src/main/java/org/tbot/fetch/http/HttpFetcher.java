package org.tbot.fetch.http;

import java.io.FileInputStream;
import org.tbot.conf.CrawlerConfig;
import org.tbot.fetch.ProtocolResponse;
//import org.tbot.entity.feedback.HttpState;
import org.tbot.loggin.LoggerCreator;
import org.tbot.parse.ContentType;
import org.tbot.util.StreamUtil;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.tbot.fetch.ProtocolException;
import org.tbot.fetch.ProtocolType;
import org.tbot.fetch.ProtocolFetcher;
import org.tbot.fetch.ProtocolOutput;
import org.tbot.fetch.ProtocolStatus;

/**
 *
 * Represents the Http Request and Response Based on Apache Httpcomponents
 */
public class HttpFetcher extends ProtocolFetcher {

    private static ProtocolFetcher fetcher = null;
    private static HttpClient client = null;
    private static MultiThreadedHttpConnectionManager mgr;

    public static ProtocolFetcher init(CrawlerConfig config) {
        if (fetcher != null && client != null) {
            return fetcher;
        }
        mgr = new MultiThreadedHttpConnectionManager();

        HttpConnectionManagerParams prs = new HttpConnectionManagerParams();
        prs.setDefaultMaxConnectionsPerHost(config.CONNECTIONS_PER_HOST);
        prs.setMaxTotalConnections(config.TOTAL_CONNECTIONS);
        prs.setSoTimeout(config.SOCKET_TIMEOUT);
        prs.setConnectionTimeout(config.CONNECTION_TIMEOUT);
        prs.setStaleCheckingEnabled(true);
        mgr.setParams(prs);
        client = new HttpClient(mgr);
        fetcher = new HttpFetcher();
        return fetcher;
    }

    @Override
    public ProtocolResponse fetch(String path) throws ProtocolException {
        return fetch(path, 0, 5);
    }

    private ProtocolResponse fetch(String path, int depth, int maxDepth) throws ProtocolException {
        if ((path == null) || path.toString().isEmpty()) {
            return new ProtocolResponse(new ProtocolStatus("path null or empty", ProtocolStatus.Type.InvalidPath), null);
        }
        if (depth > maxDepth) {
            return new ProtocolResponse(new ProtocolStatus("path null or empty", ProtocolStatus.Type.TooManyRedirections), null);
        }
        path = path.trim();

        HttpResponse response = null;
        try {
            response = getResponse(path);
        } catch (UnknownHostException ex) {
            return new ProtocolResponse(new ProtocolStatus("Unknown host: " + path, ProtocolStatus.Type.UnknownHost), null);
        } catch (IllegalStateException ex) {
            return new ProtocolResponse(new ProtocolStatus("unknown protocol: " + path, ProtocolStatus.Type.UnknownProtocol), null);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(HttpFetcher.class.getName()).log(Level.SEVERE, null, ex);
            throw new ProtocolException(ex);
        }
        int code = response.getCode();
        if (code == 200) {
            String encoding = response.getHeader("content-encoding");
            Map<String, String> params = parseType(response.getHeader("content-type"));
            ContentType ctype = ContentType.forName(params.get("type"));
            if (encoding == null) {
                encoding = params.get("charset");
            }
            return new ProtocolResponse(new ProtocolStatus("Got page, code: 200", ProtocolStatus.Type.Valid),
                    new ProtocolOutput(response.getContent(), encoding, ctype, path.toString()));
        }
        ProtocolOutput pout = new ProtocolOutput(response.getContent(), null, ContentType.Unknown, path.toString());
        switch (code) {
            case 300:
                return new ProtocolResponse(new ProtocolStatus("Multiple Choices", ProtocolStatus.Type.UnknownState), pout);
            case 301:
            case 302:
            case 303:
            case 307:
                String refrUrl = response.getHeader("refresh");
                if (refrUrl == null) {
                    return new ProtocolResponse(new ProtocolStatus("Moved temporaly", ProtocolStatus.Type.Moved), pout);
                }
                return handleRedirect(pout, path, refrUrl, depth, maxDepth);
            case 304:
                return new ProtocolResponse(new ProtocolStatus("Not modified", ProtocolStatus.Type.NotModified), pout);
            case 400:
                return new ProtocolResponse(new ProtocolStatus("Bad request", ProtocolStatus.Type.NotFound), pout);
            case 401:
            case 403:
                return new ProtocolResponse(new ProtocolStatus("Unauthorized", ProtocolStatus.Type.Unauthorized), pout);
            case 404:
            case 410:
                return new ProtocolResponse(new ProtocolStatus("Not found", ProtocolStatus.Type.NotFound), pout);
            case 500:
            case 501:
            case 502:
            case 503:
                return new ProtocolResponse(new ProtocolStatus("Internal server error", ProtocolStatus.Type.ServerError), pout);
            default:
                return new ProtocolResponse(new ProtocolStatus("Unknown error", ProtocolStatus.Type.UnknownError), pout);
        }

    }

    protected HttpResponse getResponse(String url) throws IOException {
        GetMethod get = null;
        try {
            get = new GetMethod(url);
            get.getParams().setParameter(HttpMethodParams.RETRY_HANDLER,
                    new DefaultHttpMethodRetryHandler(3, false));
            int code = client.executeMethod(get);
            Map<String, String> headers = new HashMap<>();
            for (Header head : get.getResponseHeaders()) {
                if (head == null || head.getName() == null || head.getValue() == null) {
                    continue;
                }
                headers.put(head.getName().trim().toLowerCase(), head.getValue().trim().toLowerCase());
            }
            ByteBuffer content = StreamUtil.streamToByteBuffer(get.getResponseBodyAsStream());
            return new HttpResponse(code, content, get.getStatusText(), headers);
        } finally {
            if (get != null && !get.isAborted()) {
                get.abort();
            }
        }
    }

    private ProtocolResponse handleRedirect(ProtocolOutput pout, String path, String newPath, int depth, int maxDepth) throws ProtocolException {
        try {
            URL url = new URL(path);
            URL newUrl = new URL(newPath);
            if (url.getHost().trim().equals(newUrl.getHost().trim())) {
                return fetch(newPath, depth + 1, maxDepth);
            }
            return new ProtocolResponse(new ProtocolStatus("Unknown Host", ProtocolStatus.Type.Moved), pout);
        } catch (MalformedURLException ex) {
            return new ProtocolResponse(new ProtocolStatus("Bad url : " + ex.getLocalizedMessage(), ProtocolStatus.Type.UnknownHost), pout);
        }
    }

    private Map<String, String> parseType(String text) {
        Map<String, String> parameters = new HashMap<>();
        String[] params = text.split(";");
        String contentType = params[0].trim();
        for (int i = 1; i < params.length; ++i) {
            String[] vals = params[i].split("=");
            parameters.put(vals[0].trim(), vals[1].trim());
        }
        parameters.put("type", contentType);
        return parameters;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        try {
            CrawlerConfig crawlerConfig = new CrawlerConfig(System.getProperties());
            ProtocolFetcher.build(crawlerConfig);
            ProtocolFetcher httpFetcher = ProtocolFetcher.getFetcher(ProtocolType.Http);
            String url = "http://google.com";
            ProtocolResponse resp = httpFetcher.fetch(url);
            System.out.println(resp.getProtocolOutput().getEncoding());
        } catch (ProtocolException ex) {
            System.out.println(ex.getLocalizedMessage());
        }
    }

}

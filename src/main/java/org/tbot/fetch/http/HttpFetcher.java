package org.tbot.fetch.http;

import java.io.ByteArrayOutputStream;
import org.tbot.conf.CrawlerConfig;
import org.tbot.fetch.ProtocolResponse;
import org.tbot.parse.ContentType;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.http.Header;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.config.SocketConfig;
import org.apache.http.impl.DefaultBHttpClientConnection;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;
import org.apache.http.protocol.HttpProcessor;
import org.apache.http.protocol.HttpProcessorBuilder;
import org.apache.http.protocol.HttpRequestExecutor;
import org.apache.http.protocol.RequestConnControl;
import org.apache.http.protocol.RequestContent;
import org.apache.http.protocol.RequestExpectContinue;
import org.apache.http.protocol.RequestTargetHost;
import org.apache.http.protocol.RequestUserAgent;
import org.tbot.fetch.ProtocolException;
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

    public static ProtocolFetcher init(CrawlerConfig config) {
        if (fetcher != null && client != null) {
            return fetcher;
        }
        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setDefaultSocketConfig(SocketConfig.custom().setSoKeepAlive(true).setSoTimeout(10000).build());
        cm.setDefaultMaxPerRoute(10);
        cm.setMaxTotal(100);
        client = HttpClients.custom().setRedirectStrategy(new DefaultRedirectStrategy()).setConnectionManager(cm).build();
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
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            response = getResponse(path, buffer);
        } catch (UnknownHostException ex) {
            return new ProtocolResponse(new ProtocolStatus("Unknown host: " + path, ProtocolStatus.Type.UnknownHost), null);
        } catch (IllegalStateException ex) {
            return new ProtocolResponse(new ProtocolStatus("unknown protocol: " + path, ProtocolStatus.Type.UnknownProtocol), null);
        } catch (SocketTimeoutException ex) {
            return new ProtocolResponse(new ProtocolStatus("socket timeout exception: " + path, ProtocolStatus.Type.UnknownError), null);
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(HttpFetcher.class.getName()).log(Level.SEVERE, null, ex);
            throw new ProtocolException(ex);
        }
        int code = response.getStatusLine().getStatusCode();
        if (code == 200) {
            Header encodingHeader = response.getEntity().getContentEncoding();
            Header contentTypeHeader = response.getEntity().getContentType();
            Map<String, String> params = parseType(contentTypeHeader);
            ContentType ctype = ContentType.forName(params.get("type"));
            String encoding;
            if (encodingHeader == null) {
                encoding = params.get("charset");
            } else {
                encoding = encodingHeader.getValue();
            }
            return new ProtocolResponse(new ProtocolStatus("Got page, code: 200", ProtocolStatus.Type.Valid),
                    new ProtocolOutput(ByteBuffer.wrap(buffer.toByteArray()), encoding, ctype, path.toString()));
        }
        ProtocolOutput pout = new ProtocolOutput(ByteBuffer.allocate(0), null, ContentType.Unknown, path.toString());
        switch (code) {
            case 300:
                return new ProtocolResponse(new ProtocolStatus("Multiple Choices", ProtocolStatus.Type.UnknownState), pout);
            case 301:
            case 302:
            case 303:
            case 307:
                Header refrUrlHeader = response.getFirstHeader("Location");
                if (refrUrlHeader == null) {
                    return new ProtocolResponse(new ProtocolStatus("Moved temporaly", ProtocolStatus.Type.Moved), pout);
                }
                return handleRedirect(pout, path, refrUrlHeader.getValue(), depth, maxDepth);
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

    protected HttpResponse getResponse(String url, ByteArrayOutputStream content) throws IOException, SocketTimeoutException {
        HttpGet get = null;
        try {
            get = new HttpGet(url);
            HttpContext context = new BasicHttpContext();
            HttpResponse resp = client.execute(get, context);
            resp.getEntity().writeTo(content);
            get.releaseConnection();
            return resp;
        } finally {
            if (get != null && !get.isAborted()) {
                get.abort();
            }
        }
    }

    protected HttpResponse getResponse_v2(String url, ByteArrayOutputStream content) throws IOException, HttpException {
        HttpProcessor httpproc = HttpProcessorBuilder.create()
                .add(new RequestContent())
                .add(new RequestTargetHost())
                .add(new RequestConnControl())
                .add(new RequestUserAgent("Test/1.1"))
                .add(new RequestExpectContinue(true)).build();
        URL u = new URL(url);
        HttpRequestExecutor executor = new HttpRequestExecutor();
        HttpCoreContext coreContext = HttpCoreContext.create();
        coreContext.setTargetHost(new HttpHost(u.getHost(), 80));
        DefaultBHttpClientConnection conn = new DefaultBHttpClientConnection(2048);
        Socket socket = new Socket(u.getHost(), 80);
        conn.bind(socket);
        String path = u.getPath();
        if (path.isEmpty()) {
            path = "/";
        }
        BasicHttpRequest req = new BasicHttpRequest("GET", path);
        executor.preProcess(req, httpproc, coreContext);
        HttpResponse resp = executor.execute(req, conn, coreContext);
        executor.postProcess(resp, httpproc, coreContext);

        resp.getEntity().writeTo(content);
        conn.close();
        return resp;
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

    private Map<String, String> parseType(Header header) {
        Map<String, String> parameters = new HashMap<>();
        if (header == null) {
            return parameters;
        }
        String[] params = header.getValue().split(";");
        String contentType = params[0].trim();
        for (int i = 1; i < params.length; ++i) {
            String[] vals = params[i].split("=");
            parameters.put(vals[0].trim(), vals[1].trim());
        }
        parameters.put("type", contentType);
        return parameters;
    }

    public static void main(String[] args) throws UnsupportedEncodingException, IOException, ProtocolException {
        String url = "http://en.wikipedia.org/";
        ProtocolResponse resp = HttpFetcher.init(new CrawlerConfig()).fetch(url);
        System.out.println(resp.getStatusState());
    }

}

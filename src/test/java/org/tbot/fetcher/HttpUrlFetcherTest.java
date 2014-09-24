package org.tbot.fetcher;

import org.tbot.conf.CrawlerConfig;
import org.tbot.fetch.ProtocolType;
import org.tbot.fetch.ProtocolFetcher;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.tbot.fetch.ProtocolException;
import org.tbot.fetch.ProtocolResponse;
import org.tbot.fetch.ProtocolStatus;

public class HttpUrlFetcherTest {

    private final ProtocolFetcher httpFetcher;

    public HttpUrlFetcherTest() throws IOException {
        CrawlerConfig crawlerConfig = new CrawlerConfig(System.getProperties());
        ProtocolFetcher.build(crawlerConfig);
        this.httpFetcher = ProtocolFetcher.getFetcher(ProtocolType.Http);
    }

    @Test
    public void testEmptyPath() {
        try {
            ProtocolResponse resp = httpFetcher.fetch("");
            Assert.assertEquals(resp.getStatusState().getState(), ProtocolStatus.Type.InvalidPath);
            Assert.assertEquals(null, resp.getProtocolOutput());
        } catch (ProtocolException ex) {
            Assert.fail(ex.getLocalizedMessage());
        }
    }

    @Test
    public void testUnknownHost() {
        try {
            ProtocolResponse resp = httpFetcher.fetch("http://unknownhostwithoutdomain");
            Assert.assertEquals(resp.getStatusState().getState(), ProtocolStatus.Type.UnknownHost);
        } catch (ProtocolException ex) {
            Assert.fail(ex.getLocalizedMessage());
        }
    }

    @Test
    public void testUnknownProtocol() {
        try {
            ProtocolResponse resp = httpFetcher.fetch("http://unknownhostwithoutdomain");
            Assert.assertEquals(resp.getStatusState().getState(), ProtocolStatus.Type.UnknownHost);
        } catch (ProtocolException ex) {
            Assert.fail(ex.getLocalizedMessage());
        }
    }

    @Test
    public void testNotFound() {
        try {
            ProtocolResponse resp = httpFetcher.fetch("http://en.wikipedia.org/unexistingpagwi-thoutanydata123");
            Assert.assertEquals(resp.getStatusState().getState(), ProtocolStatus.Type.NotFound);
        } catch (ProtocolException ex) {
            Assert.fail(ex.getLocalizedMessage());
        }
    }

    @Test
    public void testSimpleUrl() {
        try {
            ProtocolResponse resp = httpFetcher.fetch("http://google.com");
            Assert.assertEquals(resp.getStatusState().getState(), ProtocolStatus.Type.Valid);
            Assert.assertTrue(resp.getProtocolOutput() != null);
            Assert.assertTrue(resp.getProtocolOutput().getContent().capacity() > 0);
            Assert.assertTrue(resp.getProtocolOutput().getEncoding() != null);
        } catch (ProtocolException ex) {
            Assert.fail(ex.getLocalizedMessage());
        }
    }
}

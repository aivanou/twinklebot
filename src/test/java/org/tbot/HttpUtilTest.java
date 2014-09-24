package org.tbot;

import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;
import org.tbot.fetch.ProtocolType;
import org.tbot.objects.Link;
import org.tbot.util.HttpUtil;

/**
 *
 */
public class HttpUtilTest {

    @Test
    public void testUrlNormalization() {
        String domain = "http://domain.com";
        String urlFrom = domain + "/someUrl.html";
        Assert.assertEquals(domain + "/testpage.html", HttpUtil.normalizeUrl(urlFrom, "testpage.html"));
        Assert.assertEquals(domain + "/testpage.html", HttpUtil.normalizeUrl(urlFrom, "/testpage.html"));
        Assert.assertEquals(urlFrom, HttpUtil.normalizeUrl(urlFrom, ""));
        Assert.assertEquals(urlFrom, HttpUtil.normalizeUrl(urlFrom, "../testpage.html"));
        Assert.assertEquals(domain + "/newtestpage.html", HttpUtil.normalizeUrl(urlFrom, "./newtestpage.html"));
        Assert.assertEquals("http://testdomain.com/testpage.html", HttpUtil.normalizeUrl(urlFrom, "//testdomain.com/testpage.html"));
    }

    @Test
    public void testGetDomain() {
        Assert.assertEquals("", HttpUtil.getDomain(""));
        Assert.assertEquals("domain.com", HttpUtil.getDomain("http://domain.com"));
        Assert.assertEquals("domain.com", HttpUtil.getDomain("http://domain.com/somepage"));
        Assert.assertEquals("domain.com", HttpUtil.getDomain("http://domain.com#index"));
        Assert.assertEquals("domain.com", HttpUtil.getDomain("http://domain.com/somepage#"));
    }

    @Test
    public void testGetProtocol() {
        Assert.assertEquals(ProtocolType.Unknown, HttpUtil.getProtocol(""));
        Assert.assertEquals(ProtocolType.Http, HttpUtil.getProtocol("http://url.com"));
        Assert.assertEquals(ProtocolType.Http, HttpUtil.getProtocol("http://url.com/something"));
        Assert.assertEquals(ProtocolType.Unknown, HttpUtil.getProtocol("unknownprotocol://url.com"));
    }

    @Test
    public void testGetDomainsWithProtocol() {
        Set<String> urls = new HashSet<>(Arrays.asList("http://domain.com/1", "http://domain.com/2", "http://newdomain.com"));
        Set<String> domains = HttpUtil.getDomainsWithProtocolFromUrls(urls);
        Assert.assertEquals(2, domains.size());
        Assert.assertTrue(domains.contains("http://domain.com"));
        Assert.assertTrue(domains.contains("http://newdomain.com"));
    }

    @Test
    public void testGetDomainsFromLinksTo() throws MalformedURLException {
        Set<Link> links = new HashSet<>(Arrays.asList(new Link("http://domainfrom.com/page", "http://domainto.com/page"),
                new Link("http://domainfrom1.com/page", "http://domainto2.com/page"),
                new Link("http://domainfrom.com/page", "http://domainto1.com/page"),
                new Link("http://domainfrom1.com/page", "http://domainto.com/page"),
                new Link("http://domainfrom.com/page", "http://domainto.com/page")));

        Assert.assertEquals(3, HttpUtil.getDomainsFromLinksTo(links).size());
    }

}

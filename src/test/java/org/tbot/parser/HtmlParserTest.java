package org.tbot.parser;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import org.junit.Assert;
import org.junit.Test;
import org.tbot.parse.HtmlParser;
import org.tbot.parse.ParsedPage;
import org.tbot.parse.ParserException;

public class HtmlParserTest {

    private final HtmlParser parser;

    public HtmlParserTest() {
        this.parser = HtmlParser.instance();
    }

    public static String readFile(String filename) throws Exception {
        File testFile = new File(ClassLoader.getSystemResource(filename).toURI());
        BufferedInputStream str = new BufferedInputStream(new FileInputStream(testFile));
        StringBuilder sb = new StringBuilder();
        int a;
        while ((a = str.read()) > 0) {
            sb.append((char) a);
        }
        str.close();
        return sb.toString();
    }

    @Test
    public void testEmptyContent() {
        try {
            ParsedPage page = parser.parse("", "http://urlfrom.com");
            Assert.assertTrue(page.getHtmlPage().isEmpty());
            Assert.assertEquals(0, page.getInLinks().size());
            Assert.assertEquals(2, page.getParsedContent().size());
            Assert.assertTrue(page.getParsedContent().get("title").isEmpty());
            Assert.assertTrue(page.getParsedContent().get("content").isEmpty());
        } catch (ParserException ex) {
            Assert.fail(ex.getLocalizedMessage());
        }
    }

    @Test
    public void testTest1() {
        try {
            ParsedPage page = parser.parse(readFile("test1.html"), "http://urlfrom.com");
            Assert.assertTrue(!page.getHtmlPage().isEmpty());
            Assert.assertEquals(14, page.getOutLinks().size());
            Assert.assertEquals(0, page.getInLinks().size());
            Assert.assertEquals(2, page.getParsedContent().size());

        } catch (Exception ex) {
            Assert.fail(ex.getLocalizedMessage());
        }

    }

    @Test
    public void testTest2() {
        try {
            ParsedPage page = parser.parse(readFile("test2.html"), "http://urlfrom.com");
            Assert.assertTrue(!page.getHtmlPage().isEmpty());
            Assert.assertEquals(201, page.getOutLinks().size());
            Assert.assertEquals(0, page.getInLinks().size());
            Assert.assertEquals(2, page.getParsedContent().size());

        } catch (Exception ex) {
            Assert.fail(ex.getLocalizedMessage());
        }

    }

    @Test
    public void testTest3() {
        try {
            ParsedPage page = parser.parse(readFile("test3.html"), "http://stackoverflow.com");
            Assert.assertTrue(!page.getHtmlPage().isEmpty());
            Assert.assertEquals(97, page.getOutLinks().size());
            Assert.assertEquals(116, page.getInLinks().size());
            Assert.assertEquals(2, page.getParsedContent().size());

        } catch (Exception ex) {
            Assert.fail(ex.getLocalizedMessage());
        }

    }
}

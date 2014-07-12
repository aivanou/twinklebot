package org.tbot;

import org.tbot.conf.CrawlerConfig;
import org.tbot.conf.DyatelConfig;
import org.tbot.conf.SearchConfig;
import org.tbot.entity.RobotRules;
import org.tbot.entity.Runner;
import org.tbot.fetch.FetchRunner;
import org.tbot.fetch.ProtocolFetcher;
import org.tbot.lang.CustomLanguageDetector;
import org.tbot.lang.LangDetector;
import org.tbot.loggin.LoggerCreator;
import org.tbot.objects.CrawlObjectType;
import org.tbot.objects.ObjectToCrawl;
import org.tbot.parse.ContentType;
import org.tbot.parse.Parser;
import org.tbot.pool.CommonPool;
import org.tbot.pool.FetcherPool;
import org.tbot.state.RobotRulesFactory;
import org.tbot.statistics.Statistics;
import org.tbot.storage.SiteStorage;
import org.tbot.storage.SiteStorageState;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.log4j.Logger;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.tbot.entity.FetchUrl;

/**
 *
 */
public class Bot {

    private final CommonPool<FetchUrl> fetchPool;
    private ExecutorService fetchers;
    private SiteStorage storage = null;
    private static String[] ids = null;
    public static Statistics statistics;
    public static CrawlerConfig crawlerConfig;
    private static Logger logger = null;

    public Bot(String path, boolean readFromGS, boolean readFromFile) throws InterruptedException, MalformedURLException {
        this.storage = new SiteStorage(path, Bot.crawlerConfig.GS_REQUEST_POOL_SIZE, readFromGS, readFromFile);
        fetchPool = new FetcherPool(crawlerConfig.FETCH_POOL_SIZE, storage, crawlerConfig.CONCURRENT_DOMAINS, crawlerConfig.CONNECTIONS_PER_HOST);
    }

    protected void submit(ExecutorService exec, int size, Runner runner) {
        for (int i = 0; i < size; i++) {
            //exec.submit(runnder);
            new Thread(runner).start();
        }
    }

    public void start(SearchConfig conf) throws InterruptedException, MalformedURLException {
        List<Map.Entry<String, Integer>> lst = new LinkedList<Map.Entry<String, Integer>>();
        lst.add(new SimpleEntry<>(conf.getHost(), conf.getPort()));
        fetchers = Executors.newFixedThreadPool(crawlerConfig.TOTAL_CONNECTIONS);

        System.out.println("starting threads");
        for (int i = 0; i < crawlerConfig.CONCURRENT_DOMAINS; i++) {
            String id = ids[i];
            this.submit(fetchers, crawlerConfig.CONNECTIONS_PER_HOST, new FetchRunner(fetchPool, this, id));
        }
    }

    public void setRule() {
        //implement
    }

    static int parseIntProperty(String name, int defaultValue) {
        if (System.getProperty(name) != null) {
            try {
                return Integer.parseInt(System.getProperty(name));
            } catch (Exception ex) {
                return defaultValue;
            }
        }
        return defaultValue;
    }

    public static void main(String[] args) throws InterruptedException, MalformedURLException, IOException, Exception {
        String workingType = args.length == 0 ? "crawler" : args[0];
        String confDir = "conf";
        if (System.getProperty("conf") != null) {
            confDir = System.getProperty("conf");
        }
        String propFile = confDir + "/fetcher.properties";
        loadProperties(propFile);
        String baseDir = System.getProperty("user.dir");
        System.out.println("baseDir:  " + baseDir);
        Bot.crawlerConfig = new CrawlerConfig(System.getProperties());
        DyatelConfig dConfig = new DyatelConfig(System.getProperties());
        System.out.println("config dir: " + baseDir + "/" + dConfig.getConfigDir());
        String log4jProps = System.getProperty("log4j");
        SearchConfig sConfig = new SearchConfig(System.getProperties());
        if (log4jProps == null) {
            log4jProps = baseDir + "/" + dConfig.getConfigDir() + "/log4j.properties";
            System.getProperties().setProperty("log4j", log4jProps);
        }
        LoggerCreator.configure(log4jProps);
        LoggerCreator.setName("bot");

        String alph = baseDir + "/" + dConfig.getConfigDir() + "/alphabet";
        String text = baseDir + "/" + dConfig.getConfigDir() + "/testText";
        String domainFile = baseDir + "/" + dConfig.getConfigDir() + "/domain";
        //testLanguageDetector(alph, text);

        Bot.logger = LoggerCreator.getLogger();
        if (System.getProperty("twinklebot.nextDepthUrls") == null) {//wtf??
            System.setProperty("twinklebot.nextDepthUrls", "4000");
        }
        System.out.println(System.getProperty("twinklebot.nextDepthUrls"));
        String urlPath = baseDir + "/" + dConfig.getConfigDir() + "/urls.txt";
        String countPath = baseDir + "/" + dConfig.getConfigDir() + "/countTime.txt";
        Bot.statistics = new Statistics(urlPath, countPath);

        String urlStoragePath = baseDir + "/" + dConfig.getConfigDir();
        if (System.getProperties().getProperty("twinklebot.urlStorage") != null) {
            urlStoragePath += "/" + System.getProperties().getProperty("twinklebot.urlStorage");
        } else {
            urlStoragePath += "/backupSites1";
        }
        System.out.println("storage path: " + urlStoragePath.toLowerCase());
        logger.info("each fetcher will have pool size: " + Bot.crawlerConfig.FETCH_POOL_SIZE);
        Bot.initHttpScheme();
        Bot.generateIds();
        Parser.build();
        ProtocolFetcher.build(crawlerConfig);
        System.out.println(alph);
        System.out.println(domainFile);
        System.out.println("starting bot with : " + workingType);
        boolean readFromFile = "true".equals(System.getProperty("twinklebot.readFromFile"));
        boolean readFromGS = "true".equals(System.getProperty("twinklebot.readFromGS"));
        if (workingType.equals("crawler")) {
            Bot b = new Bot(urlStoragePath, readFromGS, readFromFile);
            b.start(sConfig);
        } else if (workingType.equals("langDetector")) {
//            langDetectorTest(dConfig, domainFile, alph);
        }
//        changeURI();
    }

    public static void changeURI() {
        String l = "http://khochea.multiply.com/item/reply/khochea:photos:10:6+1?xurl=http%3A%2F%2Fkhochea.multiply.com%2Fphotos%2Falbum%2F10%2Fvillage-old%3F%26album%3D10%26view%3Areplies%3Dchronological";
    }

    public static void loadProperties(String file) throws IOException {
        Properties props = new Properties();
        props.load(new FileInputStream(file));
        for (Object prop : props.keySet()) {
            System.getProperties().setProperty(prop.toString(), props.getProperty(prop.toString()));
        }
    }

    public static void testLanguageDetector(DyatelConfig config, String alphPath, String textFile) throws FileNotFoundException, IOException {
        LangDetector detector = new CustomLanguageDetector(alphPath, config);
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(textFile)));
        StringBuilder text = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            text.append(line);
        }
        System.out.println(detector.detect("http://somehost.com", text.toString()));
    }

    public static void initHttpScheme() {
        Scheme http = new Scheme("http", 80, PlainSocketFactory.getSocketFactory());
        crawlerConfig.sr.register(http);
    }

    private static ContentType parseType(String type) {
        String pTypeString = type.contains("/") ? type.split("/")[1] : type;
        pTypeString = pTypeString.contains(";") ? pTypeString.split(";")[0] : pTypeString;
        for (ContentType pType : ContentType.values()) {
            if (pType.toString().equals(pTypeString.toLowerCase().trim())) {
                return pType;
            }
        }
        return ContentType.Unknown;
    }

    public static void generateIds() {
        ids = new String[crawlerConfig.CONCURRENT_DOMAINS];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = "group" + i;
        }
    }

    public static String[] getIds() {
        return ids;
    }

    public static int maxNextDepth() {
        return Integer.valueOf(System.getProperty("twinklebot.nextDepthUrls"));
    }

    public static void rewriteStorageFile(String urlFile, String storagePath) throws Exception {
        SiteStorageState st = new SiteStorageState(storagePath);
        BufferedReader reader = new BufferedReader(new FileReader(urlFile));
        String line;
        List<ObjectToCrawl> lst = new ArrayList<ObjectToCrawl>();
        Set<String> domains = new HashSet<String>();
        Map<String, Float> boostMap = new HashMap<String, Float>();
        while ((line = reader.readLine()) != null) {
            try {
                float boost = 1.0f;
                line = line.startsWith("http") ? line : "http://" + line;
                URL url = new URL(line);
                String correct_domain = url.getProtocol() + "://" + url.getHost();
                if (!domains.contains(correct_domain)) {
                    domains.add(correct_domain);
                    boostMap.put(correct_domain, boost);
                }
            } catch (MalformedURLException ex) {
            }
        }
        for (String domain : domains) {
            ObjectToCrawl cr = new ObjectToCrawl(domain, 2, 2000, 10, CrawlObjectType.Domain, boostMap.get(domain));
            lst.add(cr);
        }
    }

    public static void robotRulesTest(String url) throws MalformedURLException {
        RobotRules rules = RobotRulesFactory.getRobotRules(new URL(url));
    }

}

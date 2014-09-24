package org.tbot;

import org.tbot.parse.ContentType;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.tbot.entity.FetchUrl;
import org.tbot.fetch.FetchRunner;
import org.tbot.fetch.PageMetadata;
import org.tbot.objects.Domain;
import org.tbot.objects.DomainMetadata;
import org.tbot.parse.ParserFactory;
import org.tbot.repository.QueueRepository;
import org.tbot.repository.impl.BlockingMemoryRepository;
import org.tbot.repository.impl.MemorySetRepository;
//#2137
/**
 *
 */
public class Bot {

    public void start() throws MalformedURLException {
        int threads = 10;
        ExecutorService exec = Executors.newCachedThreadPool();
        ParserFactory parserFactory = ParserFactory.build();
        QueueRepository<FetchUrl> urlQueue = new BlockingMemoryRepository<>(2000);
        QueueRepository<Domain> domainQueue = new MemorySetRepository<>(10000);
        Domain d1 = Domain.parse("http://en.wikipedia.org/", new DomainMetadata());
        Domain d2 = Domain.parse("http://apache.org/", new DomainMetadata());
        urlQueue.insert(new FetchUrl("http://en.wikipedia.org/", d1, new PageMetadata(0)));
        urlQueue.insert(new FetchUrl("http://apache.org/", d2, new PageMetadata(0)));
        d1.getMetadata().acquireUrl();
        d2.getMetadata().acquireUrl();
        for (int i = 0; i < threads; i++) {
            Runnable fetcher = new FetchRunner(urlQueue, domainQueue, parserFactory, i + "");
            exec.execute(fetcher);
        }

        CompletionService<Integer> t;
    }

    public static void main(String[] args) throws InterruptedException, MalformedURLException, IOException, Exception {

        new Bot().start();
//        String workingType = args.length == 0 ? "crawler" : args[0];
//        String confDir = "conf";
//        if (System.getProperty("conf") != null) {
//            confDir = System.getProperty("conf");
//        }
//        String propFile = confDir + "/fetcher.properties";
//        loadProperties(propFile);
//        String baseDir = System.getProperty("user.dir");
//        System.out.println("baseDir:  " + baseDir);
//        Bot.crawlerConfig = new CrawlerConfig(System.getProperties());
//        DyatelConfig dConfig = new DyatelConfig(System.getProperties());
//        System.out.println("config dir: " + baseDir + "/" + dConfig.getConfigDir());
//        String log4jProps = System.getProperty("log4j");
//        SearchConfig sConfig = new SearchConfig(System.getProperties());
//        if (log4jProps == null) {
//            log4jProps = baseDir + "/" + dConfig.getConfigDir() + "/log4j.properties";
//            System.getProperties().setProperty("log4j", log4jProps);
//        }
//        LoggerCreator.configure(log4jProps);
//        LoggerCreator.setName("bot");
//
//        String alph = baseDir + "/" + dConfig.getConfigDir() + "/alphabet";
//        String text = baseDir + "/" + dConfig.getConfigDir() + "/testText";
//        String domainFile = baseDir + "/" + dConfig.getConfigDir() + "/domain";
//        //testLanguageDetector(alph, text);
//
//        Bot.logger = LoggerCreator.getLogger();
//        if (System.getProperty("twinklebot.nextDepthUrls") == null) {//wtf??
//            System.setProperty("twinklebot.nextDepthUrls", "4000");
//        }
//        System.out.println(System.getProperty("twinklebot.nextDepthUrls"));
//        String urlPath = baseDir + "/" + dConfig.getConfigDir() + "/urls.txt";
//        String countPath = baseDir + "/" + dConfig.getConfigDir() + "/countTime.txt";
//        Bot.statistics = new Statistics(urlPath, countPath);
//
//        String urlStoragePath = baseDir + "/" + dConfig.getConfigDir();
//        if (System.getProperties().getProperty("twinklebot.urlStorage") != null) {
//            urlStoragePath += "/" + System.getProperties().getProperty("twinklebot.urlStorage");
//        } else {
//            urlStoragePath += "/backupSites1";
//        }
//        System.out.println("storage path: " + urlStoragePath.toLowerCase());
//        logger.info("each fetcher will have pool size: " + Bot.crawlerConfig.FETCH_POOL_SIZE);
//        Bot.initHttpScheme();
//        Bot.generateIds();
//        ProtocolFetcher.build(crawlerConfig);
//        System.out.println(alph);
//        System.out.println(domainFile);
//        System.out.println("starting bot with : " + workingType);
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

    public static int maxNextDepth() {
        return Integer.valueOf(System.getProperty("twinklebot.nextDepthUrls"));
    }

    public static void rewriteStorageFile(String urlFile, String storagePath) throws Exception {
//        SiteStorageState st = new SiteStorageState(storagePath);
//        BufferedReader reader = new BufferedReader(new FileReader(urlFile));
//        String line;
//        List<ObjectToCrawl> lst = new ArrayList<ObjectToCrawl>();
//        Set<String> domains = new HashSet<String>();
//        Map<String, Float> boostMap = new HashMap<String, Float>();
//        while ((line = reader.readLine()) != null) {
//            try {
//                float boost = 1.0f;
//                line = line.startsWith("http") ? line : "http://" + line;
//                URL url = new URL(line);
//                String correct_domain = url.getProtocol() + "://" + url.getHost();
//                if (!domains.contains(correct_domain)) {
//                    domains.add(correct_domain);
//                    boostMap.put(correct_domain, boost);
//                }
//            } catch (MalformedURLException ex) {
//            }
//        }
//        for (String domain : domains) {
//            ObjectToCrawl cr = new ObjectToCrawl(domain, 2, 2000, 10, CrawlObjectType.Domain, boostMap.get(domain));
//            lst.add(cr);
//        }
    }

}

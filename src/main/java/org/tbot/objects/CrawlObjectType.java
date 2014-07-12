package org.tbot.objects;

/**
 *
 */
public enum CrawlObjectType {

    Domain("domain"),
    URL("url");
    private final String type;

    private CrawlObjectType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }

    public static CrawlObjectType getTypeByName(String name) {
        for (CrawlObjectType type : CrawlObjectType.values()) {
            if (name.equals(type.toString())) {
                return type;
            }
        }
        return null;
    }
}

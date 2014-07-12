package org.tbot.objects;

/**
 *
 * Represents the domain state relative to the pool, NotCrawled - when the
 * domain is not crawled Busy - domain is currently crawling Crawled - finished
 * crawling
 */
public enum DomainState {

    NotCrawled("not crawled"),
    Busy("busy"),
    Crawled("crawled");
    private final String st;

    private DomainState(String st) {
        this.st = st;
    }

    @Override
    public String toString() {
        return st;
    }

    public static DomainState getType(String st) {
        for (DomainState ds : DomainState.values()) {
            if (ds.toString().equals(st.toLowerCase().toString())) {
                return ds;
            }
        }
        return null;
    }

}

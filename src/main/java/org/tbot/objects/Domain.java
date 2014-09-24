package org.tbot.objects;

import java.util.Objects;
import org.tbot.fetch.ProtocolType;
import org.tbot.util.HttpUtil;

/**
 *
 * Represents the domain of the site
 * 
*/
public class Domain implements Comparable<Domain> {

    private final ProtocolType protocol;
    private final String domain;
    private DomainMetadata metadata;

    private Domain(ProtocolType protocol, String domain, DomainMetadata metadata) {
        this(protocol, domain);
        this.metadata = metadata;
    }

    private Domain(ProtocolType protocol, String domain) {
        this.protocol = protocol;
        this.domain = domain;
        this.metadata = new DomainMetadata();
    }

    public static Domain parse(String url, DomainMetadata metadata) {
        ProtocolType p = HttpUtil.getProtocol(url);
        String domain = HttpUtil.getDomain(url);
        Domain d = new Domain(p, domain, metadata);
        return d;

    }

    public static Domain parse(String url) {
        ProtocolType p = HttpUtil.getProtocol(url);
        String domain = HttpUtil.getDomain(url);
        Domain d = new Domain(p, domain);
        return d;
    }

    public static Domain buildDomain(ProtocolType protocol, String domainName) {
        return new Domain(protocol, domainName);
    }

    public static Domain buildDomain(ProtocolType protocol, String domainName, DomainMetadata metadata) {
        return new Domain(protocol, domainName, metadata);
    }

    public DomainMetadata getMetadata() {
        return metadata;
    }

    public ProtocolType getProtocol() {
        return protocol;
    }

    public String getDomain() {
        return domain;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 73 * hash + Objects.hashCode(this.protocol);
        hash = 73 * hash + Objects.hashCode(this.domain);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Domain other = (Domain) obj;
        if (this.protocol != other.protocol) {
            return false;
        }
        if (!Objects.equals(this.domain, other.domain)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return protocol.toString() + "://" + domain.toLowerCase();
    }

    @Override
    public int compareTo(Domain o) {
        if (o == null) {
            return -1;
        }
        return toString().compareTo(o.toString());
    }

}

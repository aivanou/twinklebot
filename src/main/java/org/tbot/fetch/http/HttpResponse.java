package org.tbot.fetch.http;

import java.nio.ByteBuffer;
import java.util.Map;
import java.util.Objects;

/**
 *
 */
public class HttpResponse {

    private final int code;
    private final ByteBuffer content;
    private final String textStatus;
    private final Map<String, String> headers;

    public HttpResponse(int code, ByteBuffer content, String textStatus, Map<String, String> headers) {
        this.code = code;
        this.content = content;
        this.textStatus = textStatus;
        this.headers = headers;
    }

    public int getCode() {
        return code;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public ByteBuffer getContent() {
        return content;
    }

    public String getTextStatus() {
        return textStatus;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + this.code;
        hash = 41 * hash + Objects.hashCode(this.content);
        hash = 41 * hash + Objects.hashCode(this.textStatus);
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
        final HttpResponse other = (HttpResponse) obj;
        if (this.code != other.code) {
            return false;
        }
        if (!Objects.equals(this.content, other.content)) {
            return false;
        }
        return Objects.equals(this.textStatus, other.textStatus);
    }

}

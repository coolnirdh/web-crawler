package org.nirdh.apps.webcrawler.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import org.apache.commons.lang3.Validate;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Represents a link found in a web page.
 */
@Data
public class Link {
    @Getter(AccessLevel.NONE) private final String domain;
    private final String url;
    private final String text;

    @JsonCreator
    public Link(
            @JsonProperty("url") String url,
            @JsonProperty("text") String text) {
        Validate.notNull(url, String.format("Url must not be null for link with text: [%s]", text));
        this.url = url.replaceFirst("[?\\/#]+$", "");
        this.text = text;
        try {
            URL parsedUrl = new URL(this.url);
            this.domain = parsedUrl.getHost();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(String.format("Malformed Url [%s] for link with text: [%s]", url, text), e);
        }
    }

    public boolean belongsTo(String domain) {
        return this.domain.endsWith(domain);
    }

    public String getUrlWithoutFragment() {
        return url.replaceFirst("#.*$", "");
    }
}

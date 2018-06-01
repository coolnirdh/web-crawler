package org.nirdh.apps.webcrawler.domain;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.Data;
import org.apache.commons.lang3.Validate;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.IOException;
import java.net.URI;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Represents the information extracted from parsing the contents of a web document.
 * Some information like links is made redundant for read performance,
 * such caching is commonly done when working with key-value stores like Cassandra and Gemfire.
 */
@Data
@Document(indexName = "pages", type = "page")
@JsonDeserialize(using = PageDeserializer.class)
public class Page {
    private final String domain;
    private final String url;
    private final String title;
    private final List<Link> allLinks = new LinkedList<>();
    private final List<Link> internalLinks = new LinkedList<>();
    private final List<Link> externalLinks = new LinkedList<>();

    public Page(String url, String title) {
        Validate.notNull(url, String.format("Url must not be null for page with title: [%s]", title));
        this.url = url;
        this.title = title;
        this.domain = URI.create(url).getHost();
    }

    public void add(Link link) {
        Validate.notNull(link, "Link must not be null");
        allLinks.add(link);
        if (link.belongsTo(domain)) {
            internalLinks.add(link);
        } else {
            externalLinks.add(link);
        }
    }

    public List<Link> getAllLinks() {
        return Collections.unmodifiableList(allLinks);
    }

    public List<Link> getInternalLinks() {
        return Collections.unmodifiableList(internalLinks);
    }

    public List<Link> getExternalLinks() {
        return Collections.unmodifiableList(externalLinks);
    }
}

class PageDeserializer extends StdDeserializer<Page> {

    public PageDeserializer() {
        this(null);
    }

    public PageDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Page deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String url = node.get("url").asText();
        String title = node.get("title").asText();
        Page page = new Page(url, title);
        for (JsonNode linkNode : node.get("allLinks")) {
            url = linkNode.get("url").asText();
            String text = linkNode.get("text").asText();
            page.add(new Link(url, text));
        }
        return page;
    }
}
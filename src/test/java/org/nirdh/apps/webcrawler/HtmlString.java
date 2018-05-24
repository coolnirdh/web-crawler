package org.nirdh.apps.webcrawler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nirdh on 24-05-2018.
 */
public class HtmlString {

    private String title;
    private List<String> links;

    public HtmlString() {
        this.title = "TITLE";
        this.links = new ArrayList<>();
    }

    public HtmlString withTitle(String title) {
        this.title = title;
        return this;
    }

    public HtmlString withLink(String url) {
        links.add(url);
        return this;
    }

    public String build() {
        StringBuilder html = new StringBuilder("<html><head>");
        if (title != null) {
            html.append("<title>").append(title).append("</title>");
        }
        html.append(title).append("</head><body><p>Some text");
        for (String link : links) {
            if (link == null) {
                html.append("<a style=\"\" >Click me</a>");
            } else {
                html.append("<a style=\"\" href=\"").append(link).append("\">Click me</a>");
            }
        }
        return html.append("</p></body></html>").toString();
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.contrib.faqsuck;

import java.io.IOException;
import java.net.URL;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.netbeans.contrib.wikitools.spi.WikiTasks;

/**
 *
 * @author Tim Boudreau
 */
public class Entry {

    public final String url;
    public final String title;
    public final String baseUrl;

    public Entry(String url, String title, String baseUrl) {
        this.url = url;
        this.title = title;
        this.baseUrl = baseUrl;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Entry other = (Entry) obj;
        return !((this.url == null) ? (other.url != null) : !this.url.equals(other.url));
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 73 * hash + (this.url != null ? this.url.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return title + "(" + url + ")";
    }

    public String getName() {
        int ix = url.lastIndexOf("/");
        return url.substring(ix + 1);
    }

    public boolean isExternal() {
        return url.startsWith("http") || url.startsWith("file");
    }

    public String getTocHtml() {
        return "<a href=\"#" + url.substring(1) + "\">" + title + "</a>";
    }

    public String toHtml(WikiTasks wt, FaqsuckMojo mojo) throws IOException {
        StringBuilder sb = new StringBuilder("\n<h4 class=\"itemTitle\" id=\"" + getName() + "\">");
        sb.append(title);
        sb.append("<a href=\"#TOC\" style=\"font-size:small\">Back to top</a>" + "</h4>\n");
        Document document = Jsoup.connect(baseUrl + this.url).get();
        System.err.println("Read " + url);
        sb.append(wt.strip(document, mojo, this));
        return sb.toString();

    }

    public URL getWebUrl() throws IOException {
        if (isExternal()) {
            return new URL(url);
        } else {
            return new URL(baseUrl + url);
        }
    }
   
}

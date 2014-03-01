/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.contrib.faqsuck;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.netbeans.contrib.wikitools.spi.WikiTasks;

/**
 *
 * @author Tim Boudreau
 * 
 * 
 */
public class Category {

    public final String title;
    private final List<Entry> entries = new ArrayList<>();
    private final String id;

    public Category(String title, String id) {
        this.title = title;
        this.id = id;
    }

    public void addEntry(Entry entry) {
        entries.add(entry);
    }

    public List<? extends Entry> entries() {
        return entries;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Category other = (Category) obj;
        return !((this.title == null) ? (other.title != null) : !this.title.equals(other.title));
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.title != null ? this.title.hashCode() : 0);
        return hash;
    }

    @Override
    public String toString() {
        return title;
    }

    public boolean allExternal() {
        boolean result = true;
        for (Entry entry : entries) {
            result &= entry.isExternal();
        }
        return result;
    }

    public String toTocHtml() throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("  <li class=\"tocCategory\"><a href=\"#").append(id).append("\"><b>");
        sb.append(title).append("</b></a>");
        sb.append('\n');

        sb.append("    <ul>\n");
        int ix = 0;
        for (Entry entry : entries) {
            if (!entry.isExternal()) {
                sb.append("    <li>");
                sb.append(entry.getTocHtml());
                sb.append("</li>\n");
            }
            ix++;
            if (ix > FaqUtils.LIMIT) { //for debugging w/o reading all entries
                break;
            }
        }
        sb.append("</ul>\n");
        sb.append("  </li>\n");

        return sb.toString();
    }

    public String toBodyHtml(WikiTasks wt, FaqsuckMojo mojo) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append("<h3 class=\"catTitle\" id=\"").append(id).append("\">");
        sb.append(title);
        sb.append("</h3>");
        int ix = 0;
        for (Entry entry : entries) {
            if (!entry.isExternal()) {
                sb.append(entry.toHtml(wt, mojo));
                sb.append('\n');
                sb.append('\n');
            }
            ix++;
            if (ix > FaqUtils.LIMIT) { //for debugging w/o reading all entries
                break;
            }
        }
        return sb.toString();
    }

}

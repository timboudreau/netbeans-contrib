/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package faqsuck;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Tim Boudreau
 */
public class Entry {
    public final String url;
    final String title;
    private final String baseUrl;

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
        if ((this.url == null) ? (other.url != null) : !this.url.equals(other.url)) {
            return false;
        }
        return true;
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
        return url.startsWith ("http") || url.startsWith("file");
    }

    public String getTocHtml() {
        return "<a href=\"#" + url.substring(1) + "\">" + title + "</a>";
    }

    public String toHtml() throws IOException {
        StringBuilder sb = new StringBuilder("\n<h4 class=\"itemTitle\"><a name=\"" + getName() + "\">");
        sb.append (title);
        sb.append ("</a></h4>\n");
        URL url = new URL(baseUrl + this.url);
        System.err.println("Read " + url);
        InputStream in = url.openStream();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Main.copy (in, out);
            String content = new String (out.toByteArray());
            sb.append(strip(content));
            return sb.toString();
        } finally {
            in.close();
        }
    }

    public URL getWebUrl() throws IOException {
        if (isExternal()) {
            return new URL(url);
        } else {
            return new URL( baseUrl + url);
        }
    }

    private static Pattern CONTENT_PATTERN = Pattern.compile ("<span class=\"mw-headline\".*?</h[123456]>(.*)<!--\\s*?NewPP limit", Pattern.MULTILINE | Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.UNIX_LINES);
    private String strip(String content) throws IOException {
        Matcher m = CONTENT_PATTERN.matcher(content);
        if (m.find()) {
            String result = m.group(1);
            result = result.replaceAll ("<a href=\"/", "<a href=\"#");
            result = result.replaceAll ("<img.*?>", "<a href=\"" + getWebUrl() + "\" target=\"ext\"><i>[image - see online version]</i></a>");
            result = result.replaceAll ("</img>", "");
            result = result.replaceAll ("<h[123456]>", "<h5>");
            result = result.replaceAll ("</h[123456]>", "</h5>");
            result = result.replaceAll ("<hr\\s*?/>", "");

            int ix = result.indexOf("<p><b><span style=\"color:#ff6633;\">Attachments</span></b>");
            if (ix > 0) {
                int newIx = result.lastIndexOf("<div", ix);
                ix = newIx < 0 ? ix : newIx;
                result = result.substring (0, ix);
            }

            return "<div class=\"itemBody\">"+ result + "\n</div>";
        } else {
            return "[no content found]";
        }
    }

    /**
     * for testing parsing
     * @param args
     * @throws Exception
     */
    public static void main(String[] ignored) throws Exception {
        String url = "http://wiki.netbeans.org/NetBeansCertifiedEngineerCourse";
        String title = "Becoming Proficient";
//        Entry e = new Entry ("file:///H:/oneFaq.html", title, "");
//        Entry e = new Entry ("http://wiki.netbeans.org/NetBeansCertifiedEngineerCourse", title, "");
//        Entry e = new Entry ("http://wiki.netbeans.org/DevFaqHowToReuseModules", title, "");
        Entry e = new Entry ("http://wiki.netbeans.org/DevFaqSpecifyJdkVersion", title, "");
        System.out.println(e.toHtml());
    }

}

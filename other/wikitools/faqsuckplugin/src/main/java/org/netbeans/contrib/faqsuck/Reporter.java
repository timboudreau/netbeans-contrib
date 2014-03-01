/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.contrib.faqsuck;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.apache.maven.doxia.sink.Sink;

/**
 * Manage a web page to help seeking glitch in wiki
 *
 *
 */
public class Reporter {

    private static final Map<String, List<String>> allURL = new TreeMap<>();
    private static final Map<String, List<String>> apidoc = new TreeMap<>();
    private static final Map<String, List<String>> wikinb = new TreeMap<>();
    private static final Map<String, List<String>> mercurialnb = new TreeMap<>();
    private static final Map<String, List<String>> bugzillanb = new TreeMap<>();

    private static Sink out1;
    private static String baseUrl;

    static void printHead() {
        out1.head();
        out1.title();
        out1.text("NetBeans Developer FAQ Log");
        out1.title_();
        out1.head_();
        out1.body();
    }

    static void setup(Sink printStream, String b) {
        out1 = printStream;
        baseUrl = b;
    }

    static void addHeader(String sues) {
        out1.sectionTitle2();
        out1.text(sues);
        out1.sectionTitle2_();
    }

    static void displayBadWiki(List<Entry> list) {
        out1.list();
        for (Entry e : list) {
            if (e.url.contains("wiki.netbeans.org")) {
                out1.listItem();
                out1.text("In page ");
                out1.link(e.url);
                out1.text(e.title);
                out1.link_();
                out1.text(e.url);
                out1.listItem_();
            }
        }
        out1.list_();

    }

    static void displayLinks() {
        displayLinks(allURL, "all links");
        displayLinks(apidoc, "api doc links");
        displayLinks(wikinb, "nb wiki links");
        displayLinks(mercurialnb, "mercurial nb links");
        displayLinks(bugzillanb, "bugzilla nb links");
    }

    static void printFooter() {
        out1.body_();
    }

    static void internaladdURL(String attr, String title, Map<String, List<String>> m) {
        if (!m.containsKey(attr)) {
            m.put(attr, new ArrayList());
        }
        if (!m.get(attr).contains(title)) {
            m.get(attr).add(title);
        }
    }

    public static void wrongWikiLink(String url, String title, String text) {
        out1.paragraph();
        out1.text("In ");
        out1.link(baseUrl + url);
        out1.text(text);
        out1.link_();
        out1.text(" wiki link must be [[]] " + text);
        out1.paragraph_();
    }

    public static void addURL(String attr, String title, String related) {
        if (attr.isEmpty()) {
            return;
        }

        String second = title + " <a href=\"" + baseUrl + related + "\">" + related + "</a>";
        if (attr.startsWith("http://bits.netbeans.org/dev/javadoc")) {
            internaladdURL(attr, second, apidoc);
            return;
        }
        // wiki is http
        if (attr.startsWith("http://wiki.netbeans.org")) {
            internaladdURL(attr, second, wikinb);
            return;
        }
        // both https are valid
        if (attr.startsWith("http://hg.netbeans.org") || attr.startsWith("https://hg.netbeans.org")) {
            internaladdURL(attr, second, mercurialnb);
            return;
        }
        // https only
        if (attr.startsWith("https://netbeans.org/bugzilla")) {
            internaladdURL(attr, second, bugzillanb);
            return;
        }
        internaladdURL(attr, second, allURL);

    }

    /* static void println(String string) {
     out1.println(string);
     }*/
    private static void displayLinks(Map<String, List<String>> apidoc, String title) {
        Reporter.addHeader(title);
        out1.table();
        for (Map.Entry<String, List<String>> e : apidoc.entrySet()) {
            out1.tableRow();
            out1.tableCell();
            out1.text(e.getKey());
            out1.tableCell_();
            out1.tableCell();
            for (String s : e.getValue()) {
                out1.text(" " + s);
                out1.lineBreak();
            }
            out1.tableCell_();
            out1.tableRow_();
        }
        out1.table_();

    }

}

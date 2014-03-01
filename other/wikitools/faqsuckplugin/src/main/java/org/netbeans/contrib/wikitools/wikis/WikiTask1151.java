/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.contrib.wikitools.wikis;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jsoup.nodes.Comment;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.netbeans.contrib.faqsuck.Category;
import org.netbeans.contrib.faqsuck.Entry;
import org.netbeans.contrib.faqsuck.FaqUtils;
import org.netbeans.contrib.faqsuck.FaqsuckMojo;
import org.netbeans.contrib.faqsuck.Reporter;
import org.netbeans.contrib.wikitools.spi.WikiTasks;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Eric
 */
@ServiceProvider(service = WikiTasks.class)
public class WikiTask1151 implements WikiTasks {

    @Override
    public String getVersion() {
        return "MediaWiki 1.15.1";
    }

    @Override
    public void feedCategory(URL baseUrl, Document tocContent, List<Category> categories, FaqsuckMojo mojo) {
        Elements toc = tocContent.select("span.mw-headline");
        int ix = 0;
        for (Element e : toc) {
            Element aa = e.parent().previousElementSibling();

            Category category = null;
            if (ix < FaqUtils.LIMIT) { //for debugging w/o reading all entries
                category = new Category(e.text(), aa.id());
                categories.add(category);
            }
            ix++;
            Element ul = e.parent().nextElementSibling();
            Elements items = ul.select("li");
            int ixx = 0;
            for (Element item : items) {
                Elements anchros = item.select("a");
                if (anchros.size() > 1) {
                    mojo.getLog().debug("<p>Category: <i>" + e.text() + "</i> contains more than one anchor for item: <b>" + item.text() + "</b></p>");
                }
                for (Element a : anchros) {
                    if (a.attr("href").startsWith("/")) {
                        mojo.getLog().debug("Main.knownurl.add(a.attr(\"href\"));" + a.attr("href"));
                        mojo.addTOCURL(a.attr("href"));

                    }

                    if (ixx < FaqUtils.LIMIT && category != null) { //for debugging w/o reading all entries
                        category.addEntry(new Entry(a.attr("href"), a.text(), baseUrl.toExternalForm()));
                        // break;
                    }
                    ixx++;
                }

            }

        }
    }
    private final List<String> selectorForRemoval = Arrays.asList(".printfooter", ".firstHeading", "#contentSub", "#toc", "hr");
    private final List<String> selectorForParentRemoval = Arrays.asList(".mw-headline");

    private void removeElements(Element e) {
// candidate for deletion
        List<Node> nn = new ArrayList<>();
        Elements a = e.select("a");
        for (Element aa : a) {
            // remove anchor that have a name inside
            if ((aa.attr("name").equals(aa.attr("id")) && (!aa.attr("id").isEmpty()))) {
                nn.add(aa);
            }
        }

        for (String selector : selectorForRemoval) {
            nn.addAll(e.select(selector));
        }
        for (String selector : selectorForParentRemoval) {
            for (Element printfoot : e.select(selector)) {
                nn.add(printfoot.parent());

            }
        }
        for (Node n : e.childNodes()) {
            // remove comment block to save space
            if (n instanceof Comment) {
                nn.add(n);
            }

        }
        for (Node n : nn) {
            n.remove();
        }

    }

    @Override
    public String strip(Document content, FaqsuckMojo mojos, Entry ent) {
        // Netbeans template #content
        Element e = content.select("#content").first();
        removeElements(e);
        // href managment
        Elements a = e.select("a");
        for (Element aa : a) {

            //  considers all remainings link  (except image) as external and assure that they oppen new page or tab
            if (!aa.hasClass("image")) {
                aa.addClass("external");
            }
            aa.attr("target", "_blank");

            // Not well formatted wiki must use [[]] instead  []
            if (aa.attr("href").contains("wiki.netbeans.org")) {
                Reporter.wrongWikiLink(ent.url, ent.title, aa.text());
            }
            // Start with / => local wiki link
            if (aa.attr("href").startsWith("/")) {
                if (mojos.getKnownUrl().contains(aa.attr("href"))) {
                    // its a know internal url replace to navigate in the same file
                    aa.attr("href", aa.attr("href").replaceFirst("/", "#"));
                    aa.removeClass("external");
                    aa.removeAttr("target");
                } else {
                    // wiki netbeans css class
                    aa.addClass("wnetbeans");
                    aa.attr("href", ent.baseUrl + aa.attr("href"));

                }
            }
            // 
            if (aa.attr("href").startsWith("http://bits.netbeans.org/dev/javadoc")) {
                aa.addClass("jdocnetbeans");
            }
            if (!aa.attr("href").startsWith("#")) {
                Reporter.addURL(aa.attr("href"), aa.text(), ent.url);

            }
        }
        // all headers => becomes h5 
        for (int i = 1; i < 7; i++) {
            Elements headers = e.select("h" + i);
            for (Element header : headers) {
                header.replaceWith(new Element(Tag.valueOf("h5"), "").html(header.html()));
            }
        }
        // all tt => becomes span with monospace font
        Elements truetype = e.select("tt");
        for (Element element : truetype) {
            element.replaceWith(new Element(Tag.valueOf("span"), "").addClass("trt").html(element.html()));
        }
        // img removal + link inclusion
        Elements images = e.select(".image");
        for (Element image : images) {
            try {
                image.replaceWith(new Element(Tag.valueOf("a"), "").attr("target", "_blank").attr("href", ent.getWebUrl().toString()).html("[image - see online version]"));
            } catch (IOException ex) {
                Logger.getLogger(WikiTask1151.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        e.removeAttr("id");
        return e.outerHtml();
    }

}

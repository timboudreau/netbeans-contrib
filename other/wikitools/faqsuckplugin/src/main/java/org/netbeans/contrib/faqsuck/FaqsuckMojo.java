/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.contrib.faqsuck;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import org.apache.maven.doxia.sink.Sink;
import org.apache.maven.doxia.siterenderer.Renderer;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.AbstractMavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.netbeans.contrib.wikitools.spi.WikiTasks;

/**
 * main algorithm is inspired by previous work by Tim Boudreau
 *
 * @author Eric
 */
@Mojo(name = "faqsuck", requiresProject = false)
public class FaqsuckMojo extends AbstractMavenReport {

    /**
     * The greeting to display.
     */
    @Parameter(required = true)
    private URL mainURL;

    /**
     * The greeting to display.
     */
    @Parameter(required = true)
    private URL faqTOC;

    /**
     * The greeting to display.
     */
    @Parameter(required = true)
    private File faqFile;

    @Component
    protected Renderer siteRenderer;

    private Document tocDocument;

    // List of url in the toc
    private List<String> knownurl;
    PrintStream outfaq;
    @Parameter(defaultValue = "${project.reporting.outputDirectory}", required = true)
    private File outputDirectory;

    @Override
    public String getOutputName() {
        return "Netbeans Dev Report";
    }

    @Override
    public void execute() throws MojoExecutionException {
        try {
            knownurl = new ArrayList<>();
            tocDocument = Jsoup.connect(faqTOC.toExternalForm()).get();
            faqFile.getParentFile().mkdirs();
            Sink sink = getSink();
            Reporter.setup(sink, mainURL.toExternalForm());
            outfaq = new PrintStream(new BufferedOutputStream(new FileOutputStream(faqFile)));
            readCategories(tocDocument);

        } catch (IOException ex) {
            throw new MojoExecutionException("", ex);
        } finally {
            outfaq.close();
        }
        getLog().info("faqsuck file created in " + faqFile.toString());
    }

    private void readCategories(Document tocDocument) throws MojoExecutionException, IOException {

        List<Category> categories = new ArrayList<>();
        getLog().info("reading categories");
        readCategories(mainURL, tocDocument, categories);
        WikiTasks wikitask = FaqUtils.getWikiTask(tocDocument, this);
        wikitask.feedCategory(mainURL, tocDocument, categories, this);
        Reporter.printHead();
        outfaq.println(HEAD);
        outfaq.println("<span class=\"generated\"><i>Generated on " + new Date() + "</i>"
                + " from <a href=\"" + faqTOC.toExternalForm() + "\">" + faqTOC.toExternalForm() + "</a>"
                + "</span><p>&nbsp;</p>");
        outfaq.println("<ul id=\"TOC\">");
        Reporter.addHeader("Issues");
        int ix = 0;
        for (Category c : categories) {
            if (!c.allExternal()) {
                outfaq.println(c.toTocHtml());
            }
            ix++;
            if (ix > FaqUtils.LIMIT) { //for debugging w/o reading all entries
                break;
            }
        }
        outfaq.println("</ul>\n");
        outfaq.println("\n<hr/><p>&nbsp;</p>\n");
        ix = 0;
        for (Category c : categories) {
            if (!c.allExternal()) {
                outfaq.println(c.toBodyHtml(wikitask, this));
            }
            ix++;
            if (ix > FaqUtils.LIMIT) { //for debugging w/o reading all entries
                break;
            }
        }

        List<Entry> list = new ArrayList<>();
        for (Category c : categories) {
            for (Entry e : c.entries()) {
                if (e.isExternal()) {
                    list.add(e);
                }
            }
        }
        if (!list.isEmpty()) {
            outfaq.println("<p>&nbsp;</p>");
            outfaq.println("<h1>External FAQ Entries</h1>");
            outfaq.println("The following FAQ entries are not hosted in the NetBeans wiki and could not be included:<ul>");
            Reporter.displayBadWiki(list);
            for (Entry e : list) {
                outfaq.println("<li><a target=\"extfaq\" href=\"" + e.url + "\">" + e.title + "</a></li>");
            }

            outfaq.println("</ul>");
        }

        Reporter.displayLinks();

        outfaq.println(FOOT);
        Reporter.printFooter();
    }

    private void readCategories(URL baseUrl, Document tocContent, List<Category> categories) throws MojoExecutionException, IOException {

    }

    private static final String HEAD = "<!DOCTYPE html>\n"
            + "<html>\n"
            + " <head>\n"
            + "  <title>NetBeans Developer FAQ</title>\n"
            + "  <style type=\"text/css\">\n"
            + "    body { margin-left: 1.25em;  margin-right: 1.25em;font-family:\"Verdana\",\"sans-serif\";}\n"
            + "    li {margin-top:0.45em;}\n"
            + "    .catTitle {background-color:#AA3322; color:#FFFFFF; border-color:#AA3322; border-style:solid; border-width:0.37em; margin-top:1em; font-size:1.7em;}\n"
            + "    .itemTitle {color:#AA3322; border-bottom-style:solid; border-bottom-width:2px; border-bottom-color:#DDAA88; margin-top:0.75em; font-size:1.45em;}\n"
            + "    .itemBody {margin-left:1.5em;  margin-right:1.5em;}\n"
            + "    .tocCategory {margin-top: 1.1em; margin-bottom:0.4em;}\n"
            + "    pre { background-color:#FFFFD9; overflow:auto; border: 1px solid #CCCCCC;}\n"
            + "    .generated { font-size:small}\n"
            + "    a.external:after {content:\"\\21D7\";color: #AA3322;font-size:x-small;}\n"
            + "    .trt{font-family: monospace;}\n"
            + "    a.wnetbeans:after{content:\"\\21D7 Netbeans Wiki\";}\n"
            + "    a.jdocnetbeans:after{content:\"\\21D7 APIdoc\";}\n"
            + "    a:link {text-decoration:none;}\n"
            + "    a:visited {text-decoration:none;}\n"
            + "  </style>\n"
            + " </head>\n"
            + "<body>\n"
            + " <h1>NetBeans Developer Faq</h1>";
    private static final String FOOT = "</body>\n"
            + "</html>";

    public void addTOCURL(String attr) {
        knownurl.add(attr);
    }

    public List<String> getKnownUrl() {
        return knownurl;
    }

    @Override
    protected Renderer getSiteRenderer() {
        return siteRenderer;
    }

    @Override
    protected String getOutputDirectory() {
        return outputDirectory.getAbsolutePath();
    }

    @Override
    protected MavenProject getProject() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected void executeReport(Locale locale) throws MavenReportException {
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }
        try {
            this.execute();
        } catch (MojoExecutionException ex) {
            throw new MavenReportException(ex.getMessage(), ex);
        }
    }

    @Override
    public String getName(Locale locale) {
        return "faqsuck";
    }

    @Override
    public String getDescription(Locale locale) {
        return "faq suck description";
    }
}

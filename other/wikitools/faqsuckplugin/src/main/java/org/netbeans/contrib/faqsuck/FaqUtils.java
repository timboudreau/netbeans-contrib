/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.contrib.faqsuck;

import org.apache.maven.plugin.MojoExecutionException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.netbeans.contrib.wikitools.spi.WikiTasks;
import org.openide.util.Lookup;

/**
 *
 * @author Eric
 */
public class FaqUtils {

    private FaqUtils() {
    }
    public static final int LIMIT = Integer.MAX_VALUE; //set to 3 during development

    static WikiTasks getWikiTask(Document doc, FaqsuckMojo mojo) throws MojoExecutionException {
        Elements metaElements = doc.select("meta");
        String version = "no";
        for (Element e : metaElements) {
            if (e.attr("name").equals("generator")) {
                version = e.attr("content");
                for (WikiTasks wt : Lookup.getDefault().lookupAll(WikiTasks.class)) {
                    if (wt.getVersion().equals(version)) {
                        return wt;
                    }
                }
            }
        }
        throw new MojoExecutionException("No wiki task for wiki " + version);
    }

    
}

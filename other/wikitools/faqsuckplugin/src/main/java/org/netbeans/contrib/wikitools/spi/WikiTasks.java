/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.contrib.wikitools.spi;

import java.net.URL;
import java.util.List;
import org.jsoup.nodes.Document;
import org.netbeans.contrib.faqsuck.Category;
import org.netbeans.contrib.faqsuck.Entry;
import org.netbeans.contrib.faqsuck.FaqsuckMojo;

/**
 *
 */
public interface WikiTasks {

    /**
     * Wiki version string
     * @return wiki version as in meta
     */
    String getVersion();

    /**
     * 
     * @param baseUrl
     * @param tocContent
     * @param categories
     * @param mojo 
     */
    void feedCategory(URL baseUrl, Document tocContent, List<Category> categories, FaqsuckMojo mojo);

    /**
     * 
     * @param doc
     * @param mojos
     * @param ent
     * @return 
     */
    String strip(Document doc, FaqsuckMojo mojos, Entry ent);
}

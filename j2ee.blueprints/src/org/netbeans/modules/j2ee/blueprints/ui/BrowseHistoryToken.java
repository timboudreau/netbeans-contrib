/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.j2ee.blueprints.ui;

/**
 * Encapsulates information about where the user was browsing at the time
 * the view changed.
 *
 * @author Mark Roth
 */
public class BrowseHistoryToken {
    
    private String category;
    private String article;
    // Use constants defined in BluePrintsPanel
    private String tab;
    private int scrollPosition;
    
    public BrowseHistoryToken() {
    }
    
    public BrowseHistoryToken(String category, String article, String tab, 
        int scrollPosition) 
    {
        this.category = category;
        this.article = article;
        this.tab = tab;
        this.scrollPosition = scrollPosition;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public String getTab() {
        return tab;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }
    
    public int getScrollPosition() {
        return scrollPosition;
    }

    public void setScrollPosition(int scrollPosition) {
        this.scrollPosition = scrollPosition;
    }
    
    public String toString() {
        return "[BrowseHistoryToken;"
            + "category='" + category
            + "',article='" + article
            + "',tab='" + tab
            + "',scrollPosition='" + scrollPosition
            + "']";
    }
}

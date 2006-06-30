/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

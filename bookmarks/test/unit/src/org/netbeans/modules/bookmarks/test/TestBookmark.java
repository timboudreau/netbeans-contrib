/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2003 Nokia.
 * All Rights Reserved.
 */

package org.netbeans.modules.bookmarks.test;

import javax.swing.AbstractAction;
import org.netbeans.api.bookmarks.Bookmark;

/** 
 * Custom implementation of the Bookmark interface.
 */
public class TestBookmark extends AbstractAction implements Bookmark, java.io.Serializable {
    
    private static final long serialVersionUID = 1L;
    
    private String name;
    
    public TestBookmark() {}
    
    public TestBookmark(String name) {
        this.name = name;
        putValue(NAME, name);
    }
    
    public java.awt.Component getToolbarPresenter() {
        return getMenuPresenter();
    }
    
    public javax.swing.JMenuItem getMenuPresenter() {
        return new javax.swing.JMenuItem("test Item");
    }
    
    public String getName() {
        return name;
    }
    
    public void invoke() {
    }
    
    public void actionPerformed(java.awt.event.ActionEvent e) {
    }    
    
}


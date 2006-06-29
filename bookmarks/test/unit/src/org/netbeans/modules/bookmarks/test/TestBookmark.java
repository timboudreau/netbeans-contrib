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
 * Software is Nokia. Portions Copyright 2003 Nokia.
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
    
    public void setName(String newName) {
        String oldValue = this.name;
        this.name = newName;
        firePropertyChange("name", oldValue, newName);
    }

    public void firePropertyChange(String name, Object oldValue, Object newValue) {
        super.firePropertyChange(name, oldValue, newValue);
    }    
}


/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2005 Nokia.
 * All Rights Reserved.
 */
package org.netbeans.modules.bookmarks;

import java.awt.EventQueue;
import org.openide.windows.TopComponent;

/**
 * Special case for bookmarking the Manager bookmark tool itself
 * @author David Strupl
 */
public class ManageBookmarksBookmarkImpl extends BookmarkImpl {
    
    /** Creates a new instance of ManageBookmarksBookmarkImpl */
    public ManageBookmarksBookmarkImpl() {
        putValue(NAME, ""); // NOI18N - tmp value
        Runnable r = new Runnable() {
            public void run() {
                ManageBookmarksTool mbt = ManageBookmarksTool.getInstance();
                putValue(NAME, mbt.getDisplayName());
            }
        };
        if (EventQueue.isDispatchThread()) {
            r.run();
        } else {
            EventQueue.invokeLater(r);
        }
    }
    
    public TopComponent getTopComponent() {
        return ManageBookmarksTool.getInstance();
    }
    
    public String getName() {
        return ManageBookmarksTool.getInstance().getDisplayName();
    }
    
    void readProperties(java.util.Properties p) {
    }
    
    void writeProperties(java.util.Properties p) {
    }
    
    public Object clone() throws CloneNotSupportedException {
        BookmarkImpl res = new ManageBookmarksBookmarkImpl();
        return res;
    }
    
}

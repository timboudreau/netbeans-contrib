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
 * Software is Nokia. Portions Copyright 2005 Nokia.
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

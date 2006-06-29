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
package org.netbeans.modules.bookmarks.actions;

import javax.swing.Action;
import javax.swing.AbstractAction;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.nodes.NodeOperation;

import org.netbeans.modules.bookmarks.*;
import org.openide.windows.Mode;
import org.openide.windows.WindowManager;

/**
 * This action opens an explorer allowing to customize the contents
 * of the bookmarks folder.
 * @author David Strupl
 */
public class ManageBookmarksAction extends AbstractAction implements HelpCtx.Provider {

    /** Default constructor. */
public ManageBookmarksAction() {
        putValue(Action.NAME, getName());
    }
    
    /**
     * @returns localized name for the action
     */
    public String getName() {
        return NbBundle.getBundle(AddBookmarkAction.class).getString("ManageBookmarks");
    }
    
    /**
     * Method implementint interface HelpCtx.Provider.
     * The ID for the help is created from the class name of this class.
     */
    public HelpCtx getHelpCtx() {
        return new HelpCtx(ManageBookmarksAction.class);
    }    
    
    /**
     * Open BookmarksRootNode in an explorer.
     */
    public void actionPerformed(java.awt.event.ActionEvent e) {
        ManageBookmarksTool t = ManageBookmarksTool.getInstance();
        if (t.isOpened()) {
            t.requestActive();
            return;
        }
        Mode target = WindowManager.getDefault().findMode("explorer");
        if (target != null) {
            target.dockInto(t);
        }
        t.open();
        t.requestActive();
    }
}

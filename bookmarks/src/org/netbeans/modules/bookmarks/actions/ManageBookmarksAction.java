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
package org.netbeans.modules.bookmarks.actions;

import javax.swing.Action;
import javax.swing.AbstractAction;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.nodes.NodeOperation;

import org.netbeans.modules.bookmarks.*;

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
            NodeOperation.getDefault().explore(new BookmarksRootNode());
    }
}

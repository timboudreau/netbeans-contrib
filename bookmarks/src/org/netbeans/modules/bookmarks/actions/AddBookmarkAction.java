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
import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

import org.openide.windows.*;

import org.netbeans.api.bookmarks.*;
import org.netbeans.modules.bookmarks.*;

/**
 * An action for the main menu bar. It finds the activated
 * top component, creates a deafult bookmark and stores it
 * with the BookmarkService.
 * @author David Strupl
 */
public class AddBookmarkAction extends AbstractAction implements HelpCtx.Provider {
    
    /**
     * Default constructor.
     */
    public AddBookmarkAction() {
        putValue(Action.NAME, getName());
        putValue(Action.SMALL_ICON, getIcon());
    }
    
    /**
     * @returns localized name for the action
     */
    public String getName() {
        return NbBundle.getBundle(AddBookmarkAction.class).getString("AddBookmark");
    }
    
    /**
     * @returns icon for the action
     */
    public Icon getIcon() {
        return new ImageIcon(Utilities.loadImage("org/netbeans/modules/bookmarks/resources/add.gif"));
    }

    /**
     * Method implementint interface HelpCtx.Provider.
     * The ID for the help is created from the class name of this class.
     */
    public HelpCtx getHelpCtx() {
        return new HelpCtx(AddBookmarkAction.class);
    }    
    
    /**
     * Main method for the action. Stores the created
     * bookmark.
     */
    public void actionPerformed(java.awt.event.ActionEvent e) {
        WindowManager wm = WindowManager.getDefault();
        TopComponent tc = wm.getRegistry().getActivated();
        BookmarkService bs = BookmarkService.getDefault();
        bs.storeBookmark(bs.createDefaultBookmark(tc));
    }
}

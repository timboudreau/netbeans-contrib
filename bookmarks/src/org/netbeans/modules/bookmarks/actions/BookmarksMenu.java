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

import javax.swing.JMenu;
import javax.swing.JMenuItem;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.ErrorManager;
import org.openide.util.actions.Presenter;

import org.netbeans.modules.bookmarks.*;
/**
 * This is the item in the top level menu. Implements suprisingly
 * Presenter.Toolbar because that is the thing that is added to the
 * top level menu (unlike submenus which must implement Presenter.Menu).
 * @author David Strupl
 */
public class BookmarksMenu implements Presenter.Toolbar, HelpCtx.Provider {
    
    /**
     * @returns localized name for the action
     */
    public String getName() {
        return NbBundle.getBundle(BookmarksMenu.class).getString("Bookmarks");
    }
    
    /**
     * Method implementint interface HelpCtx.Provider.
     * The ID for the help is created from the class name of this class.
     */
    public HelpCtx getHelpCtx() {
        return new HelpCtx(BookmarksMenu.class);
    }   

    /**
     * Method implementing interface Presenter.Toolbar. Creates two fixed
     * items for the menu: Add Bookmark and Manager Bookmarks. The rest
     * of this menu is created dynamically by MenuFromFolder.
     */
    public java.awt.Component getToolbarPresenter() {
        JMenuItem[] fixed = new JMenuItem[] {
            new JMenuItem(new AddBookmarkAction()),
            new JMenuItem(new ManageBookmarksAction()),
        };
        JMenu jm = new MenuFromFolder(BookmarkServiceImpl.BOOKMARKS_FOLDER, fixed).getMenu();
        jm.setText(getName());
        return jm;
    }    
}

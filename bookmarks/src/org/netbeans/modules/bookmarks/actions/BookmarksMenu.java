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

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.awt.Mnemonics;

import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.ErrorManager;
import org.openide.awt.Actions;
import org.openide.util.actions.Presenter;

import org.netbeans.modules.bookmarks.*;
/**
 * This is the item in the top level menu. Implements suprisingly
 * Presenter.Toolbar because that is the thing that is added to the
 * top level menu (unlike submenus which must implement Presenter.Menu).
 * @author David Strupl
*/
public class BookmarksMenu implements Presenter.Toolbar, HelpCtx.Provider {
    
    private static BookmarksMenu instance = new BookmarksMenu();
    private JMenu myToolbarPresenter = null;
    
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
        if (myToolbarPresenter != null) {
            return myToolbarPresenter;
        }
        JMenuItem addBookmarkItem = new JMenuItem();
        Actions.connect(addBookmarkItem, new AddBookmarkAction(), false);
        JMenuItem manageBookmarkItem = new JMenuItem();
        Actions.connect(manageBookmarkItem, new ManageBookmarksAction(), false);
        JMenuItem[] fixed = new JMenuItem[] {
            addBookmarkItem,
            manageBookmarkItem,
        };
        myToolbarPresenter = new MenuFromFolder(BookmarkServiceImpl.BOOKMARKS_FOLDER, fixed).getMenu();
        Mnemonics.setLocalizedText(myToolbarPresenter, getName());
        return myToolbarPresenter;
    }   

    /** 
     * To be called from the layer file.
     */
    public static BookmarksMenu getInstance() {
        return instance;
    }
}

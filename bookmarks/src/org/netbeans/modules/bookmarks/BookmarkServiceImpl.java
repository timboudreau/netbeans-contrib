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
package org.netbeans.modules.bookmarks;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.*;
import javax.naming.*;

import org.openide.ErrorManager;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

import org.netbeans.api.bookmarks.*;

/**
 * Implementation of the BookmarkService. Stores the bookmarks in
 * folder BOOKMARKS_FOLDER and the top components in TOP_COMPONENTS_FOLDER.
 * @author David Strupl
 */
public class BookmarkServiceImpl extends BookmarkService {
    
    /** Folder name on the system file system where the
     * user bookmarks are stored
     */
    public static final String BOOKMARKS_FOLDER = "Bookmarks/Bookmarks";
    
    /**
     * Folder for storing the TopComponents referenced from the bookmarks
     */
    public static final String TOP_COMPONENTS_FOLDER = "Bookmarks/TopComponents";
    
    /** Folder name on the system file system where the
     * user bookmarks are stored
     */
    public static final String BOOKMARKS_TOOLBAR = "Toolbars/Bookmarks";
    
    /** Cache of the initial context */
    private static Context incon;
    
    /** This constructor is public for the META-INF/services lookup
     * to be able to instantiate this service
     */
    public BookmarkServiceImpl() {
    }
    
    /**
     * By invoking this method you are asking to store the bookmark
     * in the storage maintained by the service. It means that after
     * calling this method your bookmark will appear in the bookmarks menu
     * (or in whatever visual presentation the bookmarks module uses).
     * After the bookmark is stored the user will be able to invoke
     * your bookmark - the method invoke will be called on it
     * after the user clicks the menu or toolbar representation of
     * the bookmark. <p> Example: you want to create and store a bookmark
     * for your TopComponent tc you can use: <pre>
     * BookmarkService.getDefault().storeBookmark(b);
     * </pre>. Where b is either your own implementation of the interface
     * Bookmark or result of calling method createDefaultBookmark. 
     * This or similar code is used in the action provided by
     * the module in the main menu bar called "Add Bookmark".
     * @see Bookmark
     */
    public void storeBookmark(Bookmark b) {
        try {
            Context c = getInitialContext();
            Object folder = c.lookup(BOOKMARKS_FOLDER);
            Object tcFolder = c.lookup(TOP_COMPONENTS_FOLDER);
            if ((folder instanceof Context) && (tcFolder instanceof Context)){
                Context targetFolder = (Context)folder;
                Context targetTcFolder = (Context)tcFolder;
                String safeName = findUnusedName(targetFolder, b.getName());
                // and top component to the tc folder
                if (b instanceof BookmarkImpl) {
                    BookmarkImpl bi = (BookmarkImpl)b;
                    targetTcFolder.bind(safeName, bi.getTopComponent());
                    bi.setTopComponentFileName(safeName);
                }
                // following line will save the bookmark to the system file system
                targetFolder.bind(safeName, b);
            }
        } catch (Exception x) {
            ErrorManager.getDefault().notify(x);
        }
    }
    
    /**
     * This method creates a Bookmark object saving the state of the
     * supplied TopComponent. Invoking such a bookmark means to open
     * a clone of the TopComponent.
     * @returns default implementation of the Bookmark interface
     */
    public Bookmark createDefaultBookmark(TopComponent tc) {
        BookmarkProvider bp = (BookmarkProvider)tc.getLookup().lookup(BookmarkProvider.class);
        Bookmark b = null;
        if (bp != null) {
            b = bp.createBookmark();
        }
        if (b == null) {
            b = new BookmarkImpl(tc);
        }
        return b;
    }
    
    /**
     * Tries to find unused name in the specified context by
     * trying to append a number to the specified name.
     */
    public static String findUnusedName(Context targetFolder, String name) {
        String result = name;
        try {
            int i = 0;
            while (true) {
                targetFolder.lookup(result);
                result = name + i++;
            }
        } catch (NamingException ne) {
            // if the name is not found --> fine, we can return it
            return result;
        }
    }
    
    /**
     * Lazy initialization of the initial context
     */
    public static Context getInitialContext() throws NamingException {
        if (incon == null) {
            incon = (Context)new InitialContext().lookup("nbres:/");
        }
        return incon;
    }

    /**
     * Loads top component from the top components folder.
     */
    public TopComponent loadTopComponent(String name) {
        try {
            Context c = getInitialContext();
            Object folder = c.lookup(TOP_COMPONENTS_FOLDER);
            if (folder instanceof Context) {
                Context targetFolder = (Context)folder;
                TopComponent tc = (TopComponent)targetFolder.lookup(name);
                return tc;
            }
        } catch (Exception x) {
            ErrorManager.getDefault().notify(x);
        }
        return null;
    }
}

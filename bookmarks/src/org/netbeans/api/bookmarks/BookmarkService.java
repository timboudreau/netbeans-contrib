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
package org.netbeans.api.bookmarks;

import org.openide.windows.TopComponent;
import org.openide.util.Lookup;

/**
 * This is the main API class for the bookmarks module. The default
 * instance of the service is returned by BookmarkService.getDefault().
 * You should always use this instance to call into this module.
 * This class contains methods used by the GUI classes of the bookmarks
 * module - so you can call the same functionality programatically.
 * In the very rare case you would like to alter the behaviour of this
 * module you can crate a subclass of this classs and register it in
 * the META-INF/services lookup.
 * @author David Strupl
 */
public abstract class BookmarkService {

    /**
     * This is the preferred way to access the singleton instance of
     * this service. The implementation calls the default lookup
     * to get an instance. If you want to supply your own version of
     * the service please register the instance in META-INF/services
     * lookup.
     */
    public static BookmarkService getDefault() {
        return (BookmarkService)Lookup.getDefault().lookup(BookmarkService.class);
    }
    
    /** In the very rare case you are providing your own subclass
     * of BookmarkService yo will need this constructor. It should be
     * called only by subclasses - if you need an instance of this class
     * please check the method getDefault().
     */
    protected BookmarkService() {
    }
    
    // ---------------------------------------------------
    
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
    public abstract void storeBookmark(Bookmark b);
    
    /**
     * This method creates a Bookmark object saving the state of the
     * supplied TopComponent. Invoking such a bookmark means to open
     * a clone of the TopComponent.
     * @returns default implementation of the Bookmark interface
     */
    public abstract Bookmark createDefaultBookmark(TopComponent tc);
    
}

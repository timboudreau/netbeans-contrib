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

/**
 * The Bookmark object can have an associated TopComponent. If the
 * TopComponent wants to provide its own subclass of Bookmark to
 * be used by the BookmarkService it should place an object
 * implementing this interface in its lookup.
 * @author David Strupl
 */
public interface BookmarkProvider {
    /**
     * Method called by the BookmarkService to create a bookmark.
     */
    public Bookmark createBookmark();
}

/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2003-2004 Nokia.
 * All Rights Reserved.
 */
package org.netbeans.modules.bookmarks;

import java.util.Properties;
import java.util.Iterator;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;

import org.openide.util.NbBundle;
import org.openide.ErrorManager;

import org.netbeans.api.bookmarks.*;
import org.netbeans.api.registry.*;

/**
 * Action used for shortcuts to bookmarks.
 * Storing is achieved using core/settings module properties convertor - 
 * see methods readProperties, writeProperties.
 * @author David Strupl
 */
public class BookmarkActionImpl extends AbstractAction {
    
    private static final long serialVersionUID = 1L;
    
    /** Name of the property used from readProperties, writeProperties.
     * The path is path to the "real" bookmark.
     */
    private static final String PROP_PATH = "path";
    
    /** Path to the bookmark */
    private String path;
    
//    private transient Bookmark bookmark;
    
    /** 
     */
    public BookmarkActionImpl() {
    }
    
    /** Creates the bookmark with the associated TopComponent.
     * If you use this constructor the bookmark is able to be
     * saved after the call to setTopComponentFileName.
     */
    public BookmarkActionImpl(String p) {
        this.path = p;
        if (path.length() > 0 && path.charAt(0) == '/') {
            path = path.substring(1);
        }
        String name = getBookmark().getName();
        putValue(NAME, name);
    }
    
    public BookmarkActionImpl(String p, Bookmark b) {
        this.path = p;
        if (path.length() > 0 && path.charAt(0) == '/') {
            path = path.substring(1);
        }
        String name = b.getName();
        putValue(NAME, name);
    }
    
    /**
     * Implementing the javax.swing.Action interface.
     */
    public void actionPerformed(ActionEvent e) {
        getBookmark().invoke();
    }
   
    public String getPath() {
        return path;
    }
    
    /**
     * Locates the bookmark using path variable.
     */
    Bookmark getBookmark() {
        int lastSlash = path.lastIndexOf('/');
        Object obj = null;
        if (lastSlash >= 0) {
            Context c = Context.getDefault().getSubcontext(path.substring(0, lastSlash));
            if (c == null) {
                throw new IllegalStateException("Bookmark not found, context does not exist for path " + path); // NOI18N
            }
            obj = c.getObject(path.substring(lastSlash+1), null);
        } else {
            obj = Context.getDefault().getObject(path, null);
        }
        if (obj instanceof Bookmark) {
            return (Bookmark)obj;
        }
        throw new IllegalStateException("Bookmark not found with path " + path + " object == " + obj); // NOI18N
    }
    
    /**
     * Tries to fullfill the contract of Object.toString().
     * @returns informative string representation of this object.
     */
    public String toString() {
        return "BookmarkActionImpl [path==" + path + "]";
    }
    
    // readProperties/writeProperties called by XMLPropertiesConvertor
    
    /**
     * Called by XMLPropertiesConvertor to restore the state
     * of this object after calling the default constructor.
     */
    private void readProperties(Properties p) {
        path = p.getProperty(PROP_PATH);
        String name = getBookmark().getName();
        putValue(NAME, name);
    }
    
    /**
     * XMLPropertiesConvertor calls this method when it wants
     * to persist this object.
     */
    private void writeProperties(Properties p) {
        p.setProperty(PROP_PATH, path);
    }
    
}

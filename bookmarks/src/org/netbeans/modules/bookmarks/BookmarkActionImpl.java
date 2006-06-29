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
 * Software is Nokia. Portions Copyright 2003-2004 Nokia.
 * All Rights Reserved.
 */
package org.netbeans.modules.bookmarks;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Externalizable;
import java.util.Properties;
import java.util.Iterator;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.lang.ref.WeakReference;
import javax.swing.SwingUtilities;

import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.DialogDisplayer;

import org.netbeans.api.bookmarks.*;
import org.netbeans.api.registry.*;

/**
 * Action used for shortcuts to bookmarks. Storing is achieved using
 * serialization - see methods readExternal, writeExternal.
 *
 * Please note that serialization must me used in this case since
 * two different APIs (Registry, Loaders) are trying to access
 * the same instance. Registry does not support settings (core/settings)
 * and Loaders don't support openide/convertor. So serialization
 * is the only common format that can be used here.
 *
 * @author David Strupl
 */
public class BookmarkActionImpl extends AbstractAction implements Externalizable, PropertyChangeListener {
    
    static {
        BookmarkService.getDefault();
    }

    private static final long serialVersionUID = 1L;
    
    /** Name of the property used from readProperties, writeProperties.
     * The path is path to the "real" bookmark.
     */
    private static final String PROP_PATH = "path";
    
    /** Name of the property used from readProperties, writeProperties.
     * The path is path to this object.
     */
    private static final String PROP_MY_PATH = "myPath";
    
    /** Path to the bookmark */
    private String path;
    
    /** Name of this object in the actions folder. */
    private String myBindingName;
    
    /** Caching "my" bookmark reference */
    private WeakReference bookmark;
    
    /**
     * Constructor used by the serialization mechanism.
     */
    public BookmarkActionImpl() {
    }
    
    /**
     * Creates the action pointing to the original bookmark
     * object stored on path p.
     */
    public BookmarkActionImpl(String p, String myBindingName) {
        this.path = p;
        this.myBindingName = myBindingName;
        if (path.length() > 0 && path.charAt(0) == '/') {
            path = path.substring(1);
        }
        updateName();
    }

    /**
     * Updates name of this action. The name is taken from
     * the bookmark object.
     */
    private void updateName() {
        Bookmark b = getBookmark();
        if (b != null) {
            String name = b.getName();
            putValue(NAME, name);
        } else {
            putValue(NAME, NbBundle.getBundle(BookmarkActionImpl.class).getString("LBL_Invalid_Bookmark"));
        }
    }
    
    /**
     * Implementing the javax.swing.Action interface.
     */
    public void actionPerformed(ActionEvent e) {
        final Bookmark b = getBookmark();
        if (b != null) {
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    b.invoke();
                }
            });
        } else {
            NotifyDescriptor.Message warning = 
                new NotifyDescriptor.Message(
                    NbBundle.getBundle(BookmarkActionImpl.class).getString("WARN_Bookmark_Deleted"),
                    NotifyDescriptor.WARNING_MESSAGE);
            DialogDisplayer.getDefault().notify(warning);
        }
    }
    
    /**
     * Returns path in the registry where the original Bookmark object is stored.
     */
    public String getPath() {
        return path;
    }
    
    /**
     * Locates the bookmark using path variable. If the bookmark
     * is not found returns null.
     */
    Bookmark getBookmark() {
        if (bookmark != null) {
            Object cachedValue = bookmark.get();
            if (cachedValue != null) {
                return (Bookmark)cachedValue;
            }
        }
        int lastSlash = path.lastIndexOf('/');
        Object obj = null;
        if (lastSlash >= 0) {
            Context c = Context.getDefault().getSubcontext(path.substring(0, lastSlash));
            if (c == null) {
                return null;
            }
            obj = c.getObject(path.substring(lastSlash+1), null);
        } else {
            obj = Context.getDefault().getObject(path, null);
        }
        if (obj instanceof Bookmark) {
            Bookmark b = (Bookmark)obj;
            b.removePropertyChangeListener(this);
            b.addPropertyChangeListener(this);
            bookmark = new WeakReference(b);
            return b;
        }
        return null;
    }
    
    /**
     * As we listen only on the bookmark this method tries
     * to always update the name from the bookmark.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(BookmarksNode.PROP_DESTROYED)) {
            Context targetFolder = Context.getDefault().getSubcontext(BookmarkServiceImpl.BOOKMARKS_ACTIONS);
            if (targetFolder != null) {
                targetFolder.putObject(myBindingName, null);
                BookmarkServiceImpl.refreshShortcutsFolder();
            }
            return;
        }
        updateName();
    }
    
    /**
     * Tries to fullfill the contract of Object.toString().
     * @returns informative string representation of this object.
     */
    public String toString() {
        return "BookmarkActionImpl [path==" + path + "]";
    }
    
    /**
     * Reads only the path variable and tries to load
     * the bookmark object.
     */
    public void readExternal(java.io.ObjectInput in) throws java.io.IOException, ClassNotFoundException {
        path = (String)in.readObject();
        myBindingName = (String)in.readObject();
        updateName();
    }
    
    /**
     * We store only the path.
     */
    public void writeExternal(java.io.ObjectOutput out) throws java.io.IOException {
        out.writeObject(path);
        out.writeObject(myBindingName);
    }
    
}

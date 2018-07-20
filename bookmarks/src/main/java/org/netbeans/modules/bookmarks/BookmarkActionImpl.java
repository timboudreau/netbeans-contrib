/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Nokia. Portions Copyright 2003-2004 Nokia.
 * All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
package org.netbeans.modules.bookmarks;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Externalizable;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import java.lang.ref.WeakReference;
import javax.swing.SwingUtilities;

import org.openide.util.NbBundle;
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

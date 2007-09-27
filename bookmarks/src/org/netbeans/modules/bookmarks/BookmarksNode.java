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

import java.awt.Image;
import java.io.IOException;
import java.util.*;
import javax.swing.Action;
import javax.swing.Icon;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

import org.openide.nodes.*;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;
import org.openide.util.WeakListeners;
import org.openide.actions.*;

import org.netbeans.api.bookmarks.Bookmark;
import org.netbeans.api.registry.*;

/**
 * The nodes that serve for the bookmarks customization
 * are 
 * @author David Strupl
 */
public class BookmarksNode extends AbstractNode implements PropertyChangeListener {
    
    public static final String PROP_DESTROYED = "destroyed";
    
    /** Should be either Context or Bookmark*/
    private Bookmark bookmark;
    
    /** Absolute path of this object */
    private String path;
    
    /**
     * The only supported constructor takes the original node
     * as its parameter.
     */
    public BookmarksNode(Bookmark b, String path) {
        super(Children.LEAF, Lookups.fixed(new Object[] { b, path } ));
        this.bookmark = b;
        bookmark.addPropertyChangeListener(
            WeakListeners.propertyChange(this, bookmark));
        this.path = path;
        setIconBaseWithExtension("org/netbeans/modules/bookmarks/resources/BookmarksRootNodeIcon.gif"); // NOI18N
    }
 
    /**
     * The handle just uses the original node's handle and wraps it
     * by BookmarksHandle.
     */
    public Node.Handle getHandle() {
        return new BookmarksHandle(path);
    }

    /** Overriden to enable renaming */
    public boolean canRename() {
        return true;
    }

    /** Overriden to disable deleting of fixed folders */
    public boolean canDestroy() {
        return true;
    }

    /** Overriden to disable deleting of fixed folders */
    public boolean canCut() {
        return true;
    }
        
    /** Overriding to also delete the bookmark from its context. */
    public void destroy() throws IOException {
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash >= 0) {
            Context c = Context.getDefault().getSubcontext(path.substring(0, lastSlash));
            if (c != null) {
                c.putObject(path.substring(lastSlash+1), null);
            }
        } else {
            Context.getDefault().putObject(path, null);
        }
        bookmark.firePropertyChange(PROP_DESTROYED, null, null);
        
        super.destroy();
    }

    /**
     *
     */
    public String getName() {
        return bookmark.getName();
    }
    
    /**
     * 
     */
    public void setName(String newName) {
        bookmark.setName(newName);
        super.setName(newName);
        
        // now store the change to the registry:
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash >= 0) {
            Context c = Context.getDefault().getSubcontext(path.substring(0, lastSlash));
            if (c != null) {
                c.putObject(path.substring(lastSlash+1), bookmark);
            }
        } else {
            Context.getDefault().putObject(path, bookmark);
        }
    }
    
    /**
     * Don't show the regular properties - just name.
     * @return the array of property sets.
     */
    public PropertySet[] getPropertySets () {
        return new PropertySet[0];
    }

    /**
     * We are attached as a weak property change listener
     * to the bookmark object. Bookmarks should fire name
     * changes.
     */
    public void propertyChange(PropertyChangeEvent evt) {
        firePropertyChange(PROP_NAME, null, null);
        firePropertyChange(PROP_DISPLAY_NAME, null, null);
    }
    
    /**
     * The list of the actions returned by this method contains
     * only those that should be provided when customizing bookmarks.
     */
    public Action[] getActions(boolean context) {
        return new SystemAction[] {
            SystemAction.get(CutAction.class),
            SystemAction.get(CopyAction.class),
            SystemAction.get(DeleteAction.class),
            SystemAction.get(RenameAction.class),
            null,
            SystemAction.get(MoveUpAction.class),
            SystemAction.get(MoveDownAction.class)
        };
    }
    
    /**
     * Computes the icon from the associated Bookmark object. The icon
     * is extracted from the bookmark using 
     * <code>getMenuPresenter().getIcon()</code>.
     */
    public Image getIcon (int type) {
        if (bookmark == null) {
            return super.getIcon(type);
        }
        Icon icon = bookmark.getMenuPresenter().getIcon();
        if (icon == null) {
            return super.getIcon(type);
        }
        Image res = createBufferedImage(icon.getIconWidth(), icon.getIconHeight());
        icon.paintIcon(null, res.getGraphics(), 0, 0);
        return res;
    }

    /**
     * Computes the icon from the associated Bookmark object. The icon
     * is extracted from the bookmark using 
     * <code>getMenuPresenter().getIcon()</code>. 
     */
    public Image getOpenedIcon (int type) {
        // The code is almost the same
        // as in the previous method. The onlyd difference is in the super.
        // calls.
        if (bookmark == null) {
            return super.getOpenedIcon(type);
        }
        Icon icon = bookmark.getMenuPresenter().getIcon();
        if (icon == null) {
            return super.getOpenedIcon(type);
        }
        Image res = createBufferedImage(icon.getIconWidth(), icon.getIconHeight());
        icon.paintIcon(null, res.getGraphics(), 0, 0);
        return res;
    }

    /**  */
    private static class BookmarksHandle implements Node.Handle {
        private static final long serialVersionUID = 1L;
        private String name;
        public BookmarksHandle(String name) {
            this.name = name;
        }
        public Node getNode() throws IOException {
            Object d = Context.getDefault().getObject(name, null);
            if (d instanceof Bookmark) {
                return new BookmarksNode((Bookmark)d , name);
            }
            throw new IOException("Cannot create node with name " + name); // NOI18N
        }
    }
    
    
    /** Creates BufferedImage 16x16 and Transparency.BITMASK */
    private static final java.awt.image.BufferedImage createBufferedImage(int width, int height) {
        java.awt.image.ColorModel model = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().
                                          getDefaultScreenDevice().getDefaultConfiguration().getColorModel(java.awt.Transparency.BITMASK);
        java.awt.image.BufferedImage buffImage = new java.awt.image.BufferedImage(model,
                model.createCompatibleWritableRaster(width, height), model.isAlphaPremultiplied(), null);
        return buffImage;
    }

    
}

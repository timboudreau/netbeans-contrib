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

import java.awt.Image;
import java.io.IOException;
import javax.swing.Icon;

import org.openide.ErrorManager;
import org.openide.nodes.*;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.NewType;
import org.openide.cookies.InstanceCookie;
import org.openide.actions.*;
import org.openide.loaders.DataFolder;

import org.netbeans.api.bookmarks.Bookmark;

/**
 * The nodes that serve for the bookmarks customization
 * are wrapped in this filter. The filter changes the
 * list of actions available on the node, otherwise it
 * tries to leave everything from the original node.
 * @author David Strupl
 */
public class BookmarksNode extends FilterNode {
    
    /** Whether this node can be copied */
    private boolean canCopy;
    
    /**
     * The only supported constructor takes the original node
     * as its parameter.
     */
    public BookmarksNode(Node original, boolean canCopy) {
        super(original, new BookmarksChildren(original));
        this.canCopy = canCopy;
    }
 
    /** 
     * Overriden to just wrap the original in a new instance of this filter.
     */
    public Node cloneNode() {
        return new BookmarksNode(getOriginal(), canCopy);
    }
    
    /**
     * The handle just uses the original node's handle and wraps it
     * by BookmarksHandle.
     */
    public Node.Handle getHandle() {
        Node.Handle origHandle = getOriginal().getHandle();
        // Simplest behavior: just store the original node and try to recreate
        // a filter based on that.
        if (origHandle != null) {
            return new BookmarksHandle(origHandle, canCopy);
        } else {
            return null; // cannot persist original, do not persist filter
        }
    }
    
    /**
     * The list of the actions returned by this method contains
     * only those that should be provided when customizing bookmarks.
     */
    public SystemAction[] getActions () {
        if (canCopy) {
            return new SystemAction[] {
                SystemAction.get(NewAction.class),
                SystemAction.get(CutAction.class),
                SystemAction.get(CopyAction.class),
                SystemAction.get(PasteAction.class),
                SystemAction.get(DeleteAction.class),
                null,
                SystemAction.get(MoveUpAction.class),
                SystemAction.get(MoveDownAction.class)
            };
        } else {
            return new SystemAction[] {
                SystemAction.get(NewAction.class),
                SystemAction.get(ReorderAction.class),
                null,
                SystemAction.get(PasteAction.class)
            };
        }
    }
    
    /**
     * Computes the icon from the associated Bookmark object. The icon
     * is extracted from the bookmark using 
     * <code>getMenuPresenter().getIcon()</code>.
     */
    public Image getIcon (int type) {
        InstanceCookie.Of icof = (InstanceCookie.Of)getLookup().lookup(InstanceCookie.Of.class);
        if (icof != null) {
            if (! icof.instanceOf(Bookmark.class)) {
                return super.getIcon (type);
            }
        }
        try {
            InstanceCookie ic = (InstanceCookie)getLookup().lookup(InstanceCookie.class);
            if (ic == null) {
                return super.getIcon (type);
            }
            Class actualClass = ic.instanceClass ();
            if (! Bookmark.class.isAssignableFrom (actualClass)) {
                return super.getIcon (type);
            }

            // ok - now we know that this node represents a bookmark
            Bookmark b = (Bookmark)ic.instanceCreate();
            if (b == null) {
                return super.getIcon(type);
            }
            Icon icon = b.getMenuPresenter().getIcon();
            if (icon == null) {
                return super.getIcon(type);
            }
            Image res = createBufferedImage(icon.getIconWidth(), icon.getIconHeight());
            icon.paintIcon(null, res.getGraphics(), 0, 0);
            return res;
        } catch (IOException ioe) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ioe);
        } catch (ClassNotFoundException cnfe) {
            ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, cnfe);
        }
        return super.getIcon(type);
    }


    /* List new types that can be created in this node.
     * @return new types
     */
    public NewType[] getNewTypes () {
        final DataFolder folder = (DataFolder)getCookie(DataFolder.class);
        if (folder == null) {
            return super.getNewTypes();
        }
        // only if we are folder we allow for creating of subfolders
        return new NewType[] {
            new NewType() {
                public String getName() {
                    return NbBundle.getMessage(BookmarksNode.class, "LBL_NewFolder");
                }
                public void create () throws IOException {
                    DataFolder.create(folder, 
                        NbBundle.getMessage(BookmarksNode.class, "LBL_NewFolder"));
                }
            }
        };
    }

    /** 
     * Index cookie does not work automatically with filter nodes, because
     * the index refers to the original nodes. So if you wish to propagate
     * index cookies, generally you must provide your own index cookie based
     * on the original, or constructed some other way. For example, if you are
     * filtering data folders and wish Reorder/Move Up/Move Down to work:
     */
    public Node.Cookie getCookie(Class clazz) {
        if (clazz.isAssignableFrom(DataFolder.Index.class)) {
            DataFolder folder = (DataFolder)super.getCookie(DataFolder.class);
            if (folder != null) {
                return new DataFolder.Index(folder, this);
            }
        }
        return super.getCookie(clazz);
    }
    
    /** Wrap the original handle. */
    private static class BookmarksHandle implements Node.Handle {
        private static final long serialVersionUID = 1L;
        private Node.Handle origHandle;
        private boolean canCopy;
        public BookmarksHandle(Node.Handle origHandle, boolean canCopy) {
            this.origHandle = origHandle;
            this.canCopy = canCopy;
        }
        public Node getNode() throws IOException {
            return new BookmarksNode(origHandle.getNode(), canCopy);
        }
    }
    
    /** 
     * The children of the bookmarks filter are again filtered
     * by the same class.
     */
    private static class BookmarksChildren extends FilterNode.Children {
        
        public BookmarksChildren(Node orig) {
            // This is the original parent node:
            super(orig);
        }
        
        public Object clone() {
            return new BookmarksChildren(original);
        }

        /**
         * Recursively filter.
         */
        protected Node copyNode(Node child) {
            return new BookmarksNode(child, true);
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

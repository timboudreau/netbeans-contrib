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

import java.awt.datatransfer.*;
import javax.naming.*;
import javax.naming.event.*;
import java.util.*;
import java.io.IOException;

import org.openide.actions.*;
import org.openide.nodes.*;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.datatransfer.*;
import org.openide.util.actions.SystemAction;
import org.openide.util.actions.Presenter;
import org.openide.filesystems.Repository;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataFolder;
import org.openide.ErrorManager;
import org.openide.cookies.InstanceCookie;

import org.netbeans.api.bookmarks.*;

/**
 * This node is the node for customization of the bookmarks toolbar.
 * It allows pasting nodes providing bookmarks. 
 * @author David Strupl
 */
public class BookmarksToolbarNode extends AbstractNode {
    
    /** String denoting current JNDI context */
    private static final String CURRENT = ""; // NOI18N
    
    /**
     * Default constructor.
     */
    public BookmarksToolbarNode() {
        super(new BookmarksToolbarChildren());
        setIconBase("org/netbeans/modules/bookmarks/resources/toolbars");
        // Whatever is most relevant to a user:
        setDefaultAction(SystemAction.get(PropertiesAction.class));
        // Set FeatureDescriptor stuff:
        setName("BookmarksToolbar");
        setDisplayName(NbBundle.getMessage(BookmarksToolbarNode.class, "LBL_BookmarksToolbar"));
        setShortDescription(NbBundle.getMessage(BookmarksToolbarNode.class, "HINT_BookmarksToolbar"));
    }
    
    /**
     * Adds actoin to the popup menu for this noce.
     */
    protected SystemAction[] createActions() {
        return new SystemAction[] {
           SystemAction.get(ReorderAction.class),
           null,
           SystemAction.get(PasteAction.class),
        };
    }
    
    /**
     * Uses this class as the ID for the help.
     */
    public HelpCtx getHelpCtx() {
        return new HelpCtx(BookmarksToolbarNode.class);
    }
    
    /**
     * This node supports cloning simply by creating a new instance.
     */
    public Node cloneNode() {
        // Try to pass in similar constructor params to what you originally got:
        return new BookmarksToolbarNode();
    }
    
    /**
     * Allows pasting bookmark nodes to the bookmark toolbar.
     * First computes whether the node we are trying to paste has
     * a bookmark in its lookup (as instance cookie). If it supplies
     * one the paste operation is enabled.
     */
    protected void createPasteTypes(final Transferable t, List l) {
        // Make sure to pick up super impl, which adds intelligent node paste type:
        super.createPasteTypes(t, l);
        Node n = NodeTransfer.node(t, NodeTransfer.COPY);
        if (n == null) {
            n = NodeTransfer.node(t, NodeTransfer.MOVE);
        }
        if (n != null) {
            InstanceCookie ic = (InstanceCookie)n.getLookup().lookup(InstanceCookie.class);
            if (ic == null) {
                return;
            }
            Bookmark b = null;
            try {
                Object res = ic.instanceCreate();
                if (res instanceof Bookmark) {
                    b = (Bookmark)res;
                }
            } catch (ClassNotFoundException cnfe) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, cnfe);
                return;
            } catch (IOException ioe) {
                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL, ioe);
                return;
            }
            final Bookmark b1 = b;
            if (b1 != null) {
                l.add(new PasteType() {
                    public String getName() {
                        return NbBundle.getMessage(BookmarksToolbarNode.class, "LBL_PasteType");
                    }
                    public Transferable paste() throws IOException {
                        try {
                            Context c = BookmarkServiceImpl.getInitialContext();
                            Object folder = c.lookup(BookmarkServiceImpl.BOOKMARKS_TOOLBAR);
                            if (folder instanceof Context){
                                Context targetFolder = (Context)folder;
                                String safeName = BookmarkServiceImpl.findUnusedName(targetFolder, b1.getName());
                                // following line will save the bookmark to the system file system
                                targetFolder.bind(safeName, b1);
                                return ExTransferable.EMPTY;
                            }
                            throw new IOException("Folder " + BookmarkServiceImpl.BOOKMARKS_TOOLBAR + " does not exists (or is not a folder)");
                        } catch (NamingException x) {
                            IOException ioe =  new IOException();
                            ErrorManager.getDefault().annotate(ioe, x);
                            throw ioe;
                        }
                    }
                });
            }
        }
    }
    
    /** List of children of the BookmarksToolbarNode.
     * Each child node is represented by one key of the type String. The String
     * is the name to what is the a bookmark bound in the bookmarks folder (context).
     */
    public static class BookmarksToolbarChildren extends Children.Keys implements ObjectChangeListener, NamespaceChangeListener {
        
        public BookmarksToolbarChildren() {
        }
        
        /** Implements the naming listener interface */
        public void namingExceptionThrown(NamingExceptionEvent evt) {
            updateKeys();
        }
        
        /** Implements the naming listener interface */
        public void objectAdded(NamingEvent evt) {
            updateKeys();
        }
        
        /** Implements the naming listener interface */
        public void objectChanged(NamingEvent evt) {
            updateKeys();
        }
        
        /** Implements the naming listener interface */
        public void objectRemoved(NamingEvent evt) {
            updateKeys();
        }
        
        /** Implements the naming listener interface */
        public void objectRenamed(NamingEvent evt) {
            updateKeys();
        }

        /** Overriden from Children.Keys */
        protected void addNotify() {
            super.addNotify();
            // set the children to use:
            updateKeys();
            // and listen to changes in the model too:
            try {
                Context con = (Context)BookmarkServiceImpl.getInitialContext().lookup(CURRENT);
                if (con instanceof EventContext) {
                    EventContext ec = (EventContext)con;
                    ec.addNamingListener(BookmarkServiceImpl.BOOKMARKS_TOOLBAR, EventContext.SUBTREE_SCOPE, this);
                }
            } catch (NamingException ne) {
                ErrorManager.getDefault().notify(ne);
            }
        }
        
        /**
         * Computes new children.
         */
        private void updateKeys() {
            ArrayList al = new ArrayList();
            try {
                Context con = (Context)BookmarkServiceImpl.getInitialContext().lookup(CURRENT);
                NamingEnumeration en = con.listBindings(BookmarkServiceImpl.BOOKMARKS_TOOLBAR);
                while (en.hasMoreElements()) {
                    Binding b = (Binding)en.nextElement();
                    Object obj = b.getObject();
                    b.setRelative(true);
                    if (obj instanceof Presenter.Toolbar) {
                        al.add(b.getName());
                    }
                    if (obj instanceof Context) {
                        al.add(b.getName());
                    }
                }
            } catch (NamingException ne) {
                ErrorManager.getDefault().notify(ne);
            }
            setKeys(al);
        }
        

        /** Overriden from Children.Keys */
        protected void removeNotify() {
            try {
                Context con = (Context)BookmarkServiceImpl.getInitialContext().lookup(CURRENT);
                if (con instanceof EventContext) {
                    EventContext ec = (EventContext)con;
                    ec.removeNamingListener(this);
                }
            } catch (NamingException ne) {
                ErrorManager.getDefault().notify(ne);
            }
            setKeys(Collections.EMPTY_SET);
            super.removeNotify();
        }
        
        /** Overriden from Children.Keys */
        protected Node[] createNodes(Object key) {
            if (! (key instanceof String)) {
                return new Node[0];
            }
            try {
                FileObject fo = Repository.getDefault().getDefaultFileSystem().
                    findResource(BookmarkServiceImpl.BOOKMARKS_TOOLBAR);
                if (fo == null) {
                    throw new IOException(BookmarkServiceImpl.BOOKMARKS_TOOLBAR + " not found.");
                }
                DataFolder dFolder = DataFolder.findFolder(fo);
                DataObject children[] = dFolder.getChildren();
                int childNo = -1;
                for (int i = 0; i < children.length; i++) {
                    if (children[i].getName().equals(key)) {
                        childNo = i;
                    }
                }
                if (childNo == -1) {
                    throw new IOException("Child not found for " + key);
                }
                Node n = children[childNo].getNodeDelegate().cloneNode();
                Node filter = new BookmarksNode(n, true);
                return new Node[] { filter };
            } catch (IOException ioe) {
                ErrorManager.getDefault().notify(ioe);
            }
            return new Node[0];
        }
    }
}

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

import java.awt.datatransfer.*;
import java.util.*;
import java.io.IOException;

import org.openide.actions.*;
import org.openide.nodes.*;
import org.openide.util.lookup.*;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.datatransfer.*;
import org.openide.util.actions.SystemAction;
import org.openide.util.actions.Presenter;
import org.openide.ErrorManager;

import org.netbeans.api.bookmarks.*;
import org.netbeans.api.registry.*;

/**
 * This node is the node for customization of the bookmarks toolbar.
 * It allows pasting nodes providing bookmarks. 
 * @author David Strupl
 */
public class BookmarksToolbarNode extends AbstractNode {
    
    /**
     * Default constructor.
     */
    public BookmarksToolbarNode() {
        this(null);
        setIconBase("org/netbeans/modules/bookmarks/resources/toolbars");
        // Set FeatureDescriptor stuff:
        setName("BookmarksToolbar");
        setDisplayName(NbBundle.getMessage(BookmarksToolbarNode.class, "LBL_BookmarksToolbar"));
        setShortDescription(NbBundle.getMessage(BookmarksToolbarNode.class, "HINT_BookmarksToolbar"));
    }
    
    /** This private constructor is here because of the trick with
     * InstanceContent.
     */
    private BookmarksToolbarNode(InstanceContent ic) {
        super(new BookmarksToolbarChildren(), new AbstractLookup(ic = new InstanceContent()));
        ic.add(new ReorderSupport());
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
        boolean moving = false;
        Node n = NodeTransfer.node(t, NodeTransfer.COPY);
        if (n == null) {
            n = NodeTransfer.node(t, NodeTransfer.MOVE);
            moving = true;
        }
        if (n != null) {
            final Bookmark b = (Bookmark)n.getLookup().lookup(Bookmark.class);
            if (b == null) {
                return;
            }
            Context con = null;
            if (moving) {
                con = (Context)n.getLookup().lookup(Context.class);
            }
            final Context whereToDelete = con;
            if (b != null) {
                l.add(new PasteType() {
                    public String getName() {
                        return NbBundle.getMessage(BookmarksToolbarNode.class, "LBL_PasteType");
                    }
                    public Transferable paste() throws IOException {
                        try {
                            Context targetFolder = Context.getDefault().createSubcontext(BookmarkServiceImpl.BOOKMARKS_TOOLBAR);
                            String safeName = BookmarkServiceImpl.findUnusedName(targetFolder, b.getName());
                            
                            // following line will save the bookmark to the system file system
                            targetFolder.putObject(safeName, b);
                                
                            if (whereToDelete != null) {
                                // if this is the move operation:
                                
                            }
                            return ExTransferable.EMPTY;
                        } catch (ContextException x) {
                            IOException ioe =  new IOException();
                            ErrorManager.getDefault().annotate(ioe, x);
                            throw ioe;
                        }
                    }
                });
            }
        }
    }
    
    private class ReorderSupport extends Index.Support {
        
        public Node[] getNodes() {
            return getChildren().getNodes();
        }
        
        public int getNodesCount() {
            return getNodes().length;
        }
        
        public void reorder(int[] perm) {
            try {
                Context context = Context.getDefault().createSubcontext(BookmarkServiceImpl.BOOKMARKS_TOOLBAR);
                // Remember: {2, 0, 1} cycles three items forwards.
                String[] items = (String[])context.getOrderedNames().toArray(new String[0]);
                if (items.length != perm.length) {
                    StringBuffer sb = new StringBuffer();
                    sb.append("Items[");
                    for (int i = 0; i < items.length; i++) {
                        sb.append(" " + items[i]);
                    }
                    sb.append("] Perm[");
                    for (int i = 0; i < perm.length; i++) {
                        sb.append(" " + perm[i]);
                    }
                    sb.append("]");
                    throw new IllegalArgumentException(sb.toString());
                }
                String[] nue = new String[perm.length];
                for (int i = 0; i < perm.length; i++) {
                    nue[i] = items[perm[i]];
                }
                // Should trigger an automatic child node update because the children
                // should be listening:
                context.orderContext(Arrays.asList(nue));
            } catch (ContextException ne) {
                ErrorManager.getDefault().getInstance("org.netbeans.modules.bookmarks").notify(ne); // NOI18N
            }
            
        }
    }
    
    /** List of children of the BookmarksToolbarNode.
     * Each child node is represented by one key of the type String. The String
     * is the name to what is the a bookmark bound in the bookmarks folder (context).
     */
    public static class BookmarksToolbarChildren extends Children.Keys implements ContextListener, Runnable {
        
        public BookmarksToolbarChildren() {
        }
        
        public void attributeChanged(AttributeEvent evt) {
            updateKeys();
        }
        
        public void bindingChanged(BindingEvent evt) {
            updateKeys();
        }
        
        public void subcontextChanged(SubcontextEvent evt) {
            updateKeys();
        }
        
        /** Overriden from Children.Keys */
        protected void addNotify() {
            super.addNotify();
            // set the children to use:
            updateKeys();
            // and listen to changes in the model too:
            try {
                Context con = Context.getDefault().createSubcontext(BookmarkServiceImpl.BOOKMARKS_TOOLBAR);
                con.addContextListener(this);
            } catch (ContextException ne) {
                ErrorManager.getDefault().getInstance("org.netbeans.modules.bookmarks").notify(ne); // NOI18N
            }
        }
        
        /**
         * Computes new children.
         */
        private void updateKeys() {
            // posting asynchronously since while setting the order
            // this method would be called to update the state of the children.
            // But it tries to access the list of children so it would either
            // loop or deadlock.
            RequestProcessor.getDefault().post(this);
        }

        /**
         * This method is the original body of updateKeys. We are now
         * calling it in a new thread.
         */
        public void run() {
            try {
                Context con = Context.getDefault().createSubcontext(BookmarkServiceImpl.BOOKMARKS_TOOLBAR);
                setKeys(con.getOrderedNames());
            } catch (ContextException ne) {
                ErrorManager.getDefault().getInstance("org.netbeans.modules.bookmarks").notify(ne); // NOI18N
            }
        }
        
        /** Overriden from Children.Keys */
        protected void removeNotify() {
            try {
                Context con = Context.getDefault().createSubcontext(BookmarkServiceImpl.BOOKMARKS_TOOLBAR);
                con.removeContextListener(this);
            } catch (ContextException ne) {
                ErrorManager.getDefault().getInstance("org.netbeans.modules.bookmarks").notify(ne); // NOI18N
            }
            setKeys(Collections.EMPTY_SET);
            super.removeNotify();
        }
        
        /** Overriden from Children.Keys */
        protected Node[] createNodes(Object key) {
            if (! (key instanceof String)) {
                return new Node[0];
            }
            String name = (String)key;
            try {
                Context con = Context.getDefault().createSubcontext(BookmarkServiceImpl.BOOKMARKS_TOOLBAR);
                Object data = con.getObject(name, null);
                if (data == null) {
                    return new Node[0];
                }
                if (data instanceof Bookmark) {
                    String absName = con.getAbsoluteContextName() + "/" + name; // NOI18N
                    absName = absName.substring(1);
                    return new Node[] { new BookmarksNode((Bookmark)data, absName) };
                }
            } catch (ContextException ne) {
                ErrorManager.getDefault().getInstance("org.netbeans.modules.bookmarks").notify(ne); // NOI18N
            }
            return new Node[0];
        }
    }
}

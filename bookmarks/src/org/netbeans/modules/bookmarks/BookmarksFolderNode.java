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
import java.awt.Image;
import java.io.IOException;
import java.util.*;
import javax.swing.Icon;

import org.openide.ErrorManager;
import org.openide.nodes.*;
import org.openide.util.NbBundle;
import org.openide.util.lookup.*;
import org.openide.util.RequestProcessor;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.*;

import org.openide.actions.*;

import org.netbeans.api.bookmarks.Bookmark;
import org.netbeans.api.registry.*;

/**
 * The nodes that serve for the bookmarks customization
 * are 
 * @author David Strupl
 */
public class BookmarksFolderNode extends AbstractNode {
    
    /** Should be either Context or Bookmark*/
    private Context context;
    
    private boolean deletable;
    
    /**
     * The only supported constructor takes the original node
     * as its parameter.
     */
    public BookmarksFolderNode(Context c, boolean d) {
        this(c, d, null);
        this.context = c;
        this.deletable = d;
        setIconBase("org/netbeans/modules/bookmarks/resources/defaultFolder"); // NOI18N
    }
 
    /** This private constructor is here because of the trick with
     * InstanceContent.
     */
    private BookmarksFolderNode(Context c, boolean d, InstanceContent ac) {
        super(new BookmarksChildren(c), new AbstractLookup(ac = new InstanceContent()));
        ac.add(c);
        ac.add(new ReorderSupport());
    }
    
    /**
     * The handle just uses the original node's handle and wraps it
     * by BookmarksHandle.
     */
    public Node.Handle getHandle() {
        return new BookmarksFolderHandle(context.getAbsoluteContextName().substring(1), deletable);
    }

    /** Overriden to enable renaming */
    public boolean canRename() {
        return deletable;
    }

    /** Overriden to disable deleting of fixed folders */
    public boolean canDestroy() {
        return deletable;
    }

    /** Overriden to disable deleting of fixed folders */
    public boolean canCut() {
        return deletable;
    }
    
    /** Overriding to also delete the underlying context. */
    public void destroy() throws IOException {
        if (! canDestroy()) {
            throw new IOException("Cannot delete " + context);
        }
        try {
            context.getParentContext().destroySubcontext(context.getContextName());
        } catch (ContextException ce) {
            IOException e = new IOException();
            ErrorManager.getDefault().annotate(e, ce);
            throw e;
        }
        super.destroy();
    }
    
    /**
     * Don't show the regular properties - just name.
     * @return the array of property sets.
     */
    public PropertySet[] getPropertySets () {
        return new PropertySet[0];
    }

    
    /**
     * The list of the actions returned by this method contains
     * only those that should be provided when customizing bookmarks.
     */
    public SystemAction[] getActions () {
        if (deletable) {
            return new SystemAction[] {
                SystemAction.get(NewAction.class),
                SystemAction.get(CutAction.class),
                SystemAction.get(CopyAction.class),
                SystemAction.get(PasteAction.class),
                SystemAction.get(DeleteAction.class),
                SystemAction.get(RenameAction.class),
                null,
                SystemAction.get(ReorderAction.class),
                SystemAction.get(MoveUpAction.class),
                SystemAction.get(MoveDownAction.class)
            };
        }
        return new SystemAction[] {
            SystemAction.get(NewAction.class),
            SystemAction.get(PasteAction.class),
            SystemAction.get(ReorderAction.class),
        };
    }
    
    public String getName() {
        String storedDisplayName = context.getAttribute(null, PROP_DISPLAY_NAME, null);
        if (storedDisplayName != null) {
            return storedDisplayName;
        }
        return context.getContextName();
    }
    
    public void setName(String newName) {
        context.setAttribute(null, PROP_DISPLAY_NAME, newName);
        super.setName(newName);
    }
    
    /* List new types that can be created in this node.
     * @return new types
     */
    public NewType[] getNewTypes () {
        return new NewType[] {
            new NewType() {
                public String getName() {
                    return NbBundle.getMessage(BookmarksNode.class, "LBL_NewFolder");
                }
                public void create () throws IOException {
                    String name = NbBundle.getMessage(BookmarksNode.class, "LBL_NewFolder");
                    
                    // find an unique name
                    String resName = "Folder"; // NOI18N
                    Collection childrenNames = context.getBindingNames();
                    int i = 0;
                    while (childrenNames.contains(resName)) {
                        resName = name + "_" + i++; // NOI18N
                    }
                    // create the folder with the found name
                    try {
                        Context c = context.createSubcontext(resName);
                        c.setAttribute(null, PROP_DISPLAY_NAME, name);
                    } catch (ContextException ce) {
                        ErrorManager.getDefault().getInstance("org.netbeans.modules.bookmarks").notify(ce); // NOI18N
                    }
                }
            }
        };
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
            Context c = null;
            String bookmarkLocation = null;
            if (b == null) {
                c = (Context)n.getLookup().lookup(Context.class);
            }
            if (moving && b != null) {
                bookmarkLocation = (String)n.getLookup().lookup(String.class);
            }
            final String whereToDelete = bookmarkLocation;
            final Context con = c;
            final boolean move = moving;
            if (b != null) {
                l.add(new PasteType() {
                    public String getName() {
                        return NbBundle.getMessage(BookmarksToolbarNode.class, "LBL_PasteType");
                    }
                    public Transferable paste() throws IOException {
//                        try {
                            if (b != null) {
                                String safeName = BookmarkServiceImpl.findUnusedName(context, b.getName());

                                // following line will save the bookmark to the system file system
                                context.putObject(safeName, b);
                            }
                            if (con != null) {
                            }
                            if (move) {
                                if ( (b != null) && (whereToDelete != null)) {
                                    // moving bookmark
                                    int lastSlash = whereToDelete.lastIndexOf('/');
                                    if (lastSlash >= 0) {
                                        Context c = Context.getDefault().getSubcontext(whereToDelete.substring(0, lastSlash));
                                        if (c != null) {
                                            c.putObject(whereToDelete.substring(lastSlash+1), null);
                                        }
                                    } else {
                                        Context.getDefault().putObject(whereToDelete, null);
                                    }
                                }
                                
                                if (con != null) {
                                    // moving folder
                                }
                            }
                            return ExTransferable.EMPTY;
//                        } catch (ContextException x) {
//                            IOException ioe =  new IOException();
//                            ErrorManager.getDefault().annotate(ioe, x);
//                            throw ioe;
//                        }
                    }
                });
            }
        }
    }


    /**  */
    private static class BookmarksFolderHandle implements Node.Handle {
        private static final long serialVersionUID = 1L;
        private String name;
        private boolean del;
        public BookmarksFolderHandle(String name, boolean del) {
            this.name = name;
            this.del = del;
        }
        public Node getNode() throws IOException {
            Context d = Context.getDefault().getSubcontext(name);
            if (d != null) {
                return new BookmarksFolderNode(d, del);
            }
            throw new IOException("Cannot create node with name " + name); // NOI18N
        }
    }
    
    /** 
     * The children of the bookmarks folder.
     */
    private static class BookmarksChildren extends Children.Keys implements ContextListener, Runnable {
        
        private Context context;
        
        public BookmarksChildren(Context c) {
            context = c;
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
            if (context != null) {
                context.addContextListener(this);
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
            setKeys(context.getOrderedNames());
        }
        
        /** Overriden from Children.Keys */
        protected void removeNotify() {
            if (context != null) {
                context.removeContextListener(this);
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
            if (context != null) {
                Object data = context.getObject(name, null);
                if (data instanceof Bookmark) {
                    String absName = context.getAbsoluteContextName() + "/" + name; // NOI18N
                    absName = absName.substring(1);
                    return new Node[] { new BookmarksNode((Bookmark)data, absName) };
                }
                Context sub = context.getSubcontext(name);
                if (sub != null) {
                    Node n = new BookmarksFolderNode(sub, true);
                    return new Node[] { n };
                }
            }
            return new Node[0];
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
           // Remember: {2, 0, 1} cycles three items forwards.
            String[] items = (String[])context.getOrderedNames().toArray(new String[0]);
            if (items.length != perm.length) {
                StringBuffer sb = new StringBuffer();
                sb.append("Items["); // NOI18N
                for (int i = 0; i < items.length; i++) {
                    sb.append(" " + items[i]); // NOI18N
                }
                sb.append("] Perm["); // NOI18N
                for (int i = 0; i < perm.length; i++) {
                    sb.append(" " + perm[i]);// NOI18N
                }
                sb.append("]");// NOI18N
                throw new IllegalArgumentException(sb.toString());
            }
            String[] nue = new String[perm.length];
            for (int i = 0; i < perm.length; i++) {
                nue[i] = items[perm[i]];
            }
            // Should trigger an automatic child node update because the children
            // should be listening:
            context.orderContext(Arrays.asList(nue));
        }
        
    }
}

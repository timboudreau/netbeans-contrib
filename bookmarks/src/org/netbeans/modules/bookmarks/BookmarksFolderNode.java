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

import java.awt.Cursor;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.SwingUtilities;
import org.openide.DialogDisplayer;

import org.openide.NotifyDescriptor;
import org.openide.nodes.*;
import org.openide.util.NbBundle;
import org.openide.util.lookup.*;
import org.openide.util.RequestProcessor;
import org.openide.util.actions.SystemAction;
import org.openide.util.datatransfer.*;

import org.openide.actions.*;

import org.netbeans.api.bookmarks.Bookmark;
import org.netbeans.api.registry.*;
import org.openide.windows.WindowManager;

/**
 * The nodes that serve for the bookmarks customization.
 * @author David Strupl
 */
public class BookmarksFolderNode extends AbstractNode {
    
    /** Let's cache it.*/
    private static final Logger log = Logger.getLogger(BookmarksFolderNode.class.getName());
    
    /** Should be either Context or Bookmark*/
    private Context context;
 
    /**
     * Nested folders are deletable, but the top level
     * ones are not (at least not from this GUI).
     */
    private boolean deletable;
    
    /**
     * The only supported constructor takes the original node
     * as its parameter.
     */
    public BookmarksFolderNode(Context c, boolean d) {
        this(c, d, null);
        this.context = c;
        this.deletable = d;
        setIconBaseWithExtension("org/netbeans/modules/bookmarks/resources/defaultFolder.gifr"); // NOI18N
    }
 
    /** This private constructor is here because of the trick with
     * InstanceContent.
     */
    private BookmarksFolderNode(Context c, boolean d, InstanceContent ac) {
        super(new BookmarksChildren(c), new AbstractLookup(ac = new InstanceContent()));
        ac.add(c);
        ac.add(new ReorderSupport());
        c.addContextListener(new ContextListener() {
            public void attributeChanged(AttributeEvent evt) {
                fireNameChange(null, null);
                fireDisplayNameChange(null, null);
            }
            public void bindingChanged(BindingEvent evt) {
            }
            public void subcontextChanged(SubcontextEvent evt) {
            }
        });
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
            deleteBookmarksFolder(context);
        } catch (ContextException ce) {
            IOException e = new IOException();
            e.initCause(ce);
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
    public Action[] getActions(boolean context) {
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
    
    /**
     * The name is stored in a speical attribute. If the attribute
     * is not present, context name is returned instead.
     */
    public String getName() {
        String storedDisplayName = context.getAttribute(null, PROP_DISPLAY_NAME, null);
        if (storedDisplayName != null) {
            return storedDisplayName;
        }
        return context.getContextName();
    }
    
    /**
     * Display name is an attribute of the context.
     */
    public void setName(final String newName) {
        final String oldName = getName();
        WindowManager.getDefault().getMainWindow().setCursor(
            Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                try {
                    context.setAttribute(null, PROP_DISPLAY_NAME, newName);
                } finally {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            WindowManager.getDefault().getMainWindow().setCursor(
                                Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                        }
                    });
                }
            }
        });
    }
    
    /**
     * List new types that can be created in this node.
     * This implementaion allows creating new subfolders (subcontexts).
     * @return new types
     */
    public NewType[] getNewTypes () {
        return new NewType[] {
            new NewType() {
                public String getName() {
                    return NbBundle.getMessage(BookmarksNode.class, "LBL_NewFolder");
                }
                public void create () throws IOException {
                    String baseName = "Folder"; // NOI18N
                    // find an unique name to resName
                    String resName = baseName + "/"; // NOI18N
                    Collection childrenNames = context.getOrderedNames();
                    int i = 0;
                    while (childrenNames.contains(resName)) {
                        resName = baseName + "_" + i++ + "/"; // NOI18N
                    }
                    String folderName = 
                        NbBundle.getMessage(BookmarksNode.class, "LBL_NewFolder_Number", new Integer(i));
                    folderName = askUserAboutNewFolderName(folderName);
                    if (folderName == null) {
                        // cancelled
                        return;
                    }
                    // create the folder with the found name
                    try {
                        Context c = context.createSubcontext(resName);
                        c.setAttribute(null, PROP_DISPLAY_NAME, folderName);
                    } catch (ContextException ce) {
                        log.log(Level.INFO, resName, ce); // NOI18N
                    }
                    ArrayList al = new ArrayList(childrenNames);
                    al.add(resName);
                    context.orderContext(al);
                }
            }
        };
    }
    
    /**
     * @returns name or null if the action should be cancelled.
     */
    static String askUserAboutNewFolderName(String name) {
        
        NotifyDescriptor.InputLine nd = new NotifyDescriptor.InputLine(
                NbBundle.getBundle(BookmarksFolderNode.class).getString("CTL_NewBookmarkFolder"),
                NbBundle.getBundle(BookmarksFolderNode.class).getString("CTL_CreateBookmarkFolder"),
                NotifyDescriptor.OK_CANCEL_OPTION,
                NotifyDescriptor.QUESTION_MESSAGE
        );
        nd.setInputText(name);
        DialogDisplayer dd = DialogDisplayer.getDefault();
        Object ok = dd.notify(nd);
        if (ok == NotifyDescriptor.OK_OPTION) {
            return nd.getInputText();
        }
        
        // if the dialog was cancelled null means cancel the bookmark creation
        return null;
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
            if ((b != null) || (con != null)) {
                l.add(new PasteType() {
                    public String getName() {
                        return NbBundle.getMessage(BookmarksToolbarNode.class, "LBL_PasteType");
                    }
                    public Transferable paste() throws IOException {
                        try {
                            if (b != null) {
                                String safeName = BookmarkServiceImpl.findUnusedName(context, b.getName());
                                Collection childrenNames = context.getOrderedNames();
                                Bookmark b1 = BookmarkServiceImpl.cloneBookmark(b);
                                // following line will save the bookmark to the system file system
                                context.putObject(safeName, b1);
                                // make sure the added item ended at the end
                                ArrayList al = new ArrayList(childrenNames);
                                al.add(safeName);
                                context.orderContext(al);
                                BookmarkServiceImpl.saveBookmarkActionImpl(context, safeName);
                            }
                            if (con != null) {
                                try {
                                    RegistryUtil.copy(con, context, con.getContextName());
                                } catch (ContextException x) {
                                    IOException ioe = new IOException();
                                    ioe.initCause(x);
                                    throw ioe;
                                }
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
                                    b.firePropertyChange(BookmarksNode.PROP_DESTROYED, null, null);
                                }
                                
                                if (con != null) {
                                    // moving folder - delete the original
                                    Context parent = con.getParentContext();
                                    String conName = con.getContextName();
                                    deleteBookmarksFolder(con);
                                    if (parent.getSubcontext(conName) != null) {
                                        throw new IOException(conName + " was not deleted!");
                                    }
                                }
                            }
                            return ExTransferable.EMPTY;
                        } catch (ContextException x) {
                            IOException ioe =  new IOException();
                            ioe.initCause(x);
                            throw ioe;
                        }
                    }
                });
            }
        }
    }

    /**
     * Carefullly recursivelly deletes given context.
     */
    private static void deleteBookmarksFolder(Context con) throws ContextException {
        Iterator it = con.getSubcontextNames().iterator();
        while (it.hasNext()) {
            String subName = (String)it.next();
            Context sub = con.getSubcontext(subName);
            if (sub != null) {
                deleteBookmarksFolder(sub);
            }
        }
        Iterator it2 = con.getBindingNames().iterator();
        while (it2.hasNext()) {
            String bindingName = (String)it2.next();
            Object obj = con.getObject(bindingName, null);
            con.putObject(bindingName, null);
            if (obj instanceof Bookmark) {
                Bookmark impl = (Bookmark)obj;
                impl.firePropertyChange(BookmarksNode.PROP_DESTROYED, null, null);
            }
        }
        con.getParentContext().destroySubcontext(con.getContextName());
    }

    /** Serializable handle. */
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

        /** Our context - the context from where the children are created. */
        private Context context;
        
        /**
         * Simply remember the param.
         */
        public BookmarksChildren(Context c) {
            context = c;
        }

        /** Implementing ContextListener. Just update the keys. */
        public void attributeChanged(AttributeEvent evt) {
            updateKeys();
        }
        
        /** Implementing ContextListener. Just update the keys. */
        public void bindingChanged(BindingEvent evt) {
            updateKeys();
        }
        
        /** Implementing ContextListener. Just update the keys. */
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
        public void updateKeys() {
            // posting asynchronously since while setting the order
            // this method would be called to update the state of the children.
            // But it tries to access the list of children so it would either
            // loop or deadlock.
            RequestProcessor.getDefault().post(this, 300);
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
    
    /**
     * Class supporting reorder operation on current node.
     */
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
                nue[perm[i]] = items[i];
            }
            // Should trigger an automatic child node update because the children
            // should be listening:
            context.orderContext(Arrays.asList(nue));
        }
    }
}

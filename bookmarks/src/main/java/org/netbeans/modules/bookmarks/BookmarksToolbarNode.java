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

import java.awt.datatransfer.*;
import java.util.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;

import org.openide.actions.*;
import org.openide.nodes.*;
import org.openide.util.lookup.*;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.datatransfer.*;
import org.openide.util.actions.SystemAction;

import org.netbeans.api.bookmarks.*;
import org.netbeans.api.registry.*;

/**
 * This node is the node for customization of the bookmarks toolbar.
 * It allows pasting nodes providing bookmarks. 
 * @author David Strupl
 */
public class BookmarksToolbarNode extends AbstractNode {
    
    private Context context;
    
    /**
     * Default constructor.
     */
    public BookmarksToolbarNode() {
        this(null, null);
        setIconBaseWithExtension("org/netbeans/modules/bookmarks/resources/toolbars.gif");
        // Set FeatureDescriptor stuff:
        setName("BookmarksToolbar");
        setDisplayName(NbBundle.getMessage(BookmarksToolbarNode.class, "LBL_BookmarksToolbar"));
        setShortDescription(NbBundle.getMessage(BookmarksToolbarNode.class, "HINT_BookmarksToolbar"));
    }
    
    /** This private constructor is here because of the trick with
     * InstanceContent.
     */
    private BookmarksToolbarNode(InstanceContent ic, BookmarksToolbarChildren ch) {
        super(ch = new BookmarksToolbarChildren(), new AbstractLookup(ic = new InstanceContent()));
        ic.add(new ReorderSupport());
        ch.setContext(getContext());
    }
    
    /**
     * Adds actoin to the popup menu for this noce.
     */
    public Action[] getActions(boolean context) {
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
    
    /** Can this node be copied?
     * @return <code>false</code>
     */
    public boolean canCopy() {
        return false;
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
            String s = null;
            if (moving) {
                s = (String)n.getLookup().lookup(String.class);
            }
            final String whereToDelete = s;
            final boolean move = moving;
            if (b != null) {
                l.add(new PasteType() {
                    public String getName() {
                        return NbBundle.getMessage(BookmarksToolbarNode.class, "LBL_PasteType");
                    }
                    public Transferable paste() throws IOException {
                        if (b != null) {
                            Context targetFolder = getContext();
                            String safeName = BookmarkServiceImpl.findUnusedName(targetFolder, b.getName());
                            Collection childrenNames = context.getOrderedNames();
                            Bookmark b1 = BookmarkServiceImpl.cloneBookmark(b);
                            // following line will save the bookmark to the system file system
                            targetFolder.putObject(safeName, b1);
                            // make sure the added item ended at the end
                            ArrayList al = new ArrayList(childrenNames);
                            al.add(safeName);
                            context.orderContext(al);
                            putOldOrderAttribute(al);
                            BookmarkServiceImpl.saveBookmarkActionImpl(targetFolder, safeName);
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
                        }
                        return ExTransferable.EMPTY;
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
            // Remember: {2, 0, 1} cycles three items forwards.
            String[] items = (String[])getContext().getOrderedNames().toArray(new String[0]);
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
                nue[perm[i]] = items[i];
            }
            // Should trigger an automatic child node update because the children
            // should be listening:
            getContext().orderContext(Arrays.asList(nue));
            putOldOrderAttribute(Arrays.asList(nue));
        }
    }
    
    /**
     * Hack for the old data systems API that can read the order
     */
    private void putOldOrderAttribute(List names) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < names.size(); i++) {
            if (i != 0) {
                sb.append('/');
            }
            if ("org-netbeans-modules-bookmarks-actions-AddBookmarkAction".equals(names.get(i))) {
                // special case:
                sb.append(names.get(i) + ".instance");
            } else {
                // this is for normal bookmarks:
                sb.append(names.get(i) + ".xml");
            }
        }
        getContext().setAttribute(null, "OpenIDE-Folder-Order", sb.toString());
    }
    
    /** Lazy init of the context BOOKMARKS_TOOLBAR */
    private Context getContext() {
        if (context == null) {
            try {
                context = Context.getDefault().createSubcontext(BookmarkServiceImpl.BOOKMARKS_TOOLBAR);
            } catch (ContextException ne) {
                Logger.getLogger(BookmarksToolbarNode.class.getName()).log(
                    Level.WARNING, BookmarkServiceImpl.BOOKMARKS_TOOLBAR, ne);            }
        }
        return context;
    }
    
    /** List of children of the BookmarksToolbarNode.
     * Each child node is represented by one key of the type String. The String
     * is the name to what is the a bookmark bound in the bookmarks folder (context).
     */
    public static class BookmarksToolbarChildren extends Children.Keys implements ContextListener, Runnable {
        
        private Context context;
        
        public BookmarksToolbarChildren() {
        }

        private Context getContext() {
            return context;
        }
        
        public void setContext(Context c) {
            this.context = c;
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
            getContext().addContextListener(this);
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
            setKeys(getContext().getOrderedNames());
        }
        
        /** Overriden from Children.Keys */
        protected void removeNotify() {
            getContext().removeContextListener(this);
            setKeys(Collections.EMPTY_SET);
            super.removeNotify();
        }
        
        /** Overriden from Children.Keys */
        protected Node[] createNodes(Object key) {
            if (! (key instanceof String)) {
                return new Node[0];
            }
            String name = (String)key;
            Object data = getContext().getObject(name, null);
            if (data == null) {
                return new Node[0];
            }
            if (data instanceof Bookmark) {
                String absName = getContext().getAbsoluteContextName() + "/" + name; // NOI18N
                absName = absName.substring(1);
                return new Node[] { new BookmarksNode((Bookmark)data, absName) };
            }
            if (data instanceof Action) {
                String absName = getContext().getAbsoluteContextName() + "/" + name; // NOI18N
                absName = absName.substring(1);
                return new Node[] { new AddBookmarkNode((Action)data, absName) };
            }
            return new Node[0];
        }
    }
}

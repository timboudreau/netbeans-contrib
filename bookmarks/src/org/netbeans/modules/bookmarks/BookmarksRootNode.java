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
 * Software is Nokia. Portions Copyright 2003 Nokia.
 * All Rights Reserved.
 */

package org.netbeans.modules.bookmarks;

import javax.swing.Action;
import org.openide.actions.*;
import org.openide.nodes.*;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.ErrorManager;

import org.netbeans.api.registry.*;

/**
 * This node is the root node for the customization of bookmarks.
 * @author David Strupl
 */
public class BookmarksRootNode extends AbstractNode {

    /** Default constructor. Does not need any parameters since
     * this node is always the same.
     */
    public BookmarksRootNode() {
        super(new RootChildren());
        setIconBaseWithExtension("org/netbeans/modules/bookmarks/resources/BookmarksRootNodeIcon.gif");
        // Set FeatureDescriptor stuff:
        setName("BookmarksRoot"); // NOI18N
        setDisplayName(NbBundle.getMessage(BookmarksRootNode.class, "LBL_BookmarksRoot"));
        setShortDescription(NbBundle.getMessage(BookmarksRootNode.class, "HINT_BookmarksRoot"));
    }
    
    /** Nothing can be done with this node. */
    public Action[] getActions(boolean context) {
        return new SystemAction[] {
        };
    }
    
    /** Can this node be copied?
     * @return <code>false</code>
     */
    public boolean canCopy() {
        return false;
    }
    
   /**
     * Help context with ID corresponding to this class.
     */
    public HelpCtx getHelpCtx() {
        return new HelpCtx(BookmarksRootNode.class);
    }

    /**
     * Cloning just creates a new instance in this case.
     */
    public Node cloneNode() {
        return new BookmarksRootNode();
    }

    /** We are a root node so we need a serializable handle */
    public Node.Handle getHandle() {
        return new BookmarksRootHandle();
    }
    
    /**
     * This handle does not have to serialize anything - it is 
     * safe to create a fresh instance of BookmarksRootNode
     * each time.
     */
    private static final class BookmarksRootHandle implements Node.Handle {
        private static final long serialVersionUID = 1L;
        public BookmarksRootHandle() { }
        public Node getNode() throws java.io.IOException {
            return new BookmarksRootNode();
        }
    }
    
    /**
     * Creates one of the two children of this node.
     */
    private static Node getBookmarksNode() {
        try {
            Context con = Context.getDefault().createSubcontext(BookmarkServiceImpl.BOOKMARKS_FOLDER);
            return new BookmarksFolderNode(con, false) {
                public boolean canCopy() {
                    return false;
                }
            };
        } catch (ContextException ce) {
            ErrorManager.getDefault().getInstance("org.netbeans.modules.bookmarks").notify(ce);
        }
        return null;
    }
    
    /**
     * Creates one of the two children of this node.
     */
    private static Node getBookmarksToolbarNode() {
        return new BookmarksToolbarNode();
    }
    
    /** The children of this node are rather simple:
     * always two fixed children.
     */
    private static class RootChildren extends Children.Array {
        private Node[] children;
        public void addNotify() {
            children = new Node[] {
                getBookmarksToolbarNode(),
                getBookmarksNode()
            };
            add(children);
        }
        public void removeNotify() {
            remove(children);
            children = null;
        }
    }
}

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

import org.openide.actions.*;
import org.openide.nodes.*;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.filesystems.Repository;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.ErrorManager;

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
        setIconBase("org/netbeans/modules/bookmarks/resources/BookmarksRootNodeIcon");
        // Set FeatureDescriptor stuff:
        setName("BookmarksRoot"); // NOI18N
        setDisplayName(NbBundle.getMessage(BookmarksRootNode.class, "LBL_BookmarksRoot"));
        setShortDescription(NbBundle.getMessage(BookmarksRootNode.class, "HINT_BookmarksRoot"));
    }
    
    /** Nothing can be done with this node. */
    protected SystemAction[] createActions() {
        return new SystemAction[] {
        };
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
            FileObject fo = Repository.getDefault().getDefaultFileSystem().
            findResource(BookmarkServiceImpl.BOOKMARKS_FOLDER);
            Node n = DataObject.find(fo).getNodeDelegate().cloneNode();
            Node filter = new BookmarksNode(n);
            return filter;
        } catch (java.io.IOException ioe) {
            ErrorManager.getDefault().notify(ioe);
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

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
 * Software is Nokia. Portions Copyright 2003-2004 Nokia.
 * All Rights Reserved.
 */

package org.netbeans.modules.bookmarks;

import java.io.IOException;
import java.util.*;
import javax.swing.Action;

import org.openide.nodes.*;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;
import org.openide.actions.*;

import org.netbeans.api.registry.*;

/**
 * The nodes that serve for the bookmarks customization
 * are 
 * @author David Strupl
 */
public class AddBookmarkNode extends AbstractNode {
    
    public static final String PROP_DESTROYED = "destroyed";
    
    /** Should be either Context or Bookmark*/
    private Action addBookmarkAction;
    
    /** Absolute path of this object */
    private String path;
    
    /**
     * The only supported constructor takes the original node
     * as its parameter.
     */
    public AddBookmarkNode(Action a, String path) {
        super(Children.LEAF, Lookups.fixed(new Object[] { a, path } ));
        this.addBookmarkAction = a;
        this.path = path;
        setIconBaseWithExtension("org/netbeans/modules/bookmarks/resources/add.gif"); // NOI18N
    }
 
    /**
     * The handle just uses the original node's handle and wraps it
     * by BookmarksHandle.
     */
    public Node.Handle getHandle() {
        return new AddBookmarkHandle(path);
    }

    /**
     *
     */
    public String getName() {
        String s = (String)addBookmarkAction.getValue(Action.NAME);
        return org.openide.awt.Actions.cutAmpersand(s);
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
        return new SystemAction[] {
            SystemAction.get(MoveUpAction.class),
            SystemAction.get(MoveDownAction.class)
        };
    }
    
    /**  */
    private static class AddBookmarkHandle implements Node.Handle {
        private static final long serialVersionUID = 1L;
        private String name;
        public AddBookmarkHandle(String name) {
            this.name = name;
        }
        public Node getNode() throws IOException {
            Object d = Context.getDefault().getObject(name, null);
            if (d instanceof Action) {
                return new AddBookmarkNode((Action)d , name);
            }
            throw new IOException("Cannot create node with name " + name); // NOI18N
        }
    }
}

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

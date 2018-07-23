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
 * Software is Nokia. Portions Copyright 2003 Nokia.
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

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import org.openide.actions.*;
import org.openide.nodes.*;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;

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
            Logger.getLogger(BookmarksRootNode.class.getName()).log(
                Level.WARNING, BookmarkServiceImpl.BOOKMARKS_FOLDER, ce);
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

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
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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

package org.netbeans.modules.tasklist.usertasks.treetable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;

import javax.swing.tree.TreeNode;

/**
 * Abstract node for a TreeTable
 */
public abstract class AbstractTreeTableNode implements TreeTableNode {
    /** could be used to show that this node has no children */
    protected static TreeTableNode[] EMPTY_CHILDREN = {};

    protected TreeTableNode parent;

    /** children of this node. null means "not yet loaded" */
    protected TreeTableNode children[];
    
    /** 
     * Creates a new instance of AbstractTreeTableNode 
     *
     * @param parent parent of this node or null if this node is a root
     */
    public AbstractTreeTableNode(TreeTableNode parent) {
        this.parent = parent;
    }
    
    /**
     * Should load the children in the field <code>children</code>
     */
    protected abstract void loadChildren();
    
    /**
     * Returns an array with children of this node
     *
     * @return array with children
     */
    public TreeTableNode[] getChildren() {
        if (children == null)
            loadChildren();
        return children;
    }

    public boolean isCellEditable(int column) {
        return false;
    }

    public TreeNode getChildAt(int childIndex) {
        return getChildren()[childIndex];
    }

    public int getChildCount() {
        return getChildren().length;
    }

    public TreeNode getParent() {
        return parent;
    }

    public int getIndex(TreeNode node) {
        TreeTableNode[] ch = getChildren();
        for (int i = 0; i < ch.length; i++) {
            if (ch[i] == node)
                return i;
        }
        return -1;
    }

    public boolean getAllowsChildren() {
        return true;
    }

    public boolean isLeaf() {
        return false;
    }

    public Enumeration children() {
        return Collections.enumeration(Arrays.asList(getChildren()));
    }

    /**
     * todo
     */
    public void refreshChildren() {
        this.children = null;
    }
    
    /**
     * Returns path from this node to the root 
     *
     * @return path to the root
     */
    public TreeTableNode[] getPathToRoot() {
        List<TreeTableNode> path = new ArrayList<TreeTableNode>();
        TreeTableNode n = this;
        while (n != null) {
            path.add(0, n);
            n = (TreeTableNode) n.getParent();
        }
        return path.toArray(new TreeTableNode[path.size()]);
    }
    
    /**
     * Finds the next node that should be selected after this node was
     * deleted
     *
     * @return node to select or null
     */
    public TreeTableNode findNextNodeAfterDelete() {
        if (getParent() == null)
            return null;
        if (getParent().getChildCount() == 1)
            return (TreeTableNode) getParent();
        int index = getParent().getIndex(this);
        if (index == getParent().getChildCount() - 1)
            return (TreeTableNode) getParent().getChildAt(index - 1);
        else
            return (TreeTableNode) getParent().getChildAt(index + 1);
    }
}

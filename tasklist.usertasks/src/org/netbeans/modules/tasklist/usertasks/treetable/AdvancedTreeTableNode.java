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

import javax.swing.Icon;
import org.netbeans.modules.tasklist.usertasks.treetable.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.netbeans.modules.tasklist.usertasks.util.AWTThread;

/**
 * This "advanced" class provides filtering and sorting of nodes
 *
 * @param T - type of the object in this node
 * @author tl
 */
public abstract class AdvancedTreeTableNode<T>
        extends AbstractTreeTableNode {
    protected FilterIntf filter;
    protected Comparator<AdvancedTreeTableNode> comparator;
    protected DefaultTreeTableModel model;
    
    /** an object associated with this node. */
    protected T object;
    
    /** 
     * Creates a new instance of AdvancedTreeTableNode 
     *
     * @param model tree table model this node belongs to
     * @param parent parent of this node or null if this node is a root
     * @param object object associated with this node
     */
    public AdvancedTreeTableNode(DefaultTreeTableModel model, 
        TreeTableNode parent, T object) {
        super(parent);
        this.model = model;
        this.object = object;
    }

    /**
     * Returns object associated with this node
     *
     * @return object
     */
    public T getObject() {
        return object;
    }
    
    /**
     * Searches in this node and all subnodes at all levels for an object using
     * == for comparison.
     * 
     * @param obj this object should be found
     * @return found node or null 
     */
    public AdvancedTreeTableNode findObjectDeep(Object obj) {
        if (this.getObject() == obj)
            return this;
        else {
            for (int i = 0; i < getChildCount(); i++) {
                AdvancedTreeTableNode ch = (AdvancedTreeTableNode) getChildAt(i);
                AdvancedTreeTableNode n = ch.findObjectDeep(obj);
                if (n != null)
                    return n;
            }
            return null;
        }
    }
    
    /**
     * Finds a child with the specified user object
     *
     * @param obj user object
     * @return found node index or -1
     */
    public int getIndexOfObject(Object obj) {
        AdvancedTreeTableNode[] ch = (AdvancedTreeTableNode[]) getChildren();
        for (int i = 0; i < ch.length; i++) {
            if (ch[i].getObject() == obj)
                return i;
        }
        return -1;
    }

    /**
     * Sets new comparator or null. This comparator will compare child nodes.
     *
     * @param comparator new comparator
     */
    public void setComparator(Comparator<AdvancedTreeTableNode> comparator) {
        if (this.comparator == comparator)
            return;
        
        this.comparator = comparator;
        
        if (this.children != null) {
            if (this.comparator != null) {
                Arrays.sort((AdvancedTreeTableNode[]) children, 
                        this.comparator);
            } else {
                AdvancedTreeTableNode[] newch = 
                    new AdvancedTreeTableNode[children.length];
                Iterator<?> it = this.getChildrenObjectsIterator();
                int i = 0;
                while (it.hasNext()) {
                    int index = getIndexOfObject(it.next());
                    if (index >= 0)
                        newch[i++] = (AdvancedTreeTableNode) children[index];
                }
                children = newch;
            }
            model.fireTreeStructureChanged(model, getPathToRoot());
            for (int i = 0; i < children.length; i++) {
                ((AdvancedTreeTableNode<?>) children[i]).
                        setComparator(comparator);
            }
        }
    }
    
    /**
     * Gets a comparator
     *
     * @return comparator or null
     */
    public Comparator<AdvancedTreeTableNode> getComparator() {
        return comparator;
    }
    
    /**
     * Gets a filter. You should not call getFilter().accept(). 
     * Use AdvancedTreeTableNode.accept() instead
     *
     * @return filter or null
     */
    public FilterIntf getFilter() {
        return filter;
    }

    /**
     * Sets new filter
     *
     * @param filter new filter or null
     */
    public void setFilter(FilterIntf filter) {
        this.filter = filter;
        this.children = null;
    }
    
    /**
     * Iterator over the objects of children nodes. The filter should not
     * be yet applied.
     *
     * @return the iterator
     */
    public abstract Iterator<?> getChildrenObjectsIterator();
    
    /**
     * Creates a children node
     *
     * @param child child's object
     * @return created node
     */
    public abstract AdvancedTreeTableNode<?> createChildNode(Object child);
    
    /**
     * Filtering
     *
     * @param child a child object
     * @return true = the object is accepted
     */
    public boolean accept(Object child) {
        if (getFilter() != null)
            return getFilter().accept(child);
        else
            return true;
    }
    
    protected void loadChildren() {
        List<Object> ch = new ArrayList<Object>();
        Iterator<?> it = getChildrenObjectsIterator();
        while (it.hasNext()) {
            ch.add(it.next());
        }
        
        // filtering
        if (getFilter() != null) {
            it = ch.iterator();
            while (it.hasNext()) {
                if (!accept(it.next()))
                    it.remove();
            }
        }
        
        children = new AdvancedTreeTableNode[ch.size()];
        for (int i = 0; i < ch.size(); i++) {
            children[i] = createChildNode(ch.get(i));
        }

        // sorting
        if (getComparator() != null)
            Arrays.sort((AdvancedTreeTableNode[]) children, getComparator());
    }

    public void refreshChildren() {
        if (children != null) {
            for (int i = 0; i < children.length; i++) {
                ((AdvancedTreeTableNode) children[i]).destroy();
            }
            this.children = null;
        }
        model.fireTreeStructureChanged(model, this.getPathToRoot());
    }

    /**
     * Will be called after removing the node from the hierarchy
     */
    public void destroy() {
        this.parent = null;
        if (children != null) {
            for (int i = 0; i < children.length; i++) {
                ((AdvancedTreeTableNode) children[i]).destroy();
            }
        }
    }
    
    /**
     * Fires the appropriate events if the children under this node
     * were reordered.
     */
    protected void fireChildObjectsReordered() {
        if (children != null) {
            if (this.comparator == null) {
                AdvancedTreeTableNode[] newch = 
                    new AdvancedTreeTableNode[children.length];
                Iterator<?> it = this.getChildrenObjectsIterator();
                int i = 0;
                while (it.hasNext()) {
                    int index = getIndexOfObject(it.next());
                    if (index >= 0)
                        newch[i++] = (AdvancedTreeTableNode) children[index];
                }
                children = newch;
                model.fireTreeStructureChanged(model, getPathToRoot());
            }
        }
    }
    
    /**
     * Fires the appropriate events if a child object was removed
     *
     * @param obj one of the child nodes objects
     */
    protected void fireChildObjectRemoved(Object obj) {
        if (children != null) {
            int ind = getIndexOfObject(obj);
            if (ind >= 0) {
                AdvancedTreeTableNode rem = 
                        (AdvancedTreeTableNode) children[ind];
                AdvancedTreeTableNode[] newChildren =
                        new AdvancedTreeTableNode[children.length - 1];
                System.arraycopy(children, 0, newChildren, 0, ind);
                System.arraycopy(children, ind + 1, newChildren, 
                        ind, children.length - ind - 1);
                rem.destroy();
                children = newChildren;
                model.fireTreeNodesRemoved(model, 
                        getPathToRoot(), 
                        new int[] {ind}, new Object[] {rem});
            }
        }
    }
    
    /**
     * Fires the appropriate events if a child object was added
     *
     * @param obj new child nodes object
     */
    protected void fireChildObjectAdded(Object obj) {
        if (children != null) {
            if (!accept(obj))
                return;
            
            AdvancedTreeTableNode cn = createChildNode(obj);
            
            int index;
            if (getComparator() != null) {
                index = Arrays.binarySearch(
                        (AdvancedTreeTableNode[]) children, cn, getComparator());
                if (index < 0)
                    index = -(index + 1);
            } else {
                index = -1;
                Iterator<?> it = getChildrenObjectsIterator();
                while (it.hasNext()) {
                    Object next = it.next();
                    if (!accept(next))
                        continue;
                    index++;
                    if (next == obj)
                        break;
                }
            }
            
            AdvancedTreeTableNode[] newch = 
                new AdvancedTreeTableNode[children.length + 1];
            System.arraycopy(children, 0, newch, 0, index);
            newch[index] = cn;
            System.arraycopy(children, index, newch, index + 1, 
                    children.length - index);
            this.children = newch;
            model.fireTreeNodesInserted(model, getPathToRoot(), 
                    new int[] {index}, new Object[] {cn});
        }
    }

    /**
     * Fires the appropriate events if the object in this node has changed.
     * WARNING: This method could only be called from the Swing Event Thread!
     */
    @AWTThread
    @SuppressWarnings("unchecked")
    protected void fireObjectChanged() {
        // TreeNode is not generic => SuppressWarnings
        AdvancedTreeTableNode parent = 
                (AdvancedTreeTableNode) getParent(); 
        if (parent != null) {
            TreeTableNode[] path = parent.getPathToRoot();

            assert parent.getIndex(this) != -1 : "parent=" + parent + // NOI18N
                " this=" + this +  // NOI18N
                " parent.getChildCount=" + parent.getChildCount() +  // NOI18N
                " parent.getChild(0)=" + parent.getChildAt(0); // NOI18N
            model.fireTreeNodesChanged(model, path, 
                new int[] {parent.getIndex(this)}, new Object[] {this});

            parent.childNodeChanged(this);
        } else {
            model.fireTreeNodesChanged(model, new Object[0], 
                    new int[1], new Object[] {this});
        }
    }
    
    /**
     * This method will be called to notify this node that a child node's
     * object has changed and the node should be probably removed 
     * according to the current filter
     *
     * @param child changed child node
     */
    protected void childNodeChanged(AdvancedTreeTableNode<?> child) {
        if (getFilter() != null) {
            if (!accept(child.getObject())) {
                // this call is not really the right one here.
                // The object was not removed. The current filter just
                // does not accept it.
                fireChildObjectRemoved(child.getObject());
                return;
            }
        }
        
        if (this.comparator != null) {
            Arrays.sort((AdvancedTreeTableNode[]) children, 
                    this.comparator);
            model.fireTreeStructureChanged(model, getPathToRoot());
        }
    }

    public boolean isLeaf() {
        return getChildren().length == 0;
    }

    public void setValueAt(Object aValue, int column) {
    }
    
    /**
     * Returns icon for this node.
     * 
     * @return icon or null 
     */
    public Icon getOpenIcon() {
        return null;
    }
    
    /**
     * Returns icon for this node (when the node is closed).
     * 
     * @return icon or null 
     */
    public Icon getClosedIcon() {
        return null;
    }
}

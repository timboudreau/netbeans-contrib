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

/*
 * this file is derived from the "Creating TreeTable" article at
 * http://java.sun.com/products/jfc/tsc/articles/treetable2/index.html
 */
package org.netbeans.modules.tasklist.usertasks.treetable;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreePath;
import org.netbeans.modules.tasklist.core.table.SortingModel;

/**
 * An abstract implementation of the TreeTableModel interface, handling the list
 * of listeners.
 *
 * @author Philip Milne
 * @author tl
 */
public abstract class AbstractTreeTableModel implements TreeTableModel {
    /** root node */
    protected Object root;
    
    /** TreeModelListener[] */
    protected EventListenerList listenerList = new EventListenerList();
    
    /**
     * Creates a table model for the given root node.
     *
     * @param root root node
     */
    public AbstractTreeTableModel(Object root) {
        this.root = root;
    }
    
    /**
     * Sets new root node
     *
     * @param root new root node
     */
    public void setRoot(Object root) {
        if (this.root != root) {
            this.root = root;
            fireTreeStructureChanged(this, new Object[] {root}, null, null);
        }
    }
    
    //
    // Default implmentations for methods in the TreeModel interface.
    //
    
    public Object getRoot() {
        return root;
    }
    
    public boolean isLeaf(Object node) {
        return getChildCount(node) == 0;
    }
    
    public void valueForPathChanged(TreePath path, Object newValue) {}
    
    // This is not called in the JTree's default mode: use a naive implementation.
    public int getIndexOfChild(Object parent, Object child) {
        for (int i = 0; i < getChildCount(parent); i++) {
            if (getChild(parent, i).equals(child)) {
                return i;
            }
        }
        return -1;
    }
    
    public void addTreeModelListener(TreeModelListener l) {
        listenerList.add(TreeModelListener.class, l);
    }
    
    public void removeTreeModelListener(TreeModelListener l) {
        listenerList.remove(TreeModelListener.class, l);
    }
    
    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @see EventListenerList
     */
    public void fireTreeStructureChanged(Object source, Object[] path) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        TreePath tp = path == null ? null : new TreePath(path);
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, tp);
                ((TreeModelListener)listeners[i+1]).treeStructureChanged(e);
            }
        }
    }
    
    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @see EventListenerList
     */
    protected void fireTreeNodesChanged(Object source, Object[] path,
    int[] childIndices,
    Object[] children) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path,
                    childIndices, children);
                ((TreeModelListener)listeners[i+1]).treeNodesChanged(e);
            }
        }
    }
    
    /**
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @see EventListenerList
     */
    protected void fireTreeNodesInserted(Object source, Object[] path,
    int[] childIndices,
    Object[] children) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path,
                    childIndices, children);
                ((TreeModelListener)listeners[i+1]).treeNodesInserted(e);
            }
        }
    }
    
    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @see EventListenerList
     */
    protected void fireTreeNodesRemoved(Object source, Object[] path,
    int[] childIndices,
    Object[] children) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path,
                    childIndices, children);
                ((TreeModelListener)listeners[i+1]).treeNodesRemoved(e);
            }
        }
    }
    
    /*
     * Notify all listeners that have registered interest for
     * notification on this event type.  The event instance
     * is lazily created using the parameters passed into
     * the fire method.
     * @see EventListenerList
     */
    public void fireTreeStructureChanged(Object source, Object[] path,
    int[] childIndices, Object[] children) {
        // Guaranteed to return a non-null array
        Object[] listeners = listenerList.getListenerList();
        TreeModelEvent e = null;
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = listeners.length-2; i>=0; i-=2) {
            if (listeners[i]==TreeModelListener.class) {
                // Lazily create the event:
                if (e == null)
                    e = new TreeModelEvent(source, path,
                    childIndices, children);
                ((TreeModelListener)listeners[i+1]).treeStructureChanged(e);
            }
        }
    }
    
    //
    // Default implementations for methods in the TreeTableModel interface.
    //
    
    public Class getColumnClass(int column) { return Object.class; }
    
    /** By default, make the column with the Tree in it the only editable one.
     *  Making this column editable causes the JTable to forward mouse
     *  and keyboard events in the Tree column to the underlying JTree.
     */
    public boolean isCellEditable(Object node, int column) {
        // CHANGEEDIT
        // return getColumnClass(column) == TreeTableModel.class;
        return false;
    }
    
    public void setValueAt(Object aValue, Object node, int column) {}
    
    
    // Left to be implemented in the subclass:
    
    /*
     *   public Object getChild(Object parent, int index)
     *   public int getChildCount(Object parent)
     *   public int getColumnCount()
     *   public String getColumnName(Object node, int column)
     *   public Object getValueAt(Object node, int column)
     */
}

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

package org.netbeans.api.tableview;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import javax.swing.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.Iterator;

import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.NbBundle;

/**
* Table model with properties (<code>Node.Property</code>) as columns and nodes (<code>Node</code>) as rows.
* It is used as model for displaying node properties in table. Each column is represented by
* <code>Node.Property</code> object. Each row is represented by <code>Node</code> object.
* Each cell contains <code>Node.Property</code> property which equals with column object
* and should be in property sets of row representant (<code>Node</code>).
*
* @author David Strupl
*/
public class NodeTableModel extends AbstractTableModel {

    /**
     * Boolean attribute of Node.Property that can determine whether
     * the column for the property will be sortable.
     */
    public static final String ATTR_SORTABLE_COLUMN = "SortableColumn"; // NOI18N
    
    /** all columns of model */
    private Node.Property[] allPropertyColumns = new Node.Property[0];
    /** rows of model */
    private List/*<Node>*/ nodeRows = new ArrayList();
    
    /** listener on node properties changes, recreates displayed data */
    private PropertyChangeListener pcl = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent evt) {
            //fireTableDataChanged();
            int row = rowForNode((Node)evt.getSource());
            if (row == -1) {
                return;
            }

            int column = columnForProperty(evt.getPropertyName());
            if (column == -1) {
                fireTableRowsUpdated(row, row);
            } else {
                fireTableCellUpdated(row, column);
            }
        }
    };
    
    /**
     *
     */
    private int columnForProperty(String propName) {
        for (int i = 0; i < allPropertyColumns.length; i++) {
            if (allPropertyColumns[i].getName().equals(propName))
                return i;
        }
        return -1;
    }
    
    /** Set rows.
     * @param nodes the rows
     */
    public void setNodes(Node[] nodes) {
        for (Iterator i = nodeRows.iterator(); i.hasNext(); ) {
            Node n = (Node)i.next();
            n.removePropertyChangeListener(pcl);
        }
        nodeRows = new ArrayList(Arrays.asList(nodes));
        for (Iterator i = nodeRows.iterator(); i.hasNext(); ) {
            Node n = (Node)i.next();
            n.addPropertyChangeListener(pcl);
        }
        fireTableDataChanged();
    }
    
    /**
     * The passed in list can be mutable but you <strong>must</strong> call
     * fireTableDataChanged(...) after you do any changes in the list otherwise
     * the view will not be updated.
     */
    public void setNodes(List/*<Node>*/ list) {
        for (Iterator i = nodeRows.iterator(); i.hasNext(); ) {
            Node n = (Node)i.next();
            n.removePropertyChangeListener(pcl);
        }
        for (Iterator i = list.iterator(); i.hasNext(); ) {
            Object o = i.next();
            if (o == null) {
                throw new IllegalArgumentException("Null value not allowed here!");
            }
            if (! (o instanceof Node)) {
                throw new IllegalArgumentException("The list must contain Nodes but contained: " + o + " of class " + o.getClass().getName());
            }
            Node n = (Node)o;
            n.addPropertyChangeListener(pcl);
        }
        // ok - replace it:
        nodeRows = list;
        fireTableDataChanged();
    }

    /**
     * Adds the given nodes to the end of the list.
     */
    public void appendNodes(Node[] nodes) {
        for (int i = 0; i < nodes.length; i++) {
            nodes[i].addPropertyChangeListener(pcl);
        }
        nodeRows.addAll(Arrays.asList(nodes));
        fireTableRowsInserted(nodeRows.size() - nodes.length, nodeRows.size());
    }
    
    /**
     * Deletes several rows (nodes).
     */
    public void deleteNodes(int first, int count) {
        if ((first < 0) || (count < 0)) {
            throw new IllegalArgumentException();
        }
        if (nodeRows.size() < first+count) {
            throw new IllegalArgumentException();
        }
        for (int i = first; i < first + count; i++) {
            Node n = (Node)nodeRows.get(first);
            n.removePropertyChangeListener(pcl);
            nodeRows.remove(first);
        }
        fireTableRowsDeleted(first, first+count-1);
    }
    
    /**
     * Inserts several rows (nodes).
     */ 
    public void insertNodes(int index, Node[] nodes) {
        if ((index < 0) || (index >= nodeRows.size())) {
            throw new IllegalArgumentException();
        }
        if (nodes.length == 0) {
            return;
        }
        for (int i = 0; i < nodes.length; i++) {
            nodes[i].addPropertyChangeListener(pcl);
            nodeRows.add(index + i, nodes[i]);
        }
        fireTableRowsInserted(index, index + nodes.length-1);
    }
    
    /** Set columns.
     * @param props the columns
     */
    public void setProperties(Property[] props) {
        allPropertyColumns = props;
        fireTableStructureChanged();
    }
    
    /* If true, column property should be comparable - allows sorting
     */
    boolean isComparableColumn(int column) {
        Property p = allPropertyColumns[column];
        Object o = p.getValue(ATTR_SORTABLE_COLUMN);
        if (o != null && o instanceof Boolean) {
            return ((Boolean)o).booleanValue();
        }
        return true;
    }
    
    /** Returns node property if found in nodes property sets.
     * @param node represents single row
     * @param prop represents column
     * @return nodes property
     */
    static Property getPropertyFor(Node node, Property prop) {
        Node.PropertySet[] propSets = node.getPropertySets();
        for (int i = 0; i < propSets.length; i++) {
            Node.Property[] props = propSets[i].getProperties();
            for (int j = 0; j < props.length; j++) {
                if (prop.equals(props[j]))
                    return props[j];
            }
        }
        return null;
    }
    
    /**
     * 
     */
    public Node nodeForRow(int row) {
        return (Node)nodeRows.get(row);
    }
    
    /** Helper method to ask for a property representant of column.
     */
    public Property propertyForColumn(int column) {
        return allPropertyColumns[column];
    }

    /**
     * Not terribly efficient method for what it does ;-(
     */
    private int rowForNode(Node node) {
        int i = 0; 
        for (Iterator it = nodeRows.iterator(); it.hasNext(); i++) {
            Node n = (Node)it.next();
            if (node.equals(n)) {
                return i;
            }
        }
        return -1;
    }
    
    //
    // TableModel methods
    //
    
    /** Getter for row count.
     * @return row count
     */
    public int getRowCount() {
        return nodeRows.size();
    }

    /** Getter for column count.
     * @return column count
     */
    public int getColumnCount() {
        return allPropertyColumns.length;
    }

    /** Getter for property.
     * @param row table row index
     * @param column table column index
     * @return property at (row, column)
     */
    public Object getValueAt(int row, int column) {
        return getPropertyFor((Node)nodeRows.get(row), allPropertyColumns[column]);
    }

    /** Cell is editable only if it has non null value.
     * @param row table row index
     * @param column table column index
     * @return true if cell contains non null value
     */
    public boolean isCellEditable(int row, int column) {
        return getValueAt(row, column) != null;
    }

    /** Getter for column class.
     * @param column table column index
     * @return  <code>Node.Property.class</code>
     */
    public Class getColumnClass(int column) {
        return Node.Property.class;
    }

    /** Getter for column name
     * @param column table column index
     * @return display name of property which represents column
     */
    public String getColumnName(int column) {
        return allPropertyColumns[column].getDisplayName();
    }
}

/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is the ETable module. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2005 Nokia. All Rights Reserved.
 */
package org.netbeans.api.tableview;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.WeakHashMap;
import javax.swing.event.TableModelEvent;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.netbeans.swing.outline.Outline;
import org.netbeans.swing.outline.RowModel;
import org.openide.ErrorManager;
import org.openide.explorer.view.Visualizer;
import org.openide.nodes.Node;
import org.openide.util.WeakListeners;

/**
 *
 * @author David Strupl
 */
class PropertiesRowModel implements RowModel {
   
    private Node.Property[] prop = new Node.Property[0];
    private Outline outline;
    private WeakHashMap/*<Node, PropertyChangeListener>*/ nodesListenersCache = new WeakHashMap();
    
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
                outline.tableChanged(new TableModelEvent(outline.getModel(), row, row,
                             TableModelEvent.ALL_COLUMNS, TableModelEvent.UPDATE));
            } else {
                outline.tableChanged(new TableModelEvent(outline.getModel(), row, row,
                             column+1, TableModelEvent.UPDATE));
            }
        }
    };
    
    /** Creates a new instance of PropertiesRowModel */
    public PropertiesRowModel() {
        
    }
    
    public void setOutline(Outline outline) {
        this.outline = outline;
    }
    
    private int rowForNode(Node n) {
        TreeNode tn = Visualizer.findVisualizer(n);
        if (tn != null) {
            ArrayList al = new ArrayList();
            while (tn != null) {
                al.add(tn);
                tn = tn.getParent();
            }
            Collections.reverse(al);
            TreePath tp = new TreePath(al.toArray());
            int row = outline.getLayoutCache().getRowForPath(tp);
            return row;
        }
        return -1;
    }

    public Class getColumnClass(int column) {
        return Node.Property.class;
    }

    public int getColumnCount() {
        return prop.length;
    }

    public String getColumnName(int column) {
        return prop[column].getDisplayName();
    }

    public Object getValueFor(Object node, int column) {
        Node n = Visualizer.findNode(node);
        if (n == null) {
            throw new IllegalStateException("TreeNode must be VisualizerNode but was: " + node + " of class " + node.getClass().getName());
        }
        PropertyChangeListener cacheEntry = (PropertyChangeListener)nodesListenersCache.get(n);
        if (cacheEntry == null) {
            PropertyChangeListener p = WeakListeners.propertyChange(pcl, n);
            nodesListenersCache.put(n, p);
            n.addPropertyChangeListener(p);
        }
        Node.Property theRealProperty = NodeTableModel.getPropertyFor(n, prop[column]);
        return theRealProperty;
    }

    public boolean isCellEditable(Object node, int column) {
        Node n = Visualizer.findNode(node);
        if (n == null) {
            throw new IllegalStateException("TreeNode must be VisualizerNode but was: " + node + " of class " + node.getClass().getName());
        }
        Node.Property theRealProperty = NodeTableModel.getPropertyFor(n, prop[column]);
        if (theRealProperty != null) {
            return theRealProperty.canWrite();
        } else {
            return false;
        }
    }

    public void setValueFor(Object node, int column, Object value) {
        // Intentionally left empty. The cell editor components are
        // PropertyPanels that will propagate the change into the target
        // property object - no need to do anything in this method.
    }
    
    public void setProperties(Node.Property[] newProperties) {
        prop = newProperties;
    }
    
    /**
     * Of the parameter is of type Node.Property this methods
     * calls getValue on the property and returns the value.
     * If the parameter is something else <code>null</code>
     * is returned.
     */
    public static Object getValueFromProperty(Object property) {
        if (property instanceof Node.Property) {
            Node.Property prop = (Node.Property)property;
            try {
                return prop.getValue();
            } catch (Exception x) {
                ErrorManager.getDefault().getInstance(
                    PropertiesRowModel.class.getName()).notify(
                        ErrorManager.INFORMATIONAL, x);
            }
        }
        return null;
    }
    /**
     * Search the properties for given property name.
     * The returned value is the index of property: you
     * have to add 1 to make it the column index because the
     * column with index 0 is reserved for the tree!
     */
    private int columnForProperty(String propName) {
        for (int i = 0; i < prop.length; i++) {
            if (prop[i].getName().equals(propName))
                return i;
        }
        return -1;
    }

    /**
     * Changes the value of the boolean property.
     */
    public static void toggleBooleanProperty(Node.Property p) {
        if (p.getValueType() == Boolean.class || p.getValueType() == Boolean.TYPE) {
            if (!p.canWrite()) {
                return;
            }
            try {
                Boolean val = (Boolean) p.getValue();
                if (Boolean.FALSE.equals(val)) {
                    p.setValue(Boolean.TRUE);
                } else {
                    //This covers null multi-selections too
                    p.setValue(Boolean.FALSE);
                }
            } catch (Exception e1) {
                ErrorManager.getDefault().notify(ErrorManager.WARNING, e1);
            }
        }
    }    
}

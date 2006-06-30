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
 * The Original Software is the LaTeX module.
 * The Initial Developer of the Original Software is Jan Lahoda.
 * Portions created by Jan Lahoda_ are Copyright (C) 2002-2004.
 * All Rights Reserved.
 *
 * Contributor(s): Jan Lahoda.
 */
package org.netbeans.modules.latex.bibtex;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.swing.ActionMap;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;

import javax.swing.table.TableColumn;
import javax.swing.text.DefaultEditorKit;
import org.netbeans.modules.latex.bibtex.loaders.BiBTexDataObject;
import org.netbeans.modules.latex.bibtex.nodes.PublicationEntryNode;
import org.netbeans.modules.latex.bibtex.table.SortingTable;
import org.openide.ErrorManager;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.ExplorerManager.Provider;
import org.openide.nodes.Node;
import org.openide.nodes.NodeEvent;
import org.openide.nodes.NodeListener;
import org.openide.nodes.NodeMemberEvent;
import org.openide.nodes.NodeReorderEvent;
import org.openide.nodes.Node.Property;
import org.openide.nodes.Node.PropertySet;
import org.openide.nodes.PropertySupport.ReadOnly;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;
import org.openide.windows.Mode;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;


/**TODO: free the resources when the component becomes invisible.
 *
 * @author Jan Lahoda
 */
public class OpenBiBComponent extends TopComponent implements ListSelectionListener, PropertyChangeListener, NodeListener, Provider {
    
    private BiBTexDataObject od;
    private JTable       table;
    private EntryTableModel tableModel;
    private ExplorerManager manager;
    
    /** Creates a new instance of BiBTest */
    private OpenBiBComponent(BiBTexDataObject od) {
        this.od = od;
        if (od == null)
            throw new NullPointerException();
        
        manager = new ExplorerManager();
    }
    
    private static Map/*<DataObject, OpenBiBComponent>*/ file2TC = null;
    
    public static synchronized void open(BiBTexDataObject od) {
        if (file2TC == null)
            file2TC = new HashMap();
        
        OpenBiBComponent open = (OpenBiBComponent) file2TC.get(od);
        
        if (open == null) {
            open = new OpenBiBComponent(od);
            
            file2TC.put(od, open);
            open.open();
        }
        
        open.requestActive();
    }

    public void open() {
        setLayout(new BorderLayout());
        table = new SortingTable();
        table.setModel(tableModel = getNodeTableModel());
        TableColumn typeColumn = table.getColumnModel().getColumn(0); //TODO: 0->some better way
        
        JComboBox comboBox = new JComboBox();
        
        for (Iterator i = FieldDatabase.getDefault().getKnownTypes().iterator(); i.hasNext(); ) {
            comboBox.addItem(i.next());
        }
        
        comboBox.setEditable(true);
        
        typeColumn.setCellEditor(new DefaultCellEditor(comboBox));
        
        getExplorerManager().addPropertyChangeListener(this);
        getExplorerManager().setRootContext(od.getNodeDelegate());
        
        table.getSelectionModel().addListSelectionListener(this);
        
        table.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent me) {
                showPopup(me);
            }
 
            public void mouseReleased(MouseEvent me) {
                showPopup(me);
            }
        });
        JScrollPane scrollPane = new JScrollPane(table);
        
        scrollPane.setCorner(JScrollPane.UPPER_RIGHT_CORNER, new JButton("..."));
        add(scrollPane);

        ActionMap map = getActionMap ();
        map.put(DefaultEditorKit.copyAction, ExplorerUtils.actionCopy(manager));
        map.put(DefaultEditorKit.cutAction, ExplorerUtils.actionCut(manager));
        map.put(DefaultEditorKit.pasteAction, ExplorerUtils.actionPaste(manager));
        map.put("delete", ExplorerUtils.actionDelete(manager, true)); // or false

        // following line tells the top component which lookup should be associated with it
        associateLookup (ExplorerUtils.createLookup (manager, map));//!???

        Mode mode = WindowManager.getDefault().findMode("output");
        
        mode.dockInto(this);
        
        setDisplayName("BiBTeX - " + od.getName());
        
        super.open();
    }
    
    private void showPopup(MouseEvent ev) {
        if (ev.isPopupTrigger()) {
            int row = table.rowAtPoint(ev.getPoint());
            
            if (row != (-1)) {
                Node n = tableModel.getNodes()[row];
                JPopupMenu m = n.getContextMenu();
                
                m.show(table, (int) ev.getPoint().getX(), (int) ev.getPoint().getY());
            }
        }
    }
    
    
    private EntryTableModel getNodeTableModel() {
        Node[] nodes = getTheMainNode().getChildren().getNodes(true);
        
        EntryTableModel m = new EntryTableModel();
        
        m.setProperties(new String[] {"type", "tag", "title", "author"});
//        m.setNodes(nodes);
        
        return m;
    }
    
    private Node getTheMainNode() {
        return od.getNodeDelegate();
    }

    public void valueChanged(ListSelectionEvent e) {
        doSetActivatedNodes();
    }
    
    private void doSetActivatedNodes() {
        int[] rows = table.getSelectedRows();
        Node[] toSet = null;
        
        if (rows.length != 0) {
            toSet = new Node[rows.length];
            
            for (int cntr = 0; cntr < rows.length; cntr++) {
                toSet[cntr] = tableModel.getNodes()[rows[cntr]];
            }
            
            System.err.println("settings activated nodes: " + Arrays.asList(toSet));
        } else {
            toSet = new Node[0];
        }
        
        try {
            getExplorerManager().setSelectedNodes(toSet);
        } catch (PropertyVetoException ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }
    
    public ExplorerManager getExplorerManager() {
        return manager;
    }
    
    private void showMessage(String message) {
        ErrorManager.getDefault().log(ErrorManager.ERROR, message);
    }
    
    public void propertyChange(PropertyChangeEvent evt) {
        showMessage("propertyChange(" + evt + ")");
        if (evt.getSource() == getExplorerManager() && ExplorerManager.PROP_ROOT_CONTEXT.equals(evt.getPropertyName())) {
            updateRoot(evt);
        }
    }
    
    private NodeListener nodeListener = null;
    
    private void updateRoot(PropertyChangeEvent evt) {
        showMessage("updateRoot");
        Node node = getExplorerManager().getRootContext();
        
        node.addNodeListener(nodeListener = (NodeListener) WeakListeners.create(NodeListener.class, this, node));
        
        update();
    }

    private void update() {
        showMessage("update");
        Node node = getExplorerManager().getRootContext();
        
        Node[] nodes = node.getChildren().getNodes();
//        System.err.println("setting:" + Arrays.asList(nodes));
        tableModel.setNodes(nodes);
        
        try {
            getExplorerManager().setSelectedNodes(new Node[0]);//TODO: this probably can be made better...
        } catch (PropertyVetoException ex) {
            ErrorManager.getDefault().notify(ex);
        }
    }
    
    public void childrenAdded(NodeMemberEvent ev) {
        update();
    }
    
    public void childrenRemoved(NodeMemberEvent ev) {
        update();
    }
    
    public void childrenReordered(NodeReorderEvent ev) {
        update();
    }
    
    public void nodeDestroyed(NodeEvent ev) {
        update(); //??
    }
    
    public int getPersistenceType() {
//        return PERSISTENCE_ONLY_OPENED;//TODO: it should be done this way, but it won't work currently..
        return PERSISTENCE_NEVER;
    }
    
    private static class ROProperty extends ReadOnly {
        
        private Property property;
        
        public ROProperty(Property property) {
            super(property.getName(), property.getValueType(), property.getDisplayName(), property.getShortDescription());
            this.property = property;
        }

        public Object getValue() throws IllegalAccessException, InvocationTargetException {
            return property.getValue();
        }
        
    }
    
    public static class EntryTableModel extends AbstractTableModel implements PropertyChangeListener {
        
        private String[] properties;
        private String[] displayNames;
//        private Node.Property[] properties;
        private Node[]          nodes;
        
        public EntryTableModel() {
            nodes = new Node[0];
            properties = new String[0];
        }
        
        /** Set columns.
         * @param props the columns
         */
        public void setProperties(String[] props) {
            properties = props;
            
            if (displayNames == null || displayNames.length != properties.length)
                displayNames = new String[properties.length];
            
            for (int cntr = 0; cntr < properties.length; cntr++) {
                displayNames[cntr] = NbBundle.getBundle(OpenBiBComponent.class).getString("CN_" + properties[cntr]);
            }
            
            fireTableStructureChanged();
//            super.setProperties(props);
        }
        
        public void setNodes(Node[] newNodes) {
            //remove the listeners on the current nodes:
            if (nodes != null) {
                for (int cntr = 0; cntr < nodes.length; cntr++) {
                    nodes[cntr].removePropertyChangeListener(this);
                }
            }
            //the nodes are fitered so only PublicationEntryNodes are there:
            List result = new ArrayList();
            
            for (int cntr = 0; cntr < newNodes.length; cntr++) {
                if (newNodes[cntr] instanceof PublicationEntryNode) {
                    result.add(newNodes[cntr]);
                }
            }
            
            nodes = (Node[] ) result.toArray(new Node[0]);
            
            //add the listeners to the current nodes:
            for (int cntr = 0; cntr < nodes.length; cntr++) {
                nodes[cntr].addPropertyChangeListener(this);
            }
            
            fireTableDataChanged();
        }
        
        public Node[] getNodes() {
            return nodes;
        }
        
        private Property getProperty(int row, int col) {
            Node node = getNodes()[row];
            String property = properties[col];
            
//            System.err.println("Looking for:" + property);
            
            PropertySet[] propertySets = node.getPropertySets();
            
            for (int cntr = 0; cntr < propertySets.length; cntr++) {
                PropertySet set = propertySets[cntr];
                Property[] properties = set.getProperties();
                
//                System.err.println("set=" + set);
                
                for (int propCount = 0; propCount < properties.length; propCount++) {
//                    System.err.println("testing: " + properties[propCount].getName());
                    if (properties[propCount].getName().equals(property))
                        return properties[propCount];
                }
            }
            
            return null;
        }
        
        public Class getColumnClass(int columnIndex) {
//            return getValueType();
            return String.class;
        }
        
        public Object getValueAt(int row, int col) {
            Property p = getProperty(row, col);
            
            if (p == null)
                return null;
            
            try {
//                System.err.println("p.getValue().getClass()= " + p.getValue().getClass());
                return p.getValue();
            } catch (IllegalAccessException e) {
                ErrorManager.getDefault().notify(e);
            } catch (InvocationTargetException e) {
                ErrorManager.getDefault().notify(e);
            }
            
            return null; //!!!
        }
        
        public boolean isCellEditable(int row, int col) {
            Property p = getProperty(row, col);
            
            System.err.println("p.canWrite()=" + p.canWrite());
            return p.canWrite();
        }
        
        public void setValueAt(Object o, int row, int col) {
            Property p = getProperty(row, col);
            
            try {
                p.setValue(o);
            } catch (IllegalAccessException e) {
                ErrorManager.getDefault().notify(e);
            } catch (InvocationTargetException e) {
                ErrorManager.getDefault().notify(e);
            }
        }
        
        public int getColumnCount() {
            return properties.length;
        }
        
        public int getRowCount() {
            return nodes.length;
        }
        
        public String getColumnName(int col) {
            return displayNames[col];
        }
        
        public void propertyChange(PropertyChangeEvent evt) {
            fireTableDataChanged();
        }
        
    }
    
}

/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is the ETable module. The Initial Developer of the Original
 * Code is Nokia. Portions Copyright 2004 Nokia. All Rights Reserved.
 */
package org.netbeans.swing.etable;

import java.awt.event.KeyEvent;
import junit.framework.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;

/**
 * Tests for class ETable.
 * @author David Strupl
 */
public class ETableTest extends TestCase {
    
    public ETableTest(String testName) {
        super(testName);
    }

    public static junit.framework.Test suite() {
        junit.framework.TestSuite suite = new junit.framework.TestSuite(ETableTest.class);
        return suite;
    }

    /**
     * Test of isCellEditable method, of class org.netbeans.swing.etable.ETable.
     */
    public void testIsCellEditable() {
        System.out.println("testIsCellEditable");
        ETable t = createTestingTable(true);
        assertTrue("Should be editable according to the model", t.isCellEditable(0, 0));
        t.setFullyNonEditable(true);
        assertFalse("Should be non-editable when in fully non-ed mode" , t.isCellEditable(0, 0));
        t.setFullyEditable(true);
        assertTrue("Should be editable after fully editable", t.isCellEditable(0, 0));
    }

    /**
     * Test of convertRowIndexToModel method, of class org.netbeans.swing.etable.ETable.
     */
    public void testConvertRowIndexToModel() {
        System.out.println("testConvertRowIndexToModel");
        ETable t = createTestingTable(true);
        t.setQuickFilter(0, "b");
        assertEquals("Filter should hide rows", 3, t.convertRowIndexToModel(1));
        t.unsetQuickFilter();
        assertEquals("Unsetting filter should return original value", 1, t.convertRowIndexToModel(1));
        ETableColumnModel etcm = (ETableColumnModel)t.getColumnModel();
        ETableColumn etc = (ETableColumn)etcm.getColumn(3);
        etcm.toggleSortedColumn(etc, true);
        t.sortingPermutation = null; // because that is what we do after calling toggleSortedColumn
        assertEquals("Sort reorder (3) not ok", 3, t.convertRowIndexToModel(0));
        assertEquals("Sort reorder (4) not ok", 4, t.convertRowIndexToModel(5));
    }

    
    /**
     * Test of getFullyEditable method, of class org.netbeans.swing.etable.ETable.
     */
    public void testGetFullyEditable() {
        System.out.println("testGetFullyEditable");
        ETable t = createTestingTable(true);
        assertFalse("False after creation ", t.getFullyEditable());
        t.setFullyEditable(true);
        assertTrue("Should be editable after setting" , t.getFullyEditable());
        t.setFullyNonEditable(true);
        assertFalse("Should be false if fully non-editable", t.getFullyEditable());
    }

    /**
     * Test of getFullyNonEditable method, of class org.netbeans.swing.etable.ETable.
     */
    public void testGetFullyNonEditable() {
        System.out.println("testGetFullyNonEditable");
        
        ETable t = createTestingTable(true);
        assertFalse("False after creation ", t.getFullyNonEditable());
        t.setFullyNonEditable(true);
        assertTrue("Should be non-editable after setting" , t.getFullyNonEditable());
        t.setFullyEditable(true);
        assertFalse("Should be false if fully editable", t.getFullyNonEditable());
    }

    /**
     * Tests passing a QuickFilter object as a parameter to setQuickFilter method.
     */
    public void testSetQuickFilter() {
        System.out.println("testSetQuickFilter");
        ETable t = createTestingTable(true);
        QuickFilter quick = new QuickFilter() {
            public boolean accept(Object object) {
                return "x".equals(object);
            }
        };
        t.setQuickFilter(1, quick);
        assertEquals(1, t.getRowCount());
    }
    
    /**
     * Test of createDefaultColumnsFromModel method, of class org.netbeans.swing.etable.ETable.
     */
    public void testCreateDefaultColumnsFromModel() {
        System.out.println("testCreateDefaultColumnsFromModel");
        ETable t = createTestingTable(true);
        assertTrue("Should create ETableColumnModel", t.createDefaultColumnModel() instanceof ETableColumnModel);
    }

    /**
     * Test of createColumn method, of class org.netbeans.swing.etable.ETable.
     */
    public void testCreateColumn() {
        System.out.println("testCreateColumn");
        final boolean [] called = new boolean[1];
        ETable t = new ETable(1, 1) {
            public TableColumn createColumn(int index){ 
                TableColumn tc = super.createColumn(index);
                called[0] = tc instanceof ETableColumn;
                return tc;
            }
        };
        assertTrue("createColumn should have been called and returned correct type", called[0]);
    }

    /**
     * Test of createDefaultColumnModel method, of class org.netbeans.swing.etable.ETable.
     */
    public void testCreateDefaultColumnModel() {
        System.out.println("testCreateDefaultColumnModel");
        final boolean [] called = new boolean[1];
        ETable t = new ETable(1, 1) {
            protected TableColumnModel createDefaultColumnModel() {  
                TableColumnModel tcm = super.createDefaultColumnModel();
                called[0] = tcm instanceof ETableColumnModel;
                return tcm;
            }
        };
        assertTrue("createColumn should have been called and created correct type", called[0]);
    }

    /**
     * Test of getValueAt method, of class org.netbeans.swing.etable.ETable.
     */
    public void testGetValueAt() {
        System.out.println("testGetValueAt");
        System.out.println("testSetValueAt");
        ETable t = createTestingTable(true);
        t.setQuickFilter(0, "b");
        assertEquals("Filter should hide rows", t.getValueAt(1,1), t.getModel().getValueAt(3, 1));
        t.unsetQuickFilter();
        ETableColumnModel etcm = (ETableColumnModel)t.getColumnModel();
        ETableColumn etc = (ETableColumn)etcm.getColumn(3);
        etcm.toggleSortedColumn(etc, true);
        t.sortingPermutation = null; // because that is what we do after calling toggleSortedColumn
        assertEquals("Sort reorder (3) not ok", t.getValueAt(0,1), t.getModel().getValueAt(3, 1));
        assertEquals("Sort reorder (4) not ok", t.getValueAt(5,1), t.getModel().getValueAt(4, 1));
    }

    /**
     * Test of setValueAt method, of class org.netbeans.swing.etable.ETable.
     */
    public void testSetValueAt() {
        System.out.println("testSetValueAt");
        ETable t = createTestingTable(true);
        t.setQuickFilter(0, "b");
        t.setValueAt("ahoj", 1, 1);
        assertEquals("Filter should hide rows", "ahoj", t.getModel().getValueAt(3, 1));
        t.unsetQuickFilter();
        ETableColumnModel etcm = (ETableColumnModel)t.getColumnModel();
        ETableColumn etc = (ETableColumn)etcm.getColumn(3);
        etcm.toggleSortedColumn(etc, true);
        t.sortingPermutation = null; // because that is what we do after calling toggleSortedColumn
        t.setValueAt("ahoj1", 0, 1);
        t.setValueAt("ahoj2", 5, 1);
        assertEquals("Sort reorder (3) not ok", "ahoj1", t.getModel().getValueAt(3, 1));
        assertEquals("Sort reorder (4) not ok", "ahoj2", t.getModel().getValueAt(4, 1));
    }

    /**
     * Test of getRowCount method, of class org.netbeans.swing.etable.ETable.
     */
    public void testGetRowCount() {
        System.out.println("testGetRowCount");
        
        ETable t = createTestingTable(true);
        t.setQuickFilter(0, "b");
        assertEquals("Filter should hide rows", 2, t.getRowCount());
    }

    /**
     * Test of setModel method, of class org.netbeans.swing.etable.ETable.
     */
    public void testSetModel() {
        System.out.println("testSetModel");
        ETable t = createTestingTable(true);
        t.setQuickFilter(0, "b");
        t.setModel(new DefaultTableModel(100, 100));
        assertEquals("row count should be according to the new model", 100, t.getRowCount());
    }

    /**
     * Test of initializeLocalVars method, of class org.netbeans.swing.etable.ETable.
     */
    public void testInitializeLocalVars() {
        System.out.println("testInitializeLocalVars");
        ETable t = createTestingTable(true);
        for (int i = 0; i < t.getColumnCount(); i++) {
            int pw = t.getColumnModel().getColumn(i).getPreferredWidth();
            if ((pw == 0) || (pw == 75)) { // the default values
                fail("PreferredWidth is " + pw);
            }
        }
    }

    /**
     * Test of processKeyBinding method, of class org.netbeans.swing.etable.ETable.
     */
    public void testProcessKeyBinding() {
        System.out.println("testProcessKeyBinding");
        final boolean []called = new boolean[1];
        ETable t = new ETable() {
            void updatePreferredWidths() {
                super.updatePreferredWidths();
                called[0] = true;
            }
        };
        KeyEvent ke = new KeyEvent(t, 0, System.currentTimeMillis(), InputEvent.CTRL_MASK, 0, '+');
        t.processKeyBinding(null, ke, 0, true);
        assertTrue("update pref size not called", called[0]);
    }

    /**
     * Test of readSettings and writeSettings methods, 
     * of class org.netbeans.swing.etable.ETable.
     */
    public void testWriteReadSettings() {
        System.out.println("testWriteReadSettings");
        ETable t = createTestingTable(false);
        ETableColumnModel etcm = (ETableColumnModel) t.getColumnModel();
        ETableColumn etc = (ETableColumn)etcm.getColumn(3);
        etcm.setColumnHidden(etcm.getColumn(0), true);
        etcm.toggleSortedColumn(etc, true);

        assertEquals("One column should be hidden", 3, t.getColumnCount());
        assertEquals("Sort reorder (3) not ok", 3, t.convertRowIndexToModel(0));
        assertEquals("Sort reorder (4) not ok", 4, t.convertRowIndexToModel(5));
        assertEquals("Sort reorder (3) not ok", t.getValueAt(0, 0), t.getModel().getValueAt(3, 1));
        assertEquals("Sort reorder (4) not ok", t.getValueAt(5, 0), t.getModel().getValueAt(4, 1));
        
        Properties p = new Properties();
        t.writeSettings(p, "blabla");
        
        ETable t2 = createTestingTable(false);
        t2.readSettings(p, "blabla");
        
        assertEquals("One column should be hidden", 3, t2.getColumnCount());
        assertEquals("Sort reorder (3) not ok", 3, t2.convertRowIndexToModel(0));
        assertEquals("Sort reorder (4) not ok", 4, t2.convertRowIndexToModel(5));
        assertEquals("Sort reorder (3) not ok", t2.getValueAt(0, 0), t2.getModel().getValueAt(3, 1));
        assertEquals("Sort reorder (4) not ok", t2.getValueAt(5, 0), t2.getModel().getValueAt(4, 1));
    }
    
    /**
     * Create a test ETable instance with some dummy data. BUT please
     * be aware that the tests result depend on this data so if you do
     * any change here make sure you fix all the tests.
     */
    private ETable createTestingTable(final boolean cellsEditable) {
        ETable eTable1 = new ETable();
        eTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {"a", "x", "tttttttt", new Integer(5)},
                {"a", "y", "ggggggggg", new Integer(10)},
                {"b", "z", "nnnnnnnn", new Integer(7)},
                {"b", "w", "mmmmmm", new Integer(1)},
                {"c", "m", "kkkkkkkkkk", new Integer(10000)},
                {"c", "n", "kkkkk", new Integer(4)}
            },
            new String [] { "AA", "BB", "CC", "DD"}
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Object.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                cellsEditable, cellsEditable, cellsEditable, cellsEditable
            };
            
            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
            
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        return eTable1;
    }
}

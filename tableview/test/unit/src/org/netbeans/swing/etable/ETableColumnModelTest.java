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
 * The Original Software is the ETable module. The Initial Developer of the Original
 * Software is Nokia. Portions Copyright 2004 Nokia. All Rights Reserved.
 */

package org.netbeans.swing.etable;

import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.util.Properties;
import junit.framework.TestCase;

/**
 * Tests for class ETableColumnModel.
 * @author David Strupl
 */
public class ETableColumnModelTest extends TestCase {

    public ETableColumnModelTest(String testName) {
        super(testName);
    }

    /**
     * Test of readSettings, writeSettings methods, of class org.netbeans.swing.etable.ETableColumnModel.
     */
    public void testReadWriteSettings() {
        ETable et = new ETable();
        System.out.println("testReadWriteSettings");
        ETableColumnModel etcm = new ETableColumnModel();
        ETableColumn etc1 = new ETableColumn(0, et);
        etcm.addColumn(etc1);
        ETableColumn etc2 = new ETableColumn(1, et);
        etcm.addColumn(etc2);
        ETableColumn etc3 = new ETableColumn(2, et);
        etcm.addColumn(etc3);
        etcm.setColumnHidden(etc3, true);
        Properties p = new Properties();
        
        etcm.writeSettings(p, "test");
        ETableColumnModel etcm2 = new ETableColumnModel();
        etcm2.readSettings(p, "test", et);
        
        assertEquals("Should restore 2 columns", 2, etcm2.getColumnCount());
        assertEquals("One hidden column", 1, etcm2.hiddenColumns.size());
    }

    /**
     * Test of getComparator method, of class org.netbeans.swing.etable.ETableColumnModel.
     */
    public void testGetComparator() {
        System.out.println("testGetComparator");
        ETableColumnModel etcm = new ETableColumnModel();
        assertTrue(etcm.getComparator() instanceof ETable.OriginalRowComparator);
        TableModel tm = new DefaultTableModel(new Object[][] {{"b"},{"a"}}, new Object[] {"a", "b"}); 
        ETable.RowMapping rm1 = new ETable.RowMapping(0, tm);
        ETable.RowMapping rm2 = new ETable.RowMapping(1, tm);
        assertTrue("Without sort use index of rows, ", etcm.getComparator().compare(rm1, rm2) < 0);
        
        ETableColumn etc = new ETableColumn(0, new ETable());
        etcm.addColumn(etc);
        etcm.toggleSortedColumn(etc, true);
        assertTrue("Sorting according to data model failed, ", etcm.getComparator().compare(rm1, rm2) > 0);
    }

    /**
     * Test of toggleSortedColumn method, of class org.netbeans.swing.etable.ETableColumnModel.
     */
    public void testToggleSortedColumn() {
        System.out.println("testToggleSortedColumn");
        ETableColumnModel etcm = new ETableColumnModel();
        ETableColumn etc = new ETableColumn(0, null);
        etcm.addColumn(etc);
        
        etcm.toggleSortedColumn(etc, true);
        assertTrue(etcm.sortedColumns.contains(etc));
        assertTrue(etc.isAscending());
        assertTrue(etc.isSorted());
        
        etcm.toggleSortedColumn(etc, true);
        assertTrue(etcm.sortedColumns.contains(etc));
        assertFalse(etc.isAscending());
        assertTrue(etc.isSorted());
        
        etcm.toggleSortedColumn(etc, true);
        assertFalse(etcm.sortedColumns.contains(etc));
        assertFalse(etc.isSorted());
    }

    /**
     * Test of setColumnHidden method, of class org.netbeans.swing.etable.ETableColumnModel.
     */
    public void testSetColumnHidden() {
        System.out.println("testSetColumnHidden");
        ETableColumnModel etcm = new ETableColumnModel();
        ETableColumn etc = new ETableColumn(0, null);
        etcm.addColumn(etc);
        
        etcm.setColumnHidden(etc, true);
        assertTrue(etcm.hiddenColumns.contains(etc));
        assertTrue(etcm.getColumnCount() == 0);
        assertTrue(etcm.isColumnHidden(etc));
        
        etcm.setColumnHidden(etc, false);
        assertFalse(etcm.hiddenColumns.contains(etc));
        assertTrue(etcm.getColumnCount() == 1);
        assertFalse(etcm.isColumnHidden(etc));
    }

    /**
     * Test of clearSortedColumns method, of class org.netbeans.swing.etable.ETableColumnModel.
     */
    public void testClearSortedColumns() {
        System.out.println("testClearSortedColumns");
        ETableColumnModel etcm = new ETableColumnModel();
        ETableColumn etc = new ETableColumn(0, null);
        etcm.addColumn(etc);
        etcm.toggleSortedColumn(etc, true);
        
        etcm.clearSortedColumns();
        assertFalse(etcm.sortedColumns.contains(etc));
    }
}

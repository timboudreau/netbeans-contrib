/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.usertasks.test;

import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.table.TableCellEditor;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.netbeans.jellytools.FilesTabOperator;
import org.netbeans.jellytools.JellyTestCase;
import org.netbeans.jellytools.NbDialogOperator;
import org.netbeans.jellytools.TopComponentOperator;
import org.netbeans.jellytools.actions.Action;
import org.netbeans.jellytools.actions.OpenAction;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jemmy.EventTool;
import org.netbeans.jemmy.operators.ButtonOperator;
import org.netbeans.jemmy.operators.DialogOperator;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JPopupMenuOperator;
import org.netbeans.jemmy.operators.JTableOperator;
import org.netbeans.jemmy.operators.Operator;
import org.netbeans.junit.NbTestSuite;
import org.netbeans.modules.tasklist.usertasks.UserTaskTreeTableNode;
import org.netbeans.modules.tasklist.core.table.TreeTable;

/**
 * Tests for the TreeTable
 */
public class TestTable extends JellyTestCase {
    public TestTable(java.lang.String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new NbTestSuite(TestTable.class);
        return suite;
    }
    
    public void setUp() {                
    }
    
    public void tearDown() {
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    /**
     * Test the "Show Task" dialog
     */
    public void testShowTask() {
        Node n = FilesTabOperator.invoke().getProjectNode("SampleApp"); // NOI18N

        Node buildXml = new Node(n, "build.xml"); // NOI18N
        buildXml.select(); 
        new OpenAction().perform(buildXml);
        
        TopComponentOperator tc = openIcsFile("test10.ics"); // NOI18N
        
        JTableOperator t = new JTableOperator(tc, 0);
        t.waitHasFocus();
        assertEquals(0, t.getSelectedRow());
        t.selectCell(1, 0);
        
        Action ea = new Action(null, "Expand All"); // NOI18N
        ea.performPopup(t);
        t.selectCell(1, 0);
        
        t.clickForPopup();
        
        new EventTool().waitNoEvent(1500);

        JPopupMenuOperator pm = new JPopupMenuOperator();
        pm.pushMenuNoBlock("Show Task"); // NOI18N
        
        new NbDialogOperator("Show Task").close(); // NOI18N
    }

    /**
     * see description for the test 14
     */
    public void testStartTaskFillsOwner() {
        /*
         This does not work because of #51882
        TopComponentOperator tc = openIcsFile("test14.ics");
        
        JTableOperator t = new JTableOperator(tc, 0);
        t.waitHasFocus();
        
        Action ea = new Action(null, "Expand All");
        ea.performPopup(t);
        
        Point p = t.findCell("R", 
            new Operator.DefaultStringComparator(true, true), 1);
        t.selectCell(p.y, p.x);
        
        t.clickForPopup();
        
        new EventTool().waitNoEvent(1500);

        JPopupMenuOperator pm = new JPopupMenuOperator();
        pm.pushMenuNoBlock("Start");
        
        TreeTable tt = (TreeTable) t.getSource();
        UserTaskTreeTableNode n = (UserTaskTreeTableNode) tt.getNodeForRow(p.y);
         
        assertEquals(System.getProperty("user.name"), n.getUserTask().getOwner());
         */
    }
    
    /**
     * See description in testCases.html
     */
    public void testDelete() {
        TopComponentOperator tc = openIcsFile("test15.ics"); // NOI18N
        
        JTableOperator t = new JTableOperator(tc, 0);
        t.waitHasFocus();
        
        Action ea = new Action(null, "Expand All"); // NOI18N
        ea.performPopup(t);
        
        t.addRowSelectionInterval(6, 8);
        t.addRowSelectionInterval(10, 10);
        
        Rectangle r = t.getCellRect(6, 0, false);
        t.clickForPopup(r.x, r.y);
        
        new EventTool().waitNoEvent(1500);

        JPopupMenuOperator pm = new JPopupMenuOperator();
        pm.pushMenuNoBlock("Delete"); // NOI18N

        DialogOperator dop = new DialogOperator("Confirm Multiple Object Deletion"); // NOI18N
        JButtonOperator bo = new JButtonOperator(dop, "Yes"); // NOI18N
        bo.push();
        
        new EventTool().waitNoEvent(1500);
        
        assertEquals(7, t.getSelectedRow());
    }
    
    /**
     * see description for the test 13
     */
    public void testCategoryInplace() {
        TopComponentOperator tc = openIcsFile("test13.ics"); // NOI18N
        
        JTableOperator t = new JTableOperator(tc, 0);
        t.waitHasFocus();
        t.selectCell(1, 0);
        
        Action ea = new Action(null, "Expand All"); // NOI18N
        ea.performPopup(t);
        t.selectCell(3, 3);
        t.editCellAt(3, 3);
        TableCellEditor ed = t.getCellEditor();
        JComboBox cb = ((JComboBox) ((DefaultCellEditor) ed).getComponent());
        assertEquals(2, cb.getItemCount());
        assertEquals(cb.getItemAt(0), "CatA"); // NOI18N
        assertEquals(cb.getItemAt(1), "CatB"); // NOI18N
    }

    /**
     * Opens an ICS file
     *
     * @param name file name under /ics
     * @return opened TC
     */
    private TopComponentOperator openIcsFile(String name) {
        Node n = FilesTabOperator.invoke().getProjectNode("SampleApp"); // NOI18N

        Node data = new Node(n, "ics|" + name); // NOI18N
        data.select(); 
        new OpenAction().perform(data);
        
        TopComponentOperator op = new TopComponentOperator(name);

        return op;
    }
}

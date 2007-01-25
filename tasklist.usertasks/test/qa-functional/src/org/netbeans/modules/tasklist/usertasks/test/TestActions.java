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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.usertasks.test;

import java.awt.Component;
import java.awt.Container;
import java.awt.Rectangle;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.table.TableCellRenderer;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.netbeans.jellytools.FilesTabOperator;
import org.netbeans.jellytools.JellyTestCase;
import org.netbeans.jellytools.NbDialogOperator;
import org.netbeans.jellytools.TopComponentOperator;
import org.netbeans.jellytools.actions.OpenAction;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.EventTool;
import org.netbeans.jemmy.operators.ComponentOperator;
import org.netbeans.jemmy.operators.DialogOperator;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JComponentOperator;
import org.netbeans.jemmy.operators.JPopupMenuOperator;
import org.netbeans.jemmy.operators.JTabbedPaneOperator;
import org.netbeans.jemmy.operators.JTableOperator;
import org.netbeans.jemmy.operators.JTextFieldOperator;
import org.netbeans.jemmy.util.NameComponentChooser;
import org.netbeans.jemmy.util.PropChooser;
import org.netbeans.junit.NbTestSuite;

/**
 * Tests for the actions.
 * 
 * @author tl
 */
public class TestActions extends JellyTestCase {
    public TestActions(java.lang.String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new NbTestSuite(TestActions.class);
        return suite;
    }
    
    public static void main(java.lang.String[] args) {
        junit.textui.TestRunner.run(suite());
    }

    private static int getLevel(JTableOperator t, int row) {
        TableCellRenderer tcr = t.getCellRenderer(row, 2);
        JTree tree = (JTree) t.prepareRenderer(tcr, row, 2);
        return tree.getPathForRow(row).getPathCount();
    }
    
    private static String getSummary(JTableOperator t, int row) {
        t.editCellAt(row, 2);
        JTextFieldOperator op = new JTextFieldOperator(t);
        return op.getText();
    }
    
    /**
     * Test the "Paste at top level".
     */
    public void testPasteAtTopLevel() {
        TopComponentOperator tc = openIcsFile("testPasteAtTopLevel.ics"); // NOI18N
        
        JTableOperator t = new JTableOperator(tc, 0);

        t.clickForPopup();
        JPopupMenuOperator pm = new JPopupMenuOperator();
        pm.pushMenu("Expand All"); // NOI18N
        new EventTool().waitNoEvent(1500);
        
        Rectangle r = t.getCellRect(2, 2, false);
        t.clickForPopup(r.x, r.y);
        pm = new JPopupMenuOperator();
        pm.pushMenu("Cut"); // NOI18N
        
        t.clickForPopup();
        pm = new JPopupMenuOperator();
        pm.pushMenu("Paste at Top Level"); // NOI18N
        
        t.clickForPopup();
        pm = new JPopupMenuOperator();
        pm.pushMenu("Expand All"); // NOI18N
        new EventTool().waitNoEvent(1500);
        
        assertEquals(9, t.getRowCount());
        assertEquals("D", getSummary(t, 0));
        assertEquals("E", getSummary(t, 1));
        assertEquals("F", getSummary(t, 2));
        assertEquals("G", getSummary(t, 3));
        assertEquals("C", getSummary(t, 4));
        assertEquals("A", getSummary(t, 5));
        assertEquals("B", getSummary(t, 6));
        assertEquals("H", getSummary(t, 7));
        assertEquals("I", getSummary(t, 8));

        assertEquals(2, getLevel(t, 7));
        assertEquals(3, getLevel(t, 8));
    }
    
    /**
     * Test for the "Start" action. It should be disabled if a dependancy on
     * another task is added and re-enabled if this dependancy is removed.
     */
    public void testStartWithDeps() {
        TopComponentOperator tc = openIcsFile("testStartWithDeps.ics"); // NOI18N
        JTableOperator t = new JTableOperator(tc, 0);
        
        t.selectCell(0, 2);
        Rectangle r = t.getCellRect(0, 2, false);
        t.clickForPopup(r.x, r.y);
        JPopupMenuOperator pm = new JPopupMenuOperator();
        pm.pushMenuNoBlock("Show Task"); // NOI18N
        
        NbDialogOperator dop = new NbDialogOperator("Show Task"); // NOI18N
        JTabbedPaneOperator tpop = new JTabbedPaneOperator(dop, 0);
        tpop.setSelectedIndex(2);        
        JComponentOperator pop = new JComponentOperator(
                (JComponent) tpop.getSelectedComponent());
        new JButtonOperator(pop, "Add").push();
        NbDialogOperator addop = new NbDialogOperator(
                "Add Dependency"); // NOI18N
        JTree tree = (JTree) ComponentOperator.findComponent(
                (Container) addop.getSource(), 
                new ComponentChooser() {
            public boolean checkComponent(Component arg0) {
                return arg0 instanceof JTree;
            }

            public String getDescription() {
                return "";
            }
        });
        tree.setSelectionRow(1);
        addop.ok();
        dop.ok();
        
        JComponentOperator tb = new JComponentOperator(
                tc, new PropChooser(new String[] {"getClass"},
                new Object[] {JToolBar.class}));

        JButtonOperator start = new JButtonOperator(tb, new ComponentChooser() {
            public boolean checkComponent(Component c) {
                if (c instanceof JButton) {
                   String s = ((JButton) c).getToolTipText();
                   return "Start".equals(s);
                }
                return false;
            }

            public String getDescription() {
                return "";
            }
        });
        assertTrue(!start.isEnabled());

        t.selectCell(0, 2);
        r = t.getCellRect(0, 2, false);
        t.clickForPopup(r.x, r.y);
        pm = new JPopupMenuOperator();
        pm.pushMenuNoBlock("Show Task"); // NOI18N
        
        dop = new NbDialogOperator("Show Task"); // NOI18N
        tpop = new JTabbedPaneOperator(dop, 0);
        tpop.setSelectedIndex(2);        
        pop = new JComponentOperator(
                (JComponent) tpop.getSelectedComponent());
        new JButtonOperator(pop, "Remove").push();
        addop = new NbDialogOperator("Question"); // NOI18N
        addop.yes();
        dop.ok();
        assertTrue(start.isEnabled());
    }
    
    /**
     * Test the "Purge Completed".
     */
    public void testPurgeCompleted() {
        TopComponentOperator tc = openIcsFile("testPurgeCompleted.ics"); // NOI18N
        
        JTableOperator t = new JTableOperator(tc, 0);
        t.selectAll();
                
        t.clickForPopup();
        JPopupMenuOperator pm = new JPopupMenuOperator();
        pm.pushMenuNoBlock("Purge Completed"); // NOI18N
        
        DialogOperator dop = new DialogOperator("Question"); // NOI18N
        JButtonOperator bo = new JButtonOperator(dop, "OK"); // NOI18N
        bo.push();
        
        t.clickForPopup();
        pm = new JPopupMenuOperator();
        pm.pushMenu("Expand All"); // NOI18N
        
        assertEquals(5, t.getRowCount());
        assertEquals("D", getSummary(t, 0));
        assertEquals("E", getSummary(t, 1));
        assertEquals("H", getSummary(t, 2));
        assertEquals("I", getSummary(t, 3));
        assertEquals("A", getSummary(t, 4));
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

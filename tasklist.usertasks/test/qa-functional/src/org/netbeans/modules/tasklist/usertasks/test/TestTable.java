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

import junit.framework.Test;
import junit.framework.TestSuite;

import org.netbeans.jellytools.FilesTabOperator;
import org.netbeans.jellytools.JellyTestCase;
import org.netbeans.jellytools.NbDialogOperator;
import org.netbeans.jellytools.TopComponentOperator;
import org.netbeans.jellytools.actions.Action;
import org.netbeans.jellytools.actions.CopyAction;
import org.netbeans.jellytools.actions.DeleteAction;
import org.netbeans.jellytools.actions.OpenAction;
import org.netbeans.jellytools.actions.PasteAction;
import org.netbeans.jellytools.nodes.Node;
import org.netbeans.jemmy.EventTool;
import org.netbeans.jemmy.operators.JButtonOperator;
import org.netbeans.jemmy.operators.JPopupMenuOperator;
import org.netbeans.jemmy.operators.JTableOperator;
import org.netbeans.junit.NbTestSuite;

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
     * Test
     */
    public void testIt() {
        Node n = FilesTabOperator.invoke().getProjectNode("SampleApp");

        Node buildXml = new Node(n, "build.xml"); 
        buildXml.select(); 
        new OpenAction().perform(buildXml);
        
        TopComponentOperator tc = openCopy("test10.ics");
        
        JTableOperator t = new JTableOperator(tc, 0);
        t.waitHasFocus();
        assertEquals(0, t.getSelectedRow());
        t.selectCell(1, 0);
        
        Action ea = new Action(null, "Expand All");
        ea.performPopup(t);
        t.selectCell(1, 0);
        
        t.clickForPopup();
        
        new EventTool().waitNoEvent(1500);

        JPopupMenuOperator pm = new JPopupMenuOperator();
        pm.pushMenuNoBlock("Show Task");
        
        new NbDialogOperator("Show Task").close();
        
        deleteCopy("test10.ics");
    }

    /**
     * Copies a file with the specified name from the folder ics 
     * to the folder test and opens it
     *
     * @param name file name under /ics
     * @return opened TC
     */
    private TopComponentOperator openCopy(String name) {
        Node n = FilesTabOperator.invoke().getProjectNode("SampleApp");

        Node data = new Node(n, "ics|" + name); 
        data.select(); 
        new CopyAction().perform(data);
        
        Node dir = new Node(n, "test");
        dir.select(); 
        new PasteAction().perform(dir);
        
        data = new Node(n, "test|" + name); 
        data.select(); 
        new OpenAction().perform(data);
        
        TopComponentOperator op = new TopComponentOperator(name);

        return op;
    }
    
    private void deleteCopy(String name) {
        Node n = FilesTabOperator.invoke().getProjectNode("SampleApp");

        Node data = new Node(n, "test|" + name); 
        data.select(); 
        new DeleteAction().perform(data);
        
        NbDialogOperator op = new NbDialogOperator("Confirm Object Deletion");
        JButtonOperator b = new JButtonOperator(op, "Yes");
        b.push();
    }
}

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

import java.io.File;
import java.util.HashSet;

import junit.framework.*;
import junit.extensions.TestSetup;

import org.netbeans.junit.*;

import org.netbeans.jemmy.EventTool;

import org.netbeans.jellytools.Bundle;

import org.netbeans.jellytools.JellyTestCase;
import org.netbeans.jellytools.NbDialogOperator;
import org.netbeans.jellytools.EditorOperator;

import org.netbeans.jellytools.actions.SaveAllAction;
import org.netbeans.jellytools.actions.CopyAction;
import org.netbeans.jellytools.actions.PasteAction;
import org.netbeans.jellytools.actions.CutAction;
import org.netbeans.jellytools.actions.DeleteAction;

import org.netbeans.jellytools.nodes.Node;

import org.netbeans.jellytools.MainWindowOperator;
import org.netbeans.jellytools.OutputOperator;

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
    
    public void testIt() {
        assertTrue(true);
        assertEquals(1, 1);
    }
}

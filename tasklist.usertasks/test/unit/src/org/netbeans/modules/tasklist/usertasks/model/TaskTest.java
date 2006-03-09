/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.usertasks.model;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.netbeans.modules.tasklist.usertasks.util.UTUtils;

/**
 *
 * @author Petr Kuzel
 */
public class TaskTest extends TestCase {
    
    public TaskTest(java.lang.String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(TaskTest.class);
        return suite;
    }

    /**
     * Test for UserTask.getEffort() method
     */
    public void testGetEffort() {
        UserTaskList utl = new UserTaskList();
        UserTask root = new UserTask("root", utl); // NOI18N
        root.setEffortComputed(true);
        
        UserTask a = new UserTask("a", utl); // NOI18N
        a.setEffort(200);
        assertTrue(a.getEffort() == 200);
        
        UserTask b = new UserTask("b", utl); // NOI18N
        b.setEffort(300);
        
        root.getSubtasks().add(a);
        root.getSubtasks().add(b);
        
        assertTrue(root.computeEffort() == 500);
        assertTrue(root.getEffort() == 500);
    }

    /**
     * Test for UserTask.getEffort() method
     */
    public void testGetEffort2() {
        UserTaskList utl = new UserTaskList();
        UserTask root = new UserTask("root", utl); // NOI18N
        root.setEffortComputed(true);
        root.setProgressComputed(true);
        
        UserTask a = new UserTask("a", utl); // NOI18N
        a.setEffort(200);
        a.setPercentComplete(25);
        
        UserTask b = new UserTask("b", utl); // NOI18N
        b.setEffort(300);
        b.setPercentComplete(75);
        
        root.getSubtasks().add(a);
        root.getSubtasks().add(b);
        
        assertEquals((50 + 225) * 100 / (500), root.getPercentComplete());
    }
    
    /**
     * Tests dependencies between tasks.
     */
    public void testDependencies() {
        UserTaskList utl = new UserTaskList();
        UserTask a = new UserTask("a", utl);
        UserTask b = new UserTask("b", utl);
        utl.getSubtasks().add(a);
        utl.getSubtasks().add(b);
        
        a.getDependencies().add(new Dependency(b, Dependency.END_BEGIN));
        
        b.setDone(true);
        assertTrue(b.isDone());
        assertFalse(a.isDone());
        
        a.setDone(true);
        assertTrue(b.isDone());
        assertTrue(a.isDone());
        
        b.setDone(false);
        assertFalse(b.isDone());
        assertFalse(a.isDone());
    }
}

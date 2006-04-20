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

import java.util.Date;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.netbeans.modules.tasklist.core.util.ObjectList;
import org.netbeans.modules.tasklist.usertasks.Settings;

/**
 * Tests for o.n.m.t.u.UserTask
 *
 * @author tl
 */
public class UserTaskTest extends TestCase {
    
    public UserTaskTest(java.lang.String testName) {
        super(testName);
    }
    
    public static Test suite() {
        TestSuite suite = new TestSuite(UserTaskTest.class);
        return suite;
    }

    public void testSplitDuration() {
        Duration d = new Duration(19 * 8 * 60 + 60, 8, 5);
        assertEquals(d.weeks, 3);
        assertEquals(d.days, 4);
        assertEquals(d.hours, 1);
        assertEquals(d.minutes, 0);
    }
    
    public void testHashCode() {
        UserTaskList list = new UserTaskList();
        UserTask a = new UserTask("A", list);
        UserTask b = new UserTask("B", list);
        assertTrue(a.hashCode() != b.hashCode());
    }
    
    public void testGetSetStart() {
        UserTaskList list = new UserTaskList();
        UserTask a = new UserTask("A", list);
        assertEquals(-1, a.getStart());
        assertEquals(null, a.getStartDate());
        
        long d = System.currentTimeMillis();
        a.setStart(d);
        assertEquals(d, a.getStartDate().getTime());
        
        Date dd = new Date();
        a.setStartDate(dd);
        assertEquals(dd,  a.getStartDate());
        
        a.setStartDate(null);
        assertEquals(a.getStartDate(), null);
    }
    
    public void testMoveUpDown() {
        UserTaskList list = new UserTaskList();
        UserTask a = new UserTask("A", list);
        UserTask b = new UserTask("B", list);
        
        list.getSubtasks().add(a);
        list.getSubtasks().add(b);

        b.moveUp();
        assertEquals(list.getSubtasks().getUserTask(0).getSummary(), "B");
        b.moveDown();
        assertEquals(list.getSubtasks().getUserTask(0).getSummary(), "A");
    }
    
    public void testStartStop() throws InterruptedException {
        UserTaskList list = new UserTaskList();
        UserTask a = new UserTask("A", list);
        list.getSubtasks().add(a);
        
        Settings.getDefault().setCollectWorkPeriods(true);
        a.start();
        Thread.sleep(65 * 1000);
        a.stop();
        
        ObjectList wp = a.getWorkPeriods();
        assertEquals(1, wp.size());
        
        UserTask.WorkPeriod w = (UserTask.WorkPeriod) wp.get(0);
        assertEquals(1, w.getDuration());
    }
    
    public void testStopIfSpentTimeComputed() {
        UserTaskList list = new UserTaskList();
        UserTask a = new UserTask("A", list);
        list.getSubtasks().add(a);
        
        Settings.getDefault().setAutoSwitchToComputed(true);
        a.start();
        
        UserTask b = new UserTask("B", list);
        a.getSubtasks().add(b);
        
        assertFalse(a.isStarted());
    }
}

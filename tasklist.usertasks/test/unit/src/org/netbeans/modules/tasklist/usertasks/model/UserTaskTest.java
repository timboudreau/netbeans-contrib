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


/**
 * Tests for o.n.m.t.u.UserTask
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
}

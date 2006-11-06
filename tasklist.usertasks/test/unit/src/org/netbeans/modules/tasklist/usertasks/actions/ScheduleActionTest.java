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

package org.netbeans.modules.tasklist.usertasks.actions;

import junit.framework.*;
import java.util.Calendar;

/**
 * Test for ScheduleAction.
 * 
 * @author tl
 */
public class ScheduleActionTest extends TestCase {
    
    public ScheduleActionTest(String testName) {
        super(testName);
    }

    public static Test suite() {
        TestSuite suite = new TestSuite(ScheduleActionTest.class);
        
        return suite;
    }

    /**
     * Test of add method.
     */
    public void testAdd() {
        Calendar cal = Calendar.getInstance();
        cal.set(1999, 5, 20, 15, 29);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        ScheduleAction.add(cal, 1000, start, end, new boolean[] {
            true, true, true, true, true, true, true
        });
        Calendar cal2 = Calendar.getInstance();
        cal2.set(1999, 5, 20, 15, 29);
        cal2.set(Calendar.SECOND, 0);
        cal2.set(Calendar.MILLISECOND, 0);
        assertEquals(cal2.getTime() + " != " + start.getTime(),
                cal2, start);
        cal2.set(1999, 5, 22, 16, 9);
        assertEquals(cal2.getTime() + " != " + end.getTime(),
                cal2, end);
    }
}

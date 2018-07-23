/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */

package org.netbeans.modules.tasklist.usertasks.model;

import java.util.Date;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import org.netbeans.modules.tasklist.usertasks.options.Settings;
import org.netbeans.modules.tasklist.usertasks.util.ObjectList;

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
        
        a.start();
        
        UserTask b = new UserTask("B", list);
        a.setValuesComputed(true);
        
        assertFalse(a.isStarted());
    }
}

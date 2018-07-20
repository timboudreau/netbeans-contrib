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
        root.setValuesComputed(true);
        
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
        root.setValuesComputed(true);
        
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

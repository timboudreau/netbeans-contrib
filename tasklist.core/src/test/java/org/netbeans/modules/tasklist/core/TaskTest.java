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

package org.netbeans.modules.tasklist.core;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;

/**
 *
 * @author Petr Kuzel
 */
public class TaskTest extends TestCase {

    public TaskTest(String testName) {
        super(testName);
    }

    /**
     * Test of clear method, of class org.netbeans.modules.tasklist.core.Task.
     */
    public void testClear() {
        System.out.println("testClear");

        // TODO add your test code below by replacing the default call to fail.

    }
    
    /**
     * Test of getLevel method, of class org.netbeans.modules.tasklist.core.Task.
     */
    public void testGetLevel() {
        System.out.println("testGetLevel");
        
        // TODO add your test code below by replacing the default call to fail.

    }
    
    /**
     * Test of setSummary method, of class org.netbeans.modules.tasklist.core.Task.
     */
    public void testSetSummary() {
        System.out.println("testSetSummary");
        
        // TODO add your test code below by replacing the default call to fail.

    }
    
    /**
     * Test of setDetails method, of class org.netbeans.modules.tasklist.core.Task.
     */
    public void testSetDetails() {
        System.out.println("testSetDetails");
        
        // TODO add your test code below by replacing the default call to fail.

    }
    
    /**
     * Test of setPriority method, of class org.netbeans.modules.tasklist.core.Task.
     */
    public void testSetPriority() {
        System.out.println("testSetPriority");
        
        // TODO add your test code below by replacing the default call to fail.

    }
    
    /**
     * Test of isVisitable method, of class org.netbeans.modules.tasklist.core.Task.
     */
    public void testIsVisitable() {
        System.out.println("testIsVisitable");
        
        // TODO add your test code below by replacing the default call to fail.

    }
    
    /**
     * Test of setVisitable method, of class org.netbeans.modules.tasklist.core.Task.
     */
    public void testSetVisitable() {
        System.out.println("testSetVisitable");
        
        // TODO add your test code below by replacing the default call to fail.

    }

    /**
     * Test of getDisplayName method, of class org.netbeans.modules.tasklist.core.Task.
     */
    public void testGetDisplayName() {
        System.out.println("testGetDisplayName");
        
        // TODO add your test code below by replacing the default call to fail.

    }
    
    /**
     * Test of recursivePropertyChange method, of class org.netbeans.modules.tasklist.core.Task.
     */
    public void testRecursivePropertyChange() {
        System.out.println("testRecursivePropertyChange");
        
        // TODO add your test code below by replacing the default call to fail.

    }
    
    /**
     * Test of getSubtasks method, of class org.netbeans.modules.tasklist.core.Task.
     */
    public void testGetSubtasks() {
        System.out.println("testGetSubtasks");
        
        // TODO add your test code below by replacing the default call to fail.

    }
    
    /**
     * Test of addSubtask method, of class org.netbeans.modules.tasklist.core.Task.
     */
    public void testAddSubtask() {
        System.out.println("testAddSubtask");

        Task task = new Task("Root", null);
        final Task c1 = new Task("Child 1", null);
        final Task c2 = new Task("Child 2", null);

        ObservableList list = new TaskList();

        final boolean tlCallbacks[] = new boolean[2];
        
        list.addTaskListener(new TaskListener() {
            public void selectedTask(Task t) {
                System.out.println("selectedTask:" + t);
            }

            public void warpedTask(Task t) {
                System.out.println("warpedTask:" + t);
            }

            public void addedTask(Task t) {
                System.out.println("addedTask:" + t);
                if (t == c1) tlCallbacks[0] = true;
                if (t == c2) tlCallbacks[1] = true;
            }

            public void removedTask(Task pt, Task t, int index) {
                System.out.println("removedTask:" + t);
            }

            public void structureChanged(Task t) {
                System.out.println("structureChangedTask:" + t);
                fail("Unexpected event");
            }
        });

        task.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                System.out.println("PCE" + evt.getPropertyName() + " " + evt.getNewValue());
            }
        });

        task.addSubtask(c1);
        task.addSubtask(c2);
        
        assertTrue(tlCallbacks[0]);
        assertTrue(tlCallbacks[1]);
        
    }
    
    /**
     * Test of addSubtasks method, of class org.netbeans.modules.tasklist.core.Task.
     * Test fired events.
     */
    public void testAddSubtasks() {
        System.out.println("testAddSubtasks");
        
        Task task = new Task("Root", null);
        final Task c1 = new Task("Child 1", null);
        final Task c2 = new Task("Child 2", null);

        ObservableList list = new TaskList();
//        task = list.getRoot();
        final Task root = task;

        final boolean tlCallbacks[]  = new boolean[2];
        list.addTaskListener(new TaskListener() {
            public void selectedTask(Task t) {
                System.out.println("selectedTask:" + t);
            }

            public void warpedTask(Task t) {
                System.out.println("warpedTask:" + t);
            }

            public void addedTask(Task t) {
                System.out.println("addedTask:" + t);
                fail("Unexpected event");
            }

            public void removedTask(Task pt, Task t, int index) {
                System.out.println("removedTask:" + t);
            }

            public void structureChanged(Task t) {
                System.out.println("structureChangedTask:" + t);
                if (t == root) tlCallbacks[0] = true;
            }
        });

        task.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                System.out.println("PCE" + evt.getPropertyName() + " " + evt.getNewValue());
            }
        });

        List l = new ArrayList();
        l.add(c1);
        l.add(c2);
        task.addSubtasks(l, true, null);
        
        assertTrue(tlCallbacks[0]);
        
    }
    
    /**
     * Test of removeSubtask method, of class org.netbeans.modules.tasklist.core.Task.
     * Test fired events.
     */
    public void testRemoveSubtask() {
        System.out.println("testRemoveSubtask");
        
        Task task = new Task("Root", null);
        final Task c1 = new Task("Child 1", null);
        final Task c2 = new Task("Child 2", null);

        ObservableList list = new TaskList();
        task.getRoot();

        List l = new ArrayList();
        l.add(c1);
        l.add(c2);
        task.addSubtasks(l, true, null);

        final boolean tlCallbacks[]  = new boolean[2];
        list.addTaskListener(new TaskListener() {
            public void selectedTask(Task t) {
                System.out.println("selectedTask:" + t);
            }

            public void warpedTask(Task t) {
                System.out.println("warpedTask:" + t);
            }

            public void addedTask(Task t) {
                System.out.println("addedTask:" + t);
            }

            public void removedTask(Task pt, Task t, int index) {
                System.out.println("removedTask:" + t);
                if (c1 == t) tlCallbacks[0] = true;
                if (c2 == t) tlCallbacks[1] = true;
            }

            public void structureChanged(Task t) {
                System.out.println("structureChangedTask:" + t);
                fail("Unexpected event");
            }
        });

        task.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent evt) {
                System.out.println("PCE" + evt.getPropertyName() + " " + evt.getNewValue());
            }
        });

        task.removeSubtask(c1);
        task.removeSubtask(c2);
        
        assertTrue(tlCallbacks[0]);
        assertTrue(tlCallbacks[1]);
    }
    
    /**
     * Test of hasSubtasks method, of class org.netbeans.modules.tasklist.core.Task.
     */
    public void testHasSubtasks() {
        System.out.println("testHasSubtasks");
        
        // TODO add your test code below by replacing the default call to fail.

    }
    
    /**
     * Test of getParent method, of class org.netbeans.modules.tasklist.core.Task.
     */
    public void testGetParent() {
        System.out.println("testGetParent");
        
        // TODO add your test code below by replacing the default call to fail.

    }
    
    /**
     * Test of isParentOf method, of class org.netbeans.modules.tasklist.core.Task.
     */
    public void testIsParentOf() {
        System.out.println("testIsParentOf");
        
        // TODO add your test code below by replacing the default call to fail.

    }
    
    /**
     * Test of setParent method, of class org.netbeans.modules.tasklist.core.Task.
     */
    public void testSetParent() {
        System.out.println("testSetParent");
        
        // TODO add your test code below by replacing the default call to fail.

    }
    
    /**
     * Test of isZombie method, of class org.netbeans.modules.tasklist.core.Task.
     */
    public void testIsZombie() {
        System.out.println("testIsZombie");
        
        // TODO add your test code below by replacing the default call to fail.

    }
    
    /**
     * Test of generate method, of class org.netbeans.modules.tasklist.core.Task.
     */
    public void testGenerate() {
        System.out.println("testGenerate");
        
        // TODO add your test code below by replacing the default call to fail.

    }
    
    /**
     * Test of parse method, of class org.netbeans.modules.tasklist.core.Task.
     */
    public void testParse() {
        System.out.println("testParse");
        
        // TODO add your test code below by replacing the default call to fail.

    }
    
    /**
     * Test of setSilentUpdate method, of class org.netbeans.modules.tasklist.core.Task.
     */
    public void testSetSilentUpdate() {
        System.out.println("testSetSilentUpdate");
        
        // TODO add your test code below by replacing the default call to fail.

    }
    
    /**
     * Test of setList method, of class org.netbeans.modules.tasklist.core.Task.
     */
    public void testSetList() {
        System.out.println("testSetList");
        
        // TODO add your test code below by replacing the default call to fail.
        // test that is was propagated recursivelly
    }
    
    /**
     * Test of getList method, of class org.netbeans.modules.tasklist.core.Task.
     */
    public void testGetList() {
        System.out.println("testGetList");
        
        // TODO add your test code below by replacing the default call to fail.

    }
    
    /**
     * Test of getSubtaskCountRecursively method, of class org.netbeans.modules.tasklist.core.Task.
     */
    public void testGetSubtaskCountRecursively() {
        System.out.println("testGetSubtaskCountRecursively");
        
        // TODO add your test code below by replacing the default call to fail.

    }
    
    /**
     * Test of createNode method, of class org.netbeans.modules.tasklist.core.Task.
     */
    public void testCreateNode() {
        System.out.println("testCreateNode");
        
        // TODO add your test code below by replacing the default call to fail.

    }
    
    /**
     * Test of clone method, of class org.netbeans.modules.tasklist.core.Task.
     */
    public void testClone() {
        System.out.println("testClone");
        
        // TODO add your test code below by replacing the default call to fail.

    }
    
    /**
     * Test of cloneTask method, of class org.netbeans.modules.tasklist.core.Task.
     */
    public void testCloneTask() {
        System.out.println("testCloneTask");
        
        // TODO add your test code below by replacing the default call to fail.

    }
    
    /**
     * Test of getKey method, of class org.netbeans.modules.tasklist.core.Task.
     */
    public void testGetKey() {
        System.out.println("testGetKey");
        
        // TODO add your test code below by replacing the default call to fail.

    }
    
    /**
     * Test of getSeed method, of class org.netbeans.modules.tasklist.core.Task.
     */
    public void testGetSeed() {
        System.out.println("testGetSeed");
        
        // TODO add your test code below by replacing the default call to fail.

    }
    
    /**
     * Test of copyFrom method, of class org.netbeans.modules.tasklist.core.Task.
     */
    public void testCopyFrom() {
        System.out.println("testCopyFrom");
        
        // TODO add your test code below by replacing the default call to fail.

    }
    
}

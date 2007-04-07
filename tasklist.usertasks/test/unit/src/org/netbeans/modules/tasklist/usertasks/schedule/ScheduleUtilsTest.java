/*
 * ScheduleUtilsTest.java
 * JUnit based test
 *
 * Created on 30. Januar 2007, 11:38
 */

package org.netbeans.modules.tasklist.usertasks.schedule;

import java.util.ArrayList;
import java.util.List;
import junit.framework.TestCase;
import org.netbeans.modules.tasklist.usertasks.model.Dependency;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskList;

/**
 * Tests for ScheduleUtils
 * 
 * @author tim
 */
public class ScheduleUtilsTest extends TestCase {
    /**
     * Constructor.
     * 
     * @param testName name of the test
     */
    public ScheduleUtilsTest(String testName) {
        super(testName);
    }
    
    /**
     * Tests sortForDependencies.
     * a->b->c
     */
    public void testSortForDependencies() {
        UserTaskList utl = new UserTaskList();
        UserTask a = new UserTask("a", utl);
        UserTask b = new UserTask("b", utl);
        UserTask c = new UserTask("c", utl);
        
        c.getDependencies().add(new Dependency(b, Dependency.END_BEGIN));
        b.getDependencies().add(new Dependency(a, Dependency.END_BEGIN));

        List<UserTask> tasks = new ArrayList<UserTask>();
        tasks.add(c);
        tasks.add(a);
        tasks.add(b);
        
        ScheduleUtils.sortForDependencies(tasks);
        
        assertEquals(tasks.get(0), a);
        assertEquals(tasks.get(1), b);
        assertEquals(tasks.get(2), c);
    }
    
    /**
     * 4 tasks: 
     * A: 3 minutes
     * B: 2 minutes
     * C: 4 minutes
     * D: 2 minutes
     * A depends on B
     * C depends on B
     * A depends on C
     * D depends on C
     * 
     * Dependency matrix: 
     *   A B C D
     * A   t t
     * B   
     * C   t 
     * D     t 
     */
    public void testBackflow() {
        boolean[][] deps = new boolean[][] {
            {false, true, true, false},
            {false, false, false, false},
            {false, true, false, false},
            {false, false, true, false}
        };
        float[] durations = new float[] {3, 2, 4, 2};
        float[] ct = ScheduleUtils.backflow(deps, durations);
        assertEquals(ct.length, 4);
        assertEquals(ct[0], 3, 1e-5);
        assertEquals(ct[1], 9, 1e-5);
        assertEquals(ct[2], 7, 1e-5);
        assertEquals(ct[3], 2, 1e-5);
    }

    /**
     * Test for createPriorityListBackflow (see testBackflow for data
     * definition) 
     */
    public void testCreatePriorityListBackflow() {
        UserTaskList utl = new UserTaskList();
        UserTask a = new UserTask("A", utl);
        UserTask b = new UserTask("B", utl);
        UserTask c = new UserTask("C", utl);
        UserTask d = new UserTask("D", utl);
        a.getDependencies().add(new Dependency(b, Dependency.END_BEGIN));
        c.getDependencies().add(new Dependency(b, Dependency.END_BEGIN));
        a.getDependencies().add(new Dependency(c, Dependency.END_BEGIN));
        d.getDependencies().add(new Dependency(c, Dependency.END_BEGIN));
        a.setEffort(3);
        b.setEffort(2);
        c.setEffort(4);
        d.setEffort(2);
        
        List<UserTask> v = new ArrayList<UserTask>();
        v.add(a);
        v.add(b);
        v.add(c);
        v.add(d);
        
        ScheduleUtils.createPriorityListBackflow(v);
        
        assertEquals("B", v.get(0).getSummary());
        assertEquals("C", v.get(1).getSummary());
        assertEquals("A", v.get(2).getSummary());
        assertEquals("D", v.get(3).getSummary());
    }

    /**
     * Test for subtasks.
     * 
     * A
     * |--B
     * |--C
     * D
     * |--E
     * |--F
     * 
     * Additionally A depends on D, B on C, F on E and C on D.
     */
    public void testSortForDependencies2() {
        UserTaskList utl = new UserTaskList();
        UserTask a = new UserTask("a", utl);
        UserTask b = new UserTask("b", utl);
        UserTask c = new UserTask("c", utl);
        UserTask d = new UserTask("d", utl);
        UserTask e = new UserTask("e", utl);
        UserTask f = new UserTask("f", utl);
        
        a.getSubtasks().add(b);
        a.getSubtasks().add(c);
        d.getSubtasks().add(e);
        d.getSubtasks().add(f);
        
        a.setValuesComputed(true);
        d.setValuesComputed(true);
        
        a.getDependencies().add(new Dependency(d, Dependency.END_BEGIN));
        b.getDependencies().add(new Dependency(c, Dependency.END_BEGIN));
        f.getDependencies().add(new Dependency(e, Dependency.END_BEGIN));
        c.getDependencies().add(new Dependency(d, Dependency.END_BEGIN));

        List<UserTask> tasks = new ArrayList<UserTask>();
        tasks.add(a);
        tasks.add(b);
        tasks.add(c);
        tasks.add(d);
        tasks.add(e);
        tasks.add(f);
        
        ScheduleUtils.sortForDependencies(tasks);
        
        assertEquals(tasks.get(0), e);
        assertEquals(tasks.get(1), f);
        assertEquals(tasks.get(2), d);
        assertEquals(tasks.get(3), c);
        assertEquals(tasks.get(4), b);
        assertEquals(tasks.get(5), a);
    }
}

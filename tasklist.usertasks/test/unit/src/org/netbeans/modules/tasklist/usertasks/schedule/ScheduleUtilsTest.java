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
}

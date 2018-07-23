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

package org.netbeans.modules.tasklist.usertasks.schedule;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.netbeans.modules.tasklist.usertasks.model.Dependency;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.util.ObjectList;
import org.netbeans.modules.tasklist.usertasks.util.UTUtils;

/**
 * Some utility methods for scheduling.
 *
 * @author tl
 */
public class ScheduleUtils {
    /**
     * Creates a new instance of ScheduleUtils.
     */
    private ScheduleUtils() {
    }
    
    /**
     * Just sorts tasks by priority.
     * 
     * @param tasks list of tasks 
     */
    public static void createPriorityListByPriority(List<UserTask> tasks) {
        Collections.sort(tasks, new Comparator() {
            public int compare(Object o1, Object o2) {
                UserTask ut1 = (UserTask) o1;
                UserTask ut2 = (UserTask) o2;
                return ut1.getPriority() - ut2.getPriority();
            }
        });
    }

    /**
     * Schedule long tasks first.
     * Decreasing-Time Algorithm (DTA).
     * see http://www.ctl.ua.edu/math103/scheduling/scheduling_algorithms.htm#
     * Scheduling%20Algorithms
     * 
     * @param tasks list of tasks 
     */
    public static void createPriorityListByDuration(List<UserTask> tasks) {
        Collections.sort(tasks, new Comparator() {
            public int compare(Object o1, Object o2) {
                UserTask ut1 = (UserTask) o1;
                UserTask ut2 = (UserTask) o2;
                return ut2.getEffort() - ut1.getEffort();
            }
        });
    }
       
    /**
     * Holds a UserTask and it's critical time 
     */
    private static class UserTaskAndCriticalTime {
        /** a task */
        public UserTask ut;
        
        /** critical time */
        public float ct;
    }
    
    /**
     * Schedule tasks with higher critical times first.
     * Critical-Path Algorithm (CPA).
     * see http://www.ctl.ua.edu/math103/scheduling/scheduling_algorithms.htm#
     * Scheduling%20Algorithms
     * 
     * @param tasks list of tasks 
     */
    public static void createPriorityListBackflow(List<UserTask> tasks) {
        // convert data to call backflow
        boolean[][] deps = new boolean[tasks.size()][tasks.size()];
        for (int i = 0; i < tasks.size(); i++) {
            ObjectList<Dependency> ds = tasks.get(i).getDependencies();
            for (int j = 0; j < ds.size(); j++) {
                Dependency d = ds.get(j);
                if (d.getType() == Dependency.END_BEGIN) {
                    UserTask don = d.getDependsOn();
                    int index = UTUtils.identityIndexOf(tasks, don);
                    if (index >= 0)
                        deps[i][index] = true;
                }
            }
            
            // implicit dependencies on subtasks
            if (tasks.get(i).isValuesComputed()) {
                for (int j = 0; j < tasks.get(j).getSubtasks().size(); j++) {
                    UserTask st = tasks.get(j).getSubtasks().get(j);
                    int index = UTUtils.identityIndexOf(tasks, st);
                    if (index > 0) {
                        deps[i][index] = true;
                    }
                }
            }
        }
        float[] durations = new float[tasks.size()];
        for (int i = 0; i < tasks.size(); i++) {
            durations[i] = tasks.get(i).getRemainingEffort();
        }
        float[] ct = backflow(deps, durations);
        
        // sorting using critical times
        UserTaskAndCriticalTime[] v = new UserTaskAndCriticalTime[tasks.size()];
        for (int i = 0; i < v.length; i++) {
            UserTaskAndCriticalTime utct = new UserTaskAndCriticalTime();
            utct.ut = tasks.get(i);
            utct.ct = ct[i];
            v[i] = utct;
        }        
        Arrays.sort(v, new Comparator() {
            public int compare(Object o1, Object o2) {
                UserTaskAndCriticalTime ut1 = (UserTaskAndCriticalTime) o1;
                UserTaskAndCriticalTime ut2 = (UserTaskAndCriticalTime) o2;
                return Float.compare(ut2.ct, ut1.ct);
            }
        });
        
        // writing results back
        tasks.clear();        
        for (int i = 0; i < v.length; i++) {
            tasks.add(v[i].ut);
        }
    }
       
    /**
     * Backflow algorithm.
     * http://www.ctl.ua.edu/math103/scheduling/cpaprelim.htm#The%20Backflow%20Algorithm 
     * 
     * @param deps dependencies between tasks. Square matrix with 
     *     deps[i][j] = true if task[i] depends on task[j]
     * @param durations durations of tasks
     * @return critical times
     */
    public static float[] backflow(boolean[][] deps, float[] durations) {
        assert deps.length == durations.length;
        
        int n = durations.length;
        if (n == 0)
            return new float[0];
        
        float[] ct = new float[n];
        Arrays.fill(ct, -1);
        
        for (int i = 0; i < n; i++) {
            backflow2(ct, deps, durations, i);
        }
        
        return ct;
    }
    
    private static void backflow2(float[] ct, boolean[][] deps, float[] durations,
            int knot) {
        if (ct[knot] >= 0)
            return;
        
        float max = 0;
        for (int i = 0; i < durations.length; i++) {
            if (deps[i][knot]) {
                backflow2(ct, deps, durations, i);
                if (ct[i] > max)
                    max = ct[i];
            }
        }
        ct[knot] = max + durations[knot];
    }
    
    /**
     * Sorts tasks using dependencies such that tasks can be completed in 
     * the order they are in <code>tasks</code>
     * 
     * @param tasks list of tasks
     */
    public static void sortForDependencies(List<UserTask> tasks) {
        boolean swapped;
        do {
            swapped = false;
            outer:
            for (int i = 0; i < tasks.size() - 1; i++) {
                UserTask t = tasks.get(i);
                for (int j = i + 1; j < tasks.size(); j++) {
                    UserTask t2 = tasks.get(j);
                    if (depends(t, t2)) {
                        swapped = true;
                        tasks.add(i, tasks.remove(j));
                        break outer;
                    }
                }
            }
        } while (swapped);
    }
    
    /**
     * Checks whether one task depends on another one. Implicit dependencies
     * for subtasks are considered as well.
     * 
     * @param t a task
     * @param t2 another task
     * @return true if t depends on t2 
     */
    private static boolean depends(UserTask t, UserTask t2) {
        if (t.isValuesComputed() && t2.getParent() == t)
            return true;
        for (Dependency d: t.getDependencies()) {
            if (d.getType() == Dependency.END_BEGIN &&
                    d.getDependsOn() == t2) {
                return true;
            }
        }
        return false;
    }
}

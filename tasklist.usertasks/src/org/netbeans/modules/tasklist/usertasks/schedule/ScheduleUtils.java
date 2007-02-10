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

package org.netbeans.modules.tasklist.usertasks.schedule;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.netbeans.modules.tasklist.usertasks.model.Dependency;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;

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
                    for (Dependency d: t.getDependencies()) {
                        if (d.getType() == Dependency.END_BEGIN &&
                                d.getDependsOn() == t2) {
                            swapped = true;
                            tasks.add(i, tasks.remove(j));
                            break outer;
                        }
                    }
                }
            }
        } while (swapped);
    }
}

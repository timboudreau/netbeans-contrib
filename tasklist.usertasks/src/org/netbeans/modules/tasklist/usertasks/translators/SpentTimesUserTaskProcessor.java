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
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.usertasks.translators;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.model.UserTask.WorkPeriod;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskList;
import org.netbeans.modules.tasklist.usertasks.util.UnaryFunction;

/**
 * Summarizes spent times.
 *
 * @author tl
 */
public class SpentTimesUserTaskProcessor implements UnaryFunction {
    private static int[] FIELDS = {
        Calendar.DAY_OF_YEAR,
        Calendar.WEEK_OF_YEAR,
        Calendar.MONTH,
        Calendar.MONTH,
        Calendar.YEAR
    };
    private static int[] FIELD_ADDS = {
        1,
        1,
        1,
        3,
        1
    };
            
    private Date from;
    private Date to;
    private int minDuration;
    private HistoryOptionsPanel.Group group;
    
    private Comparator comp;
    private List<long[]> durations = new ArrayList<long[]>();
    private List<Date> periods = new ArrayList<Date>();
            
    /**
     * Creates a new instance of SpentTimesUserTaskProcessor.
     *
     * @param from start date
     * @param to end date
     * @param group grouping
     * @param minDuration minimal task duration
     */
    public SpentTimesUserTaskProcessor(Date from, Date to, 
            HistoryOptionsPanel.Group group, int minDuration) {
        this.from = from;
        this.to = to;
        this.group = group;
        this.minDuration = minDuration;
        
        int field = FIELDS[group.ordinal()];
        int add = FIELD_ADDS[group.ordinal()];
        Calendar c = Calendar.getInstance();
        c.setTime(from);
        switch (group) {
            case DAILY:
                break;
            case WEEKLY:
                c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
                break;
            case MONTHLY:
                c.set(Calendar.DAY_OF_MONTH, 1);
                break;
            case QUARTERLY:
                c.set(Calendar.MONTH, c.get(Calendar.MONTH) / 3 * 3);
                c.set(Calendar.DAY_OF_MONTH, 1);
                break;
            case YEARLY:
                c.set(Calendar.DAY_OF_YEAR, 1);
                break;
        }
        
        while (c.getTime().compareTo(to) < 0) {
            periods.add(c.getTime());
            c.add(field, add);
        }
        periods.add(c.getTime());
    }

    /**
     * Returns periods.
     *
     * @return array of dates. This array is one element longer than
     * returned by getTasks
     */
    public List<Date> getPeriods() {
        return periods;
    }
    
    /**
     * Returns spent times.
     *
     * @return spent times (milliseconds)
     */
    public List<long[]> getDurations() {
        return durations;
    }
    
    /**
     * Computes common part of 2 periods.
     *
     * @param start start of the first period
     * @param end end of the first period
     * @param start2 start of the second period
     * @param end2 end of the second period
     */
    private long overlap(long start, long end, long start2, long end2) {
        if (start2 >= end || end2 <= start)
            return 0;
        
        return Math.min(end, end2) - Math.max(start, start2);
    }

    public Object compute(Object obj) {
        UserTaskInfo info = (UserTaskInfo) obj;
        info.spentTimes = new long[periods.size() - 1];
        if ((info.object instanceof UserTask) && 
                !((UserTask) info.object).isSpentTimeComputed()) {
            for (WorkPeriod wp: ((UserTask) info.object).getWorkPeriods()) {
                for (int i = 0; i < periods.size() - 1; i++) {
                    long ov = overlap(wp.getStart(), wp.getStart() + 
                            wp.getDuration() * 60L * 1000,
                            periods.get(i).getTime(), 
                            periods.get(i + 1).getTime());
                    if (ov != 0) {
                        info.spentTimes[i] += ov;
                    }
                }
            }
        } else {
            for (UserTaskInfo ch: info.children) {
                for (int i = 0; i < info.spentTimes.length; i++) {
                    info.spentTimes[i] += ch.spentTimes[i];
                }
            }
        }
        return null;
    }
}

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.netbeans.modules.tasklist.usertasks.options.Settings;
import org.netbeans.modules.tasklist.usertasks.UserTaskView;
import org.netbeans.modules.tasklist.usertasks.UserTaskViewRegistry;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskList;
import org.netbeans.modules.tasklist.usertasks.util.UTListTreeAbstraction;
import org.netbeans.modules.tasklist.usertasks.util.UTUtils;
import org.netbeans.modules.tasklist.usertasks.util.UnaryFunction;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

/**
 * Collapses all tasks containing subtasks in the tasklist
 * 
 * @author tl
 */
public final class ScheduleAction extends CallableSystemAction {
    private static final long serialVersionUID = 1;
    
    private static final boolean[] WORKING_DAYS = {
        true, true, true, true, true, false, false
    };

    /**
     * Schedule tasks.
     */
    public void performAction() {
        String currentUser = System.getProperty("user.name"); // NOI18N
        String message = NbBundle.getMessage(ScheduleAction.class,
                "AreYouSureToSchedule", currentUser); // NOI18N
        String title = NbBundle.getMessage(ScheduleAction.class,
                "Warning"); // NOI18N
        NotifyDescriptor desc = new NotifyDescriptor.Confirmation(
                message, title, NotifyDescriptor.YES_NO_OPTION);
        if (!NotifyDescriptor.YES_OPTION.equals(
                DialogDisplayer.getDefault().notify(desc)))
            return;
        
        UserTaskView[] views = UserTaskViewRegistry.getInstance().getAll();
        final List<UserTask> tasks = new ArrayList<UserTask>();
        Set<String> users = new HashSet<String>();
        for (int i = 0; i < views.length; i++) {
            UserTaskList utl = views[i].getUserTaskList();
            users.addAll(Arrays.asList(utl.getOwners()));
            
            UTListTreeAbstraction tree = new UTListTreeAbstraction(utl);
            UnaryFunction f = new UnaryFunction() {
                public Object compute(Object obj) {
                    if (obj instanceof UserTask) {
                        UserTask ut = (UserTask) obj;
                        if (!ut.isValuesComputed() && !ut.isDone()) {
                            tasks.add(ut);
                        }
                    } 
                    return null;
                }
            };
            UTUtils.processDepthFirst(tree, f);
        }
        Collections.sort(tasks, new Comparator() {
            public int compare(Object o1, Object o2) {
                UserTask ut1 = (UserTask) o1;
                UserTask ut2 = (UserTask) o2;
                return ut1.getPriority() - ut2.getPriority();
            }
        });
        
        users.add(currentUser);
        
        String[] users_ = users.toArray(new String[users.size()]);
        Arrays.sort(users_);
        long[] firstFreeTime = new long[users_.length];
        Calendar time = Calendar.getInstance();
        alignTo15Min(time);
        Arrays.fill(firstFreeTime, time.getTimeInMillis());
        
        Settings s = Settings.getDefault();
        int daysPerWeek = s.getDaysPerWeek();
        boolean[] wd = s.getWorkingDays();
        
        Calendar cal = Calendar.getInstance();
        
        for (int i = 0; i < tasks.size(); i++) {
            UserTask ut = tasks.get(i);
            if (ut.getOwner().trim().length() == 0)
                ut.setOwner(currentUser);
            int index = Arrays.binarySearch(users_, ut.getOwner());
            
            cal.setTimeInMillis(firstFreeTime[index]);
            Calendar start = Calendar.getInstance();
            Calendar due = Calendar.getInstance();
            alignTo15Min(cal);
            add(cal, ut.getEffort(), start, due, wd);
            ut.setStart(start.getTimeInMillis());
            // ut.setDueDate(cal.getTime());
            firstFreeTime[index] = cal.getTimeInMillis();
        }
    }

    /**
     * @param wd[7] working days (marked with true). [0] - monday
     */
    static final void add(Calendar cal, int minutes, Calendar start, 
            Calendar due, boolean[] wd) {
        boolean first = true;
        while (minutes > 0) {
            int d = findNextWorkPeriod(cal, wd);
            if (d > minutes)
                d = minutes;
            due.setTime(cal.getTime());
            due.add(Calendar.MINUTE, d);
            if (first) {
                start.setTime(cal.getTime());
                first = false;
            }
            cal.setTime(due.getTime());
            minutes -= d;
        }
    }
    
    /**
     * Find next work period.
     * 
     * @param cal current time
     * @param wd[7] working days (marked with true). [0] - monday
     * @return period duration in minutes
     */
    private static int findNextWorkPeriod(Calendar cal, boolean[] wd) {
        scrollToWorkingDay(cal, wd);
        
        Calendar startWorkingDay = Calendar.getInstance();
        startWorkingDay.setTime(cal.getTime());
        setMinutes(startWorkingDay, 
                Settings.getDefault().getWorkingDayStart());
        
        Calendar startPause = Calendar.getInstance();
        startPause.setTime(cal.getTime());
        setMinutes(startPause, Settings.getDefault().getPauseStart());
        
        Calendar endPause = Calendar.getInstance();
        endPause.setTime(cal.getTime());
        setMinutes(endPause, Settings.getDefault().getPauseEnd());
        
        Calendar endWorkingDay = Calendar.getInstance();
        endWorkingDay.setTime(cal.getTime());
        setMinutes(endWorkingDay, Settings.getDefault().getWorkingDayEnd());

        Calendar end;
        if (cal.compareTo(startWorkingDay) <= 0) {
            cal.setTimeInMillis(startWorkingDay.getTimeInMillis());
            end = startPause;
        } else if (cal.compareTo(startPause) < 0) {
            end = startPause;
        } else if (cal.compareTo(endPause) <= 0) {
            cal.setTimeInMillis(endPause.getTimeInMillis());
            end = endWorkingDay;
        } else if (cal.compareTo(endWorkingDay) < 0) {
            end = endWorkingDay;
        } else {
            startWorkingDay.add(Calendar.DAY_OF_YEAR, 1);
            scrollToWorkingDay(startWorkingDay, wd);
            startPause.setTime(startWorkingDay.getTime());
            setMinutes(startPause, Settings.getDefault().getPauseStart());
            cal.setTime(startWorkingDay.getTime());
            end = startPause;
        }
        return (int) ((end.getTimeInMillis() - 
            cal.getTimeInMillis()) / (1000 * 60));
    }
    
    /**
     * Aligns time to a 15min-boundary.
     *
     * @param cal a time point
     */
    private static void alignTo15Min(Calendar cal) {
        int min = cal.get(Calendar.MINUTE);
        min = (min + 14) / 15 * 15;
        if (min == 60) {
            cal.add(Calendar.HOUR_OF_DAY, 1);
            cal.set(Calendar.MINUTE, 0);
        } else {
            cal.set(Calendar.MINUTE, min);
        }
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }
    
    public String getName() {
        return NbBundle.getMessage(ScheduleAction.class, 
                "Schedule"); // NOI18N
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx (MyAction.class);
    }

    /**
     * Changes time of a Calendar.
     * 
     * @param cal a calendar
     * @param min offset in minutes from 00:00. The offset cannot be bigger
     * than 60 * 24
     */
    private static void setMinutes(Calendar cal, int min) {
        assert min <= 60 * 24;
        cal.set(Calendar.HOUR_OF_DAY, min / 60);
        cal.set(Calendar.MINUTE, min % 60);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
    }

    /**
     * Scrolls cal to a working day.
     * 
     * @param cal current time (will be changed)
     * @param wd working days (marked with true). [0] - monday 
     */
    private static void scrollToWorkingDay(Calendar cal, boolean[] wd) {
        while (true) {
            int dow = cal.get(Calendar.DAY_OF_WEEK);
            if (dow == Calendar.SUNDAY)
                dow = 6;
            else 
                dow -= 2;
            if (wd[dow])
                break;
            cal.add(Calendar.DAY_OF_YEAR, 1);
            setMinutes(cal, 0);
        }
    }
}

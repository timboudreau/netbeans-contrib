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

package org.netbeans.modules.tasklist.usertasks.actions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.netbeans.modules.tasklist.usertasks.options.Settings;
import org.netbeans.modules.tasklist.usertasks.UserTaskView;
import org.netbeans.modules.tasklist.usertasks.UserTaskViewRegistry;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskList;
import org.netbeans.modules.tasklist.usertasks.schedule.ScheduleUtils;
import org.netbeans.modules.tasklist.usertasks.util.UTListTreeAbstraction;
import org.netbeans.modules.tasklist.usertasks.util.UTUtils;
import org.netbeans.modules.tasklist.usertasks.util.UnaryFunction;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

/**
 * Schedules undone tasks.
 * 
 * @author tl
 */
public final class ScheduleAction extends CallableSystemAction {
    private static final long serialVersionUID = 1;
    
    /**
     * Loads a message.
     * 
     * @param key key in Bundle.properties
     * @return the message
     */
    private static String loc(String key) {
        return NbBundle.getMessage(ScheduleAction.class, key); 
    }
    
    /**
     * Schedule tasks.
     */
    public void performAction() {
        // warn the user
        String currentUser = System.getProperty("user.name"); // NOI18N
        String message = NbBundle.getMessage(ScheduleAction.class,
                "AreYouSureToSchedule", currentUser); // NOI18N
        String title = loc("Warning"); // NOI18N
        NotifyDescriptor desc = new NotifyDescriptor.Confirmation(
                message, title, NotifyDescriptor.YES_NO_OPTION);
        if (!NotifyDescriptor.YES_OPTION.equals(
                DialogDisplayer.getDefault().notify(desc)))
            return;
        
        UserTaskView view = UserTaskViewRegistry.getInstance().getCurrent();
        UserTaskList utl = view.getUserTaskList();
        schedule(utl);
    }
    
    public static void schedule(UserTaskList utl) {
        String currentUser = System.getProperty("user.name"); // NOI18N
        final List<UserTask> tasks = new ArrayList<UserTask>();
        Set<String> users = new HashSet<String>();

        users.addAll(Arrays.asList(utl.getOwners()));

        // find all tasks with not computed values
        UTListTreeAbstraction tree = new UTListTreeAbstraction(utl);
        UnaryFunction f = new UnaryFunction() { 
            public Object compute(Object obj) {
                if (obj instanceof UserTask) {
                    UserTask ut = (UserTask) obj;
                    if (!ut.isDone()) {
                        tasks.add(ut);
                    }
                } 
                return null;
            }
        };
        UTUtils.processDepthFirst(tree, f);

        // sort them on priority
        ScheduleUtils.createPriorityListBackflow(tasks);
        
        ScheduleUtils.sortForDependencies(tasks);
        
        users.add(currentUser);
        
        String[] users_ = users.toArray(new String[users.size()]);
        Arrays.sort(users_);
        long[] firstFreeTime = new long[users_.length];
        Calendar time = Calendar.getInstance();
        alignTo15Min(time);
        Arrays.fill(firstFreeTime, time.getTimeInMillis());
        
        Settings s = Settings.getDefault();
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
            if (!ut.isValuesComputed()) {
                int effort;
                effort = ut.getRemainingEffort();
                add(cal, effort, start, due, wd);
                ut.setStart(start.getTimeInMillis());
                // ut.setDueDate(cal.getTime());
                firstFreeTime[index] = cal.getTimeInMillis();
            } else {
                ut.setStart(start.getTimeInMillis());
            }
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
        return loc("Schedule"); // NOI18N
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

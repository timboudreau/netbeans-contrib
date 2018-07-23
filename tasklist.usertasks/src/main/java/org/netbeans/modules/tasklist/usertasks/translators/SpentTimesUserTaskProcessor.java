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
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
                !((UserTask) info.object).isValuesComputed()) {
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

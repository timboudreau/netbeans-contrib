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
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.netbeans.modules.tasklist.usertasks.table.grouping;

import java.util.Calendar;
import java.util.Date;
import org.netbeans.modules.tasklist.usertasks.util.UTUtils;
import org.netbeans.modules.tasklist.usertasks.util.UnaryFunction;

/**
 * Creates default date groups for long (as in System.currentTimeMillis()).
 *
 * @author tl
 */
public class DateGroupBuilder implements UnaryFunction {
    private static long yesterday, today, tomorrow, in2days, 
            lastWeek, weekStart, nextWeekStart,
            lastMonth, monthStart, nextMonthStart;

    public DateGroup compute(Object obj) {
        long millis;
        if (obj instanceof Long)
            millis = (Long) obj;
        else if (obj instanceof Date)
            millis = ((Date) obj).getTime();
        else 
            millis = 0;

        if (millis <= 0)
            return DateGroup.GROUPS[DateGroup.Type.UNDEFINED.ordinal()];
            
        updateTimes();
        
        DateGroup.Type type;
        if (millis >= today && millis < tomorrow)
            type = DateGroup.Type.TODAY;
        else if (millis >= tomorrow && millis < in2days)
            type = DateGroup.Type.TOMORROW;
        else if (millis >= weekStart && millis < nextWeekStart)
            type = DateGroup.Type.THIS_WEEK;
        else if (millis >= monthStart && millis < nextMonthStart)
            type = DateGroup.Type.THIS_MONTH;
        else if (millis >= nextMonthStart)
            type = DateGroup.Type.FUTURE;
        else if (millis >= yesterday && millis < today)
            type = DateGroup.Type.YESTERDAY;
        else if (millis >= lastWeek && millis < weekStart)
            type = DateGroup.Type.LAST_WEEK;
        else if (millis >= lastMonth && millis < monthStart)
            type = DateGroup.Type.LAST_MONTH;
        else
            type = DateGroup.Type.PAST;
        
        
        return DateGroup.GROUPS[type.ordinal()];
    }

    private static void updateTimes() {
        long now = System.currentTimeMillis();
        if (tomorrow <= now) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            today = c.getTimeInMillis();
            c.add(Calendar.DAY_OF_YEAR, 1);
            tomorrow = c.getTimeInMillis();
            c.add(Calendar.DAY_OF_YEAR, 1);
            in2days = c.getTimeInMillis();
            c.add(Calendar.DAY_OF_YEAR, -3);
            yesterday = c.getTimeInMillis();
        }
        if (now > nextWeekStart) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            
            while (c.get(Calendar.DAY_OF_WEEK) != c.getFirstDayOfWeek()) {
                c.add(Calendar.DAY_OF_WEEK, -1);
            }
            
            weekStart = c.getTimeInMillis();
            c.add(Calendar.DAY_OF_YEAR, 7);
            nextWeekStart = c.getTimeInMillis();
            c.add(Calendar.DAY_OF_YEAR, -14);
            lastWeek = c.getTimeInMillis();
        }
        if (now > nextMonthStart) {
            Calendar c = Calendar.getInstance();
            c.set(Calendar.HOUR_OF_DAY, 0);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.MILLISECOND, 0);
            c.set(Calendar.DAY_OF_MONTH, 1);
            monthStart = c.getTimeInMillis();
            c.add(Calendar.MONTH, 1);
            nextMonthStart = c.getTimeInMillis();
            c.add(Calendar.MONTH, -2);
            lastMonth = c.getTimeInMillis();
        }
    }
}

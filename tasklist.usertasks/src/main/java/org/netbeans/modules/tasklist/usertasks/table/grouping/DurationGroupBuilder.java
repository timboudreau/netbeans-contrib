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

import org.netbeans.modules.tasklist.usertasks.model.Duration;
import org.netbeans.modules.tasklist.usertasks.options.Settings;
import org.netbeans.modules.tasklist.usertasks.util.UnaryFunction;

/**
 * Groups durations (minutes as Integers) into categories.
 *
 * @author tl
 */
public class DurationGroupBuilder implements UnaryFunction {
    public DurationGroup compute(Object obj) {
        int time = (Integer) obj;
        Settings s = Settings.getDefault();
        
        Duration d = new Duration(time, s.getMinutesPerDay(), s.getDaysPerWeek(),
                true);
        DurationGroup.Type type;
        if (d.weeks >= 52)
            type = DurationGroup.Type.MORE_THAN_1_YEAR;
        else if (d.weeks >= 26)
            type = DurationGroup.Type._6_MONTHES_TO_1_YEAR;
        else if (d.weeks >= 13)
            type = DurationGroup.Type._3_MONTHES_TO_6_MONTHES;
        else if (d.weeks >= 4)
            type = DurationGroup.Type._1_MONTH_TO_3_MONTHES;
        else if (d.weeks >= 2)
            type = DurationGroup.Type._2_WEEKS_TO_1_MONTH;
        else if (d.weeks >= 1)
            type = DurationGroup.Type._1_WEEK_TO_2_WEEKS;
        else if (d.days >= 2)
            type = DurationGroup.Type._2_DAYS_TO_1_WEEK;
        else if (d.days >= 1)
            type = DurationGroup.Type._1_TO_2_DAYS;
        else if (d.hours >= 4)
            type = DurationGroup.Type._4_TO_8_HOURS;
        else if (d.hours >= 2)
            type = DurationGroup.Type._2_TO_4_HOURS;
        else if (d.hours >= 1)
            type = DurationGroup.Type._1_TO_2_HOURS;
        else if (d.minutes >= 30)
            type = DurationGroup.Type._30_TO_60_MINUTES;
        else if (d.minutes >= 15)
            type = DurationGroup.Type._15_TO_30_MINUTES;
        else
            type = DurationGroup.Type._0_TO_15_MINUTES;
        
        return DurationGroup.GROUPS[type.ordinal()];
    }
}

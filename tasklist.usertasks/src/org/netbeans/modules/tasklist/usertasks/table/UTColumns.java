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

package org.netbeans.modules.tasklist.usertasks.table;

import org.netbeans.modules.tasklist.usertasks.model.UserTask;

/**
 * Available columns.
 *
 * @author tl
 */
public class UTColumns {
    public static final int SUMMARY = 0;
    public static final int PRIORITY = 1;
    public static final int DONE = 2;
    public static final int PERCENT_COMPLETE = 3;
    public static final int EFFORT = 4;
    public static final int REMAINING_EFFORT = 5;
    public static final int SPENT_TIME = 6;
    public static final int DETAILS = 7;
    public static final int CATEGORY = 8;
    public static final int CREATED = 9;
    public static final int LAST_EDITED = 10;
    public static final int COMPLETED_DATE = 11;
    public static final int DUE_DATE = 12;
    public static final int OWNER = 13;
    public static final int START = 14;
    public static final int SPENT_TIME_TODAY = 15;

    /**
     * Reads the value of a property from the specified UserTask
     * 
     * @param ut a task
     * @param property one of the constants in this class
     * @return read value 
     */
    public static Object getProperty(UserTask ut, int property) {
        Object r = null;
        switch (property) {
            case UTColumns.SUMMARY:
                r = ut.getSummary();
                break;
            case UTColumns.PRIORITY:
                r = ut.getPriority();
                break;
            case UTColumns.DONE:
                r = ut.isDone();
                break;
            case UTColumns.PERCENT_COMPLETE:
                r = ut.getPercentComplete();
                break;
            case UTColumns.EFFORT:
                r = ut.getEffort();
                break;
            case UTColumns.REMAINING_EFFORT:
                r = ut.getRemainingEffort();
                break;
            case UTColumns.SPENT_TIME:
                r = ut.getSpentTime();
                break;
            case UTColumns.DETAILS:
                r = ut.getDetails();
                break;
            case UTColumns.CATEGORY:
                r = ut.getCategory();
                break;
            case UTColumns.CREATED:
                r = ut.getCreatedDate();
                break;
            case UTColumns.LAST_EDITED:
                r = ut.getLastEditedDate();
                break;
            case UTColumns.COMPLETED_DATE:
                r = ut.getCompletedDate();
                break;
            case UTColumns.DUE_DATE:
                r = ut.getDueDate();
                break;
            case UTColumns.OWNER:
                r = ut.getOwner();
                break;
            case UTColumns.START:
                r = ut.getStart();
                break;
            case UTColumns.SPENT_TIME_TODAY:
                r = ut.getSpentTimeToday();
                break;
        default:
            assert false;
        }
        return r;
    }
    
    private UTColumns() {
    }
}

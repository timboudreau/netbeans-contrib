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
    public static final int FILE_BASE_NAME = 8;
    public static final int LINE_NUMBER = 9;
    public static final int CATEGORY = 10;
    public static final int CREATED = 11;
    public static final int LAST_EDITED = 12;
    public static final int COMPLETED_DATE = 13;
    public static final int DUE_DATE = 14;
    public static final int OWNER = 15;
    public static final int START = 16;
    public static final int SPENT_TIME_TODAY = 17;

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
            case UTColumns.FILE_BASE_NAME:
                r = ut.getUrl();
                break;
            case UTColumns.LINE_NUMBER:
                r = ut.getLineNumber();
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

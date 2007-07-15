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

package org.netbeans.modules.tasklist.usertasks.table.grouping;

import org.netbeans.modules.tasklist.usertasks.table.*;
import org.netbeans.modules.tasklist.usertasks.table.grouping.DateGroupBuilder;
import org.netbeans.modules.tasklist.usertasks.table.grouping.Group;
import java.util.Date;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.table.grouping.DurationGroupBuilder;
import org.netbeans.modules.tasklist.usertasks.table.grouping.FirstLetterGroupBuilder;
import org.netbeans.modules.tasklist.usertasks.table.grouping.NotEmptyStringGroupBuilder;
import org.netbeans.modules.tasklist.usertasks.table.grouping.PercentCompleteGroupBuilder;
import org.netbeans.modules.tasklist.usertasks.table.grouping.ValueGroupBuilder;
import org.netbeans.modules.tasklist.usertasks.util.UTUtils;
import org.netbeans.modules.tasklist.usertasks.util.UnaryFunction;

/**
 * Grouping for tasks.
 *
 * @author tl
 */
public class Groups {
    /**
     * Applies another function on a property of a UserTask
     */
    private static final class UserTaskPropertyGroupBuilder implements UnaryFunction {
        private int property;
        private UnaryFunction uf;
        
        /**
         * Constructor.
         * 
         * @param uf function that has to be applied
         * @param property one of the UTColumns.* constants 
         */
        public UserTaskPropertyGroupBuilder(UnaryFunction uf, int property) {
            this.uf = uf;
            this.property = property;
        }
        
        public Object compute(Object obj) {
            UserTask ut = (UserTask) obj;
            return uf.compute(UTColumns.getProperty(ut, property));
        }
    }
    
    /**
     * Returns the appropriate "group builder" for the specified property.
     * 
     * @param property one of the UTColumns.*-constants
     * @return function that returns a group object for a UserTask. The group
     * object implement Comparable
     */
    public static UnaryFunction getGroupBuilder(int property) {
        UnaryFunction f;
        switch (property) {
            case UTColumns.SUMMARY:
                f = new FirstLetterGroupBuilder();
                break;
            case UTColumns.PRIORITY:
                f = new PriorityGroupBuilder();
                break;
            case UTColumns.DONE:
                f = new DoneGroupBuilder();
                break;
            case UTColumns.CATEGORY:
            case UTColumns.OWNER:
                f = new ValueGroupBuilder();
                break;
            case UTColumns.PERCENT_COMPLETE:
                f = new PercentCompleteGroupBuilder();
                break;
            case UTColumns.EFFORT:
            case UTColumns.REMAINING_EFFORT:
            case UTColumns.SPENT_TIME:
            case UTColumns.SPENT_TIME_TODAY:
                f = new DurationGroupBuilder();
                break;
            case UTColumns.DETAILS:
                f = new NotEmptyStringGroupBuilder();
                break;
            case UTColumns.CREATED:
            case UTColumns.LAST_EDITED:
            case UTColumns.COMPLETED_DATE:
            case UTColumns.DUE_DATE:
            case UTColumns.START:
                f = new DateGroupBuilder();
                break;
            default:
                UTUtils.LOGGER.warning("Unknown property " + property); // NOI18N
                f = new ValueGroupBuilder();
        };
        return new UserTaskPropertyGroupBuilder(f, property);
    }
    
    /**
     * Constructor.
     */
    private Groups() {
    }
}

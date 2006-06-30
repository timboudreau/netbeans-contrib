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

package org.netbeans.modules.tasklist.usertasks.filter;

import org.netbeans.modules.tasklist.core.filter.AppliedFilterCondition;
import org.netbeans.modules.tasklist.core.filter.BooleanFilterCondition;
import org.netbeans.modules.tasklist.core.filter.DateFilterCondition;
import org.netbeans.modules.tasklist.core.filter.Filter;
import org.netbeans.modules.tasklist.core.filter.FilterConvertor;
import org.netbeans.modules.tasklist.core.filter.IntegerFilterCondition;
import org.netbeans.modules.tasklist.core.filter.PriorityCondition;
import org.netbeans.modules.tasklist.core.filter.StringFilterCondition;
import org.netbeans.modules.tasklist.core.filter.SuggestionProperty;

/**
 * Filter for user tasks
 *
 * @author tl
 */
public class UserTaskFilter extends Filter {
    private static final SuggestionProperty[] PROPS = new SuggestionProperty[] {
        UserTaskProperties.PROP_SUMMARY,
        UserTaskProperties.PROP_PRIORITY,
        UserTaskProperties.PROP_CATEGORY,
        UserTaskProperties.PROP_FILENAME,
        UserTaskProperties.PROP_LINE_NUMBER,
        UserTaskProperties.PROP_CREATED_DATE,
        UserTaskProperties.PROP_LAST_EDITED_DATE,
        UserTaskProperties.PROP_COMPLETED_DATE,
        UserTaskProperties.PROP_DUE_DATE,
        UserTaskProperties.PROP_DONE,
        UserTaskProperties.PROP_PERCENT_COMPLETE,
        UserTaskProperties.PROP_EFFORT,
        UserTaskProperties.PROP_REMAINING_EFFORT,
        UserTaskProperties.PROP_SPENT_TIME,
        UserTaskProperties.PROP_SPENT_TIME_TODAY,
        UserTaskProperties.PROP_OWNER,
        UserTaskProperties.PROP_START
    };
    
    /** 
     * Creates a new instance of UserTaskFilter 
     *
     * @param name name of the filter
     */
    public UserTaskFilter(String name) {
        super(name);
    }
  
    public UserTaskFilter(UserTaskFilter rhs) {
        super(rhs);
    }

    private UserTaskFilter() { // for deconvertization
    }

    public Object clone() {
        return new UserTaskFilter(this);
    }

    public SuggestionProperty[] getProperties() {
        return PROPS;
    }
    
    public AppliedFilterCondition[] createConditions(SuggestionProperty property) {
        if (property.equals(UserTaskProperties.PROP_SUMMARY)) {
            return applyConditions(property, StringFilterCondition.createConditions());
        } else if (property.equals(UserTaskProperties.PROP_PRIORITY)) {
            return applyConditions(property, PriorityCondition.createConditions());
        } else if (property.equals(UserTaskProperties.PROP_CATEGORY)) {
            return applyConditions(property, StringFilterCondition.createConditions());
        } else if (property.equals(UserTaskProperties.PROP_DETAILS)) {
            return applyConditions(property, StringFilterCondition.createConditions());
        } else if (property.equals(UserTaskProperties.PROP_FILENAME)) {
            return applyConditions(property, StringFilterCondition.createConditions());
        } else if (property.equals(UserTaskProperties.PROP_LINE_NUMBER)) {
            return applyConditions(property, IntegerFilterCondition.createConditions());
        } else if (property.equals(UserTaskProperties.PROP_CREATED_DATE)) {
            return applyConditions(property, DateFilterCondition.createConditions());
        } else if (property.equals(UserTaskProperties.PROP_LAST_EDITED_DATE)) {
            return applyConditions(property, DateFilterCondition.createConditions());
        } else if (property.equals(UserTaskProperties.PROP_COMPLETED_DATE)) {
            return applyConditions(property, DateFilterCondition.createConditions());
        } else if (property.equals(UserTaskProperties.PROP_DUE_DATE)) {
            return applyConditions(property, DateFilterCondition.createConditions());
        } else if (property.equals(UserTaskProperties.PROP_DONE)) {
            return applyConditions(property, BooleanFilterCondition.createConditions());
        } else if (property.equals(UserTaskProperties.PROP_PERCENT_COMPLETE)) {
            return applyConditions(property, IntegerFilterCondition.createConditions());
        } else if (property.equals(UserTaskProperties.PROP_EFFORT)) {
            return applyConditions(property, DurationFilterCondition.createConditions());
        } else if (property.equals(UserTaskProperties.PROP_REMAINING_EFFORT)) {
            return applyConditions(property, DurationFilterCondition.createConditions());
        } else if (property.equals(UserTaskProperties.PROP_SPENT_TIME)) {
            return applyConditions(property, DurationFilterCondition.createConditions());
        } else if (property.equals(UserTaskProperties.PROP_OWNER)) {
            return applyConditions(property, StringFilterCondition.createConditions());
        } else if (property.equals(UserTaskProperties.PROP_START)) {
            return applyConditions(property, DateFilterCondition.createConditions());
        } else if (property.equals(UserTaskProperties.PROP_SPENT_TIME_TODAY)) {
            return applyConditions(property, DurationFilterCondition.createConditions());
        } else {
            throw new InternalError("Wrong index"); // NOI18N
        }
    }
    

    private static class Convertor extends FilterConvertor {        
        public Convertor() {
            super("UserTaskFilter"); // NOI18N
        }
        
        public static UserTaskFilter.Convertor create() { 
            return new UserTaskFilter.Convertor();
        }
        
        protected Filter createFilter() { return new UserTaskFilter();}
        
        protected SuggestionProperty getProperty(String propid) {
            SuggestionProperty sp = UserTaskProperties.getProperty(propid);
            if (sp == null)
                return super.getProperty(propid);
            else
                return sp;
        }        
    }

}

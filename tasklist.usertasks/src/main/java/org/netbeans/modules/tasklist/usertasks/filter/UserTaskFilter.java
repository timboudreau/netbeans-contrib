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

package org.netbeans.modules.tasklist.usertasks.filter;

import org.netbeans.modules.tasklist.filter.AppliedFilterCondition;
import org.netbeans.modules.tasklist.filter.BooleanFilterCondition;
import org.netbeans.modules.tasklist.filter.DateFilterCondition;
import org.netbeans.modules.tasklist.filter.Filter;
import org.netbeans.modules.tasklist.filter.FilterConvertor;
import org.netbeans.modules.tasklist.filter.IntegerFilterCondition;
import org.netbeans.modules.tasklist.filter.StringFilterCondition;
import org.netbeans.modules.tasklist.filter.SuggestionProperty;

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
            return applyConditions(property, UTPriorityCondition.createConditions());
        } else if (property.equals(UserTaskProperties.PROP_CATEGORY)) {
            return applyConditions(property, StringFilterCondition.createConditions());
        } else if (property.equals(UserTaskProperties.PROP_DETAILS)) {
            return applyConditions(property, StringFilterCondition.createConditions());
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
    

    public static class Convertor extends FilterConvertor {        
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

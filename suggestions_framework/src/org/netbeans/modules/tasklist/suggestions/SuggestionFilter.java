/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.suggestions;

import java.util.Date;
import org.netbeans.modules.tasklist.client.Suggestion;
import org.netbeans.modules.tasklist.core.filter.BooleanFilterCondition;
import org.netbeans.modules.tasklist.core.filter.DateFilterCondition;
import org.netbeans.modules.tasklist.core.filter.Filter;
import org.netbeans.modules.tasklist.core.filter.IntegerFilterCondition;
import org.netbeans.modules.tasklist.core.filter.PriorityCondition;
import org.netbeans.modules.tasklist.core.filter.StringFilterCondition;
import org.openide.util.NbBundle;

/**
 * Filter for user tasks
 * @author Tim Lebedkov
 */
public class SuggestionFilter extends Filter {
    private static final String[] PROP_KEYS = {
        "SuggestionsRoot", // NOI18N
        "Details", // NOI18N
        "Priority", // NOI18N
        "File", // NOI18N
        "Line", // NOI18N
        "Category", // NOI18N
    };
    
    private static final String[] PROPS = new String[PROP_KEYS.length];
    
    static {
        for (int i = 0; i < PROPS.length; i++) {
            PROPS[i] = NbBundle.getMessage(SuggestionFilter.class, PROP_KEYS[i]);
        }
    }
    
    /** 
     * Creates a new instance of UserTaskFilter 
     *
     * @param name name of the filter
     */
    public SuggestionFilter(String name) {
        super(name);
    }
    
    public String[] getProperties() {
        return PROPS;
    }
    
    public org.netbeans.modules.tasklist.core.filter.FilterCondition[] createConditions(int index) {
        switch (index) {
            case 0:
                // SuggestionsRoot
                return StringFilterCondition.createConditions(index);
            case 1:
                // Details
                return StringFilterCondition.createConditions(index);
            case 2:
                // Priority
                return PriorityCondition.createConditions(index);
            case 3:
                // File
                return StringFilterCondition.createConditions(index);
            case 4:
                // Line
                return IntegerFilterCondition.createConditions(index);
            case 5:
                // Category
                return StringFilterCondition.createConditions(index);
            default:
                throw new InternalError("Wrong index");
        }
    }
    
    public Object getProperty(Object obj, int property) {
        SuggestionImpl s = (SuggestionImpl) obj;
        switch (property) {
            case 0:
                // SuggestionsRoot
                return s.getSummary();
            case 1:
                // Details
                return s.getDetails();
            case 2:
                // Priority
                return s.getPriority();
            case 3:
                // File
                return s.getFileBaseName();
            case 4:
                // Line
                return new Integer(s.getLineNumber());
            case 5:
                // Category
                return s.getType();
            default:
                throw new InternalError("Wrong index");
        }
    }
}

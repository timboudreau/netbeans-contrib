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

package org.netbeans.modules.tasklist.docscan;

import java.util.Date;
import org.netbeans.modules.tasklist.client.Suggestion;
import org.netbeans.modules.tasklist.core.filter.BooleanFilterCondition;
import org.netbeans.modules.tasklist.core.filter.DateFilterCondition;
import org.netbeans.modules.tasklist.core.filter.Filter;
import org.netbeans.modules.tasklist.core.filter.IntegerFilterCondition;
import org.netbeans.modules.tasklist.core.filter.PriorityCondition;
import org.netbeans.modules.tasklist.core.filter.StringFilterCondition;
import org.netbeans.modules.tasklist.suggestions.SuggestionFilter;
import org.netbeans.modules.tasklist.suggestions.SuggestionImpl;
import org.openide.util.NbBundle;

/**
 * Filter for user tasks
 * @author Tim Lebedkov
 */
final class SourceTasksFilter extends Filter {
    private static final String[] PROP_KEYS = {
        "SuggestionsRoot", // NOI18N
        "Priority", // NOI18N
        "File", // NOI18N
    };
    
    private static final String[] PROPS = new String[PROP_KEYS.length];
    
    static {
        for (int i = 0; i < PROPS.length; i++) {
            PROPS[i] = NbBundle.getMessage(SourceTasksFilter.class, PROP_KEYS[i]);
        }
    }
    
    /** 
     * Creates a new instance of UserTaskFilter 
     *
     * @param name name of the filter
     */
    public SourceTasksFilter(String name) {
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
                // Priority
                return PriorityCondition.createConditions(index);
            case 2:
                // File
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
                // Priority
                return s.getPriority();
            case 2:
                // File
                return s.getFileBaseName();
            default:
                throw new InternalError("Wrong index");
        }
    }
}

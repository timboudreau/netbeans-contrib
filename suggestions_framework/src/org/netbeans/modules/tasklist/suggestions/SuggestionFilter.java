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
import org.netbeans.modules.tasklist.core.filter.FilterCondition;
import org.netbeans.modules.tasklist.core.filter.AppliedFilterCondition;
import org.netbeans.modules.tasklist.core.filter.BooleanFilterCondition;
import org.netbeans.modules.tasklist.core.filter.DateFilterCondition;
import org.netbeans.modules.tasklist.core.filter.Filter;
import org.netbeans.modules.tasklist.core.filter.IntegerFilterCondition;
import org.netbeans.modules.tasklist.core.filter.PriorityCondition;
import org.netbeans.modules.tasklist.core.filter.StringFilterCondition;
import org.openide.util.NbBundle;
import org.netbeans.modules.tasklist.client.SuggestionProperty;
import org.netbeans.modules.tasklist.client.SuggestionProperties;

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
    
  private static final SuggestionProperty[] PROPS = new SuggestionProperty[] {
    SuggestionImplProperties.PROP_SUMMARY,
    SuggestionImplProperties.PROP_DETAILS,
    SuggestionImplProperties.PROP_PRIORITY,
    SuggestionImplProperties.PROP_FILENAME,
    SuggestionImplProperties.PROP_LINE_NUMBER,
    SuggestionImplProperties.PROP_CATEGORY    
  };

  
    
    
    /** 
     * Creates a new instance of UserTaskFilter 
     *
     * @param name name of the filter
     */
    public SuggestionFilter(String name) {
        super(name);
    }
    
    public SuggestionFilter(SuggestionFilter rhs) { super(rhs); }

    public Object clone() { return new SuggestionFilter(this);}

    public SuggestionProperty[] getProperties() {  return PROPS;}
    

    public AppliedFilterCondition[] createConditions(SuggestionProperty property) {
      if (property.equals(SuggestionProperties.PROP_SUMMARY)) {
	return applyConditions(property, StringFilterCondition.createConditions());
      } 
      else if (property.equals(SuggestionImplProperties.PROP_DETAILS)) {
	return applyConditions(property, StringFilterCondition.createConditions());
      } 
      else if (property.equals(SuggestionImplProperties.PROP_PRIORITY)) {
	return applyConditions(property, PriorityCondition.createConditions());
      } 
      else if (property.equals(SuggestionImplProperties.PROP_FILENAME)) {
	return applyConditions(property, StringFilterCondition.createConditions());
      } 
      else if (property.equals(SuggestionImplProperties.PROP_LINE_NUMBER)) {
	return applyConditions(property, IntegerFilterCondition.createConditions());
      } 
      else if (property.equals(SuggestionImplProperties.PROP_CATEGORY)) {
	return applyConditions(property, StringFilterCondition.createConditions());
      } 
      else 
	throw new IllegalArgumentException("wrong property");

    }

}

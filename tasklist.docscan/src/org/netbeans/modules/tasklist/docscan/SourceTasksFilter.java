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
import org.netbeans.modules.tasklist.core.filter.FilterConvertor;
import org.netbeans.modules.tasklist.client.SuggestionProperty;
import org.netbeans.modules.tasklist.core.filter.AppliedFilterCondition;
import org.netbeans.modules.tasklist.suggestions.SuggestionImplProperties;



/**
 * Filter for user tasks
 * @author Tim Lebedkov
 */
final class SourceTasksFilter extends Filter {
  
    // these are the properties (columns) the filter filters
    SuggestionProperty [] PROPS = new SuggestionProperty[] {
      SourceTaskProperties.PROP_TASK,
      SourceTaskProperties.PROP_PRIORITY,
      SourceTaskProperties.PROP_FILENAME};


    /** 
     * Creates a new instance of UserTaskFilter 
     *
     * @param name name of the filter
     */
    public SourceTasksFilter(String name) {
        super(name);
    }
    
    public SourceTasksFilter(final SourceTasksFilter rhs) {
        super(rhs);
        
    }
    
    public Object clone() {
        return new SourceTasksFilter(this);
    }

    /** for deconvertization **/
    private SourceTasksFilter() {}


    public SuggestionProperty[] getProperties() { return PROPS;}

  // map from properties to conditions      
    public AppliedFilterCondition[] createConditions(SuggestionProperty property) {
      if (property.equals(SourceTaskProperties.PROP_TASK)) {
	return applyConditions(property, StringFilterCondition.createConditions());
      } 
      else if (property.equals(SourceTaskProperties.PROP_PRIORITY)) {
	return applyConditions(property, PriorityCondition.createConditions());
      } 
      else if (property.equals(SourceTaskProperties.PROP_FILENAME)) {
	return applyConditions(property, StringFilterCondition.createConditions());
      } else
	throw new IllegalArgumentException("Unknown property for SourceTasksFilter : " + property.getID());
    }
	

  private static class Convertor extends FilterConvertor {

    public Convertor() {
      super("SourceTasksFilter");
    }

    public static SourceTasksFilter.Convertor create() { return new SourceTasksFilter.Convertor();}

    protected Filter createFilter() { return new SourceTasksFilter();}

    protected SuggestionProperty getProperty(String propid) {
      SuggestionProperty sp = SourceTaskProperties.getProperty(propid);
      if (sp == null) 
	return super.getProperty(propid);
      else 
	return sp;
    }
    
  }
}

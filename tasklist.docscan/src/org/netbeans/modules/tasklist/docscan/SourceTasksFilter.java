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

package org.netbeans.modules.tasklist.docscan;

import org.netbeans.modules.tasklist.filter.AppliedFilterCondition;
import org.netbeans.modules.tasklist.filter.Filter;
import org.netbeans.modules.tasklist.filter.FilterConvertor;
import org.netbeans.modules.tasklist.filter.StringFilterCondition;
import org.netbeans.modules.tasklist.filter.SuggestionProperty;
import org.netbeans.modules.tasklist.suggestions.PriorityCondition;

/**
 * Filter for user tasks
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

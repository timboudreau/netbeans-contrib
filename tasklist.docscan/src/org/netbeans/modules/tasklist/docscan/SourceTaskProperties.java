/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.docscan;

import org.netbeans.modules.tasklist.core.TaskProperties;
import org.netbeans.modules.tasklist.client.SuggestionProperty;
import org.netbeans.modules.tasklist.suggestions.SuggestionImplProperties;
import org.netbeans.modules.tasklist.client.Suggestion;

/**
 * Although Task doesn't introduce any new properties, I include this
 * class for symetry. Otherwise , developers might be looking for it
 * and be confused by not finding it.
 */
public abstract class SourceTaskProperties extends SuggestionImplProperties {

  public static final String PROPID_TASK = PROPID_SUMMARY;

  public static SuggestionProperty getProperty(String propID) {
    if (propID.equals(PROPID_TASK)) { return PROP_TASK;}
    else 
      return SuggestionImplProperties.getProperty(propID);
  }

  public static final SuggestionProperty PROP_TASK = 
    new SuggestionProperty(PROPID_TASK, String.class) {
      public Object getValue(Suggestion suggestion) {
	return SuggestionImplProperties.PROP_SUMMARY.getValue(suggestion);
      }
    };

}


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

package org.netbeans.modules.tasklist.suggestions;


import org.netbeans.modules.tasklist.client.SuggestionProperty;
import org.netbeans.modules.tasklist.core.TaskProperties;
import org.netbeans.modules.tasklist.client.Suggestion;


/**
 * Suggestion impl introduces line-number, file-name and location.
 * This class is coupled to SuggestionImpl, but defined outside just
 * for code readability.
 */
public class SuggestionImplProperties extends TaskProperties   {

  public static final String PROPID_LINE_NUMBER = "line";
  public static final String PROPID_FILENAME = "file";
  public static final String PROPID_LOCATION = "location";
  public static final String PROPID_CATEGORY = "category";


  public static SuggestionProperty getProperty(String propID) {
    if (propID.equals(PROPID_LINE_NUMBER)) { return PROP_LINE_NUMBER;}
    else if (propID.equals(PROPID_FILENAME)) { return PROP_FILENAME;}
    else if (propID.equals(PROPID_LOCATION)) { return PROP_LOCATION;}
    else if (propID.equals(PROPID_CATEGORY)) { return PROP_CATEGORY;}
    else return TaskProperties.getProperty(propID);
  }

  public static final SuggestionProperty PROP_LINE_NUMBER = 
    new SuggestionProperty(PROPID_LINE_NUMBER, Integer.class) {
      public Object getValue(Suggestion suggestion) {return new Integer(((SuggestionImpl)suggestion).getLineNumber()); }
    };
  
  public static final SuggestionProperty PROP_FILENAME = 
    new SuggestionProperty(PROPID_FILENAME, String.class) {
      public Object getValue(Suggestion suggestion) {return ((SuggestionImpl)suggestion).getFileBaseName(); }
    };
  
  public static final SuggestionProperty PROP_LOCATION = 
    new SuggestionProperty(PROPID_LOCATION, String.class) {
      public Object getValue(Suggestion suggestion) {return ((SuggestionImpl)suggestion).getLocation(); }
    };

  public static final SuggestionProperty PROP_CATEGORY = 
    new SuggestionProperty(PROPID_CATEGORY, String.class) {
      public Object getValue(Suggestion suggestion) {return ((SuggestionImpl)suggestion).getCategory(); }
    };

}


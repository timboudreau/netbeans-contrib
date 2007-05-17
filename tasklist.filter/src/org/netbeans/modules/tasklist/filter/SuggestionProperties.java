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

package org.netbeans.modules.tasklist.filter;

import org.netbeans.modules.tasklist.filter.SuggestionProperty;


/**
 * An abstract factory for creating SuggestionProperties from their id.
 */
public class SuggestionProperties {
  public static final String PROPID_PRIORITY = "priority";
  public static final String PROPID_SUMMARY = "summary";
  public static final String PROPID_DETAILS = "details";

  /**
   * A factory method for properties on Suggestion.
   * @param propID one of the PROP_* constant defined in this class
   * @return a property for accessing the property 
   */
  public static SuggestionProperty getProperty(String propID) {
    if (propID.equals(PROPID_PRIORITY)) { return PROP_PRIORITY;}
    else if (propID.equals(PROPID_SUMMARY)) { return PROP_SUMMARY;}
    else if (propID.equals(PROPID_DETAILS)) { return PROP_DETAILS;}
    else throw new IllegalArgumentException("Unresolved property id " + propID);
  }


  public static SuggestionProperty PROP_SUMMARY = 
    new SuggestionProperty(PROPID_SUMMARY, String.class) { 
      public Object getValue(Object obj) {
          return null; // TODO
          //((Suggestion) obj).getSummary(); 
      }
    };

  public static SuggestionProperty PROP_PRIORITY = 
    new SuggestionProperty(PROPID_PRIORITY, String.class) {   
      public Object getValue(Object obj) {
          return null; // TODO ((Suggestion) obj).getPriority(); 
      }
    };

  public static SuggestionProperty PROP_DETAILS = 
    new SuggestionProperty(PROPID_DETAILS, String.class) {   
      public Object getValue(Object obj) {
          return null; // TODO ((Suggestion) obj).getDetails(); 
      }
    };


}
    

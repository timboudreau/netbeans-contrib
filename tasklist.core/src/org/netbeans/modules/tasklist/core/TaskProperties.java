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

package org.netbeans.modules.tasklist.core;

import org.netbeans.modules.tasklist.client.SuggestionProperty;
import org.netbeans.modules.tasklist.client.SuggestionProperties;

/**
 * Although Task doesn't introduce any new properties, I include this
 * class for symetry. Otherwise , developers might be looking for it
 * and be confused by not finding it.
 */
public class TaskProperties extends SuggestionProperties {

  public static SuggestionProperty getProperty(String propID) {
    return SuggestionProperties.getProperty(propID);
  }

}


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

import org.netbeans.modules.tasklist.core.TaskChildren;
import org.netbeans.modules.tasklist.core.Task;
import org.netbeans.modules.tasklist.core.TaskNode;




/**
 * Subclass of TaskChildren. Empty, implemented just for regularity and
 * readability of the structure : children for node.
 */
public class SuggestionChildren extends TaskChildren {

  public SuggestionChildren(SuggestionImpl parent) {
    super(parent);
  }


  protected TaskNode createNode(Task task) {
    return new SuggestionNode((SuggestionImpl)task);
  }


}





/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.suggestions;

import org.netbeans.modules.tasklist.core.Task;
import org.netbeans.modules.tasklist.core.TaskAnnotation;

class SuggestionAnno extends TaskAnnotation {

    SuggestionAnno(Task task) {
        super(task);
    }

    public String getAnnotationType () {
        return "Suggestion"; // NOI18N
    }
    
    public String getShortDescription () {
        // Highlight task in Suggestions View as well, if possible
        showTask();

        // Can I get the confirmation panel here? Check to see
        // if its instanceof String for example?

        // Use details summary, if available
        if (task.getDetails().length() > 0) {
            return task.getSummary() + "\n\n" + task.getDetails();
        } else {
            return task.getSummary();
        }
    }
}

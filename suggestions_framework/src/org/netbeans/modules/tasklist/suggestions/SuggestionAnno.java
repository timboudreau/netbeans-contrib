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

package org.netbeans.modules.tasklist.suggestions;

import org.netbeans.modules.tasklist.core.Task;
import org.netbeans.modules.tasklist.core.TaskAnnotation;
import org.netbeans.modules.tasklist.core.TaskSelector;

public class SuggestionAnno extends TaskAnnotation {

    SuggestionAnno(Task task) {
        super(task);
    }

    public SuggestionAnno(Task task, TaskSelector view) {
        super(task, view);
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

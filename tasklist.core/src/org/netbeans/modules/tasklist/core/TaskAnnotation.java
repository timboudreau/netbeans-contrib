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

package org.netbeans.modules.tasklist.core;

import org.netbeans.modules.tasklist.core.Task;

import org.openide.text.Annotation;

/** Annotation which shows tasks in the editor
 */
public class TaskAnnotation extends Annotation {

    public TaskAnnotation(Task task) {
        this.task = task;
    }

    private Task task = null;

    public String getAnnotationType () {
        // THE TYPE IS DEFINED IN THE TASKLIST EDITOR MODULE!
        // (because it registers an Editor action - New Task, to be
        // added to the editor glyph gutter/margin menu)
        return "Task"; // NOI18N
    }
    
    public String getShortDescription () {
        // Use details summary, if available
        if (task.getDetails().length() > 0) {
            return task.getSummary() + "\n\n" + task.getDetails();
        } else {
            return task.getSummary();
        }
    }
}

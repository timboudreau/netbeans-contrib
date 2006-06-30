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

package org.netbeans.modules.tasklist.core;

import org.openide.text.Annotation;

/** Annotation which shows tasks in the editor
 */
public class TaskAnnotation extends Annotation {

    /** Construct a new TaskAnnotation which shows both a gutter
     * icon and a line highlight. */
    public TaskAnnotation(Task task) {
        this(task, true);
    }

    /** Construct a new TaskAnnotation.
     * @param task The task to show the annotation for
     * @param highlight When true, show a highlight for the task,
     *   not just a gutter icon.
     */
    public TaskAnnotation(Task task, boolean highlight) {
        this.task = task;
        this.highlight = highlight;
    }

    /**
     * Constructs annotation that is capable to locate
     * task in other tasks views (implementing TaskSelector).
     *
     * @param task
     * @param view
     */
    public TaskAnnotation(Task task, TaskSelector view) {
        this.task = task;
        this.view = view;
        highlight = true;
    }

    public String getAnnotationType () {
        // THE TYPE IS DEFINED IN THE TASKLIST EDITOR MODULE!
        // (because it registers an Editor action - New Task, to be
        // added to the editor glyph gutter/margin menu)
        if (highlight) {
            return "Task"; // NOI18N
        } else {
            return "TaskNoHighlight"; // NOI18N
        }
    }
    
    public String getShortDescription () {
        // Use details summary, if available
        showTask();

        if (task.getDetails().length() > 0) {
            return task.getSummary() + "\n\n" + task.getDetails();
        } else {
            return task.getSummary();
        }
    }

    /** Show the task for this annotation in its view */
    protected void showTask() {
        if (view != null) view.select(task);
    }

    /** Return the task associated with this annotation */
    public Task getTask() {
        return task;
    }



    protected Task task = null;
    private boolean highlight = false;
    private TaskSelector view;
}

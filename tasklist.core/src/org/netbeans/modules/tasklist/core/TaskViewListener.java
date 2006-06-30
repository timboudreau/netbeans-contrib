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


/**
 * Listener which when notified off changes in current task
 * updates in all views. Registations are done using default lookup.
 * <p>
 * For editor only selection use
 * {@link TaskListView#showTaskInEditor} aad {@link TaskListView#hideTaskInEditor}
 *
 * @author Tor Norbye
 *
 * @todo Instead of having showTask, hideTask, consider generalizing
 *        this to communicating the current selection. Obviously
 *        deleting a task will cause it to be unselected. Single-click
 *        vs. double click issue.
 */
public interface TaskViewListener  {

    /** Called to indicate that a particular task is made current.
     * Do what you can to "select" this task. 
     * @param task The task to be shown
     * @param annotation Annotation to be used to show the task, or
     *    null to use the default
     */
    void showTask(Task task, Annotation annotation);

    /** Called to indicate that a particular task should be hidden.
	This typically means that the task was deleted so it should
	no longer have any visual cues. The task referred to is the
	most recent task passed to showTask.
        NOTE: hideTaskInEditor is NOT called before every new call to showTask.
        If your task viewer implements a "singleton" marker, you'll
        want to call hideTask yourself before showing the new marker.
    */
    void hideTask();
}

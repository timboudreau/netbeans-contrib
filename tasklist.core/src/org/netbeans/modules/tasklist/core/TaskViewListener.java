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

import org.openide.text.Annotation;


/** Listener which when notified off changes in current task
 *  updates the editor
 * @author Tor Norbye
 *  @todo Instead of having showTask, hideTask, consider generalizing
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
        NOTE: hideTask is NOT called before every new call to showTask.
        If your task viewer implements a "singleton" marker, you'll
        want to call hideTask yourself before showing the new marker.
    */
    void hideTask();
}

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

import org.openide.text.Line;
import org.openide.text.Annotation;
import org.openide.text.Annotatable;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;

/**
 * Listener which when notified off changes in current task
 * updates the editor. Registered at the default lookup.
 *
 * @todo Create the view lazily so that it isn't shown until I actually
 *       encounter a Task in the user's view
 *
 * @author Tor Norbye
 * @author Petr Kuzel
 */
public final class TaskEditorListener implements TaskViewListener, 
        PropertyChangeListener {
    /** Annotation showing the current position */
    transient private Annotation taskMarker = null;

    /** Construct a new TaskEditorListener.
     */
    public TaskEditorListener() {
        deflt = this;
    }

    private static TaskEditorListener deflt = null;

    static TaskEditorListener getDefault() {
        return (deflt == null) ? new TaskEditorListener() : deflt;
    }

    /**
     * Show the given task. "Showing" means getting the editor to
     * show the associated file position, and open up an area in the
     * tasklist view where the details of the task can be fully read.
     *
     * @param item task to annotate (subject of <code>item.getLine()</code>)
     * @param annotation marker to user or <code>null</code> for default one
     */
    public void showTask(Task item, Annotation annotation) {
        hideTask();
        if (item == null) return;

        Line l = item.getLine();
        if (l != null) {
            taskMarker = (annotation != null) ? annotation : new TaskAnnotation(item);
            taskMarker.attach(l);
            l.addPropertyChangeListener(this); // detach on line edit
            TaskListView view = TaskListView.getCurrent();

            // #35917 do not move focus if in sliding mode
            if (view.getClientProperty("isSliding") == Boolean.TRUE) {  // NOi18N
                l.show(Line.SHOW_SHOW);
            } else {
                l.show(Line.SHOW_GOTO);
            }
        }

    }

    /** Called to indicate that a particular task should be hidden.
     This typically means that the task was deleted so it should
     no longer have any visual cues. The task referred to is the
     most recent task passed to showTaskInEditor.
     */
    public void hideTask() {
        if (taskMarker != null) {
            Annotatable line = taskMarker.getAttachedAnnotatable();
            line.removePropertyChangeListener(this);
            taskMarker.detach();
            taskMarker = null;
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        if (Annotatable.PROP_TEXT.equals(evt.getPropertyName())) {
            hideTask();
        }
    }

}

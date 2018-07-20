/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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

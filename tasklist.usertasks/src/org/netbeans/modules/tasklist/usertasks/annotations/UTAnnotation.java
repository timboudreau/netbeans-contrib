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

package org.netbeans.modules.tasklist.usertasks.annotations;

import org.openide.text.Annotation;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.*;

/**
 * Annotation which shows tasks in the editor
 *
 * @author tl
 */
public class UTAnnotation extends Annotation {
    protected UserTask task = null;
    private boolean highlight = false;
    private UserTaskView view;
    private boolean done;

    /**
     * Construct a new TaskAnnotation which shows both a gutter
     * icon and a line highlight. 
     */
    public UTAnnotation(UserTask task) {
        this(task, true);
    }

    /** 
     * Construct a new TaskAnnotation. 
     * @param task The task to show the annotation for
     * @param highlight When true, show a highlight for the task,
     *   not just a gutter icon.
     */
    public UTAnnotation(UserTask task, boolean highlight) {
        this.done = task.isDone();
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
    public UTAnnotation(UserTask task, UserTaskView view) {
        this.task = task;
        this.view = view;
        highlight = true;
    }

    /**
     * Changes the icon depending on the done attribute.
     *
     * @param done the task was done
     */
    public void setDone(boolean done) {
        if (this.done != done) {
            String old = getAnnotationType();
            this.done = done;
            firePropertyChange(PROP_ANNOTATION_TYPE, old, getAnnotationType());
        }
    }
    
    /**
     * Highlights the task/removes the highlighting.
     *
     * @param highlight true = highlight
     */
    public void setHighlight(boolean highlight) {
        if (this.highlight != highlight) {
            String old = getAnnotationType();
            this.highlight = highlight;
            firePropertyChange(PROP_ANNOTATION_TYPE, old, getAnnotationType());
        }
    }
    
    public String getAnnotationType () {
        if (!done) {
            if (!highlight) {
                return "org-netbeans-modules-tasklist-usertasks-UTNoHighlight"; // NOI18N
            } else {
                return "org-netbeans-modules-tasklist-usertasks-UT"; // NOI18N
            }
        } else {
            if (!highlight)
                return "org-netbeans-modules-tasklist-usertasks-UTDoneNoHighlight"; // NOI18N
            else
                return "org-netbeans-modules-tasklist-usertasks-UTDone"; // NOI18N
        }
    }
    
    public String getShortDescription () {
        // Use details summary, if available
        showTask();

        if (task.getDetails().length() > 0) {
            return task.getSummary() + "\n\n" + task.getDetails(); // NOI18N
        } else {
            return task.getSummary();
        }
    }

    /** Show the task for this annotation in its view */
    protected void showTask() {
        if (view != null) view.select(task);
    }

    /** Return the task associated with this annotation */
    public UserTask getTask() {
        return task;
    }
}

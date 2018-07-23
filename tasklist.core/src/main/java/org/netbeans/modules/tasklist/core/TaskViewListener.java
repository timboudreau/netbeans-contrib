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

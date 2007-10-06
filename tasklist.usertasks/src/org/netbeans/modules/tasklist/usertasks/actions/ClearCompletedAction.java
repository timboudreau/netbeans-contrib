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

package org.netbeans.modules.tasklist.usertasks.actions;

import java.awt.event.ActionEvent;
import java.util.Iterator;
import javax.swing.event.ListSelectionEvent;

import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.UserTaskView;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/** 
 * Clears "percent complete" properties of all subtasks recursively.
 * 
 * @author tl
 */
public class ClearCompletedAction extends UTViewAction {
    private static final long serialVersionUID = 2;

    /**
     * Constructor.
     * 
     * @param utv view associated with this task. 
     */
    public ClearCompletedAction(UserTaskView utv) {
        super(utv, NbBundle.getMessage(ClearCompletedAction.class, 
                "ClearCompleted")); // NOI18N
    }
    
    /**
     * Clears the percent complete information for the specified task
     * and all it's subtasks.
     *
     * @param ut a task
     */
    private void clearCompleted(UserTask ut) {
        if (!ut.isValuesComputed())
            ut.setDone(false);
        Iterator it = ut.getSubtasks().iterator();
        while (it.hasNext()) {
            clearCompleted((UserTask) it.next());
        }
    }
    
    public void actionPerformed(ActionEvent arg0) {
        NotifyDescriptor nd = new NotifyDescriptor.Confirmation(
            NbBundle.getMessage(PurgeTasksAction.class, "ClearCompletedQuestion"), // NOI18N
            NbBundle.getMessage(PurgeTasksAction.class, "ClearCompletedTitle"), // NOI18N
            NotifyDescriptor.OK_CANCEL_OPTION
        );
        if (DialogDisplayer.getDefault().notify(nd) == NotifyDescriptor.OK_OPTION) {
            clearCompleted(getSingleSelectedTask());
            /*if (nodes[0] instanceof UserTaskNode) {
                UserTask ptsk = ((UserTaskNode) nodes[0]).getTask();
                clearCompleted(ptsk);
            } else {
                UserTaskList utl = 
                    ((UserTaskListNode) nodes[0]).getUserTaskList();
                Iterator it = utl.getSubtasks().iterator();
                while (it.hasNext()) {
                    UserTask ut = (UserTask) it.next();
                    clearCompleted(ut);
                }
            }todo */
        }
    }

    public void valueChanged(ListSelectionEvent arg0) {
        // todo does not work for the task list node
        setEnabled(getSingleSelectedTask() != null);
    }
}

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

import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.table.UTTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.table.UTListTreeTableNode;
import java.awt.event.ActionEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.tree.TreePath;
import org.netbeans.modules.tasklist.usertasks.table.UTListTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.table.UTTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.UserTaskView;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskObjectList;
import org.netbeans.modules.tasklist.usertasks.treetable.AdvancedTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.util.UTUtils;
import org.openide.util.NbBundle;

/**
 * Moves a task to the left.
 *
 * @author tl
 */
public class MoveLeftAction extends UTViewAction {
    private static final long serialVersionUID = 1;

    /**
     * Creates a new instance.
     *
     * @param utv a view
     */
    public MoveLeftAction(UserTaskView utv) {
        super(utv, NbBundle.getMessage(MoveLeftAction.class,
                "MoveLeft")); // NOI18N
    }
    
    public void actionPerformed(ActionEvent event) {
        // figuring out where we are
        TreePath sel = utv.getTreeTable().getSelectedPath();
        UTTreeTableNode n = 
                (UTTreeTableNode) sel.getLastPathComponent();
        UTTreeTableNode parent = (UTTreeTableNode) n.getParent();
        AdvancedTreeTableNode newParent = 
                (AdvancedTreeTableNode) parent.getParent();
        TreePath newParentPath = sel.getParentPath().getParentPath();
        TreePath[] expanded = utv.getTreeTable().getExpandedNodesUnder(
                newParentPath);
        UserTask[] expandedTasks = new UserTask[expanded.length];
        for (int i = 0; i < expanded.length ; i++) {
            if (expanded[i].getLastPathComponent() instanceof
                    UTTreeTableNode) {
                expandedTasks[i] = ((UTTreeTableNode) 
                    expanded[i].getLastPathComponent()).getUserTask();
            }
        }
        utv.getTreeTable().clearSelection();
        int selColumn = utv.getTreeTable().getSelectedColumn();

        // moving the task
        UserTask ut = n.getUserTask();
        int index = UTUtils.identityIndexOf(ut.getParentObjectList(), ut);
        while (ut.getParentObjectList().size() > index + 1) {
            ut.getSubtasks().add(ut.getParentObjectList().remove(index + 1));
        }
        ut.getParentObjectList().remove(ut);
        UserTaskObjectList newParentList;
        if (newParent instanceof UTTreeTableNode)
            newParentList = ((UTTreeTableNode) newParent).
                    getUserTask().getSubtasks();
        else
            newParentList = ((UTListTreeTableNode) newParent).
                    getObject().getSubtasks();
        int parentIndex = UTUtils.identityIndexOf(
                newParentList, parent.getUserTask());
        newParentList.add(parentIndex + 1, ut);
        
        // expanding and selecting nodes
        index = newParent.getIndexOfObject(ut);
        if (index >= 0) {
            TreePath newPath = newParentPath.pathByAddingChild(
                    newParent.getChildAt(index));
            utv.getTreeTable().expandPath(newPath);
            for (int i = 0; i < expandedTasks.length; i++) {
                if (expandedTasks[i] != null) {
                    TreePath p = utv.getTreeTable().findPath(expandedTasks[i]);
                    if (p != null)
                        utv.getTreeTable().expandPath(p);
                }
            }
            utv.getTreeTable().select(newPath);
            utv.getTreeTable().getColumnModel().getSelectionModel().
                    addSelectionInterval(selColumn, selColumn);
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        TreePath[] sel = utv.getTreeTable().getSelectedPaths();
        if (utv.getTreeTable().getSortingModel().getSortedColumn() == -1 && 
                sel.length == 1) {
            Object last = sel[0].getLastPathComponent();
            if (last instanceof UTTreeTableNode) {
                UTTreeTableNode n = (UTTreeTableNode) last;
                setEnabled(n.getUserTask().getParent() != null);
            } else {
                setEnabled(false);
            }
        } else {
            setEnabled(false);
        }
    }
}

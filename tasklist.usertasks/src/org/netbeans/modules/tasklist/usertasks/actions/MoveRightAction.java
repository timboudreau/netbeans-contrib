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

import org.netbeans.modules.tasklist.usertasks.table.UTBasicTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.table.UTTreeTableNode;
import java.awt.event.ActionEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.netbeans.modules.tasklist.usertasks.table.UTTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.UserTaskView;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.util.UTUtils;
import org.openide.util.NbBundle;

/**
 * Moves a task to the left.
 *
 * @author tl
 */
public class MoveRightAction extends UTViewAction {
    private static final long serialVersionUID = 1;

    /**
     * Creates a new instance.
     *
     * @param utv a view
     */
    public MoveRightAction(UserTaskView utv) {
        super(utv, NbBundle.getMessage(MoveRightAction.class,
                "MoveRight")); // NOI18N
    }
    
    public void actionPerformed(ActionEvent event) {
        // figuring out where we are
        TreePath sel = utv.getTreeTable().getSelectedPath();
        UTBasicTreeTableNode n = 
                (UTBasicTreeTableNode) sel.getLastPathComponent();
        TreeNode parent = n.getParent();
        int index = parent.getIndex(n);
        UTTreeTableNode newParent = 
                (UTTreeTableNode) parent.getChildAt(index - 1);
        TreePath newParentPath = sel.getParentPath().
                pathByAddingChild(newParent);
        TreePath[] expanded = utv.getTreeTable().getExpandedNodesUnder(sel);
        UserTask[] expandedTasks = new UserTask[expanded.length];
        for (int i = 0; i < expanded.length ; i++) {
            expandedTasks[i] = ((UTTreeTableNode) 
                expanded[i].getLastPathComponent()).getUserTask();
        }
        utv.getTreeTable().clearSelection();

        // moving the task
        UserTask ut = n.getUserTask();
        ut.getParentObjectList().remove(ut);
        newParent.getUserTask().getSubtasks().add(ut);
        
        // expanding and selecting nodes
        utv.getTreeTable().expandPath(newParentPath);
        index = newParent.getIndexOfObject(ut);
        if (index >= 0) {
            for (int i = 0; i < expandedTasks.length; i++) {
                TreePath p = utv.getTreeTable().findPath(expandedTasks[i]);
                if (p != null)
                    utv.getTreeTable().expandPath(p);
            }
            utv.getTreeTable().select(newParentPath.pathByAddingChild(
                    newParent.getChildAt(index)));
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        TreePath[] sel = utv.getTreeTable().getSelectedPaths();
        if (utv.getTreeTable().getSortingModel().getSortedColumn() == -1 &&
                sel.length == 1) {
            Object last = sel[0].getLastPathComponent();
            if (last instanceof UTTreeTableNode) {
                UTBasicTreeTableNode n = (UTTreeTableNode) last;
                TreeNode parent = n.getParent();
                if (parent == null)
                    UTUtils.LOGGER.fine(n.getUserTask() + 
                            " does not have a parent"); // NOI18N
                int index = parent.getIndex(n);
                setEnabled(index != 0);
            } else {
                setEnabled(false);
            }
        } else {
            setEnabled(false);
        }
    }
}

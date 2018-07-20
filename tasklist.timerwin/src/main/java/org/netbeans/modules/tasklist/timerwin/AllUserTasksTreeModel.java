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

package org.netbeans.modules.tasklist.timerwin;

import javax.swing.tree.TreeModel;
import org.netbeans.modules.tasklist.core.util.ObjectList;
import org.netbeans.modules.tasklist.usertasks.UserTaskView;
import org.netbeans.modules.tasklist.usertasks.UserTaskViewRegistry;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskList;

/**
 * TreeModel for user tasks
 *
 * @author tl
 */
public class AllUserTasksTreeModel implements TreeModel {
    private Object root = new Object();
    private UserTaskList[] utl;

    /**
     * Creates a new model for a task list.
     *
     * @param utl a task list
     */
    public AllUserTasksTreeModel() {
        UserTaskView[] all = UserTaskViewRegistry.getInstance().getAll();
        utl = new UserTaskList[all.length];
        for (int i = 0; i < all.length; i++) {
            utl[i] = all[i].getUserTaskList();
        }
    }

    public boolean isLeaf(Object node) {
        if (node == root)
            return false;
        else
            return ((ObjectList.Owner) node).getObjectList().size() == 0;
    }

    public int getChildCount(Object parent) {
        if (parent == root)
            return utl.length;
        else
            return ((ObjectList.Owner) parent).getObjectList().size();
    }

    public void valueForPathChanged(javax.swing.tree.TreePath path, Object newValue) {
    }

    public void removeTreeModelListener(javax.swing.event.TreeModelListener l) {
    }

    public void addTreeModelListener(javax.swing.event.TreeModelListener l) {
    }

    public Object getChild(Object parent, int index) {
        if (parent == root)
            return utl[index];
        else
            return ((ObjectList.Owner) parent).getObjectList().get(index);
    }

    public Object getRoot() {
        return root;
    }

    public int getIndexOfChild(Object parent, Object child) {
        return -1;
    }
}

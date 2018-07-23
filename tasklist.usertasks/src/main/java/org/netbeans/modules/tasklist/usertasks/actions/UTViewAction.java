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

import java.util.ArrayList;
import java.util.List;
import org.netbeans.modules.tasklist.usertasks.table.UTTreeTableNode;
import javax.swing.AbstractAction;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.TreePath;
import org.netbeans.modules.tasklist.usertasks.table.UTTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.UserTaskView;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.table.UTBasicTreeTableNode;

/**
 * An action that is aware of selected nodes in a UserTaskView.
 *
 * @author tl
 */
public abstract class UTViewAction extends AbstractAction implements
ListSelectionListener {
    /** view for this action */
    protected UserTaskView utv;
    
    /**
     * Constructor.
     * 
     * @param utv a user task view.
     * @param name Action.NAME
     */
    public UTViewAction(UserTaskView utv, String name) {
        super(name);
        putValue(AbstractAction.SHORT_DESCRIPTION, name);
        this.utv = utv;
        utv.getTreeTable().getSelectionModel().addListSelectionListener(this);
        this.valueChanged(null);
    }
    
    /**
     * Returns the selected task. If more than one task is selected returns null.
     *
     * @return selected task or null
     */
    protected UserTask getSingleSelectedTask() {
        TreePath[] tp = utv.getTreeTable().getSelectedPaths();
        
        // tp[0] is sometimes null if "Expand All" is called...
        if (tp.length == 1 && tp[0] != null) {
            Object last = tp[0].getLastPathComponent();
            if (last instanceof UTBasicTreeTableNode) {
                return ((UTBasicTreeTableNode) last).getUserTask();
            }
        }
        return null;
    }
    
    /**
     * Returns selected tasks. Returns empty list if non-task nodes are selected.
     * 
     * @return selected tasks
     */
    protected List<UserTask> getSelectedTasks() {
        List<UserTask> r = new ArrayList();
        TreePath[] tp = utv.getTreeTable().getSelectedPaths();
        
        for (int i = 0; i < tp.length; i++) {
            TreePath treePath = tp[i];
            
            // tp[0] is sometimes null if "Expand All" is called...
            if (treePath != null && treePath.getLastPathComponent() 
                    instanceof UTBasicTreeTableNode) {
                UserTask ut = ((UTBasicTreeTableNode) treePath.
                        getLastPathComponent()).
                        getUserTask();
                r.add(ut);
            } else {
                r.clear();
                break;
            }
        }

        return r;
    }
}


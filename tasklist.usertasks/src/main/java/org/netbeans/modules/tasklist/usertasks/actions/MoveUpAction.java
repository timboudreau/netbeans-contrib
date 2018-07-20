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
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.tree.TreePath;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskObjectList;
import org.netbeans.modules.tasklist.usertasks.UserTaskView;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 * Moves a task up.
 *
 * @author tl
 */
public class MoveUpAction extends UTViewAction {
    /**
     * Creates a new instance of MoveUpAction.
     *
     * @param utv a view
     */
    public MoveUpAction(UserTaskView utv) {
        super(utv, NbBundle.getMessage(MoveUpAction.class, "MoveUp")); // NOI18N
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_UP,
                InputEvent.CTRL_MASK));
        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(
                "org/netbeans/modules/tasklist/usertasks/actions/moveUp.gif")));
    }
    
    public void actionPerformed(ActionEvent e) {
        UserTask ut = getSingleSelectedTask();
        Object es = utv.getTreeTable().getExpandedNodesAndSelection();
        ut.moveUp();
        utv.getTreeTable().setExpandedNodesAndSelection(es);
        TreePath tp = utv.getTreeTable().findPath(ut);
        utv.getTreeTable().scrollTo(tp);
    }

    public void valueChanged(ListSelectionEvent e) {
        UserTask ut = getSingleSelectedTask();
        boolean en = false;
        if (utv.getState() == UserTaskView.State.TREE && 
                utv.getTreeTable().getSortingModel().getSortedColumn() == -1 &&
                ut != null) {
            UserTaskObjectList list;
            if (ut.getParent() == null)
                list = ut.getList().getSubtasks();
            else
                list = ut.getParent().getSubtasks();

            en = list.indexOf(ut) != 0;
        }
        setEnabled(en);
    }
}

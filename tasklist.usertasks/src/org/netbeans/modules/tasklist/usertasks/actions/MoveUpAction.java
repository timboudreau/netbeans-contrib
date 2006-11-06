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
        if (utv.getTreeTable().getSortingModel().getSortedColumn() == -1 &&
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

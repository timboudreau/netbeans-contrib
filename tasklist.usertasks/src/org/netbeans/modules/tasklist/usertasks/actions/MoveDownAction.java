/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.usertasks.actions;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import javax.swing.ImageIcon;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import org.netbeans.modules.tasklist.usertasks.UserTaskViewRegistry;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.netbeans.modules.tasklist.usertasks.UserTaskNode;
import org.netbeans.modules.tasklist.usertasks.model.UserTaskObjectList;
import org.netbeans.modules.tasklist.usertasks.UserTaskView;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 * Moves a task down
 *
 * @author tl
 */
public class MoveDownAction extends UTViewAction {
    /**
     * Creates a new instance
     *
     * @param utv a view
     */
    public MoveDownAction(UserTaskView utv) {
        super(utv, NbBundle.getMessage(MoveDownAction.class, "MoveDown")); // NOI18N
        putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(KeyEvent.VK_DOWN,
                InputEvent.CTRL_MASK));
        putValue(SMALL_ICON, new ImageIcon(Utilities.loadImage(
                "org/netbeans/modules/tasklist/usertasks/actions/moveDown.gif"))); // NOI18N
    }
    
    public void actionPerformed(ActionEvent e) {
        UserTask ut = getSingleSelectedTask();
        Object es = utv.getTreeTable().getExpandedNodesAndSelection();
        ut.moveDown();
        utv.getTreeTable().setExpandedNodesAndSelection(es);
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

            en = list.indexOf(ut) != list.size() - 1; 
        }
        setEnabled(en);
    }
}

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

import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.event.ListSelectionEvent;

import org.netbeans.modules.tasklist.usertasks.EditTaskPanel;
import org.netbeans.modules.tasklist.usertasks.UserTaskView;
import org.netbeans.modules.tasklist.usertasks.util.UTUtils;
import org.netbeans.modules.tasklist.usertasks.model.UserTask;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.text.Line;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * Show a given task to the user
 *
 * @author Tor Norbye
 * @author Trond Norbye
 * @author tl
 */
public class ShowTaskAction extends UTViewAction {
    private static final long serialVersionUID = 2;

    /**
     * Constructor.
     * 
     * @param utv view for this action 
     */
    public ShowTaskAction(UserTaskView utv) {
        super(utv, NbBundle.getMessage(ShowTaskAction.class, "LBL_ShowTodo"));
    }
    
    public void actionPerformed(ActionEvent ae) {
        // safe - see enable-check above
        UserTask item = getSingleSelectedTask();
        
        EditTaskPanel panel = new EditTaskPanel(true);
        panel.setPreferredSize(new Dimension(600, 500));
        panel.fillPanel(item);
        
        DialogDescriptor d = new DialogDescriptor(panel,
            NbBundle.getMessage(ShowTaskAction.class, "TITLE_edit_todo")); // NOI18N
        d.setModal(true);
        d.setHelpCtx(new HelpCtx("org.netbeans.modules.tasklist.usertasks.NewTaskDialog")); // NOI18N
        d.setMessageType(NotifyDescriptor.PLAIN_MESSAGE);
        d.setOptionType(NotifyDescriptor.OK_CANCEL_OPTION);
        Dialog dlg = DialogDisplayer.getDefault().createDialog(d);
        dlg.pack();
        dlg.setVisible(true);

        if (d.getValue() == NotifyDescriptor.OK_OPTION) {
            panel.fillObject(item);
        }
    }

    public void valueChanged(ListSelectionEvent e) {
        setEnabled(getSingleSelectedTask() != null);
    }
}

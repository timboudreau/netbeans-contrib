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
import java.awt.event.ActionEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.tree.TreePath;
import org.netbeans.modules.tasklist.usertasks.UserTaskNode;
import org.netbeans.modules.tasklist.usertasks.UserTaskTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.UserTaskView;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.explorer.propertysheet.PropertySheet;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

/**
 * Starts a task
 *
 * @author tl
 */
public class UTPropertiesAction extends UTViewAction {
    /**
     * Constructor.
     * 
     * @param utv a user task view.
     */
    public UTPropertiesAction(UserTaskView utv) {
        super(utv, NbBundle.getMessage(StartTaskAction.class, 
                "Properties")); // NOI18N
    }
    
    public void actionPerformed(ActionEvent e) {
        TreePath[] paths = utv.getTreeTable().getSelectedPaths();
        UserTaskNode[] nodes = new UserTaskNode[paths.length];
        for (int i = 0; i < paths.length; i++) {
            UserTaskTreeTableNode n = (UserTaskTreeTableNode) paths[i].
                    getLastPathComponent();
            nodes[i] = new UserTaskNode(n, n.getUserTask(), 
                    n.getUserTask().getList(),
                    utv.getTreeTable());
        }
        PropertySheet ps = new PropertySheet();
        ps.setNodes(nodes);
        
        DialogDescriptor dd = new DialogDescriptor(ps, "Properties",
                true, new Object[] {DialogDescriptor.CLOSED_OPTION}, null, 
                DialogDescriptor.DEFAULT_ALIGN, HelpCtx.DEFAULT_HELP, null);
        Dialog d = DialogDisplayer.getDefault().createDialog(dd);
        d.setVisible(true);
    }

    public void valueChanged(ListSelectionEvent e) {
        TreePath[] paths = utv.getTreeTable().getSelectedPaths();
        setEnabled(paths.length > 0);
    }
}

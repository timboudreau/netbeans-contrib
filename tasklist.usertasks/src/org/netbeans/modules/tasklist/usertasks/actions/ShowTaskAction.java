/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.usertasks.actions;

import java.awt.Dialog;
import java.awt.Dimension;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.netbeans.modules.tasklist.core.TLUtils;
import org.netbeans.modules.tasklist.usertasks.EditTaskPanel;
import org.netbeans.modules.tasklist.usertasks.UTUtils;
import org.netbeans.modules.tasklist.usertasks.UserTask;
import org.netbeans.modules.tasklist.usertasks.UserTaskNode;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

/**
 * Show a given task to the user
 *
 * @author Tor Norbye
 * @author Trond Norbye
 */
public class ShowTaskAction extends NodeAction {
    private static final long serialVersionUID = 1;

    protected boolean enable(Node[] node) {
        return ((node != null) && (node.length == 1) &&
            node[0] instanceof UserTaskNode);
    }

    protected boolean asynchronous() {
        return false;
    }

    protected void performAction(Node[] node) {
        // safe - see enable-check above
        UserTask item = ((UserTaskNode) node[0]).getTask();         
        
        EditTaskPanel panel = new EditTaskPanel(true);
        panel.setPreferredSize(new Dimension(600, 500));
        panel.fillPanel(item);
        
        // find cursor position
        Object[] cursor = UTUtils.findCursorPosition(node);
        if (cursor == null) {
            Node[] editorNodes = UTUtils.getEditorNodes();
            if (editorNodes != null)
                cursor = UTUtils.findCursorPosition(editorNodes);
        }
        if (cursor != null) {
            String filename = (String) cursor[0];
            int line = ((Integer) cursor[1]).intValue();
            panel.setFilePosition(filename, line);
        }
        
        DialogDescriptor d = new DialogDescriptor(panel,
            NbBundle.getMessage(ShowTaskAction.class, "TITLE_edit_todo")); // NOI18N
        d.setModal(true);
        d.setMessageType(NotifyDescriptor.PLAIN_MESSAGE);
        d.setOptionType(NotifyDescriptor.OK_CANCEL_OPTION);
        Dialog dlg = DialogDisplayer.getDefault().createDialog(d);
        dlg.pack();
        dlg.show();

        if (d.getValue() == NotifyDescriptor.OK_OPTION) {
            panel.fillObject(item);
            UTUtils.LOGGER.fine("file " + item.getFilename()); // NOI18N
            UTUtils.LOGGER.fine("line " + item.getLineNumber()); // NOI18N
            item.updateAnnotation();
            UTUtils.LOGGER.fine("file " + item.getFilename()); // NOI18N
            UTUtils.LOGGER.fine("line " + item.getLineNumber()); // NOI18N
        }
    }

    public String getName() {
        return NbBundle.getMessage(ShowTaskAction.class, "LBL_ShowTodo"); // NOI18N
    }

    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
        // If you will provide context help then use:
        // return new HelpCtx (NewTodoItemAction.class);
    }
}

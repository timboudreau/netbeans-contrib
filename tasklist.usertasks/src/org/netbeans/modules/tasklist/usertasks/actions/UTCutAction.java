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

import java.awt.datatransfer.Clipboard;
import java.awt.event.ActionEvent;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.tree.TreePath;
import org.netbeans.modules.tasklist.usertasks.UserTaskListTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.UserTaskView;

/**
 * Cut.
 *
 * @author tl
 */
public final class UTCutAction extends UTViewAction {
    /**
     * Constructor.
     * 
     * @param utv a user task view.
     */
    public UTCutAction(UserTaskView utv) {
        super(utv, javax.swing.text.DefaultEditorKit.copyAction);
    }
    
    public void valueChanged(ListSelectionEvent e) {
        TreePath[] paths = utv.getTreeTable().getSelectedPaths();
        boolean enabled = true;
        for (int i = 0; i < paths.length; i++) {
            if (paths[i].getLastPathComponent() instanceof 
                    UserTaskListTreeTableNode) {
                enabled = false;
                break;
            }
        }
        setEnabled(enabled);
    }

    public void actionPerformed(ActionEvent e) {
        TransferHandler th = utv.getTreeTable().getTransferHandler();
        Clipboard clipboard = utv.getToolkit().getSystemClipboard();
        th.exportToClipboard(utv.getTreeTable(), clipboard, 
                TransferHandler.MOVE);
    }
}

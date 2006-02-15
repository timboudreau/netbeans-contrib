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
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.tree.TreePath;
import org.netbeans.modules.tasklist.usertasks.UserTaskListTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.UserTaskTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.UserTaskView;

/**
 * Cut.
 *
 * @author tl
 */
public final class UTPasteAction extends UTViewAction {
    /**
     * Constructor.
     * 
     * @param utv a user task view.
     */
    public UTPasteAction(UserTaskView utv) {
        super(utv, javax.swing.text.DefaultEditorKit.copyAction);
    }
    
    public void valueChanged(ListSelectionEvent e) {
        TreePath[] paths = utv.getTreeTable().getSelectedPaths();
        setEnabled(paths.length == 1 && paths[0].getLastPathComponent() 
                instanceof UserTaskTreeTableNode);
    }

    public void actionPerformed(ActionEvent e) {
        TransferHandler th = utv.getTreeTable().getTransferHandler();
        Clipboard clipboard = utv.getToolkit().getSystemClipboard();
        Transferable t = clipboard.getContents(this);
        if (t != null)
            th.importData(utv.getTreeTable(), t);
    }
}

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

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionEvent;
import javax.swing.tree.TreePath;
import org.netbeans.modules.tasklist.usertasks.UserTaskListTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.UserTaskTreeTableNode;
import org.netbeans.modules.tasklist.usertasks.UserTaskView;
import org.netbeans.modules.tasklist.usertasks.transfer.MyTransferHandler;
import org.openide.util.NbBundle;

/**
 * Paste at the top level.
 *
 * @author tl
 */
public final class UTPasteAtTopLevelAction extends AbstractAction {
    private UserTaskView utv;
    
    /**
     * Constructor.
     * 
     * @param utv a user task view.
     */
    public UTPasteAtTopLevelAction(UserTaskView utv) {
        super(NbBundle.getMessage(UTPasteAtTopLevelAction.class, 
                "PasteAtTopLevel")); // NOI18N
        this.utv = utv;
        setEnabled(true);
    }
    
    public void actionPerformed(ActionEvent e) {
        TransferHandler th = utv.getTreeTable().getTransferHandler();
        Clipboard clipboard = utv.getToolkit().getSystemClipboard();
        Transferable t = clipboard.getContents(this);
        if (t != null)
            ((MyTransferHandler) th).importData(utv.getTreeTable(), t, true);
    }
}

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
 * The Original Software is the Accelerators module.
 * The Initial Developer of the Original Software is Andrei Badea.
 * Portions Copyright 2005-2006 Andrei Badea.
 * All Rights Reserved.
 *
 * Contributor(s): Andrei Badea
 */

package org.netbeans.modules.accelerators.terminal;

import java.io.File;
import java.io.IOException;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.NodeAction;

/**
 *
 * @author Andrei Badea
 */
public class RunTerminalAction extends NodeAction {

    private static final ErrorManager LOGGER = ErrorManager.getDefault().getInstance("org.netbeans.modules.accelerators.terminal"); // NOI18N
    private static final boolean LOG = LOGGER.isLoggable(ErrorManager.INFORMATIONAL);

    public RunTerminalAction() {
        putValue("noIconInMenu", Boolean.TRUE); // NOI18N
    }
    
    public String getName() {
        return NbBundle.getMessage(RunTerminalAction.class, "LBL_RunTerminal");
    }

    public org.openide.util.HelpCtx getHelpCtx() {
        return new HelpCtx(RunTerminalAction.class);
    }
    
    protected boolean asynchronous() {
        return false;
    }

    protected void performAction(org.openide.nodes.Node[] activatedNodes) {
        FileObject fo = Util.findFileObject(activatedNodes);
        runTerminal(fo);
    }
    
    protected boolean enable(Node[] activatedNodes) {
        return true;
    }
    
    private void runTerminal(FileObject fo) {
        String command = TerminalOptions.getInstance().getTerminalCommand();
        if (command == null || command.trim().length() <= 0) {
            ErrorManager.getDefault().log(ErrorManager.USER, NbBundle.getMessage(RunTerminalAction.class, "MSG_NoCommand"));
            return;
        }
        
        if (LOG) {
            LOGGER.log(ErrorManager.INFORMATIONAL, "Active file is '" + fo + "'."); // NOI18N
        }
        
        File wd = null;
        if (fo != null ) {
            FileObject folder = fo.isFolder() ? fo : fo.getParent();
            wd = FileUtil.toFile(folder);
        }
        
        if (LOG) {
            LOGGER.log(ErrorManager.INFORMATIONAL, "Running '" + command + "'."); // NOI18N
            LOGGER.log(ErrorManager.INFORMATIONAL, "Working dir is '" + wd + "'."); // NOI18N
        }
        
        try {
            Runtime.getRuntime().exec(command, null, wd);
        } catch (IOException e) {
            ErrorManager.getDefault().notify(e);
        }
    }
}

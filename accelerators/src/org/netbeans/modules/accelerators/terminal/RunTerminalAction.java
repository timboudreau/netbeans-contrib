/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is the Accelerators module.
 * The Initial Developer of the Original Code is Andrei Badea.
 * Portions Copyright 2005-2006 Andrei Badea.
 * All Rights Reserved.

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 2, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 2] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 2 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 2 code and therefore, elected the GPL
Version 2 license, then the option applies only if the new code is
made subject to such option by the copyright holder.
 *
 * Contributor(s): Andrei Badea
 */

package org.netbeans.modules.accelerators.terminal;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import org.netbeans.modules.accelerators.AcceleratorsOptions;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.URLMapper;
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
        String command = AcceleratorsOptions.getInstance().getTerminalCommand();
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

/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is the Accelerators module.
 * The Initial Developer of the Original Software is Andrei Badea.
 * Portions Copyright 2005-2006 Andrei Badea.
 * All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 *
 * Contributor(s): Andrei Badea
 */

package org.netbeans.modules.accelerators.terminal;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
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

    private static final Logger LOGGER = Logger.getLogger("org.netbeans.modules.accelerators.terminal"); // NOI18N

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
            LOGGER.info(NbBundle.getMessage(RunTerminalAction.class, "MSG_NoCommand"));
            return;
        }
        
        LOGGER.log(Level.FINE, "Active file is {0}", fo); // NOI18N
        
        File wd = null;
        if (fo != null ) {
            FileObject folder = fo.isFolder() ? fo : fo.getParent();
            wd = FileUtil.toFile(folder);
        }
        
        LOGGER.log(Level.FINE, "Running {0}", command); // NOI18N
        LOGGER.log(Level.FINE, "Working dir is {0}", wd); // NOI18N
        
        try {
            Runtime.getRuntime().exec(command, null, wd);
        } catch (IOException e) {
            // Stupid NetBeans, would want WARNING here, but that triggers the exception dialog
            LOGGER.log(Level.INFO, null, e);
            String message = NbBundle.getMessage(RunTerminalAction.class, "MSG_Error", e.getLocalizedMessage());
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(message, NotifyDescriptor.ERROR_MESSAGE));
        }
    }
}

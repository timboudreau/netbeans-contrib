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
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
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
 */

package org.netbeans.modules.vcscore.actions;

import java.util.HashMap;

import org.netbeans.api.vcs.commands.Command;
import org.netbeans.spi.vcs.VcsCommandsProvider;

import org.openide.filesystems.FileObject;
import org.openide.loaders.DataFolder;
import org.openide.nodes.Node;
import org.openide.util.actions.NodeAction;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;

/**
 * Action that refresh the local files in a local folder.
 *
 * @author  Martin Entlicher
 */
public class RefreshLocalFolderAction extends NodeAction {
    
    /** Creates a new instance of RefreshLocalFolderAction */
    public RefreshLocalFolderAction() {
    }
    
    /**
     * The action is enabled when there is not LIST command enabled.
     */
    protected boolean enable(Node[] n) {
        HashMap fsSet = new HashMap ();
        for (int i = 0; i < n.length; i++) {
             DataFolder obj = (DataFolder) n[i].getCookie (DataFolder.class);
             if (obj != null) {
                 FileObject primary = obj.getPrimaryFile();
                 VcsCommandsProvider cmdProvider = VcsCommandsProvider.findProvider(primary);
                 if (cmdProvider == null) return false; // Disable this action on non-VCS filesystems
                 Command listCmd = cmdProvider.createCommand("LIST");
                 if (listCmd == null || listCmd.getApplicableFiles((FileObject[]) obj.files().toArray(new FileObject[0])) != null) {
                     return false;
                 }
             } else {
                 return false;
             }
        }
        return n.length > 0;
    }
    
    public HelpCtx getHelpCtx() {
        return new HelpCtx(RefreshLocalFolderAction.class);
    }
    
    public String getName() {
        return NbBundle.getMessage(RefreshLocalFolderAction.class, "LocFolder_Refresh");
    }
    
    protected boolean asynchronous() {
        return false;
    }
    
    protected void performAction(final Node[] activatedNodes) {
        RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                for (int i = 0; i < activatedNodes.length; i++) {
                    DataFolder df = (DataFolder) activatedNodes[i].getCookie (DataFolder.class);
                    if (df != null) {
                        FileObject fo = df.getPrimaryFile ();
                        fo.refresh ();
                    }
                }
            }
        });
    }
    
}

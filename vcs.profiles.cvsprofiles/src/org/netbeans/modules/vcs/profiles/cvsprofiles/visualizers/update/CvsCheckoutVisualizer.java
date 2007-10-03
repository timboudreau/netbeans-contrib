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

package org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.update;

import org.netbeans.modules.vcscore.commands.VcsDescribedTask;
import org.netbeans.modules.vcscore.registry.FSRegistry;
import org.netbeans.modules.vcscore.registry.FSInfo;
import org.netbeans.modules.vcscore.actions.VersioningExplorerAction;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Map;
import java.io.File;

/**
 * Extends update visualizer by adding navigation buttons
 * (on toolbar) that opens checked out location.
 *
 * @author Petr Kuzel
 */
public final class CvsCheckoutVisualizer extends CvsUpdateVisualizer {

    /** Visualizer contract */
    public CvsCheckoutVisualizer() {
    }

    public void outputDone() {
        super.outputDone();
        VcsDescribedTask task = (VcsDescribedTask)getVcsTask();
        Map vars = task.getVariables();
        String path = (String) vars.get("ROOTDIR");  // NOI18N
        File fsRoot = new File(path);
        Action action = new OpenInExplorerAction(fsRoot);
        getContentPane().setNavigationAction(action);
    }

    /**
     * Opens checked out directory in revision explorer.
     * It called too fast it does nothing, it depends on postcommand results.
     * It's OK, there is assumtion that user clicks once again.
     */
    private static class OpenInExplorerAction extends AbstractAction {

        private final File fsRoot;

        public OpenInExplorerAction(File fsRoot) {
            super(getString("show"));
            this.fsRoot = fsRoot;
            putValue(Action.MNEMONIC_KEY, new Integer(getString("show_mne").charAt(0)));
            putValue(Action.SHORT_DESCRIPTION, getString("show_desc"));
        }

        public void actionPerformed(ActionEvent e) {
            FSRegistry fsregistry = FSRegistry.getDefault();
            FSInfo[] infos = fsregistry.getRegistered();
            for (int i = 0; i < infos.length; i++) {
                FSInfo info = infos[i];
                if (info.getFSRoot().equals(fsRoot)) {
                    openCheckedOutWorkingDirectory(info);
                }
            }
        }

        private void openCheckedOutWorkingDirectory(FSInfo info) {
            FileObject root = info.getFileSystem().getRoot();
            VersioningExplorerAction action = (VersioningExplorerAction) VersioningExplorerAction.findObject(VersioningExplorerAction.class, true);
            action.showFileObject(root);
        }
    }

    private static final String getString(String key) {
        return NbBundle.getMessage(CvsCheckoutVisualizer.class, key);
    }

}

/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
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

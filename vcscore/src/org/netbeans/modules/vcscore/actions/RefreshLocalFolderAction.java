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

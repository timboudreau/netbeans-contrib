/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.commands;

import java.util.Set;
import java.util.Iterator;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.caching.FileCacheProvider;
import org.netbeans.modules.vcscore.caching.FileStatusProvider;

/**
 * This class contains a support for VCS commands execution.
 *
 * @author  Martin Entlicher
 */
public class CommandExecutorSupport extends Object {

    /** Creates new CommandExecutorSupport */
    private CommandExecutorSupport() {
    }
    
    /**
     * Performs an automatic refresh after the command finishes.
     */
    public static void doRefresh(VcsFileSystem fileSystem, VcsCommandExecutor vce) {
        VcsCommand cmd = vce.getCommand();
        String dir = vce.getPath();
        //String file = "";
        Set files = vce.getFiles();
        for(Iterator it = files.iterator(); it.hasNext(); ) {
            String file = (String) it.next();
            doRefresh(fileSystem, vce.getExec(), cmd, dir, file);
        }
    }
    
    private static void doRefresh(VcsFileSystem fileSystem, String exec, VcsCommand cmd, String dir, String file) {
        FileCacheProvider cache = fileSystem.getCacheProvider();
        FileStatusProvider statusProvider = fileSystem.getStatusProvider();
        if (statusProvider == null) return; // No refresh without a status provider
        boolean doRefreshCurrent = VcsCommandIO.getBooleanProperty(cmd, VcsCommand.PROPERTY_REFRESH_CURRENT_FOLDER);
        boolean doRefreshParent = VcsCommandIO.getBooleanProperty(cmd, VcsCommand.PROPERTY_REFRESH_PARENT_FOLDER);
        if((doRefreshCurrent || doRefreshParent) && fileSystem.getDoAutoRefresh(dir/*(String) vars.get("DIR")*/)) { // NOI18N
            //D.deb("Now refresh folder after CheckIn,CheckOut,Lock,Unlock... commands for convenience"); // NOI18N
            fileSystem.setAskIfDownloadRecursively(false); // do not ask if using auto refresh
            String refreshPath = dir;//(String) vars.get("DIR");
            refreshPath.replace(java.io.File.separatorChar, '/');
            String refreshPathFile = refreshPath + ((refreshPath.length() > 0) ? "/" : "") + file; //(String) vars.get("FILE");
            if (!doRefreshParent && cache != null && cache.isDir(refreshPathFile)) refreshPath = refreshPathFile;
            String pattern = (String) cmd.getProperty(VcsCommand.PROPERTY_REFRESH_RECURSIVELY_PATTERN_MATCHED);
            if (pattern != null && pattern.length() > 0 && exec.indexOf(pattern) >= 0 && (cache == null || !cache.isFile(refreshPathFile))) {
                statusProvider.refreshDirRecursive(refreshPath);
            } else {
                statusProvider.refreshDir(refreshPath); // NOI18N
            }
        }
        if (!(doRefreshCurrent || doRefreshParent)) fileSystem.removeNumDoAutoRefresh(dir); //(String)vars.get("DIR")); // NOI18N
    }

    /*
    public static String preprocessCommand(VcsCommand cmd, Hashtable vars) {
    }
     */
}

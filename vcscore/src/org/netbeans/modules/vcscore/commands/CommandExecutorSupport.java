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

package org.netbeans.modules.vcscore.commands;

import java.awt.Component;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import java.awt.Dialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.List;
import java.util.TreeMap;
import java.util.WeakHashMap;

import org.openide.NotifyDescriptor;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.RequestProcessor;
import org.openide.util.UserCancelException;

import org.netbeans.api.vcs.VcsManager;
import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;

import org.netbeans.spi.vcs.commands.CommandSupport;

import org.netbeans.modules.vcscore.VcsFileSystem;
//import org.netbeans.modules.vcscore.VcsAction;
import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.RetrievingDialog;
import org.netbeans.modules.vcscore.caching.FileCacheProvider;
import org.netbeans.modules.vcscore.caching.FileStatusProvider;
import org.netbeans.modules.vcscore.util.VariableInputDescriptor;
import org.netbeans.modules.vcscore.util.VariableInputComponent;
import org.netbeans.modules.vcscore.util.VariableInputDialog;
import org.netbeans.modules.vcscore.util.VariableInputFormatException;
import org.netbeans.modules.vcscore.util.VariableValueAdjustment;
import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.netbeans.modules.vcscore.util.Table;
import org.netbeans.modules.vcscore.util.NotifyDescriptorInputPassword;
import org.netbeans.modules.vcscore.versioning.RevisionEvent;
import org.netbeans.modules.vcscore.versioning.RevisionListener;
import org.netbeans.modules.vcscore.versioning.VersioningFileSystem;

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
     * Postprocess the command after it's execution.
     */
    public static void postprocessCommand(CommandExecutionContext executionContext, VcsCommandExecutor vce) {
        int exit = vce.getExitStatus();
        VcsCommand cmd = vce.getCommand();
        VcsFileSystem fileSystem;
        if (executionContext instanceof VcsFileSystem) {
            fileSystem = (VcsFileSystem) executionContext;
        } else {
            fileSystem = null;
        }
        if (fileSystem != null) {
            //String name = vce.getCommand().getDisplayName();
            if (VcsCommandExecutor.SUCCEEDED == exit) {
                checkForModifications(fileSystem, vce);
                doRefresh(fileSystem, vce);
                checkRevisionChanges(fileSystem, vce);
                if (VcsCommandIO.getBooleanProperty(cmd, VcsCommand.PROPERTY_CLEAN_UNIMPORTANT_FILES_ON_SUCCESS)) {
                    deleteUnimportantFiles(fileSystem, vce.getFiles());
                }
            } else {
                Object refresh = cmd.getProperty(VcsCommand.PROPERTY_REFRESH_ON_FAIL);
                if (VcsCommand.REFRESH_ON_FAIL_TRUE.equals(refresh)) {
                    doRefresh(fileSystem, vce);
                } else if (VcsCommand.REFRESH_ON_FAIL_TRUE_ON_FOLDERS.equals(refresh)) {
                    doRefresh(fileSystem, vce, true);
                }
            }
        }
        issuePostCommands(cmd, vce.getVariables(),
                          VcsCommandExecutor.SUCCEEDED == exit, executionContext);
    }
    
    private static Collection getAllFilesAssociatedWith(VcsFileSystem fileSystem, Collection fileNames) {
        java.util.HashSet files = new java.util.HashSet();
        for (Iterator filesIt = fileNames.iterator(); filesIt.hasNext(); ) {
            String name = (String) filesIt.next();
            org.openide.filesystems.FileObject fo = fileSystem.findResource(name);
            if (fo == null) continue;
            try {
                org.openide.loaders.DataObject dobj = org.openide.loaders.DataObject.find(fo);
                files.addAll(dobj.files());
            } catch (org.openide.loaders.DataObjectNotFoundException donfexc) {}
        }
        return files;
    }
    
    private static void deleteUnimportantFiles(VcsFileSystem fileSystem, Collection processedFiles) {
        FileStatusProvider statusProvider = fileSystem.getStatusProvider();
        String localFileStatus = (statusProvider != null) ? statusProvider.getLocalFileStatus() : null;
        String ignoredFileStatus = org.netbeans.modules.vcscore.caching.VcsCacheFile.STATUS_IGNORED;
        for (Iterator filesIt = getAllFilesAssociatedWith(fileSystem, processedFiles).iterator(); filesIt.hasNext(); ) {
            org.openide.filesystems.FileObject fo = (org.openide.filesystems.FileObject) filesIt.next();
            String name = fo.getPath();
            if (!fileSystem.isImportant(name)) {
                if (statusProvider != null) {
                    String status = statusProvider.getFileStatus(name);
                    // Do not delete unimportant files, that are version controled.
                    if (!(localFileStatus.equals(status) || ignoredFileStatus.equals(status))) continue;
                }
                if (fo != null) {
                    try {
                        fo.delete(fo.lock());
                    } catch (java.io.IOException ioexc) {}
                } else {
                    try {
                        fileSystem.delete(name);
                    } catch (java.io.IOException ioexc) {}
                }
            }
        }
    }
    
    private static void issuePostCommands(VcsCommand cmd, Hashtable vars,
                                          boolean success, CommandExecutionContext executionContext) {
        String commands;
        if (success) {
            commands = (String) cmd.getProperty(VcsCommand.PROPERTY_COMMANDS_AFTER_SUCCESS);
        } else {
            commands = (String) cmd.getProperty(VcsCommand.PROPERTY_COMMANDS_AFTER_FAIL);
        }
        if (commands == null) return ;
        commands = Variables.expand(vars, commands, false).trim();
        if (commands.length() == 0) return ;
        String[] cmdNames = VcsUtilities.getQuotedStrings(commands);
        for (int i = 0; i < cmdNames.length; i++) {
            CommandSupport cs = executionContext.getCommandSupport(cmdNames[i]);
            if (cs != null) {
                Command c = cs.createCommand();
                if (c instanceof VcsDescribedCommand) {
                    ((VcsDescribedCommand) c).setAdditionalVariables(new Hashtable(vars));
                }
                if (VcsManager.getDefault().showCustomizer(c)) {
                    c.execute();
                }
            }
            /*
            VcsCommand c = executionContext.getCommand(cmdNames[i]);
            if (c != null) {
                Hashtable cVars = new Hashtable(vars);
                VcsCommandExecutor vce = fileSystem.getVcsFactory().getCommandExecutor(c, cVars);
                fileSystem.getCommandsPool().preprocessCommand(vce, cVars, fileSystem);
                fileSystem.getCommandsPool().startExecutor(vce);
            }
             */
        }
    }
    
    private static void checkForModifications(VcsFileSystem fileSystem, VcsCommandExecutor vce) {
        if (VcsCommandIO.getBooleanProperty(vce.getCommand(), VcsCommand.PROPERTY_CHECK_FOR_MODIFICATIONS)) {
            Collection files = vce.getFiles();
            for (Iterator it = files.iterator(); it.hasNext(); ) {
                String path = (String) it.next();
                fileSystem.checkForModifications(path);
                /*
                org.openide.filesystems.FileObject fo = fileSystem.findResource(path);
                System.out.println("fo("+path+") = "+fo);
                 */
            }
        }
    }

    
    /**
     * Performs an automatic refresh after the command finishes.
     */
    private static void doRefresh(VcsFileSystem fileSystem, VcsCommandExecutor vce) {
        doRefresh(fileSystem, vce, false);
    }
    
    /** The map of filesystems weakly referenced and correponding map of
     * folders which are to be refreshed later. */
    private static final Map foldersToRefreshByFilesystems = new WeakHashMap();
    
    /**
     * Performs an automatic refresh after the command finishes.
     */
    private static void doRefresh(VcsFileSystem fileSystem, VcsCommandExecutor vce, boolean foldersOnly) {
        //System.out.println("doRefresh("+vce+", "+foldersOnly+")");
        Map foldersToRefresh = getFoldersToRefresh(fileSystem, vce, foldersOnly);
        //System.out.println("  have folders = "+foldersToRefresh);
        synchronized (foldersToRefreshByFilesystems) {
            boolean refreshLater = false;
            CommandTask[] tasks = CommandProcessor.getInstance().getRunningCommandTasks();
            for (int i = 0; i < tasks.length; i++) {
                if (tasks[i] instanceof VcsDescribedTask) {
                    if (vce.equals(((VcsDescribedTask) tasks[i]).getExecutor())) {
                        //System.out.println("    detected myself, skipping...");
                        continue;
                    }
                    VcsCommand cmd = ((VcsDescribedTask) tasks[i]).getVcsCommand();
                    boolean doRefreshCurrent = VcsCommandIO.getBooleanProperty(cmd, VcsCommand.PROPERTY_REFRESH_CURRENT_FOLDER);
                    boolean doRefreshParent = VcsCommandIO.getBooleanProperty(cmd, VcsCommand.PROPERTY_REFRESH_PARENT_FOLDER);
                    if (doRefreshCurrent || doRefreshParent) {
                        //System.out.println("  Command "+cmd+" running, will refresh later...");
                        refreshLater = true;
                        break;
                    }
                }
            }
            if (refreshLater) {
                Map fsFoldresMap = (Map) foldersToRefreshByFilesystems.get(fileSystem);
                if (fsFoldresMap == null) {
                    fsFoldresMap = new TreeMap();
                    foldersToRefreshByFilesystems.put(fileSystem, fsFoldresMap);
                }
                copyFoldersToRefresh(foldersToRefresh, fsFoldresMap);
                foldersToRefresh = Collections.EMPTY_MAP;
            } else {
                Map fsFoldresMap = (Map) foldersToRefreshByFilesystems.remove(fileSystem);
                if (fsFoldresMap != null) {
                    copyFoldersToRefresh(fsFoldresMap, foldersToRefresh);
                }
            }
        }
        doRefresh(fileSystem, foldersToRefresh);
    }
    
    private static void copyFoldersToRefresh(Map src, Map dest) {
        for (Iterator it = src.keySet().iterator(); it.hasNext(); ) {
            String folderName = (String) it.next();
            Boolean srcRec = (Boolean) src.get(folderName);
            Boolean destRec = (Boolean) dest.get(folderName);
            if (!Boolean.TRUE.equals(destRec)) {
                dest.put(folderName, srcRec);
            }
        }
    }
    
    private static void doRefresh(VcsFileSystem fileSystem, Map foldersToRefresh) {
        //System.out.println("doRefresh("+foldersToRefresh+")");
        for (Iterator it = foldersToRefresh.keySet().iterator(); it.hasNext(); ) {
            String folderName = (String) it.next();
            Boolean rec = (Boolean) foldersToRefresh.get(folderName);
            //System.out.println("Calling doRefresh("+folderName+", "+rec.booleanValue()+")");
            doRefresh(fileSystem, folderName, rec.booleanValue());
        }
    }
    
    /** Perform the refresh of a folder.
     * @param fileSystem the file system to use
     * @param refreshPath the folder to refresh
     * @param recursive whether to do the refresh recursively
     */
    public static void doRefresh(VcsFileSystem fileSystem, String refreshPath, boolean recursive) {
        FileStatusProvider statusProvider = fileSystem.getStatusProvider();
        if (statusProvider == null) return ;
        FileCacheProvider cache = fileSystem.getCacheProvider();
        String dirName = ""; // NOI18N
        if (cache == null || cache.isDir(refreshPath)) {
            dirName = refreshPath;
        }
        else{
            dirName = VcsUtilities.getDirNamePart(refreshPath);
        }
        if (recursive) {
            VcsCommand listSub = fileSystem.getCommand(VcsCommand.NAME_REFRESH_RECURSIVELY);
            Object execList = (listSub != null) ? listSub.getProperty(VcsCommand.PROPERTY_EXEC) : null;
            if (execList != null && ((String) execList).trim().length() > 0) {
                statusProvider.refreshDirRecursive(dirName);
            } else {
                RetrievingDialog rd = new RetrievingDialog(fileSystem, dirName, new javax.swing.JFrame(), false);
                VcsUtilities.centerWindow(rd);
                RequestProcessor rp = RequestProcessor.getDefault();
                rp.post(rd);
            }
        } else {
            statusProvider.refreshDir(dirName); // NOI18N
        }
    }
    
    /**
     * Get the map of names of folders, that need to be refreshed as keys and
     * a Boolean value of whether the refresh should be recursive or not as values.
     */
    private static Map getFoldersToRefresh(VcsFileSystem fileSystem, VcsCommandExecutor vce, boolean foldersOnly) {
        Map foldersToRefresh = new TreeMap();
        VcsCommand cmd = vce.getCommand();
        boolean doRefreshCurrent = VcsCommandIO.getBooleanProperty(cmd, VcsCommand.PROPERTY_REFRESH_CURRENT_FOLDER);
        boolean doRefreshParent = VcsCommandIO.getBooleanProperty(cmd, VcsCommand.PROPERTY_REFRESH_PARENT_FOLDER);
        //System.out.println("getFoldersToRefresh("+fileSystem+", "+vce+", "+foldersOnly+"), current = "+doRefreshCurrent+", parent = "+doRefreshParent);
        if (doRefreshCurrent || doRefreshParent) {
            Collection files = vce.getFiles();
            //System.out.println("  files = "+files);
            for(Iterator it = files.iterator(); it.hasNext(); ) {
                String fullPath = (String) it.next();
                String dir = VcsUtilities.getDirNamePart(fullPath);
                String file = VcsUtilities.getFileNamePart(fullPath);
                //System.out.println("  fullPath = "+fullPath+", dir = "+dir+", file = "+file);
                Boolean recursively[] = { Boolean.FALSE };
                String folderName = getFolderToRefresh(fileSystem, vce.getExec(),
                                                       cmd, dir, file, foldersOnly,
                                                       doRefreshCurrent, doRefreshParent,
                                                       recursively);
                if (folderName != null) {
                    Boolean rec = (Boolean) foldersToRefresh.get(folderName);
                    if (!Boolean.TRUE.equals(rec)) {
                        foldersToRefresh.put(folderName, recursively[0]);
                    }
                }
            }
        }
        return foldersToRefresh;
    }
    
    private static String getFolderToRefresh(VcsFileSystem fileSystem, String exec,
                                             VcsCommand cmd, String dir, String file,
                                             boolean foldersOnly, boolean doRefreshCurrent,
                                             boolean doRefreshParent, Boolean[] recursively) {
        FileCacheProvider cache = fileSystem.getCacheProvider();
        FileStatusProvider statusProvider = fileSystem.getStatusProvider();
        if (statusProvider == null) return null; // No refresh without a status provider
        if (doRefreshCurrent || doRefreshParent) { // NOI18N
            //D.deb("Now refresh folder after CheckIn,CheckOut,Lock,Unlock... commands for convenience"); // NOI18N
            //fileSystem.setAskIfDownloadRecursively(false); // do not ask if using auto refresh
            String refreshPath = dir;//(String) vars.get("DIR");
            refreshPath.replace(java.io.File.separatorChar, '/');
            String refreshPathFile = refreshPath + ((refreshPath.length() > 0) ? "/" : "") + file; //(String) vars.get("FILE");
            if (!doRefreshParent && cache != null && cache.isDir(refreshPathFile)) refreshPath = refreshPathFile;
            String patternMatch = (String) cmd.getProperty(VcsCommand.PROPERTY_REFRESH_RECURSIVELY_PATTERN_MATCHED);
            String patternUnmatch = (String) cmd.getProperty(VcsCommand.PROPERTY_REFRESH_RECURSIVELY_PATTERN_UNMATCHED);
            boolean rec = (exec != null
                && (cache == null || !(cache.isFile(refreshPathFile)
                                       || (!cache.isDir(refreshPathFile) && !fileSystem.folder(refreshPathFile))))
                && (patternMatch != null && patternMatch.length() > 0 && exec.indexOf(patternMatch) >= 0
                    || patternUnmatch != null && patternUnmatch.length() > 0 && exec.indexOf(patternUnmatch) < 0));
            recursively[0] = (rec) ? Boolean.TRUE : Boolean.FALSE;
            //System.out.println("  !foldersOnly = "+(!foldersOnly)+", cache.isDir("+refreshPath+") = "+cache.isDir(refreshPath));
            if (!foldersOnly || cache.isDir(refreshPath)) {
                //System.out.println("  CALLING REFRESH!");
                return refreshPath;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
    
    public static void checkRevisionChanges(VcsFileSystem fileSystem, VcsCommandExecutor vce) {
        int whatChanged = RevisionEvent.REVISION_NO_CHANGE;
        String changedRevision = null;
        VcsCommand cmd = vce.getCommand();
        if (VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_CHANGING_NUM_REVISIONS)) {
            whatChanged = RevisionEvent.REVISION_ALL_CHANGED;
        }
        if (VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_CHANGING_REVISION)) {
            whatChanged = RevisionEvent.REVISION_CHANGED;
            Object varName = cmd.getProperty(VcsCommand.PROPERTY_CHANGED_REVISION_VAR_NAME);
            if (varName != null) changedRevision = (String) vce.getVariables().get(varName);
        }
        //System.out.println("checkRevisionChanges(): whatChanged = "+whatChanged+", changeRevision = "+changedRevision);
        if (whatChanged != RevisionEvent.REVISION_NO_CHANGE) {
            String[] files = (String[]) vce.getFiles().toArray(new String[0]);
            for (int i = 0; i < files.length; i++) {
                Object fo = fileSystem.findResource(files[i]);
                if (fo == null) {
                    VersioningFileSystem vfs = fileSystem.getVersioningFileSystem();
                    if (vfs != null) {
                        fo = vfs.findResource(files[i]);
                    }
                }
                if (fo != null) {
                    RevisionEvent event = new RevisionEvent(fo);
                    event.setRevisionChangeID(whatChanged);
                    event.setChangedRevision(changedRevision);
                    fileSystem.fireRevisionsChanged(event);
                }
            }
        }
    }

    private static String g(String s) {
        return org.openide.util.NbBundle.getBundle(CommandExecutorSupport.class).getString(s);
    }
    
}

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

package org.netbeans.modules.vcscore.commands;

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

import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.util.RequestProcessor;

import org.netbeans.api.vcs.VcsManager;
import org.netbeans.api.vcs.commands.Command;

import org.netbeans.spi.vcs.commands.CommandSupport;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.turbo.Turbo;
import org.netbeans.modules.vcscore.turbo.TurboUtil;
import org.netbeans.modules.vcscore.turbo.Statuses;
import org.netbeans.modules.vcscore.turbo.FileProperties;
import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.netbeans.modules.vcscore.versioning.RevisionEvent;

/**
 * This class contains a support for VCS commands execution.
 *
 * @author  Martin Entlicher
 */
public class CommandExecutorSupport extends Object {
    
    private static RequestProcessor REFRESH_REQUEST_PROCESSOR;
    
    /** Creates new CommandExecutorSupport */
    private CommandExecutorSupport() {
    }
    
    private static synchronized void checkRefreshRequestProcessorCreated() {
        if (REFRESH_REQUEST_PROCESSOR == null) {
            REFRESH_REQUEST_PROCESSOR = new RequestProcessor("Post-command Refresh Request Processor", 1); // NOI18N
        }
    }
    
    /**
     * Postprocess the command after it's execution.
     */
    public static void postprocessCommand(CommandExecutionContext executionContext, final VcsCommandExecutor vce) {
        int exit = vce.getExitStatus();
        final VcsCommand cmd = vce.getCommand();
        final VcsFileSystem fileSystem;
        if (executionContext instanceof VcsFileSystem) {
            fileSystem = (VcsFileSystem) executionContext;
        } else {
            fileSystem = null;
        }
        if (VcsCommandExecutor.SUCCEEDED == exit) {
            checkForModifications(executionContext, vce);
        }
        if (fileSystem != null) {
            //String name = vce.getCommand().getDisplayName();
            if (VcsCommandExecutor.SUCCEEDED == exit) {
                doRefresh(fileSystem, vce);
                RequestProcessor.getDefault().post(new Runnable() {
                    // do that lazily, revision reload is waiting for the command,
                    // this can cause deadlock if the command can not run in parallel with this one.
                    public void run() {
                        checkRevisionChanges(fileSystem, vce);
                        if (VcsCommandIO.getBooleanProperty(cmd, VcsCommand.PROPERTY_CLEAN_UNIMPORTANT_FILES_ON_SUCCESS)) {
                            deleteUnimportantFiles(fileSystem, vce.getFiles());
                        }
                    }
                });
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
                DataObject dobj = DataObject.find(VcsUtilities.getMainFileObject(fo));
                FileObject[] allFOs = (FileObject[]) dobj.files().toArray(new FileObject[0]);
                allFOs = VcsUtilities.convertFileObjects(allFOs);
                files.addAll(java.util.Arrays.asList(allFOs));
            } catch (org.openide.loaders.DataObjectNotFoundException donfexc) {}
        }
        return files;
    }
    
    private static void deleteUnimportantFiles(VcsFileSystem fileSystem, Collection processedFiles) {

        String localFileStatus = Statuses.getLocalStatus();
        String ignoredFileStatus = Statuses.STATUS_IGNORED;
        for (Iterator filesIt = getAllFilesAssociatedWith(fileSystem, processedFiles).iterator(); filesIt.hasNext(); ) {
            org.openide.filesystems.FileObject fo = (org.openide.filesystems.FileObject) filesIt.next();
            String name = fo.getPath();
            if (!fileSystem.isImportant(name)) {
                FileProperties fprops = Turbo.getMeta(fo);
                String status = FileProperties.getStatus(fprops);  // XXX this status is VCS specific
                // Do not delete unimportant files, that are version controled.
                if (!(localFileStatus.equals(status) || ignoredFileStatus.equals(status))) continue;
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
                final Command c = cs.createCommand();
                if (c instanceof VcsDescribedCommand) {
                    ((VcsDescribedCommand) c).setAdditionalVariables(new Hashtable(vars));
                }
                RequestProcessor.getDefault().post(new Runnable() {
                    public void run() {
                        // Run the customization asynchronously. Commands that can be run
                        // in the customization process may not be able to run in parallel
                        // with this task.
                        if (VcsManager.getDefault().showCustomizer(c)) {
                            c.execute();
                        }
                    }
                });
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
    
    private static void checkForModifications(CommandExecutionContext executionContext, VcsCommandExecutor vce) {
        if (VcsCommandIO.getBooleanProperty(vce.getCommand(), VcsCommand.PROPERTY_CHECK_FOR_MODIFICATIONS)) {
            Collection files = vce.getFiles();
            if (files.size() == 0 && !(executionContext instanceof VcsFileSystem)) {
                // When there are no files, check for ROOTDIR. Necessary for global commands.
                // TODO Global commands should define the processed files somehow.
                String root = (String) vce.getVariables().get("ROOTDIR");
                if (root != null) {
                    files = Collections.singleton(root);
                }
            }
            for (Iterator it = files.iterator(); it.hasNext(); ) {
                String path = (String) it.next();
                executionContext.checkForModifications(path);
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

        // TODO commented out block causes race conditions forgeting to
        // call refresh recursively. Probably caused by CommandProcessor.getInstance().getRunningCommandTasks().
        // Anyway it's just optimalization that can be implemented in
        // RequestProcessor queue used later on

//        Optimization of refreshes - necessary when commands launch folder refreshes
//        e.g. issue #32928.
//        synchronized (foldersToRefreshByFilesystems) {
//            boolean refreshLater = false;
//            CommandTask[] tasks = CommandProcessor.getInstance().getRunningCommandTasks();
//            for (int i = 0; i < tasks.length; i++) {
//                if (tasks[i] instanceof VcsDescribedTask) {
//                    if (vce.equals(((VcsDescribedTask) tasks[i]).getExecutor())) {
//                        //System.out.println("    detected myself, skipping...");
//                        continue;
//                    }
//                    VcsCommand cmd = ((VcsDescribedTask) tasks[i]).getVcsCommand();
//                    boolean doRefreshCurrent = VcsCommandIO.getBooleanProperty(cmd, VcsCommand.PROPERTY_REFRESH_CURRENT_FOLDER);
//                    boolean doRefreshParent = VcsCommandIO.getBooleanProperty(cmd, VcsCommand.PROPERTY_REFRESH_PARENT_FOLDER);
//                    boolean doRefreshParentOfCurrent = VcsCommandIO.getBooleanProperty(cmd, VcsCommand.PROPERTY_REFRESH_PARENT_OF_CURRENT_FOLDER);
//                    if (doRefreshCurrent || doRefreshParent || doRefreshParentOfCurrent) {
//                        //System.out.println("  Command "+cmd+" running, will refresh later...");
//                        refreshLater = true;
//                        break;
//                    }
//                }
//            }
//            if (refreshLater) {
//                Map fsFoldresMap = (Map) foldersToRefreshByFilesystems.get(fileSystem);
//                if (fsFoldresMap == null) {
//                    fsFoldresMap = new TreeMap();
//                    foldersToRefreshByFilesystems.put(fileSystem, fsFoldresMap);
//                }
//                copyFoldersToRefresh(foldersToRefresh, fsFoldresMap);
//                foldersToRefresh = Collections.EMPTY_MAP;
//            } else {
//                Map fsFoldresMap = (Map) foldersToRefreshByFilesystems.remove(fileSystem);
//                if (fsFoldresMap != null) {
//                    copyFoldersToRefresh(fsFoldresMap, foldersToRefresh);
//                }
//            }
//        }
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

    /** Refreshes all files (keys) in map according to recursion (value). Turbo compatible. */
    private static void doRefresh(VcsFileSystem fileSystem, Map foldersToRefresh) {
        //System.out.println("doRefresh("+foldersToRefresh+")");
        for (Iterator it = foldersToRefresh.keySet().iterator(); it.hasNext(); ) {
            String folderName = (String) it.next();
            Boolean rec = (Boolean) foldersToRefresh.get(folderName);
            //System.out.println("Calling doRefresh("+folderName+", "+rec.booleanValue()+")");
            doRefresh(fileSystem, folderName, rec.booleanValue());
        }
    }
    
    /**
     * Perform the refresh of a folder. Turbo compatible.
     * @param fileSystem the file system to use
     * @param refreshPath the folder to refresh
     * @param recursive whether to do the refresh recursively
     */
    public static void doRefresh(VcsFileSystem fileSystem, String refreshPath, final boolean recursive) {

        final FileObject fo = fileSystem.findResource(refreshPath);
        // Spawn the refresh asynchronously, like the original implementation.
        // XXX it potentionaly spawns many threads. Requests should be
        // queued and merged
        // Must have throughoutput == 1, because otherwise childen can be
        // refreshed before their parents.
        checkRefreshRequestProcessorCreated();
        REFRESH_REQUEST_PROCESSOR.post(new Runnable() {
            public void run() {
                if (recursive) {
                    TurboUtil.refreshRecursively(fo);
                } else {
                    TurboUtil.refreshFolder(fo);
                }
            }
        });
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
        boolean doRefreshParentOfCurrent = VcsCommandIO.getBooleanProperty(cmd, VcsCommand.PROPERTY_REFRESH_PARENT_OF_CURRENT_FOLDER);
        //System.out.println("getFoldersToRefresh("+fileSystem+", "+vce+", "+foldersOnly+"), current = "+doRefreshCurrent+", parent = "+doRefreshParent);
        if (doRefreshCurrent || doRefreshParent || doRefreshParentOfCurrent) {
            Collection files = vce.getFiles();
            //System.out.println("  files = "+files);
            for(Iterator it = files.iterator(); it.hasNext(); ) {
                String fullPath = (String) it.next();
                String dir = VcsUtilities.getDirNamePart(fullPath);
                String file = VcsUtilities.getFileNamePart(fullPath);
                //System.out.println("  fullPath = "+fullPath+", dir = "+dir+", file = "+file);
                Boolean recursively[] = { Boolean.FALSE };
                String[] folderNames = getFolderToRefresh(fileSystem, vce.getExec(),
                                                          cmd, dir, file, foldersOnly,
                                                          doRefreshCurrent, doRefreshParent,
                                                          doRefreshParentOfCurrent, recursively);
                if (folderNames != null) {
                    for (int i = 0; i < folderNames.length; i++) {
                        String folderName = folderNames[i];
                        Boolean rec = (Boolean) foldersToRefresh.get(folderName);
                        if (!Boolean.TRUE.equals(rec)) {
                            rec = recursively[0];
                            if (i == 0 && rec.booleanValue() && folderNames.length > 1) {
                                // we know that the parent folder will be the first in the array.
                                // If there are two folders being refreshed, only the inner one
                                // is refreshed recursively (see issue #48723).
                                rec = Boolean.FALSE;
                            }
                            foldersToRefresh.put(folderName, rec);
                        }
                    }
                }
            }
        }
        return foldersToRefresh;
    }

    /**
     * @param recursively [0] is filled by the method
     * @return dir[+file] converted to FS path or <code>null</code>
     */
    private static String[] getFolderToRefresh(VcsFileSystem fileSystem, String exec,
                                               VcsCommand cmd, String dir, String file,
                                               boolean foldersOnly, boolean doRefreshCurrent,
                                               boolean doRefreshParent, boolean doRefreshParentOfCurrent,
                                               Boolean[] recursively) {

        if (doRefreshCurrent || doRefreshParent || doRefreshParentOfCurrent) { // NOI18N
            //D.deb("Now refresh folder after CheckIn,CheckOut,Lock,Unlock... commands for convenience"); // NOI18N
            //fileSystem.setAskIfDownloadRecursively(false); // do not ask if using auto refresh
            String refreshPath = dir;//(String) vars.get("DIR");
            refreshPath.replace(java.io.File.separatorChar, '/');
            String refreshPathFile;
            if (".".equals(file)) { // Happens when on the FS root.
                refreshPathFile = refreshPath;
            } else {
                refreshPathFile = refreshPath + ((refreshPath.length() > 0) ? "/" : "") + file; //(String) vars.get("FILE");
            }
            String currentFolder;
            String parentFolder;
            if (fileSystem.folder(refreshPathFile)) { // folder is selected
                currentFolder = refreshPathFile;
                parentFolder = refreshPath;
            } else { // file is selected
                if (foldersOnly) return null;
                currentFolder = parentFolder = refreshPath;
            }
            List refreshPaths = new ArrayList(2);
            // it's important to return the parent folder first, because of the logic in getFoldersToRefresh()
            if (doRefreshParentOfCurrent) {
                String parent = parentFolder;
                if (currentFolder.equals(parentFolder)) {
                    int index = currentFolder.lastIndexOf('/');
                    if (index > 0) {
                        parent = currentFolder.substring(0, index);
                    } else {
                        parent = "";
                    }
                }
                if (!refreshPaths.contains(parent)) {
                    refreshPaths.add(parent);
                }
            }
            if (doRefreshParent && !refreshPaths.contains(parentFolder)) {
                refreshPaths.add(parentFolder);
            }
            if (doRefreshCurrent && !refreshPaths.contains(currentFolder)) {
                refreshPaths.add(currentFolder);
            }
            String patternMatch = (String) cmd.getProperty(VcsCommand.PROPERTY_REFRESH_RECURSIVELY_PATTERN_MATCHED);
            String patternUnmatch = (String) cmd.getProperty(VcsCommand.PROPERTY_REFRESH_RECURSIVELY_PATTERN_UNMATCHED);
            String innerRefreshFolder = (String) refreshPaths.get(refreshPaths.size() - 1);
            boolean rec = (exec != null
                && ( !(fileSystem.folder(innerRefreshFolder) == false
                                       || (!fileSystem.folder(innerRefreshFolder))))
                && (patternMatch != null && patternMatch.length() > 0 && exec.indexOf(patternMatch) >= 0
                    || patternUnmatch != null && patternUnmatch.length() > 0 && exec.indexOf(patternUnmatch) < 0));
            recursively[0] = (rec) ? Boolean.TRUE : Boolean.FALSE;
            //System.out.println("  !foldersOnly = "+(!foldersOnly)+", cache.isDir("+refreshPath+") = "+cache.isDir(refreshPath));
            return (String[]) refreshPaths.toArray(new String[0]);
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

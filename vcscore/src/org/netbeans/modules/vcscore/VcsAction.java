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

package org.netbeans.modules.vcscore;

import java.util.*;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.nodes.Node;
import org.openide.loaders.DataObject;
import org.openide.cookies.SaveCookie;

import org.netbeans.api.vcs.VcsManager;
import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;

import org.netbeans.spi.vcs.commands.CommandSupport;

import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.netbeans.modules.vcscore.util.Table;
import org.netbeans.modules.vcscore.caching.FileStatusProvider;
import org.netbeans.modules.vcscore.cmdline.UserCommand;
import org.netbeans.modules.vcscore.cmdline.UserCommandSupport;
import org.netbeans.modules.vcscore.cmdline.UserCommandTask;
import org.netbeans.modules.vcscore.cmdline.WrappingCommandTask;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.turbo.Turbo;
import org.netbeans.modules.vcscore.turbo.TurboUtil;
import org.netbeans.modules.vcscore.turbo.FileProperties;
import org.openide.ErrorManager;
import org.openide.filesystems.FileSystem;

/**
 * Several static methods.
 *
 * @deprecated This class is retained just for compatibility reasons. Use the
 *             new VCS APIs instead.
 *
 * @author Martin Entlicher
 */
public final class VcsAction extends Object {//NodeAction implements ActionListener {
    
    private VcsAction() {
    }

    /**
     * Do refresh children of a directory.
     * @param path the directory path
     */
    private static void doList(VcsFileSystem fileSystem, String path) {
        FileObject fo = fileSystem.findResource(path);
        if (fo == null) return ;
        if (fo.isData()) fo = fo.getParent();
        Turbo.getRepositoryMeta(fo);
        TurboUtil.refreshFolder(fo);
    }

    /**
     * Lock files in VCS. This command does not save the file contents.
     * @param files the table pairs of file name and associated <code>FileObject</code>
     * @return The array of command executors if the lock command was found.
     *         Otherwise <code>null</code> is returned.
     */
    public static VcsCommandExecutor[] doLock(Table files, VcsFileSystem fileSystem) {
        VcsCommand cmd = fileSystem.getCommand(VcsCommand.NAME_LOCK);
        if (cmd != null) {
            return doCommand(files, cmd, null, fileSystem, null, null, null, null, false);
        } else {
            return null;
        }
    }
    
    /**
     * Unlock files in VCS.
     * @param files the table pairs of file name and associated <code>FileObject</code>
     */
    public static void doUnlock(Table files, VcsFileSystem fileSystem) {
        VcsCommand cmd = fileSystem.getCommand(VcsCommand.NAME_UNLOCK);
        if (cmd != null) doCommand(files, cmd, null, fileSystem);
    }
    
    /**
     * Prepare for edit files in VCS. This command does not save the file contents.
     * @param files the table pairs of file name and associated <code>FileObject</code>
     * @return The array of command executors if the edit command was found.
     *         Otherwise <code>null</code> is returned.
     */
    public static VcsCommandExecutor[] doEdit(Table files, VcsFileSystem fileSystem) {
        VcsCommand cmd = fileSystem.getCommand(VcsCommand.NAME_EDIT);
        if (cmd != null) {
            // Because the EDIT command is executed for read-only files,
            // the doCommand() method must be called with 'false' as last argument;
            // 'true' would indicate the files would be saved prior to execution,
            // which generally is not possible for read-only files...
            VcsCommandExecutor[] executors = doCommand(files, cmd, null, fileSystem, null, null, null, null, false);
            /* Wait for the executor(s) to finish. - according to version 1.11 this leads to a deadlock
            for (int i = 0; i < executors.length; i++) {
                try {
                    fileSystem.getCommandsPool().waitToFinish(executors[i]);
                } catch (InterruptedException ex) {
                    // Silently ignore...
                }
            }
             */
            return executors;
        } else {
            return null;
        }
    }

    /**
     * Do a command on a set of files.
     * @param files the table of pairs of files and file objects, to perform the command on
     * @param cmd the command to perform
     * @param additionalVars additional variables to FS variables, or null when no additional variables are needed
     * @param fileSystem the VCS file system
     * @return the command executors of all executed commands.
     */
    public static VcsCommandExecutor[] doCommand(Table files, VcsCommand cmd, Hashtable additionalVars, VcsFileSystem fileSystem) {
        return doCommand(files, cmd, additionalVars, fileSystem, null, null, null, null);
    }
    
    /**
     * Do a command on a set of files.
     * @param files the table of pairs of files and file objects, to perform the command on
     * @param cmd the command to perform
     * @param additionalVars additional variables to FS variables, or null when no additional variables are needed
     * @param fileSystem the VCS file system
     * @return the command executors of all executed commands.
     */
    public static VcsCommandExecutor[] doCommand(Table files, VcsCommand cmd, Hashtable additionalVars, VcsFileSystem fileSystem,
                                                 CommandOutputListener stdoutListener, CommandOutputListener stderrListener,
                                                 CommandDataOutputListener stdoutDataListener, CommandDataOutputListener stderrDataListener) {
        return doCommand(files, cmd, additionalVars, fileSystem, stdoutListener, stderrListener, stdoutDataListener, stderrDataListener, true);
    }
    
    private static class RegexDataListenerBridge extends Object implements RegexErrorListener {
        
        private CommandDataOutputListener dataListener;
        
        public RegexDataListenerBridge(CommandDataOutputListener dataListener) {
            this.dataListener = dataListener;
        }
        
        /**
         * This method is called, with elements of the parsed data.
         * @param elements the elements of parsed data.
         */
        public void outputMatchedGroups(String[] elements) {
            dataListener.outputData(elements);
        }
        
    }
    
    /**
     * Do a command on a set of files.
     * @param files the table of pairs of files and file objects, to perform the command on
     * @param cmd the command to perform
     * @param additionalVars additional variables to FS variables, or null when no additional variables are needed
     * @param fileSystem the VCS file system
     * @param saveProcessingFiles whether save processing files prior command execution
     * @return the command executors of all executed commands.
     */
    public static VcsCommandExecutor[] doCommand(Table files, VcsCommand cmd, Hashtable additionalVars, VcsFileSystem fileSystem,
                                                 CommandOutputListener stdoutListener, CommandOutputListener stderrListener,
                                                 CommandDataOutputListener stdoutDataListener, CommandDataOutputListener stderrDataListener,
                                                 boolean saveProcessingFiles) {
        //System.out.println("doCommand("+VcsUtilities.arrayToString((String[]) files.keySet().toArray(new String[0]))+", "+cmd+")");
        if (files.size() == 0) return new VcsCommandExecutor[0];
        if (saveProcessingFiles) {
            assureFilesSaved(files.values());
        }
        UserCommand ucmd = (UserCommand) cmd;
        CommandSupport cmdSupp = fileSystem.getCommandSupport(cmd.getName());
        if (cmdSupp == null || !cmd.equals(fileSystem.getCommand(cmd.getName()))) {
            cmdSupp = new UserCommandSupport(ucmd, fileSystem);
        }
        Command command = cmdSupp.createCommand();
        if (command instanceof VcsDescribedCommand) {
            VcsDescribedCommand dcmd = (VcsDescribedCommand) command;
            if (additionalVars != null) dcmd.setAdditionalVariables(additionalVars);
            if (stdoutListener != null) dcmd.addTextOutputListener(stdoutListener);
            if (stderrListener != null) dcmd.addTextErrorListener(stderrListener);
            if (stdoutDataListener != null) dcmd.addRegexOutputListener(new RegexDataListenerBridge(stdoutDataListener));
            if (stderrDataListener != null) dcmd.addRegexErrorListener(new RegexDataListenerBridge(stderrDataListener));
        }
        if (additionalVars != null) {
            String ctrlInAction = (String) additionalVars.get(VcsFileSystem.VAR_CTRL_DOWN_IN_ACTION);
            if (ctrlInAction != null && ctrlInAction.length() > 0) {
                command.setExpertMode(true);
            }
        }
        boolean anyWereSet = UserCommandSupport.setCommandFilesFromTable(command, files, fileSystem);
        if (!anyWereSet) return new VcsCommandExecutor[0];
        if (!VcsManager.getDefault().showCustomizer(command)) return new VcsCommandExecutor[0];
        if (command.getFiles() != null) {
            CommandTask task = command.execute();
            if (task instanceof UserCommandTask) {
                return new VcsCommandExecutor[] { ((UserCommandTask) task).getExecutor() };
            } else if (task instanceof WrappingCommandTask) {
                UserCommandTask[] tasks = ((WrappingCommandTask) task).getTasks();
                VcsCommandExecutor[] executors = new VcsCommandExecutor[tasks.length];
                for (int i = 0; i < executors.length; i++) {
                    executors[i] = tasks[i].getExecutor();
                }
                return executors;
            }
        }
        return new VcsCommandExecutor[0];
    }

    /** Make sure, that the files are saved. If not, save them.
     * Synchronized, so that we do not try to save the same objects twice in parallel.
     * @param fos the collection of FileObjects which or under which modified
     *        files are saved.
     */
    public static synchronized void assureFilesSaved(Collection fos) {
        DataObject[] modified = DataObject.getRegistry().getModified();
        if (modified.length == 0) return ;
        Set files = new HashSet(fos);
        List folders = null;
        for (Iterator it = fos.iterator(); it.hasNext(); ) {
            FileObject fo = (FileObject) it.next();
            if (fo == null) {
                files.remove(fo);
                continue;
            }
            if (fo.isFolder()) {
                if (folders == null) {
                    folders = new ArrayList();
                }
                folders.add(fo);
                files.remove(fo);
            }
        }
        if (folders == null) {
            folders = Collections.EMPTY_LIST;
        }
        boolean wasSaved = false;
        for (int i = 0; i < modified.length; i++) {
            Set modifiedFiles = modified[i].files();
            for (Iterator modIt = modifiedFiles.iterator(); modIt.hasNext(); ) {
                FileObject modifiedFile = (FileObject) modIt.next();
                if (shouldBeSaved(modifiedFile, files, folders)) {
                    Node.Cookie cake = modified[i].getCookie(SaveCookie.class);
                    try {
                        if (cake != null) {
                            ((SaveCookie) cake).save();
                            wasSaved = true;
                        }
                    } catch (java.io.IOException exc) {
                        ErrorManager.getDefault().notify(exc);
                    }
                    break;
                }
            }
        }
        if (wasSaved) {
            // If we saved some data, we need to wait at least one second.
            // This will assure, that any further command, that will modify
            // the conent of a saved file will actually change the modification
            // time (time resolution is ~1s). See issue #36065 for details.
            try {
                Thread.currentThread().sleep(1000);
            } catch (InterruptedException iex) {}
        }
    }
    
    private static boolean shouldBeSaved(FileObject modifiedFile, Collection files, Collection folders) {
        FileSystem fs = (FileSystem) modifiedFile.getAttribute(VcsAttributes.VCS_NATIVE_FS);
        if (fs == null) return false;
        String path = (String) modifiedFile.getAttribute(VcsAttributes.VCS_NATIVE_PACKAGE_NAME_EXT);
        modifiedFile = fs.findResource(path);
        if (modifiedFile == null) return false;
        if (files.contains(modifiedFile)) {
            return true;
        } else {
            for (Iterator it = folders.iterator(); it.hasNext(); ) {
                FileObject folder = (FileObject) it.next();
                if (isAFileInAFolder(folder, modifiedFile)) {
                    return true;
                }
            }
            return false;
        }
    }
    
    private static boolean isAFileInAFolder(FileObject folder, FileObject file) {
        boolean isIn = false;
        boolean canBeIn = true;
        while (!isIn && canBeIn) {
            file = file.getParent();
            if (file == null) break;
            if (folder.equals(file)) {
                isIn = true;
                break;
            } else {
                canBeIn = file.getPath().startsWith(folder.getPath());
            }
        }
        return isIn;
    }

    /**
     * Add files.
     * @param dd the data object from which the files are read.
     * @param res the <code>Table</code> of path and FileObject pairs.
     * @param all whether to add unimportant files as well
     * @param fileSystem the file system
     */
    public static void addImportantFiles(Collection fos, Table res, boolean all, VcsFileSystem fileSystem) {
        addImportantFiles(fos, res, all, fileSystem, false);
    }
    
    /**
     * Add files.
     * @param dd the data object from which the files are read.
     * @param res the <code>Table</code> of path and FileObject pairs.
     * @param all whether to add unimportant files as well
     * @param fileSystem the file system
     * @param doNotTestFS if true, FileObjects will not be tested whether they belong to VcsFileSystem
     */
    public static void addImportantFiles(Collection fos, Table res, boolean all, VcsFileSystem fileSystem, boolean doNotTestFS) {
        for(Iterator it = fos.iterator(); it.hasNext(); ) {
            FileObject ff = (FileObject) it.next();
            try {
                if (!doNotTestFS && ff.getFileSystem() != fileSystem)
                    continue;
            } catch (FileStateInvalidException exc) {
                continue;
            }
            String fileName = ff.getPath();
            //VcsFile file = fileSystem.getCache().getFile(fileName);
            //D.deb("file = "+file+" for "+fileName);
            //if (file == null || file.isImportant()) {
            if (all || fileSystem.isImportant(fileName)) {
                //D.deb(fileName+" is important");
                res.put(fileName, ff);
            }
            Set[] scheduled = (Set[]) ff.getAttribute(VcsAttributes.VCS_SCHEDULED_FILES_ATTR);
            if (scheduled != null && scheduled[0] != null) {
                for (Iterator sit = scheduled[0].iterator(); sit.hasNext(); ) {
                    String name = (String) sit.next();
                    res.put(name, null);
                }
            }
            //else D.deb(fileName+" is NOT important");
        }
    }
    

     /** Remove the files for which the command is disabled */
     private static Table removeDisabledWithTurbo(VcsFileSystem fileSystem, Table files, VcsCommand cmd) {
         String disabledStatus = (String) cmd.getProperty(VcsCommand.PROPERTY_DISABLED_ON_STATUS);
         if (disabledStatus == null) return files;
         Table remaining = new Table();
         for (Enumeration en = files.keys(); en.hasMoreElements(); ) {
             String name = (String) en.nextElement();
             FileObject fo = fileSystem.findResource(name);
             FileProperties fprops = Turbo.getMeta(fo);
             String status = FileProperties.getStatus(fprops);
             boolean disabled = VcsUtilities.isSetContainedInQuotedStrings(
                 disabledStatus, Collections.singleton(status));
             if (!disabled) {
                 remaining.put(name, files.get(name));
             }
         }
         return remaining;
     }


    /** Remove the files for which the command is disabled */
    private static Table removeDisabled(FileStatusProvider statusProvider, Table files, VcsCommand cmd) {
        if (statusProvider == null) return files;
        String disabledStatus = (String) cmd.getProperty(VcsCommand.PROPERTY_DISABLED_ON_STATUS);
        if (disabledStatus == null) return files;
        Table remaining = new Table();
        for (Enumeration en = files.keys(); en.hasMoreElements(); ) {
            String name = (String) en.nextElement();
            String status = statusProvider.getFileStatus(name);
            boolean disabled = VcsUtilities.isSetContainedInQuotedStrings(
                disabledStatus, Collections.singleton(status));
            if (!disabled) {
                remaining.put(name, files.get(name));
            }
        }
        return remaining;
    }

    /**
     * Perform the specified VCS command on a collection of FileObjects.
     * Can handle also LIST and LIST_SUB commands.
     */
    public static void performVcsCommand(VcsCommand cmd, VcsFileSystem fileSystem,
                                         Collection fileObjects, boolean isExpert) {
        performVcsCommand(cmd, fileSystem, fileObjects, isExpert, null);
    }

    private static String getPackageNameSlashes(FileObject fo) {
        String path = fo.getPath();
        int i = path.lastIndexOf('.');
        if (i != -1 && i > path.lastIndexOf('/')) {
            path = path.substring(0, i);
        }
        return path;
    }

    /**
     * Perform the specified VCS command on a collection of FileObjects.
     * Can handle also LIST and LIST_SUB commands.
     */
    public static void performVcsCommand(VcsCommand cmd, VcsFileSystem fileSystem,
                                         Collection fileObjects, boolean isExpert,
                                         Hashtable additionalVars) {
        boolean processAll = VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_PROCESS_ALL_FILES) || fileSystem.isProcessUnimportantFiles();
        Table files = new Table();
        //boolean refreshDone = false;
        addImportantFiles(fileObjects, files, processAll, fileSystem, true);
        files = removeDisabledWithTurbo(fileSystem, files, cmd);
        if (VcsCommand.NAME_REFRESH.equals(cmd.getName()) ||
            (VcsCommand.NAME_REFRESH + VcsCommand.NAME_SUFFIX_OFFLINE).equals(cmd.getName())) {
            ArrayList paths = new ArrayList();
            for (Iterator it = files.values().iterator(); it.hasNext(); ) {
                FileObject fo = (FileObject) it.next();
                if (fo == null) continue;
                String path = getPackageNameSlashes(fo);
                if (!paths.contains(path)) {
                    doList(fileSystem, path);  // TODO how does it differ from CommandExecutorSupport.doRefresh(fileSystem, path, false); bellow
                    paths.add(path);
                }
            }
        } else if (VcsCommand.NAME_REFRESH_RECURSIVELY.equals(cmd.getName()) ||
                   (VcsCommand.NAME_REFRESH_RECURSIVELY + VcsCommand.NAME_SUFFIX_OFFLINE).equals(cmd.getName())) {
            ArrayList paths = new ArrayList();
            for (Iterator it = files.values().iterator(); it.hasNext(); ) {
                FileObject fo = (FileObject) it.next();
                if (fo == null) continue;
                String path = getPackageNameSlashes(fo);
                if (!paths.contains(path)) {
                    CommandExecutorSupport.doRefresh(fileSystem, path, true);
                    paths.add(path);
                }
            }
        } else if (files.size() > 0) {
            if (isExpert && !fileSystem.isExpertMode()) {
                if (additionalVars == null) additionalVars = new Hashtable();
                additionalVars.put(VcsFileSystem.VAR_CTRL_DOWN_IN_ACTION, Boolean.TRUE.toString());
            }
            doCommand(files, cmd, additionalVars, fileSystem);
        }
    }

}

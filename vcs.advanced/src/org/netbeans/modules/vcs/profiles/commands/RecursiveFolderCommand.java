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

package org.netbeans.modules.vcs.profiles.commands;

import java.io.*;
import java.util.*;
import org.netbeans.api.queries.SharabilityQuery;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.VcsAction;
import org.netbeans.modules.vcscore.turbo.*;
import org.netbeans.modules.vcscore.cmdline.ExecuteCommand;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.util.*;
//import org.netbeans.modules.vcscore.cmdline.exec.*;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;

import org.netbeans.modules.vcscore.cmdline.UserCommandTask;

/**
 * This class works as a wrapper for recursive commands which needs to perform
 * some tasks in each subdirectory of a current directory.
 * Due to problems with filesystem.attributes in PVCS, the command is called explicitly
 * for EACH file in the directory excluding filesystem.attributes
 * @author  Martin Entlicher
 */
public class RecursiveFolderCommand extends Object implements VcsAdditionalCommand {

    private static final String NBATTRS = ".nbattrs"; // NOI18N
    private static final String NBINTDB = ".nbintdb"; // NOI18N
    
    public static final String LOCAL_DIR_ONLY = "-l"; // NOI18N
    public static final String PRINT_OUTPUT = "-o"; // NOI18N
    public static final String PRINT_DEBUG = "-d"; // NOI18N
    public static final String PRINT_FILE_PATH_A = "-fa"; // NOI18N
    public static final String PRINT_FILE_PATH_R = "-fr"; // NOI18N
    public static final String PRINT_FILE_PATH_W = "-fw"; // NOI18N
    public static final String PRINT_FILE_PATH_C = "-fc"; // NOI18N
    
    private VcsFileSystem fileSystem = null;
    private CommandOutputListener stdoutNRListener;
    private CommandOutputListener stderrNRListener;
    private CommandDataOutputListener stdoutListener;

    /** -l option passed. */
    private boolean localOnly;
    private boolean printOutput;
    private boolean printDebug;
    private boolean findFileResource;
    private boolean printFilesToProcess;
    private String filesToProcessSubstractPath;
    
    private String rootDir = "";
    //private Set lockedFileObjects = new HashSet();

    /** Creates new RecursiveFolderCommand */
    public RecursiveFolderCommand() {
    }
    
    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
    

    private void fillDirFilesWithTurbo(Table files, FileObject dir, CommandInfo info, boolean recursive) {
        String path = dir.getPath();
        if (printDebug) stdoutListener.outputData(new String[] { "Collecting files for command "+info.cmd.getName()+" in folder '"+path+"'" });
        FilenameFilter fsFilter = fileSystem.getFileFilter();
        File dirFile = FileUtil.toFile(dir);
        FileProperties fprops = Turbo.getMeta(dir);
        if (info.canRunOnFolders) {
            if ((info.canRunOnRoot || !(path.length() == 0 || ".".equals(path))) && info.canRunOnStatus(fprops.getStatus())) {
                if (printDebug) stdoutListener.outputData(new String[] { " Processing folder = "+path });
                files.put(path, (findFileResource) ? fileSystem.findResource(path) : null);
            }
        }
        if (path.length() > 0) path += "/";
        if (info.canRunOnFiles) {
            FileObject[] subFiles = TurboUtil.listFiles(dir);
            IgnoreList ilist = IgnoreList.forFolder(dir);
            for (int i = 0; i < subFiles.length; i++) {
                FileObject fo = subFiles[i];
                FileProperties subfprops = Turbo.getMeta(fo);
                String status = FileProperties.getStatus(subfprops);   // TODO revisit, it may return [unknown] status
                if (info.canRunOnStatus(status) &&
                    ilist.isIgnored(fo.getNameExt()) == false &&
                    fsFilter.accept(dirFile, fo.getNameExt())) {
                    if (NBATTRS.equals(fo.getNameExt())) continue;
                    if (NBINTDB.equals(fo.getNameExt())) continue;
                    String filePath = path + fo.getNameExt();
                    if (printDebug) stdoutListener.outputData(new String[] { " Processing file = "+filePath });
                    files.put(filePath, (findFileResource) ? fo : null);
                }
            }
        }

        if (recursive) {
            FileObject[] subDirs = TurboUtil.listFolders(dir);
            IgnoreList ilist = IgnoreList.forFolder(dir);
            for (int i = 0; i < subDirs.length; i++) {
                FileObject fo = subDirs[i];
                if (ilist.isIgnored(fo.getNameExt()) == false &&
                    fsFilter.accept(dirFile, fo.getNameExt())) {

                    FileProperties subprops = Turbo.getMeta(fo);
                    String status = FileProperties.getStatus(subprops); // TODO revisit, it may return [unknown] status
                    if (SharabilityQuery.getSharability(FileUtil.toFile(fo)) != SharabilityQuery.NOT_SHARABLE) {
                    //if (info.canRunOnStatus(status)) { -- The status check will be performed inside the recursion
                        fillDirFilesWithTurbo(files, fo, info, recursive);
                    }
                }
            }
        }
    }

    /** Find out if native command supports recursion otherwise emulate it by iterating. */
    private boolean runCommandsRecursivelyWithTurbo(FileObject dir, Collection cmdInfos) throws InterruptedException {
        ArrayList realRecursiveCommands = new ArrayList();
        ArrayList somewhatRecursiveCommands = new ArrayList();
        if (printFilesToProcess) {
            return printFilesRecursivelyWithTurbo(dir, cmdInfos);
        }
        for (Iterator it = cmdInfos.iterator(); it.hasNext(); ) {
            CommandInfo info = (CommandInfo) it.next();
            if (!localOnly && info.canRunOnMultipleFiles && !info.canRunOnMultipleFilesInFolder) {
                realRecursiveCommands.add(info);
            } else {
                somewhatRecursiveCommands.add(info);
            }
        }
        //System.out.println("runCommandsRecursively("+dir.getName()+", "+cmdInfos.size()+"): realRecursiveCommands = "+realRecursiveCommands+", somewhatRecursiveCommands = "+somewhatRecursiveCommands);
        boolean status = true;
        if (realRecursiveCommands.size() > 0) {
            status = runCommandsReallyRecursivelyWithTurbo(dir, realRecursiveCommands);
        }
        if (somewhatRecursiveCommands.size() > 0) {
            status &= runCommandsSomewhatRecursivelyWithTurbo(dir, somewhatRecursiveCommands);
        }
        return status;
    }

    private boolean runCommandsReallyRecursivelyWithTurbo(FileObject dir, Collection cmdInfos) throws InterruptedException {
        //String path = dir.getAbsolutePath().substring(fileSystem.getFile("").getAbsolutePath());
        //while (path.startsWith("/")) path = path.substring(1);
        //FileObject fo = fileSystem.findResource(path);
        //files.put(path, fo);
        boolean status = true;
        CommandsPool cPool = fileSystem.getCommandsPool();
        for (Iterator it = cmdInfos.iterator(); it.hasNext(); ) {
            CommandInfo info = (CommandInfo) it.next();
            Table files = new Table();
            fillDirFilesWithTurbo(files, dir, info, true);
            VcsCommandExecutor[] executors;
            if (printOutput) {
                executors = VcsAction.doCommand(files, info.cmd, info.vars, fileSystem,
                    stdoutNRListener, stderrNRListener, null, null);
            } else {
                executors = VcsAction.doCommand(files, info.cmd, info.vars, fileSystem);
            }
            for (int i = 0; i < executors.length; i++) {
                try {
                    cPool.waitToFinish(executors[i]);
                } catch (InterruptedException iexc) {
                    for (int j = i; j < executors.length; j++) {
                        cPool.kill(executors[j]);
                    }
                    throw iexc;
                }
                status &= (executors[i].getExitStatus() == VcsCommandExecutor.SUCCEEDED);
            }
        }
        return status;
    }

    /** Emulates recursive operations for commands that does not support it natively. */
    private boolean runCommandsSomewhatRecursivelyWithTurbo(FileObject dir, Collection cmdInfos) throws InterruptedException {
        //System.out.println("runCommandsSomewhatRecursively("+dir+"), localOnly = "+localOnly);
        CommandsPool cPool = fileSystem.getCommandsPool();
        FilenameFilter fsFilter = fileSystem.getFileFilter();
        IgnoreList ilist = IgnoreList.forFolder(dir);

        boolean status = true;
        for (Iterator it = cmdInfos.iterator(); it.hasNext(); ) {
            CommandInfo info = (CommandInfo) it.next();
            Table files = new Table();
            fillDirFilesWithTurbo(files, dir, info, false);
            VcsCommandExecutor[] executors;
            if (files.size() > 0) {
                if (printOutput) {
                    executors = VcsAction.doCommand(files, info.cmd, info.vars, fileSystem,
                        stdoutNRListener, stderrNRListener, null, null);
                } else {
                    executors = VcsAction.doCommand(files, info.cmd, info.vars, fileSystem);
                }
                //System.out.println("doCommand("+files+", "+info.cmd.getName());
            } else {
                executors = new VcsCommandExecutor[0];
                //System.out.println("do no Command("+files+", "+info.cmd.getName());
            }

            // join spawned command tasks

            for (int i = 0; i < executors.length; i++) {
                try {
                    cPool.waitToFinish(executors[i]);
                } catch (InterruptedException iexc) {
                    for (int j = i; j < executors.length; j++) {
                        fileSystem.getCommandsPool().kill(executors[j]);
                    }
                    throw iexc;
                }
                status &= (executors[i].getExitStatus() == VcsCommandExecutor.SUCCEEDED);
            }

            // recursive descent

            if (!localOnly) {
                FileObject[] subDirs = TurboUtil.listFolders(dir);
                File dirFile = FileUtil.toFile(dir);
                for (int i = 0; i < subDirs.length; i++) {
                    FileObject fo = subDirs[i];
                    if (ilist.isIgnored(fo.getNameExt()) == false &&
                        fsFilter.accept(dirFile, fo.getNameExt())) {
                        FileProperties fprops = Turbo.getMeta(fo);
                        String folderStatus = FileProperties.getStatus(fprops);
                        if (SharabilityQuery.getSharability(FileUtil.toFile(fo)) != SharabilityQuery.NOT_SHARABLE) {
                        //if (info.canRunOnStatus(status)) { -- The status check will be performed inside the recursion
                            status &= runCommandsSomewhatRecursivelyWithTurbo(fo, cmdInfos);
                        }
                    }
                }
            }

        }
        return status;
    }


    private boolean printFilesRecursivelyWithTurbo(FileObject dir, Collection cmdInfos) {
        CommandInfo info;
        if (cmdInfos.size() > 0) info = (CommandInfo) cmdInfos.iterator().next();
        else info = new CommandInfo(new org.netbeans.modules.vcscore.cmdline.UserCommand(), null);
        Table files = new Table();
        fillDirFilesWithTurbo(files, dir, info, !localOnly);
        File root = fileSystem.getFile("");
        int dirPathLength = dir.getPath().length();
        for (Iterator it = files.keySet().iterator(); it.hasNext(); ) {
            String fsPath = (String) it.next();
            String filePath;
            if (filesToProcessSubstractPath.equals(PRINT_FILE_PATH_A)) {
                filePath = new File(root, fsPath).getAbsolutePath();
            } else if (filesToProcessSubstractPath.equals(PRINT_FILE_PATH_R) ||
                       filesToProcessSubstractPath.equals(PRINT_FILE_PATH_W)) {
                filePath = fsPath;
            } else { // relative with respect to current dir
                if (fsPath.length() > dirPathLength) {
                    int pathIndex = dirPathLength;
                    if (dirPathLength > 0) pathIndex++; // To skip the path separator
                    filePath = fsPath.substring(pathIndex);
                } else {
                    filePath = ""; // NOI18N
                }
            }
            stdoutListener.outputData(new String[] { filePath});
        }
        return true;
    }
    
    private boolean runCommandsRecursively(String path, Collection cmdInfos) throws InterruptedException {
        FileObject fo = fileSystem.findResource(path);
        FileProperties attrs = Turbo.getMeta(fo);
        return runCommandsRecursivelyWithTurbo(fo, cmdInfos);
    }

    /**
     * This method is used to execute the command.
     * The provided command is executed recursively for all subfolders.
     * The command is executed only on non-empty folders.
     *
     * @param vars the variables that can be passed to the command
     * @param args the command line parametres passed to it in properties
     * @param stdoutNRListener listener of the standard output of the command
     * @param stderrNRListener listener of the error output of the command
     * @param stdoutListener listener of the standard output of the command which
     *                      satisfies regex <CODE>dataRegex</CODE>
     * @param dataRegex the regular expression for parsing the standard output
     * @param stderrListener listener of the error output of the command which
     *                      satisfies regex <CODE>errorRegex</CODE>
     * @param errorRegex the regular expression for parsing the error output
     * @return true if the command was succesfull
     *        false if some error occured.
     */
    public boolean exec(Hashtable vars, String[] args, CommandOutputListener stdoutNRListener,
                        CommandOutputListener stderrNRListener,
                        CommandDataOutputListener stdoutListener, String dataRegex,
                        CommandDataOutputListener stderrListener, String errorRegex) {
        this.stdoutNRListener = stdoutNRListener;
        this.stderrNRListener = stderrNRListener;
        this.stdoutListener = stdoutListener;
        if (args.length == 0 || (LOCAL_DIR_ONLY.equals(args[0]) || PRINT_OUTPUT.equals(args[0])) && args.length <= 1) {
            stderrNRListener.outputLine("Expecting a command as an argument. "+               // NOI18N
                "Can specify:\n"+
                "-l to run non-recursively,\n"+
                "-o to print commands' output,\n"+
                "-f[x] to write files into standard data output instead of executing commands on them,\n"+
                " where 'x' is one of: 'a', 'r', 'w', 'c'. Specify absolute path,"+
                " relative with respect to FS root, relative with respect to the"+
                " working directory and relative with respect to the current directory.\n"+
                "-d to print debug messages to data output (should not be used with -f[x])."); // NOI18N
            return true;
        }
        localOnly = false;
        printOutput = false;
        printDebug = false;
        findFileResource = true;
        printFilesToProcess = false;
        filesToProcessSubstractPath = null;
        boolean moreOptions;
        do {
            moreOptions = false;
            if (LOCAL_DIR_ONLY.equals(args[0])) {
                moreOptions = true;
                localOnly = true;
            } else if (PRINT_OUTPUT.equals(args[0])) {
                moreOptions = true;
                printOutput = true;
            } else if (PRINT_DEBUG.equals(args[0])) {
                moreOptions = true;
                printDebug = true;
            } else if (PRINT_FILE_PATH_A.equals(args[0]) || PRINT_FILE_PATH_C.equals(args[0]) ||
                       PRINT_FILE_PATH_R.equals(args[0]) || PRINT_FILE_PATH_W.equals(args[0])) {
                moreOptions = true;
                filesToProcessSubstractPath = args[0];
            }
            if (moreOptions) {
                String[] newArgs = new String[args.length - 1];
                System.arraycopy(args, 1, newArgs, 0, newArgs.length);
                args = newArgs;
            }
        } while (moreOptions);
        printFilesToProcess = filesToProcessSubstractPath != null;
        if (printFilesToProcess) findFileResource = false;
        
        ArrayList cmdInfos = createCommandInfos(args, vars);
        if (cmdInfos.size() == 0 && !printFilesToProcess) return true;
        //setFilteredFilesOut(vars);
        Collection processingFiles = ExecuteCommand.createProcessingFiles(fileSystem, vars);
        boolean success = true;
        if (printDebug) stdoutListener.outputData(new String[] { "Selected on "+processingFiles.size()+" files" });
        try {
            for (Iterator it = processingFiles.iterator(); it.hasNext() && success; ) {
                String path = (String) it.next();
                if (printDebug) stdoutListener.outputData(new String[] { "Processing in "+path });
                /*
                FileObject fo = fileSystem.findResource(path);
                if (fo != null) {
                    for (Enumeration enum = fo.getChildren(true); enum.hasMoreElements(); ) {
                        lockedFileObjects.add(enum.nextElement());
                    }
                }
                 */
                success &= runCommandsRecursively(path, cmdInfos);
            }
        } catch (InterruptedException iexc) {
            Thread.currentThread().interrupt();
        }
        //lockedFileObjects.clear();
        return success;
        /*
        String path = (String) vars.get("PATH");
        path = path.replace(java.io.File.separatorChar, '/');
        if (".".equals(path)) path = "";
        FileObject dir = fileSystem.findResource(path);
        return runCommandRecursively(dir, vars, fileSystem.getCommand(cmd));
         */
    }
    
    private ArrayList createCommandInfos(String[] cmdNames, Hashtable vars) {
        ArrayList cmdInfos = new ArrayList();
        for (int i = 0; i < cmdNames.length; i++) {
            VcsCommand cmd = fileSystem.getCommand(cmdNames[i]);
            if (cmd != null) {
                CommandInfo info = new CommandInfo(cmd, new Hashtable(vars));
                cmdInfos.add(info);
            } else {
                stderrNRListener.outputLine("Can not find command '"+cmdNames[i]+"'");
            }
        }
        return cmdInfos;
    }
    
    private static class CommandInfo extends Object {
        
        public VcsCommand cmd;
        public Hashtable vars;
        public boolean canRunOnFiles;
        public boolean canRunOnFolders;
        public boolean canRunOnRoot;
        public boolean canRunOnMultipleFiles;
        public boolean canRunOnMultipleFilesInFolder;
        public boolean ignoresFail;
        
        private HashSet disabledStates = null;
        
        public CommandInfo(VcsCommand cmd, Hashtable vars) {
            this.cmd = cmd;
            this.vars = vars;
            vars.put(UserCommandTask.VAR_USE_PARENT_VISUALIZER, "true");
            canRunOnFiles = VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_ON_FILE);
            canRunOnFolders = VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_ON_DIR);
            canRunOnRoot = VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_ON_ROOT);
            canRunOnMultipleFiles = VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_RUN_ON_MULTIPLE_FILES);
            canRunOnMultipleFilesInFolder = VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_RUN_ON_MULTIPLE_FILES_IN_FOLDER);
            ignoresFail = VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_IGNORE_FAIL);
            String disabledStatesStr = (String) cmd.getProperty(VcsCommand.PROPERTY_DISABLED_ON_STATUS);
            if (disabledStatesStr != null) {
                String[] disabledStatesArr = VcsUtilities.getQuotedStrings(disabledStatesStr);
                if (disabledStatesArr.length > 0) {
                    disabledStates = new HashSet(Arrays.asList(disabledStatesArr));
                }
            }
        }

        /**
         * Check whether given status is not on command's blacklist.
         * @return false if for sure cannot handle files with given status
         */
        public boolean canRunOnStatus(String status) {
            if (disabledStates == null) {
                return true;
            } else {
                return !disabledStates.contains(status);
            }
        }
        
    }
    
}

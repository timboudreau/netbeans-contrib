/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.profiles.commands;

import java.io.*;
import java.util.*;

import org.openide.filesystems.FileObject;
//import org.openide.util.enum.AlterEnumeration;

import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.VcsAction;
import org.netbeans.modules.vcscore.cache.CacheFile;
import org.netbeans.modules.vcscore.cache.CacheDir;
import org.netbeans.modules.vcscore.cache.CacheHandler;
import org.netbeans.modules.vcscore.cache.FileSystemCache;
import org.netbeans.modules.vcscore.caching.VcsCacheFile;
import org.netbeans.modules.vcscore.caching.VcsCacheDir;
import org.netbeans.modules.vcscore.cmdline.ExecuteCommand;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.util.*;
//import org.netbeans.modules.vcscore.cmdline.exec.*;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;

import org.netbeans.modules.vcs.advanced.CommandLineVcsFileSystem;

/**
 * This class works as a wrapper for recursive commands which needs to perform
 * some tasks in each subdirectory of a current directory.
 * Due to problems with filesystem.attributes in PVCS, the command is called explicitly
 * for EACH file in the directory excluding filesystem.attributes
 * @author  Martin Entlicher
 */
public class RecursiveFolderCommand extends Object implements VcsAdditionalCommand {

    private static final String NBATTRS = ".nbattrs"; // NOI18N
    
    public static final String LOCAL_DIR_ONLY = "-l"; // NOI18N
    public static final String PRINT_OUTPUT = "-o"; // NOI18N
    public static final String PRINT_DEBUG = "-d"; // NOI18N
    
    private VcsFileSystem fileSystem = null;
    private FileSystemCache cache;
    private CommandOutputListener stdoutNRListener;
    private CommandOutputListener stderrNRListener;
    private CommandDataOutputListener stdoutListener;
    private CommandDataOutputListener stderrListener;
    private String dataRegex;
    private String errorRegex;
    private Collection filteredFilesOut = null;
    private boolean filteredFilesOutCaseInsensitive = false;
    
    private boolean localOnly;
    private boolean printOutput;
    private boolean printDebug;
    
    private String rootDir = "";
    private Set lockedFileObjects = new HashSet();

    /** Creates new RecursiveFolderCommand */
    public RecursiveFolderCommand() {
    }
    
    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
    
    /*
    private String getInitDir(Hashtable vars) {
        String dir;
        this.rootDir = (String) vars.get("ROOTDIR"); // NOI18N
        if (rootDir == null) {
            rootDir = "."; // NOI18N
        }
        dir = (String) vars.get("PATH"); // NOI18N
        if (dir == null) {
            dir = ""; // NOI18N
        }
        String module = (String) vars.get("MODULE"); // NOI18N
        //D.deb("rootDir = "+rootDir+", module = "+module+", dir = "+dir); // NOI18N
        if (dir.equals("")) { // NOI18N
            dir = rootDir;
            if (module != null && module.length() > 0) dir += File.separator + module;
        } else {
            if (module == null)
                dir = rootDir + File.separator + dir;
            else
                dir = rootDir + File.separator + module + File.separator + dir;
        }
        if (dir.charAt(dir.length() - 1) == File.separatorChar) {
            dir = dir.substring(0, dir.length() - 1);
        }
        //D.deb("dir="+dir); // NOI18N
        return dir;
    }
     */
    
    /**
     * Get recursively all subdirectories inside the directory.
     * @return the array of absolute pathnames of all subdirectories.
     *
    private File[] getFiles(String dir) {
        File dirFile = new File(dir);
        if (!dirFile.exists() || !dirFile.isDirectory()) return null;
        File[] subfiles = dirFile.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory() && !filteredFilesOut.contains((filteredFilesOutCaseInsensitive)
                                                                        ? file.getName().toUpperCase()
                                                                        : file.getName());
            }
        });
        return subfiles;
    }
    
    private String[] getChildren(String folder) {
        return fileSystem.children(folder);
    }
     */

    /*
    private CommandRunnable getExternalCommand(Hashtable vars, String cmd) {
        String prepared = Variables.expand(vars, cmd, true);
        //System.out.println("prepared = "+prepared);
        final ExternalCommand ec = new ExternalCommand(prepared);
        String input = (String) vars.get("INPUT");
        if (input == null) input = "";
        long timeout = ((Long) vars.get("TIMEOUT")).longValue();
        ec.setTimeout(timeout);
        ec.setInput(input);
        if (stdoutNRListener != null) ec.addStdoutNoRegexListener(stdoutNRListener);
        if (stderrNRListener != null) ec.addStderrNoRegexListener(stderrNRListener);
        if (stdoutListener != null) {
            try {
                ec.addStdoutRegexListener(stdoutListener, dataRegex);
            } catch(BadRegexException exc) {
                org.openide.TopManager.getDefault().notifyException(exc);
            }
        }
        if (stderrListener != null) {
            try {
                ec.addStderrRegexListener(stderrListener, errorRegex);
            } catch(BadRegexException exc) {
                org.openide.TopManager.getDefault().notifyException(exc);
            }
        }
        CommandRunnable command = new CommandRunnable(ec);
        return command;
    }
     */
    
    /*
    private void setFilteredFilesOut(final Hashtable vars) {
        String filteredFiles = (String) vars.get(CommandLineVcsFileSystem.VAR_LOCAL_FILES_FILTERED_OUT);
        if (filteredFiles != null) {
            String[] files = VcsUtilities.getQuotedStrings(filteredFiles);
            filteredFilesOut = new HashSet(Arrays.asList(files));
            filteredFilesOutCaseInsensitive = VcsFileSystem.VAR_FALSE.equals(vars.get(CommandLineVcsFileSystem.VAR_LOCAL_FILES_FILTERED_OUT_CASE_SENSITIVE));
            if (filteredFilesOutCaseInsensitive) {
                Enumeration upperCaseFiles = new AlterEnumeration(Collections.enumeration(filteredFilesOut)) {
                    protected Object alter(Object o) {
                        return ((String) o).toUpperCase();
                    }
                };
                filteredFilesOut = new HashSet();
                while(upperCaseFiles.hasMoreElements()) filteredFilesOut.add(upperCaseFiles.nextElement());
            }
        }
        if (filteredFilesOut == null) {
            filteredFilesOut = Collections.singleton(IGNORE_FILE);
        } else {
            filteredFilesOut.add((filteredFilesOutCaseInsensitive) ? IGNORE_FILE.toUpperCase() : IGNORE_FILE);
        }
    }
     */
    
    private boolean runCommandRecursively(final FileObject dir, final Hashtable vars, final VcsCommand cmd) throws InterruptedException {
        /*
        String relWork;
        if (dir.length() > rootDir.length()) {
            relWork = dir.substring(rootDir.length() + 1); // the folder relative to the root directory.
        } else {
            relWork = ""; // the folder relative to the root directory.
        }
        while(relWork.length() > 0 && relWork.charAt(0) == File.separatorChar) relWork = relWork.substring(1);
        vars.put("RELWORK", relWork); // the folder relative to the root directory.
        //System.out.println("RELWORK = '"+relWork+"'");
        File dirFile = new File(dir);
        final File[] subfiles = dirFile.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return (file.isFile() && !filteredFilesOut.contains((filteredFilesOutCaseInsensitive)
                                                                    ? file.getName().toUpperCase()
                                                                    : file.getName()));
            }
        });
         */
        FileObject[] children = dir.getChildren();
        ArrayList childrenFiles = new ArrayList();
        ArrayList childrenFolders = new ArrayList();
        for (int i = 0; i < children.length; i++) {
            FileObject fo = children[i];
            if (fo.isFolder()) childrenFolders.add(fo);
            else childrenFiles.add(fo);
        }
        Table files = new Table();
        boolean processAll = VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_PROCESS_ALL_FILES) || fileSystem.isProcessUnimportantFiles();
        VcsAction.addImportantFiles(childrenFiles, files, processAll, fileSystem);
        VcsCommandExecutor[] commands = VcsAction.doCommand(files, cmd, vars, fileSystem,
                                                            stdoutNRListener, stderrNRListener, stdoutListener, stderrListener);
        boolean success = true;
        if (!localOnly) {
            for (Iterator it = childrenFolders.iterator(); it.hasNext(); ) {
                FileObject fo = (FileObject) it.next();
                Hashtable newVars = new Hashtable(vars);
                boolean recSuccess = runCommandRecursively(fo, newVars, cmd);
                success = success && recSuccess;
            }
        }
        for (int i = 0; i < commands.length; i++) {
            VcsCommandExecutor vce = commands[i];
            try {
                fileSystem.getCommandsPool().waitToFinish(vce);
            } catch (InterruptedException iexc) {
                for (int j = i; j < commands.length; j++) {
                    fileSystem.getCommandsPool().kill(commands[j]);
                }
                throw iexc;
            }
            success = success && (vce.getExitStatus() == VcsCommandExecutor.SUCCEEDED);
        }
        return success;
    }
    
    private void waitToLoad(VcsCacheDir dir) throws InterruptedException {
        //System.out.println("waitToLoad("+dir+")");
        if (!dir.isIgnoreListSet() && fileSystem.getIgnoreListSupport() != null) {
            dir.setIgnoreList(VcsUtilities.createIgnoreList(dir, dir.getFSPath(), fileSystem.getIgnoreListSupport()));
        }
        //System.out.println(" isLocal() = "+dir.isLocal()+", isLoaded() = "+dir.isLoaded()+", isIgnoreListSet() = "+dir.isIgnoreListSet());
        while (!dir.isLocal() && !dir.isLoaded()) {
            //System.out.println("  isLocal() = "+dir.isLocal()+", isLoaded() = "+dir.isLoaded()+", isIgnoreListSet() = "+dir.isIgnoreListSet());
            Thread.currentThread().sleep(100);
        }
    }
    
    private void waitToLoad(CacheDir dir, boolean recursively) throws InterruptedException {
        if (dir instanceof VcsCacheDir && !((VcsCacheDir) dir).isLoaded()) {
            waitToLoad((VcsCacheDir) dir);
            if (recursively) {
                CacheDir[] subDirs = dir.getSubDirs();
                for (int i = 0; i < subDirs.length; i++) {
                    waitToLoad(subDirs[i], recursively);
                }
            }
        }
    }
    
    private String getFSPath(CacheFile file) {
        String path = file.getAbsolutePath();
        String fsRoot = fileSystem.getFile("").getAbsolutePath();
        path = path.substring(fsRoot.length());
        while (path.startsWith("/")) path = path.substring(1);
        return path;
    }
    
    private boolean canProcessFile(String filePath) {
        if (fileSystem.isImportant(filePath)) return true;
        return fileSystem.isProcessUnimportantFiles();
    }
    
    private void fillDirFiles(Table files, CacheDir dir, CommandInfo info, boolean recursive) {
        String path;
        if (dir instanceof VcsCacheDir) {
            path = ((VcsCacheDir) dir).getFSPath();
        } else {
            path = getFSPath(dir);
        }
        if (printDebug) stdoutListener.outputData(new String[] { "Collecting files for command "+info.cmd.getName()+" in folder '"+path+"'" });
        FilenameFilter fsFilter = fileSystem.getLocalFileFilter();
        File dirFile = new File(dir.getAbsolutePath());
        if (info.canRunOnFolders) {
            if (info.canRunOnStatus(dir.getStatus())) {
                if (printDebug) stdoutListener.outputData(new String[] { " Processing folder = "+path });
                files.put(path, fileSystem.findResource(path));
            }
        }
        if (path.length() > 0) path += "/";
        if (info.canRunOnFiles) {
            CacheFile[] subFiles = dir.getFiles();
            for (int i = 0; i < subFiles.length; i++) {
                if (info.canRunOnStatus(subFiles[i].getStatus()) &&
                    !dir.isIgnored(subFiles[i].getName()) &&
                    fsFilter.accept(dirFile, subFiles[i].getName())) {
                    
                    String filePath = path + subFiles[i].getName();
                    if (printDebug) stdoutListener.outputData(new String[] { " Processing file = "+filePath });
                    files.put(filePath, fileSystem.findResource(filePath));
                }
            }
            // Add local files. Local files are not part of the cache.
            if (info.canRunOnStatus(fileSystem.getStatusProvider().getLocalFileStatus())) {
                String[] localSubFiles = dirFile.list();
                if (localSubFiles != null) {
                    for (int i = 0; i < localSubFiles.length; i++) {
                        if (!dir.isIgnored(localSubFiles[i]) &&
                            fsFilter.accept(dirFile, localSubFiles[i]) &&
                            !NBATTRS.equals(localSubFiles[i])) {
                            
                            String filePath = path + localSubFiles[i];
                            if (canProcessFile(filePath)) {
                                files.put(filePath, fileSystem.findResource(filePath));
                            }
                        }
                    }
                }
            }
        }
        if (recursive) {
            CacheDir[] subDirs = dir.getSubDirs();
            for (int i = 0; i < subDirs.length; i++) {
                if (!dir.isIgnored(subDirs[i].getName()) &&
                    fsFilter.accept(dirFile, subDirs[i].getName())) {
                    
                    fillDirFiles(files, subDirs[i], info, recursive);
                }
            }
        }
    }
    
    private boolean runCommandsRecursively(CacheDir dir, Collection cmdInfos) {
        ArrayList realRecursiveCommands = new ArrayList();
        ArrayList somewhatRecursiveCommands = new ArrayList();
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
            status = runCommandsReallyRecursively(dir, realRecursiveCommands);
        }
        if (somewhatRecursiveCommands.size() > 0) {
            try {
                status &= runCommandsSomewhatRecursively(dir, somewhatRecursiveCommands);
            } catch (InterruptedException iexc) {
                return false;
            }
        }
        return status;
    }
    
    private boolean runCommandsReallyRecursively(CacheDir dir, Collection cmdInfos) {
        try {
            waitToLoad(dir, true);
        } catch (InterruptedException iexc) {
            return false;
        }
        //String path = dir.getAbsolutePath().substring(fileSystem.getFile("").getAbsolutePath());
        //while (path.startsWith("/")) path = path.substring(1);
        //FileObject fo = fileSystem.findResource(path);
        //files.put(path, fo);
        boolean status = true;
        CommandsPool cPool = fileSystem.getCommandsPool();
        for (Iterator it = cmdInfos.iterator(); it.hasNext(); ) {
            CommandInfo info = (CommandInfo) it.next();
            Table files = new Table();
            fillDirFiles(files, dir, info, true);
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
                    return false;
                }
                status &= (executors[i].getExitStatus() == VcsCommandExecutor.SUCCEEDED);
            }
        }
        return status;
    }
    
    private boolean runCommandsSomewhatRecursively(CacheDir dir, Collection cmdInfos) throws InterruptedException {
        //System.out.println("runCommandsSomewhatRecursively("+dir+"), localOnly = "+localOnly);
        waitToLoad(dir, false);
        CommandsPool cPool = fileSystem.getCommandsPool();
        boolean status = true;
        for (Iterator it = cmdInfos.iterator(); it.hasNext(); ) {
            CommandInfo info = (CommandInfo) it.next();
            Table files = new Table();
            fillDirFiles(files, dir, info, false);
            VcsCommandExecutor[] executors;
            if (printOutput) {
                executors = VcsAction.doCommand(files, info.cmd, info.vars, fileSystem,
                    stdoutNRListener, stderrNRListener, null, null);
            } else {
                executors = VcsAction.doCommand(files, info.cmd, info.vars, fileSystem);
            }
            //System.out.println("doCommand("+files+", "+info.cmd.getName());
            if (!localOnly) {
                CacheDir[] subDirs = dir.getSubDirs();
                for (int i = 0; i < subDirs.length; i++) {
                    if (cache != null) {
                        CacheDir subDir = (CacheDir) cache.getCacheFile(new File(subDirs[i].getAbsolutePath(), "testFile"), CacheHandler.STRAT_DISK_OR_REFRESH);
                    }
                    status &= runCommandsSomewhatRecursively(subDirs[i], cmdInfos);
                }
            }
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
        }
        return status;
    }
    
    private boolean runCommandsRecursively(FileObject dir, Collection cmdInfos) {
        boolean status = true;
        for (Iterator it = cmdInfos.iterator(); it.hasNext(); ) {
            CommandInfo info = (CommandInfo) it.next();
            try {
                status &= runCommandRecursively(dir, info.vars, info.cmd);
            } catch (InterruptedException iexc) {
                return false;
            }
        }
        return status;
    }
    
    private boolean runCommandsRecursively(String path, Collection cmdInfos) {
        //System.out.println("runCommandsRecursively("+path+", "+cmdInfos.size());
        cache = CacheHandler.getInstance().getCache(fileSystem.getCacheIdStr());
        if (cache != null) {
            //System.out.println("getting cache file "+fileSystem.getFile(path));
            CacheDir cacheDir = (CacheDir) cache.getCacheFile(fileSystem.getFile(path), CacheHandler.STRAT_DISK_OR_REFRESH);
            //System.out.println("cacheDir = "+cacheDir);
            if (cacheDir == null) return true;
            return runCommandsRecursively(cacheDir, cmdInfos);
        } else {
            FileObject dir = fileSystem.findResource(path);
            return runCommandsRecursively(dir, cmdInfos);
        }
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
        this.stderrListener = stderrListener;
        this.dataRegex = dataRegex;
        this.errorRegex = errorRegex;
        //String dir = getInitDir(vars);
        //String cmd = VcsUtilities.array2string(args);
        //String cmd = args[0];
        if (args.length == 0 || (LOCAL_DIR_ONLY.equals(args[0]) || PRINT_OUTPUT.equals(args[0])) && args.length <= 1) {
            stderrNRListener.outputLine("Expecting a command as an argument.\n"+               // NOI18N
                "Can specify '-l' to run non-recursively, -o to print commands' output and -d to print debug messages to data output."); // NOI18N
            return true;
        }
        localOnly = false;
        printOutput = false;
        printDebug = false;
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
            }
            if (moreOptions) {
                String[] newArgs = new String[args.length - 1];
                System.arraycopy(args, 1, newArgs, 0, newArgs.length);
                args = newArgs;
            }
        } while (moreOptions);
        
        ArrayList cmdInfos = createCommandInfos(args, vars);
        if (cmdInfos.size() == 0) return true;
        //setFilteredFilesOut(vars);
        Collection processingFiles = ExecuteCommand.createProcessingFiles(fileSystem, vars);
        boolean success = true;
        if (printDebug) stdoutListener.outputData(new String[] { "Selected on "+processingFiles.size()+" files" });
        for (Iterator it = processingFiles.iterator(); it.hasNext() && success; ) {
            String path = (String) it.next();
            if (printDebug) stdoutListener.outputData(new String[] { "Processing in "+path });
            FileObject fo = fileSystem.findResource(path);
            if (fo != null) {
                for (Enumeration enum = fo.getChildren(true); enum.hasMoreElements(); ) {
                    lockedFileObjects.add(enum.nextElement());
                }
            }
            success &= runCommandsRecursively(path, cmdInfos);
        }
        lockedFileObjects.clear();
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
        public boolean canRunOnMultipleFiles;
        public boolean canRunOnMultipleFilesInFolder;
        public boolean ignoresFail;
        
        private HashSet disabledStates = null;
        
        public CommandInfo(VcsCommand cmd, Hashtable vars) {
            this.cmd = cmd;
            this.vars = vars;
            canRunOnFiles = VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_ON_FILE);
            canRunOnFolders = VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_ON_DIR);
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
        
        public boolean canRunOnStatus(String status) {
            if (disabledStates == null) {
                return true;
            } else {
                return !disabledStates.contains(status);
            }
        }
        
    }
    
}

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

package org.netbeans.modules.vcscore;

import java.lang.ref.WeakReference;
import java.util.*;
import javax.swing.*;
import java.awt.event.ActionListener;

import org.openide.TopManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.nodes.Node;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.cookies.SaveCookie;

import org.netbeans.api.vcs.VcsManager;
import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;

import org.netbeans.spi.vcs.commands.CommandSupport;

import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.netbeans.modules.vcscore.util.Table;
import org.netbeans.modules.vcscore.caching.FileCacheProvider;
import org.netbeans.modules.vcscore.caching.FileStatusProvider;
import org.netbeans.modules.vcscore.cmdline.UserCommand;
import org.netbeans.modules.vcscore.cmdline.UserCommandSupport;
import org.netbeans.modules.vcscore.cmdline.UserCommandTask;
import org.netbeans.modules.vcscore.cmdline.WrappingCommandTask;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.versioning.VersioningFileSystem;

/**
 * The action, that enables you to conveniently run an VCS command through
 * the old private "API". It's no longer a NodeAction, it contains just
 * several static methods.
 *
 * @deprecated This class is retained just for compatibility reasons. Use the
 *             new VCS APIs instead.
 *
 * @author Martin Entlicher
 */
public class VcsAction extends Object {//NodeAction implements ActionListener {
    
    private VcsAction() {
    }

    /*
    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = new WeakReference(fileSystem);
    }
    
    public void setSelectedFileObjects(Collection fos) {
        variablesForSelectedFiles = null;
        if (fos == null) {
            this.selectedFileObjects = getSelectedFileObjectsFromActivatedNodes();
            return ;
        }
        /*System.out.println("setSelectedFileObjects():");
        for (Iterator it = fos.iterator(); it.hasNext(); ) {
            System.out.println("  "+it.next());
        }*/
        //ArrayList reordered = VcsUtilities.reorderFileObjects(fos); -- not necessary
        // since DataObject.files() returns the "right" order
        /*System.out.println(" reorderedFileObjects():");
        for (Iterator it = reordered.iterator(); it.hasNext(); ) {
            System.out.println("  "+it.next());
        }*//*
        this.selectedFileObjects = new WeakList(fos);
        /*System.out.println(" Weak setSelectedFileObjects():");
        for (Iterator it = selectedFileObjects.iterator(); it.hasNext(); ) {
            System.out.println("  "+it.next());
        }*//*
    }
            */

    /**
     * Create the collection of selected files from the activated nodes.
     *
    private Collection getSelectedFileObjectsFromActivatedNodes() {
        ArrayList files = new ArrayList();
        Node[] nodes = getActivatedNodes();
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] instanceof VcsGroupNode) {
                VcsGroupNode grNode = (VcsGroupNode) nodes[i];
                if (variablesForSelectedFiles == null) variablesForSelectedFiles = new HashMap();
                Hashtable additionalVars = new Hashtable();
                additionalVars.put(Variables.GROUP_NAME, grNode.getDisplayName());
                String description = grNode.getShortDescription();
                if (description != null) {
                    additionalVars.put(Variables.GROUP_DESCRIPTION, description);
                }
                WeakList varFiles = new WeakList();
                Enumeration children = nodes[i].getChildren().nodes();
                while (children.hasMoreElements()) {
                    Node nd = (Node) children.nextElement();
                    DataObject dd = (DataObject) nd.getCookie(DataObject.class);
                    if (dd == null) continue;
                    FileObject primary = dd.getPrimaryFile();
                    files.addAll(dd.files());
                    varFiles.addAll(dd.files());
                    variablesForSelectedFiles.put(additionalVars, varFiles);
                }
            } else {
                DataObject dd = (DataObject) (nodes[i].getCookie(DataObject.class));
                if (dd == null) continue;
                files.addAll(dd.files());
            }
        }
        return new WeakList(files);
    }
     */
    
    /*
    public void setCommandsSubTrees(Node[] commandsSubTrees) {
        actionCommandsSubTrees = commandsSubTrees;
    }

    /*
    public VcsCacheFile parseFromCache (String[] cacheRecord) {
        return null; // TODO
    }
     */

    /**
     * Get a human presentable name of the action.
     * @return the name of the action
     *
    public String getName() {
        VcsFileSystem fileSystem = (VcsFileSystem) this.fileSystem.get();
        if (fileSystem == null) {
            return org.openide.util.NbBundle.getBundle(VcsAction.class).getString("CTL_Version_Control");
        }
        return fileSystem.getBundleProperty("CTL_Version_Control"); // NOI18N
    }

    /**
     * Get a help context for the action.
     * @return the help context for this action
     *
    public HelpCtx getHelpCtx(){
        //D.deb("getHelpCtx()"); // NOI18N
        return null;
    }
     */

    //public abstract void doList(String path);
    //public abstract void doDetails(Vector files);
    //public abstract void doCheckIn(Vector files);
    //public abstract void doCheckOut(Vector files);
    //public abstract void doAdd(Vector files);
    //public abstract void doRemove(Vector files);
    //public abstract JMenuItem getPopupPresenter();
    //public abstract void doAdditionalCommand(String name, Vector files);
    //protected abstract void doCommand(Vector files, VcsCommand cmd);

    /*
    private void killAllCommands() {
        VcsFileSystem fileSystem = (VcsFileSystem) this.fileSystem.get();
        CommandsPool cmdPool = fileSystem.getCommandsPool();
        String[] labels = cmdPool.getRunningCommandsLabels();
        if (labels.length > 0) {
            if (NotifyDescriptor.Confirmation.YES_OPTION.equals (
                            TopManager.getDefault ().notify (new NotifyDescriptor.Confirmation (
                                                             fileSystem.getBundleProperty("MSG_KILL_ALL_CMDS", VcsUtilities.arrayToString(labels)), NotifyDescriptor.Confirmation.YES_NO_OPTION)))) { // NOI18N
                cmdPool.killAll();
            }
        }
    }
     */

    /**
     * Do refresh of a directory.
     * @param path the directory path
     *
    public void doList(String path) {
        //D.deb("doList('"+path+"')"); // NOI18N
        //System.out.println("VcsAction.doList("+path+")");
        VcsFileSystem fileSystem = (VcsFileSystem) this.fileSystem.get();
        doList(fileSystem, path);
    }
     */
    
    public static void doList(VcsFileSystem fileSystem, String path) {
        FileStatusProvider statusProvider = fileSystem.getStatusProvider();
        FileCacheProvider cache = fileSystem.getCacheProvider();
        if (statusProvider == null) return;
        //System.out.println("cache = "+cache+", cache.isDir("+path+") = "+cache.isDir(path));
        if (cache == null || cache.isDir(path)) {
            statusProvider.refreshDir(path);
        } else {
            String dirName = VcsUtilities.getDirNamePart(path);
            statusProvider.refreshDir(dirName);
        }
    }

    /**
     * Do recursive refresh of a directory.
     * @param path the directory path
     *
    public void doListSub(String path) {
        VcsFileSystem fileSystem = (VcsFileSystem) this.fileSystem.get();
        CommandExecutorSupport.doRefresh(fileSystem, path, true);
        /*
        //D.deb("doListSub('"+path+"')"); // NOI18N
        FileStatusProvider statusProvider = fileSystem.getStatusProvider();
        FileCacheProvider cache = fileSystem.getCacheProvider();
        if (statusProvider == null) return;
        VcsCommand cmd = fileSystem.getCommand(VcsCommand.NAME_REFRESH_RECURSIVELY);
        String dirName = ""; // NOI18N
        if (cache == null || cache.isDir(path)) {
            dirName = path;
        }
        else{
            dirName = VcsUtilities.getDirNamePart(path);
        }
        //Object exec = VcsCommandIO.getCommandProperty(cmd, "exec", String.class);
        Object exec = cmd.getProperty(VcsCommand.PROPERTY_EXEC);
        if (cmd != null && (exec != null && ((String) exec).trim().length() > 0)) {
            statusProvider.refreshDirRecursive(dirName);
        } else {
            RetrievingDialog rd = new RetrievingDialog(fileSystem, dirName, new JFrame(), false);
            VcsUtilities.centerWindow(rd);
            Thread t = new Thread(rd, "VCS Recursive Retrieving Thread - "+dirName); // NOI18N
            t.start();
        }
         *//*
    }
            */

    /*
     * Whether the files should be locked in VCS. This command does not save the file contents.
     * Executes the <code>VcsCommand.NAME_SHOULD_DO_LOCK</code> command, which should
     * find out, whether the file is already locked.
     * @param files the table pairs of file name and associated <code>FileObject</code>
     * @return true if the command succeeds and therefore the file should be locked,
     *         false otherwise
     *
    public static boolean shouldDoLock(Table files, VcsFileSystem fileSystem) {
        VcsCommand cmd = fileSystem.getCommand(VcsCommand.NAME_SHOULD_DO_LOCK);
        if (cmd != null) {
            VcsCommandExecutor[] execs = doCommand(files, cmd, null, fileSystem, null, null, null, null, false);
            for (int i = 0; i < execs.length; i++) {
                if (execs[i].getExitStatus() != VcsCommandExecutor.SUCCEEDED) return false;
            }
        }
        return true;
    }
     */
    
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
        if (cmdSupp == null) {
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
        UserCommandSupport.setCommandFilesFromTable(command, files, fileSystem);
        if (!VcsManager.getDefault().showCustomizer(command)) return new VcsCommandExecutor[0];
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
        } else {
            return new VcsCommandExecutor[0];
        }
        
        /*
        if (VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_NEEDS_HIERARCHICAL_ORDER)) {
            files = createHierarchicalOrder(files);
        }
        ArrayList executors = new ArrayList();
        boolean[] askForEachFile = null;
        //String quoting = fileSystem.getQuoting();
        String quoting = "${QUOTE}"; // NOI18N
        int preprocessStatus;
        boolean cmdCanRunOnMultipleFiles = VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_RUN_ON_MULTIPLE_FILES);
        boolean cmdCanRunOnMultipleFilesInFolder = VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_RUN_ON_MULTIPLE_FILES_IN_FOLDER);
        VariableValueAdjustment valueAdjustment = fileSystem.getVarValueAdjustment();
        Map scheduledFilesMap = extractScheduledFiles(files, cmd);
        String scheduledAttribute = null;
        VcsCommand scheduledCmd = cmd;
        do {
            Hashtable vars = fileSystem.getVariablesAsHashtable();
            if (additionalVars != null) vars.putAll(additionalVars);
            if (files.size() > 1) {
                askForEachFile = new boolean[1];
                askForEachFile[0] = true;
            }
            Object[] askForEachFileRef = new Object[] { askForEachFile };
            Object[] varsRef = new Object[] { vars };
            do {
                //System.out.println("setupRestrictedFileMap("+VcsUtilities.arrayToString((String[]) files.keySet().toArray(new String[0]))+")");
                Table subFiles = setupRestrictedFileMap(files, varsRef, scheduledCmd);
                ArrayList subFilesNames = new ArrayList();
                for (Iterator it = subFiles.keySet().iterator(); it.hasNext(); ) {
                    subFilesNames.add(it.next());
                }
                do {
                    if (files.size() == 0) {
                        preprocessStatus = CommandsPool.PREPROCESS_DONE;
                    } else {
                        preprocessStatus = doCommandExecution(subFiles, varsRef, additionalVars, 
                                                              fileSystem, scheduledCmd,
                                                              cmdCanRunOnMultipleFiles,
                                                              cmdCanRunOnMultipleFilesInFolder,
                                                              quoting, askForEachFileRef,
                                                              valueAdjustment, executors,
                                                              stdoutListener, stderrListener,
                                                              stdoutDataListener, stderrDataListener);
                    }
                    if (CommandsPool.PREPROCESS_CANCELLED == preprocessStatus) break;
                } while (CommandsPool.PREPROCESS_NEXT_FILE == preprocessStatus);
                for (Iterator it = subFilesNames.iterator(); it.hasNext(); ) {
                    files.remove(it.next());
                }
            } while (CommandsPool.PREPROCESS_CANCELLED != preprocessStatus && files.size() > 0);
            if (CommandsPool.PREPROCESS_CANCELLED != preprocessStatus && scheduledFilesMap.size() > 0) {
                scheduledAttribute = (String) scheduledFilesMap.keySet().iterator().next();
                Table scheduledFiles = (Table) scheduledFilesMap.get(scheduledAttribute);
                files = scheduledFiles;
                scheduledFilesMap.remove(scheduledAttribute);
                preprocessStatus = CommandsPool.PREPROCESS_NEXT_FILE;
                String scheduledCmdStr = (String) cmd.getProperty(VcsCommand.PROPERTY_EXEC_SCHEDULED_COMMAND + scheduledAttribute);
                if (scheduledCmdStr != null) {
                    scheduledCmd = fileSystem.getCommand(scheduledCmdStr);
                    if (scheduledCmd == null) scheduledCmd = cmd;
                }
            }
        } while(CommandsPool.PREPROCESS_NEXT_FILE == preprocessStatus);
        //System.out.println("VcsAction.doCommand(): executors started = "+executors.size());
        return (VcsCommandExecutor[]) executors.toArray(new VcsCommandExecutor[executors.size()]);
         */
    }
    
    /*
    private static Table setupRestrictedFileMap(Table files, Object[] varsRef, VcsCommand cmd) {
        String[] attrsToVars = (String[]) cmd.getProperty(VcsCommand.PROPERTY_LOAD_ATTRS_TO_VARS);
        if (attrsToVars != null) {
            files = getAttributeRestrictedFileMap(files, varsRef, cmd, attrsToVars);
        }
        if (Boolean.TRUE.equals(cmd.getProperty(VcsCommand.PROPERTY_DISTINGUISH_BINARY_FILES))) {
            files = getBinaryRestrictedFileMap(files, varsRef, cmd);
        }
        return files;
    }
    
    private static Table getAttributeRestrictedFileMap(Table files, Object[] varsRef, VcsCommand cmd, String[] attrsToVars) {
        String[] attrNames;
        Map attrNonNullVars = null;
        Map attrValueVars = null;
        attrNames = (String[]) cmd.getProperty(PROPERTY_PARSED_ATTR_NAMES);
        if (attrNames != null) {
            attrNonNullVars = (Map) cmd.getProperty(PROPERTY_PARSED_ATTR_NEMPTY_VARS);
            attrValueVars = (Map) cmd.getProperty(PROPERTY_PARSED_ATTR_VALUES_VARS);
        } else {
            attrNonNullVars = new HashMap();
            attrValueVars = new HashMap();
            attrNames = getAttrNamesAndVars(attrNonNullVars, attrValueVars, attrsToVars);
            cmd.setProperty(PROPERTY_PARSED_ATTR_NAMES, attrNames);
            cmd.setProperty(PROPERTY_PARSED_ATTR_NEMPTY_VARS, attrNonNullVars);
            cmd.setProperty(PROPERTY_PARSED_ATTR_VALUES_VARS, attrValueVars);
        }
        Table subFiles;
        if (attrNames != null) {
            subFiles = setVarsFromAttrs(files, (Hashtable) varsRef[0], attrNames, attrNonNullVars, attrValueVars);
        } else {
            subFiles = files;
        }
        return subFiles;
    }
    
    private static Table getBinaryRestrictedFileMap(Table files, Object[] varsRef, VcsCommand cmd) {
        Table restrictedFiles = new Table();
        boolean isBinary;
        Iterator it = files.keySet().iterator();
        if (!it.hasNext()) return files;
        String name = (String) it.next();
        FileObject fo = (FileObject) files.get(name);
        if (fo == null) {
            isBinary = false;
        } else {
            isBinary = isFOBinary(fo);
        }
        restrictedFiles.put(name, fo);
        while (it.hasNext()) {
            name = (String) it.next();
            fo = (FileObject) files.get(name);
            if (fo == null) {
                if (isBinary) continue;
            } else {
                if (isBinary != isFOBinary(fo)) continue;
            }
            restrictedFiles.put(name, fo);
        }
        Hashtable vars = (Hashtable) varsRef[0];
        vars.put("PROCESSING_BINARY_FILES", isBinary ? Boolean.TRUE.toString() : ""); // NOI18N
        return restrictedFiles;
    }
    
    private static boolean isFOBinary(FileObject fo) {
        String mimeType = fo.getMIMEType();
        return !mimeType.startsWith("text") && !"content/unknown".equals(mimeType); // NOI18N
    }
     */
    
    /**
     * Do a command on a set of files.
     * @param files the table of pairs of files and file objects, to perform the command on
     * @param cmd the command to perform
     * @param additionalVars additional variables to FS variables, or null when no additional variables are needed
     *
    private void doCommand(Table files, VcsCommand cmd) {
        Hashtable map = new Hashtable(1);
        VcsFileSystem fileSystem = (VcsFileSystem) this.fileSystem.get();
        if (!fileSystem.isExpertMode()) {
            if (CTRL_Down) {
                map.put(VcsFileSystem.VAR_CTRL_DOWN_IN_ACTION, Boolean.TRUE);
            }
        }
        VcsAction.doCommand(files, cmd, map, fileSystem);
    }
    
    private static int doCommandExecution(Table files, Object[] varsRef, Hashtable additionalVars,
                                          VcsFileSystem fileSystem, VcsCommand cmd,
                                          boolean cmdCanRunOnMultipleFiles,
                                          boolean cmdCanRunOnMultipleFilesInFolder,
                                          String quoting, Object[] askForEachFileRef,
                                          VariableValueAdjustment valueAdjustment, List executors,
                                          CommandOutputListener stdoutListener, CommandOutputListener stderrListener,
                                          CommandDataOutputListener stdoutDataListener, CommandDataOutputListener stderrDataListener) {
        boolean[] askForEachFile = (boolean[]) askForEachFileRef[0];
        Hashtable vars = (Hashtable) varsRef[0];
        FileCacheProvider cacheProvider = fileSystem.getCacheProvider();
        setVariables(files, vars, quoting, valueAdjustment, cacheProvider,
                     fileSystem.getRelativeMountPoint(), true);//VcsCommandIO.getBooleanProperty(cmd, VcsCommand.PROPERTY_NEEDS_GREATEST_COMMON_PARENT));
        VcsCommandExecutor vce = fileSystem.getVcsFactory().getCommandExecutor(cmd, vars);
        CommandsPool pool = fileSystem.getCommandsPool();
        int preprocessStatus = pool.preprocessCommand(vce, vars, fileSystem, askForEachFile);
        //System.out.println("VcsAction.doCommand(): CommandsPool.preprocessCommand() = "+preprocessStatus+", askForEachFile = "+((askForEachFile.length > 0) ? ""+askForEachFile : ""+askForEachFile[0]));
        if (CommandsPool.PREPROCESS_CANCELLED == preprocessStatus) {
            return preprocessStatus;
        }
        if (!cmdCanRunOnMultipleFiles && !cmdCanRunOnMultipleFilesInFolder) {
            // When the executor can not run on more than one file, it has to be processed one by one.
            preprocessStatus = CommandsPool.PREPROCESS_NEXT_FILE;
        }
        if (files.size() == 1) preprocessStatus = CommandsPool.PREPROCESS_DONE;
        Table singleFolderTable = null;
        if (CommandsPool.PREPROCESS_NEXT_FILE == preprocessStatus) {
            Table singleFileTable = new Table();
            Object singleFile = files.keys().nextElement();
            singleFileTable.put(singleFile, files.get(singleFile));
            setVariables(singleFileTable, vars, quoting, valueAdjustment, cacheProvider,
                         fileSystem.getRelativeMountPoint(), true);//VcsCommandIO.getBooleanProperty(cmd, VcsCommand.PROPERTY_NEEDS_GREATEST_COMMON_PARENT));
        } else if (cmdCanRunOnMultipleFilesInFolder) {
            singleFolderTable = new Table();
            Enumeration keys = files.keys();
            String file = (String) keys.nextElement();
            singleFolderTable.put(file, files.get(file));
            //String folder = file.getPackageName('/');
            String folder = ""; // NOI18N
            int index = file.lastIndexOf('/');
            if (index >= 0) folder = file.substring(0, index);
            while (keys.hasMoreElements()) {
                file = (String) keys.nextElement();
                String testFolder = ""; // NOI18N
                index = file.lastIndexOf('/');
                if (index >= 0) testFolder = file.substring(0, index);
                if (folder.equals(testFolder)) {
                    singleFolderTable.put(file, files.get(file));
                }
            }
            setVariables(singleFolderTable, vars, quoting, valueAdjustment, cacheProvider,
                         fileSystem.getRelativeMountPoint(), true);//VcsCommandIO.getBooleanProperty(cmd, VcsCommand.PROPERTY_NEEDS_GREATEST_COMMON_PARENT));
        }
        executors.add(vce);
        if (stdoutListener != null) vce.addOutputListener(stdoutListener);
        if (stderrListener != null) vce.addErrorOutputListener(stderrListener);
        if (stdoutDataListener != null) vce.addDataOutputListener(stdoutDataListener);
        if (stderrDataListener != null) vce.addDataErrorOutputListener(stderrDataListener);
        pool.startExecutor(vce);
        synchronized (vars) {
            if (askForEachFile != null && askForEachFile[0] == true) {
                vars = new Hashtable(fileSystem.getVariablesAsHashtable());
                if (additionalVars != null) vars.putAll(additionalVars);
            } else {
                vars = new Hashtable(vars);
            }
        }
        if (CommandsPool.PREPROCESS_NEXT_FILE == preprocessStatus) {
            files.remove(files.keys().nextElement()); // remove the processed file
            if (files.size() == 1 && askForEachFile != null && askForEachFile[0] == true) {
                askForEachFile = null; // Do not show the check box for the last file.
            }
        } else if (cmdCanRunOnMultipleFilesInFolder) {
            for (Enumeration keys = singleFolderTable.keys(); keys.hasMoreElements(); ) {
                files.remove(keys.nextElement());
            }
            if (files.size() == 1 && askForEachFile != null && askForEachFile[0] == true) {
                askForEachFile = null; // Do not show the check box for the last file.
            }
            if (files.size() > 0) {
                preprocessStatus = CommandsPool.PREPROCESS_NEXT_FILE;
            }
        }
        varsRef[0] = vars;
        askForEachFileRef[0] = askForEachFile;
        return preprocessStatus;
    }
    
    private static Map extractScheduledFiles(Table files, VcsCommand cmd) {
        HashMap scheduledMap = new HashMap();
        //Table scheduled = new Table();
        LinkedList keys = new LinkedList();
        for (Enumeration enum = files.keys(); enum.hasMoreElements(); ) {
            keys.add(enum.nextElement());
        }
        for (Iterator it = keys.iterator(); it.hasNext(); ) {
            String fileName = (String) it.next();
            FileObject fo = (FileObject) files.get(fileName);
            String attr;
            if (fo == null) {
                attr = VcsAttributes.VCS_SCHEDULING_REMOVE;
            } else {
                attr = (String) fo.getAttribute(VcsAttributes.VCS_SCHEDULED_FILE_ATTR);
            }
            if (attr == null) continue;
            String scheduledCmdStr = (String) cmd.getProperty(VcsCommand.PROPERTY_EXEC_SCHEDULED_COMMAND + attr);
            if (scheduledCmdStr == null) continue;
            Table scheduled = (Table) scheduledMap.get(attr);
            if (scheduled == null) {
                scheduled = new Table();
                scheduledMap.put(attr, scheduled);
            }
            files.remove(fileName);
            scheduled.put(fileName, fo);
        }
        return scheduledMap;
        /*
        if (scheduled.size() > 0) {
            return scheduled;
        } else {
            return null;
        }
         *//*
    }
            */
    
    /** Make sure, that the files are saved. If not, save them.
     * @param fos the collection of FileObjects
     */
    public static void assureFilesSaved(Collection fos) {
        for (Iterator it = fos.iterator(); it.hasNext(); ) {
            FileObject fo = (FileObject) it.next();
            if (fo == null) continue;
            DataObject dobj = null;
            try {
                dobj = DataObject.find(fo);
            } catch (DataObjectNotFoundException exc) {
                // ignored
            }
            if (dobj != null && dobj.isModified()) {
                Node.Cookie cake = dobj.getCookie(SaveCookie.class);
                try {
                    if (cake != null) ((SaveCookie) cake).save();
                } catch (java.io.IOException exc) {
                    TopManager.getDefault().notifyException(exc);
                }
            }
        }
    }
    
    /** Reorder the table of files by the path hierarchical order.
     * @param files the table of pairs of files and file objects
     * @return the reordered table
     *
    private static Table createHierarchicalOrder(Table files) {
        TreeMap sorted = new TreeMap(files);
        Table sortedFiles = new Table();
        for (Iterator it = sorted.keySet().iterator(); it.hasNext(); ) {
            Object key = it.next();
            sortedFiles.put(key, files.get(key));
        }
        return sortedFiles;
    }

    /**
     * Test if some of the selected nodes are directories.
     * @return <code>true</code> if some of the selected nodes are directories,
     *         <code>false</code> otherwise.
     *
    protected boolean isOnDirectory() {
        boolean is = false;
        if (selectedFileObjects != null) {
            for (Iterator it = selectedFileObjects.iterator(); it.hasNext(); ) {
                FileObject fo = (FileObject) it.next();
                if (fo.isFolder()) is = true;
            }
        }
        return is && !isOnRoot();
    }

    /**
     * Test if some of the selected nodes are files.
     * @return <code>true</code> if some of the selected nodes are files,
     *         <code>false</code> otherwise.
     *
    protected boolean isOnFile() {
        if (selectedFileObjects != null) {
            //System.out.println("isOnFile(): selectedFileObjects = "+selectedFileObjects+", size = "+selectedFileObjects.size());
            for (Iterator it = selectedFileObjects.iterator(); it.hasNext(); ) {
                FileObject fo = (FileObject) it.next();
                //System.out.println("  fo = "+fo);
                if (!fo.isFolder()) return true;
            }
        }
        return false;
    }

    /**
     * Test if one of the selected nodes is the root node.
     * @return <code>true</code> if at least one of the selected nodes is the root node,
     *         <code>false</code> otherwise.
     *
    protected boolean isOnRoot() {
        if (selectedFileObjects != null) {
            for (Iterator it = selectedFileObjects.iterator(); it.hasNext(); ) {
                FileObject fo = (FileObject) it.next();
                if (fo.getPackageNameExt('/', '.').length() == 0) return true;
            }
        }
        return false;
    }

    /**
     * Add files marked as important.
     * @param dd the data object from which the files are read.
     * @param res the <code>Table</code> of path and FileObject pairs which are important.
     * @param all whether to add unimportant files as well
     *
    protected void addImportantFiles(Collection fos, Table res, boolean all, boolean doNotTestFS) {
        VcsFileSystem fileSystem = (VcsFileSystem) this.fileSystem.get();
        addImportantFiles(fos, res, all, fileSystem, doNotTestFS);
    }
     */

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
                if (ff.getFileSystem() instanceof VersioningFileSystem) {
                    res.put(ff.getPackageNameExt('/', '.'), ff);
                    continue;
                }
                if (!doNotTestFS && ff.getFileSystem() != fileSystem)
                    continue;
            } catch (FileStateInvalidException exc) {
                continue;
            }
            String fileName = ff.getPackageNameExt('/','.');
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
    
    /*
    private Set getSelectedFileStatusAttributes() {
        Set statuses = new HashSet();
        VcsFileSystem fileSystem = (VcsFileSystem) this.fileSystem.get();
        FileStatusProvider statusProv = fileSystem.getStatusProvider();
        boolean processAll = fileSystem.isProcessUnimportantFiles();
        if (statusProv != null) {
            if (selectedFileObjects != null) {
                for (Iterator it = selectedFileObjects.iterator(); it.hasNext(); ) {
                    FileObject fo = (FileObject) it.next();
                    String path = fo.getPackageNameExt('/', '.');
                    FileSystem fs = null;
                    try {
                        fs = fo.getFileSystem();
                    } catch (FileStateInvalidException exc) {}
                    if (fs instanceof VersioningFileSystem || processAll || fileSystem.isImportant(path)) {
                        String status = statusProv.getFileStatus(path);
                        if (status != null) statuses.add(status);
                    }
                    Set[] scheduled = (Set[]) fo.getAttribute(VcsAttributes.VCS_SCHEDULED_FILES_ATTR);
                    if (scheduled != null && scheduled[0] != null) {
                        for (Iterator sit = scheduled[0].iterator(); sit.hasNext(); ) {
                            path = (String) sit.next();
                            String status = statusProv.getFileStatus(path);
                            statuses.add(status);
                        }
                    }
                }
            }
        }
        return statuses;
    }
     */

    /**
     * Create the command menu item.
     * @param cmd the command
     *
    private static JMenuItem createItem(VcsCommand cmd, boolean expertMode,
                                        List switchableList, String advancedOptionsSign,
                                        ActionListener listener, Hashtable variables) {
        JMenuItem item = null;
        String label = cmd.getDisplayName();
        label = Variables.expand(variables, label, false);
        //if (label.indexOf('$') >= 0) {
        //    Variables v = new Variables();
        //    label = v.expandFast(vars, label, true);
        //}
        //System.out.println("VcsAction.createItem("+name+"): menu '"+label+"' created.");
        boolean hasExpert = VcsCommandIO.getBooleanProperty(cmd, VcsCommand.PROPERTY_SUPPORTS_ADVANCED_MODE);
        if (hasExpert && expertMode) label += advancedOptionsSign;
        item = new JMenuItem(label);
        String mnemonic = (String) cmd.getProperty(VcsCommand.PROPERTY_LABEL_MNEMONIC);
        if (mnemonic != null && mnemonic.length() > 0) {
            item.setMnemonic(mnemonic.charAt(0));
        }
        String[] props = cmd.getPropertyNames();
        if (props != null && props.length > 0) {
            item.setActionCommand(cmd.getName());
            item.addActionListener(listener);
        }
        if (hasExpert && (!expertMode)) {
            switchableList.add(item);
        }
        return item;
    }

    /**
     * Get a menu item that can present this action in a <code>JMenu</code>.
     *
    public JMenuItem getMenuPresenter() {
        return getPresenter(true);
    }
    
    /**
     * Get a menu item that can present this action in a <code>JPopupMenu</code>.
     *
    public JMenuItem getPopupPresenter() {
        return getPresenter(false);
    }
    
    private JMenuItem getPresenter(boolean inMenu) {
        boolean onRoot = isOnRoot();
        boolean onDir;
        boolean onFile;
        switchableList = new ArrayList();
        if (onRoot) {
            onDir = onFile = false;
        } else {
            onDir = isOnDirectory();
            onFile = isOnFile();
        }
        //System.out.println("onRoot = "+onRoot+", onDir = "+onDir+", onFile = "+onFile);
        Set statuses = getSelectedFileStatusAttributes();
        JInlineMenu inlineMenu = new JInlineMenu();
        ArrayList menuItems = new ArrayList();
        for (int i = 0; i < actionCommandsSubTrees.length; i++) {
            JMenuItem menuItem = getPopupPresenter(actionCommandsSubTrees[i], onDir,
                                                   onFile, onRoot, statuses, inMenu);
            if (menuItem != null) menuItems.add(menuItem);
        }
        inlineMenu.setMenuItems((JMenuItem[]) menuItems.toArray(new JMenuItem[menuItems.size()]));
        return inlineMenu;
    }

    /**
     * Get a menu item that can present this action in a <code>JPopupMenu</code>.
     *
    private JMenuItem getPopupPresenter(Node commandRoot, boolean onDir, boolean onFile,
                                        boolean onRoot, Set statuses, boolean inMenu) {
        String name = commandRoot.getDisplayName();
        /*
        if (name == null) {
            name = fileSystem.getBundleProperty("CTL_Version_Control");
        }
         *//*
        JMenuItem menu;
        if (!commandRoot.isLeaf()) {
            menu = new VcsAction.CommandMenu(commandRoot, onRoot, onFile, onDir,
                                             (VcsFileSystem) fileSystem.get(),
                                             statuses, switchableList, advancedOptionsSign,
                                             REMOVE_DISABLED, this);//JMenuPlus(name);
            if (inMenu) {
                menu.setIcon(getIcon());
            }
            JMenu mn = (JMenu)menu;
            menu.addMenuKeyListener(new CtrlMenuKeyListener());
            mn.addMenuListener(new javax.swing.event.MenuListener() {
                public void menuDeselected(javax.swing.event.MenuEvent e) {
                    //                deselectedMenu();
                    //                System.out.println("menu deselected");
                }
                public void menuCanceled(javax.swing.event.MenuEvent e) {
                    //                deselectedMenu();
                    //                System.out.println("menu canceled");
                }
                public void menuSelected(javax.swing.event.MenuEvent e) {
                    deselectedMenu();
                    //                System.out.println("Selected menu");
                }
            });
        } else {
            VcsCommand cmd = (VcsCommand) commandRoot.getCookie(VcsCommand.class);
            if (cmd == null) {
                return null;
            } else {
                VcsFileSystem fileSystem = (VcsFileSystem) this.fileSystem.get();
                menu = getCommandMenuItem(cmd, onFile, onDir, onRoot,
                                          fileSystem, fileSystem.getVariablesAsHashtable(),
                                          switchableList, advancedOptionsSign, this);
                if (menu != null) {
                    if (inMenu) {
                        menu.setIcon(getIcon());
                    }
                    boolean disabled = VcsUtilities.isSetContainedInQuotedStrings(
                        (String) cmd.getProperty(VcsCommand.PROPERTY_DISABLED_ON_STATUS), statuses);
                    if (disabled && REMOVE_DISABLED) menu = null;
                    else menu.setEnabled(!disabled);
                }
            }
        }

        return menu;
    }
    
    private static JMenuItem getCommandMenuItem(VcsCommand cmd, boolean onFile,
                                                boolean onDir, boolean onRoot,
                                                VcsFileSystem fileSystem,
                                                Hashtable variables,
                                                List switchableList,
                                                String advancedOptionsSign,
                                                ActionListener listener) {
        JMenuItem menu;
        String hiddenTestExpression;
        if (cmd.getDisplayName() == null
            || onDir && !VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_ON_DIR)
            || onFile && !VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_ON_FILE)
            || onRoot && !VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_ON_ROOT)
            || VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_HIDDEN)
            || ((hiddenTestExpression = (String) cmd.getProperty(VcsCommand.PROPERTY_HIDDEN_TEST_EXPRESSION)) != null
                && Variables.expand(variables, hiddenTestExpression, false).trim().length() > 0)) {
            menu = null;
        } else {
            //VcsFileSystem fileSystem = (VcsFileSystem) this.fileSystem.get();
            boolean isOffLine = fileSystem.isOffLine();
            if (VcsCommand.NAME_REFRESH.equals(cmd.getName()) && isOffLine && fileSystem.getCommand(VcsCommand.NAME_REFRESH_OFFLINE) != null) {
                menu = null;
            } else if (VcsCommand.NAME_REFRESH_OFFLINE.equals(cmd.getName()) && !isOffLine && fileSystem.getCommand(VcsCommand.NAME_REFRESH) != null) {
                menu = null;
            } else if (VcsCommand.NAME_REFRESH_RECURSIVELY.equals(cmd.getName()) && isOffLine) {
                menu = null;
            } else if (VcsCommand.NAME_REFRESH_RECURSIVELY_OFFLINE.equals(cmd.getName()) && !isOffLine && fileSystem.getCommand(VcsCommand.NAME_REFRESH_RECURSIVELY) != null) {
                menu = null;
            } else {
                menu = createItem(cmd, fileSystem.isExpertMode(), switchableList,
                                  advancedOptionsSign, listener, variables);
            }
        }
        return menu;
    }
            */

    /**
     * Test whether the action should be enabled based on the currently activated nodes.
     * @return true for non-empty set of nodes.
     *
    public boolean enable(Node[] nodes) {
        FileSystem fs = null;
        for (int i = 0; i < nodes.length; i++) {
            if (nodes[i] instanceof VcsGroupNode) {
                Enumeration children = nodes[i].getChildren().nodes();
                while (children.hasMoreElements()) {
                    Node nd = (Node) children.nextElement();
                    DataObject dd = (DataObject) nd.getCookie(DataObject.class);
                    if (dd == null) return false;
                    FileObject primary = dd.getPrimaryFile();
                    try {
                        FileSystem testFs = primary.getFileSystem();
                        if (fs == null) fs = testFs;
                        else if (fs != testFs) return false;
                    } catch (FileStateInvalidException fsiex) {
                        return false;
                    }
                }
            } else {
                DataObject dd = (DataObject) (nodes[i].getCookie(DataObject.class));
                if (dd == null) return false;
                FileObject primary = dd.getPrimaryFile();
                try {
                    FileSystem testFs = primary.getFileSystem();
                    if (fs == null) fs = testFs;
                    else if (fs != testFs) return false;
                } catch (FileStateInvalidException fsiex) {
                    return false;
                }
            }
        }
        return (nodes.length > 0);
    }
    
    /** Add files specific variables.
     * The following variables are added:
     * <br>PATH - the full path to the first file from the filesystem root
     * <br>DIR - the directory of the first file from the filesystem root
     * <br>FILE - the first file
     * <br>QFILE - the first file quoted
     * <br>MIMETYPE - the MIME type of the first file
     * <br>
     * <br>FILES - all files delimeted by the system file separator
     * <br>PATHS - full paths to all files delimeted by two system file separators
     * <br>QPATHS - full paths to all files quoted by filesystem quotation string and delimeted by spaces
     * <br>NUM_FILES - the number of files
     * <br>MULTIPLE_FILES - "true" when more than one file is to be processed, "" otherwise
     * <br>COMMON_PARENT - the greatest common parent of provided files. If defined,
     *                     all file paths change to be relative to this common parent
     *
     * @param files the table of files
     * @param vars the table of variables to extend
     * @param quoting the quotation string used when more than one file is to be processed
     * @param valueAdjustment the variable value adjustment utility object
     * @param cacheProvider the provider of cached file attributes
     * @param useGreatestParentPaths whether to define COMMON_PARENT variable and
     *        change the file paths to be relative to this greatest common parent
     *        of all provided files.
     *
    protected static void setVariables(Table files, Hashtable vars, String quoting,
                                       VariableValueAdjustment valueAdjustment,
                                       FileCacheProvider cacheProvider,
                                       String relativeMountPoint,
                                       boolean useGreatestParentPaths) {
        // At first, find the greatest parent
        String greatestParent;
        if (useGreatestParentPaths) {
            greatestParent = findGreatestParent(files);
            if (greatestParent != null && greatestParent.length() == 0) {
                greatestParent = null;
            }
        } else {
            greatestParent = null;
        }
        // Then, find the first file and set the variables
        String fullName = (String) files.keys().nextElement();
        FileObject fo = (FileObject) files.get(fullName);
        boolean isFileFolder = (fo != null && fo.isFolder());
        String origFullName = fullName;
        if (greatestParent != null) {
            fullName = fullName.substring(greatestParent.length());
            while (fullName.startsWith("/")) fullName = fullName.substring(1);
        }
        String path = VcsUtilities.getDirNamePart(fullName);
        String file = VcsUtilities.getFileNamePart(fullName);
        String separator = (String) vars.get("PS"); // NOI18N
        char separatorChar = (separator != null && separator.length() == 1) ? separator.charAt(0) : java.io.File.separatorChar;
        path = path.replace('/', separatorChar);
        fullName = fullName.replace('/', separatorChar);
        file = valueAdjustment.adjustVarValue(file);
        path = valueAdjustment.adjustVarValue(path);
        fullName = valueAdjustment.adjustVarValue(fullName);
        if (fullName.length() == 0) fullName = "."; // NOI18N
        String module = relativeMountPoint;//(String) vars.get("MODULE"); // NOI18N
        if (module == null) module = ""; // NOI18N
        if (greatestParent != null) {
            if (module.length() > 0) module += separatorChar;
            module += greatestParent;
            //vars.put("MODULE", module);
        }
        if (module.length() > 0) {
            module = module.replace('/', separatorChar);
            module = valueAdjustment.adjustVarValue(module);
        }
        vars.put("MODULE", module);
        if (module.length() > 0) {
            module += separatorChar;
        }
        vars.put("PATH", fullName); // NOI18N
        vars.put("QPATH", (fullName.length() > 0) ? quoting+fullName+quoting : fullName); // NOI18N
        vars.put("DIR", path); // NOI18N
        if (path.length() == 0 && file.length() > 0 && file.charAt(0) == '/') file = file.substring (1, file.length ());
        vars.put("FILE", file); // NOI18N
        vars.put("QFILE", quoting+file+quoting); // NOI18N
        if (fo != null) {
            vars.put("MIMETYPE", fo.getMIMEType()); // NOI18N
        } else {
            int extIndex = file.lastIndexOf('.');
            String ext = (extIndex >= 0 && extIndex < file.length() - 1) ? file.substring(extIndex + 1) : ""; // NOI18N
            String mime = FileUtil.getMIMEType(ext);
            if (mime != null) vars.put("MIMETYPE", mime); // NOI18N
        }
        if (isFileFolder) {
            CacheDir cDir = cacheProvider.getDir(origFullName);
            if (cDir != null) {
                vars.put("CACHED_ATTR", cDir.getAttr());
            } else {
                vars.remove("CACHED_ATTR");
            }
        } else {
            CacheFile cFile = cacheProvider.getFile(origFullName);
            if (cFile != null) {
                vars.put("CACHED_ATTR", cFile.getAttr());
            } else {
                vars.remove("CACHED_ATTR");
            }
        }
        vars.put("FILE_IS_FOLDER", (isFileFolder) ? Boolean.TRUE.toString() : "");// the FILE is a folder // NOI18N
        // Second, set the multifiles variables
        StringBuffer qpaths = new StringBuffer();
        StringBuffer paths = new StringBuffer();
        StringBuffer mpaths = new StringBuffer();
        StringBuffer qmpaths = new StringBuffer();
        StringBuffer vfiles = new StringBuffer();
        StringBuffer qfiles = new StringBuffer();
        int nVARS = 6;
        int nFILES = files.size();
        int[][] fileIndexes = new int[6][nFILES];
        // The array is automatically initialized with zeros.
        int iFile = 0;
        for (Enumeration enum = files.keys(); enum.hasMoreElements(); iFile++) {
            fullName = (String) enum.nextElement();
            fo = (FileObject) files.get(fullName);
            origFullName = fullName;
            if (greatestParent != null) {
                fullName = fullName.substring(greatestParent.length());
                while (fullName.startsWith("/")) fullName = fullName.substring(1);
            }
            if (fullName.length() == 0) fullName = "."; // NOI18N
            isFileFolder |= (fo != null && fo.isFolder());
            file = VcsUtilities.getFileNamePart(fullName);
            fullName = fullName.replace('/', separatorChar);
            file = valueAdjustment.adjustVarValue(file);
            fullName = valueAdjustment.adjustVarValue(fullName);
            fileIndexes[0][iFile] = vfiles.length();
            fileIndexes[1][iFile] = qfiles.length();
            fileIndexes[2][iFile] = paths.length();
            fileIndexes[3][iFile] = qpaths.length();
            fileIndexes[4][iFile] = mpaths.length();
            fileIndexes[5][iFile] = qmpaths.length();
            vfiles.append(file);
            vfiles.append(separatorChar);
            qfiles.append(quoting);
            qfiles.append(file);
            qfiles.append(quoting);
            qfiles.append(" "); // NOI18N
            paths.append(fullName);
            paths.append(""+separatorChar+separatorChar); // NOI18N
            qpaths.append(quoting);
            qpaths.append(fullName);
            qpaths.append(quoting);
            qpaths.append(" "); // NOI18N
            mpaths.append(module + fullName);
            mpaths.append(" "); // NOI18N
            qmpaths.append(quoting);
            qmpaths.append(module + fullName);
            qmpaths.append(quoting);
            qmpaths.append(" "); // NOI18N
        }
        vars.put("FILES", vfiles.delete(vfiles.length() - 1, vfiles.length()).toString()); // NOI18N
        vars.put("QFILES", qfiles.toString().trim()); // NOI18N
        vars.put("PATHS", paths.delete(paths.length() - 2, paths.length()).toString()); // NOI18N
        vars.put("QPATHS", qpaths.toString().trim()); // NOI18N
        vars.put("MPATHS", mpaths.toString().trim()); // NOI18N
        vars.put("QMPATHS", qmpaths.toString().trim()); // NOI18N
        try {
            vars.put("FILES_FILE_POS_INDEXES", VcsUtilities.encodeValue(fileIndexes[0]));
            vars.put("QFILES_FILE_POS_INDEXES", VcsUtilities.encodeValue(fileIndexes[1]));
            vars.put("PATHS_FILE_POS_INDEXES", VcsUtilities.encodeValue(fileIndexes[2]));
            vars.put("QPATHS_FILE_POS_INDEXES", VcsUtilities.encodeValue(fileIndexes[3]));
            vars.put("MPATHS_FILE_POS_INDEXES", VcsUtilities.encodeValue(fileIndexes[4]));
            vars.put("QMPATHS_FILE_POS_INDEXES", VcsUtilities.encodeValue(fileIndexes[5]));
        } catch (java.io.IOException ioex) {}
        vars.put("NUM_FILES", ""+files.size()); // NOI18N
        vars.put("MULTIPLE_FILES", (files.size() > 1) ? Boolean.TRUE.toString() : ""); // NOI18N
        vars.put("FILES_IS_FOLDER", (isFileFolder) ? Boolean.TRUE.toString() : "");// among FILES there is a folder // NOI18N
        if (greatestParent != null) {
            greatestParent = greatestParent.replace('/', separatorChar);
            greatestParent = valueAdjustment.adjustVarValue(greatestParent);
            vars.put("COMMON_PARENT", greatestParent);
        } else {
            vars.remove("COMMON_PARENT");
        }
        /*
        System.out.println("VARIABLES set after setVariables() (greatestParent = "+greatestParent+")");
        for (Iterator it = new java.util.TreeSet(vars.keySet()).iterator(); it.hasNext(); ) {
            String varName = (String) it.next();
            System.out.println("  "+varName+" = '"+vars.get(varName)+"'");
        }
        System.out.println("");
         *//*
    }
            */
    
    /**
     * Find the greatest parent folder of files.
     *
    private static String findGreatestParent(Table files) {
        String greatestParent = null;
        for (Enumeration enum = files.keys(); enum.hasMoreElements(); ) {
            String fullName = (String) enum.nextElement();
            String parent = VcsUtilities.getDirNamePart(fullName);
            //System.out.println("findGreatestParent: fullName = '"+fullName+"', parent = '"+parent+"', prev greatestParent = '"+greatestParent+"'");
            if (greatestParent == null) {
                greatestParent = parent;
            } else {
                if (!parent.startsWith(greatestParent)) {
                    StringBuffer commonParent = new StringBuffer();
                    for (int i = 0; i < parent.length() && i < greatestParent.length(); i++) {
                        char c = parent.charAt(i);
                        if (greatestParent.charAt(i) != c) break;
                        commonParent.append(c);
                    }
                    // It can happen, that I end up in a middle of a folder name
                    // (e.g. "mySources" and "myLibraries" will end up with "my" which is not a folder name)
                    int end = commonParent.length();
                    if (!(parent.length() == end || parent.charAt(end) == '/' ||
                          greatestParent.length() == end || greatestParent.charAt(end) == '/')) {
                        // I'm somewhere in the middle of a folder name!
                        for (int i = end - 1; i >= 0; i--) {
                            char c = commonParent.charAt(i);
                            commonParent.deleteCharAt(i);
                            if (c == '/') break;
                        }
                    }
                    greatestParent = commonParent.toString();
                }
            }
            //System.out.println("findGreatestParent: new greatestParent = '"+greatestParent+"'");
        }
        if (greatestParent != null) {
            while (greatestParent.endsWith("/")) {
                greatestParent = greatestParent.substring(0, greatestParent.length() - 1);
            }
        }
        return greatestParent;
    }
    
    /**
     * Parse the array of attribute names and variable names into the array
     * of attribute names and two maps with variable names.
     * @param attrNonNullVars this map is filled with pairs of attribute name
     *        and the variable name, that is set when the attribute value is
     *        <code>null</code>.
     * @param attrValueVars this map is filled with pairs of attribute name
     *        and the variable name, that is set to the string representation
     *        of the attribute value.
     * @param attrsToVars the array of attribute names and variable names
     *        as described at {@link VcsCommand.PROPERTY_LOAD_ATTRS_TO_VARS}.
     *        This array can be <code>null</code>.
     *
    private static String[] getAttrNamesAndVars(Map attrNonNullVars, Map attrValueVars, String[] attrsToVars) {
        String[] attrNames = new String[attrsToVars.length / 3];
        for (int i = 0; i < attrsToVars.length - 2; i += 3) {
            String attrName = attrsToVars[i];
            attrNames[i/3] = attrName;
            attrNonNullVars.put(attrName, attrsToVars[i + 1]);
            attrValueVars.put(attrName, attrsToVars[ i + 2]);
        }
        return attrNames;
    }
    
    /**
     * Set the variables from files attributes.
     * @param files the table of files
     * @param vars the map of variables, where the new variables will be filled
     * @param attrsToVars the array of attribute names and variable names
     *        as described at {@link VcsCommand.PROPERTY_LOAD_ATTRS_TO_VARS}.
     *        This array can be <code>null</code>.
     * @return the files for which the attributes were successfully converted
     * to variable values and the values were the same for all of them.
     *
    private static Table setVarsFromAttrs(Table files, Hashtable vars, String[] attrNames,
                                          Map attrNonNullVars, Map attrValueVars) {
        if (attrNames.length == 0) return files;
        Table result = new Table();
        Object[] attrs = getAttributes(result, files, attrNames);
        for (int i = 0; i < attrs.length; i++) {
            vars.put(attrNonNullVars.get(attrNames[i]), (attrs[i] != null) ? Boolean.TRUE.toString() : ""); // NOI18N
            if (attrs[i] != null) vars.put(attrValueVars.get(attrNames[i]), attrs[i].toString());
            else vars.remove(attrValueVars.get(attrNames[i]));
        }
        return result;
    }
     */
    
    /**
     * Get the file attribute values.
     * @param result the table filled with files, which have the same attribute values
     * @param files the table of files
     * @param attrNames the array of file attribute names
     * @return the array of attribute values
     *
    private static Object[] getAttributes(Table result, Table files, String[] attrNames) {
        ArrayList values = new ArrayList();
        Iterator it = files.keySet().iterator();
        if (!it.hasNext()) return values.toArray();
        String name = (String) it.next();
        FileObject fo = (FileObject) files.get(name);
        if (fo == null) {
            values.addAll(Collections.nCopies(attrNames.length, null));
        } else {
            for (int i = 0; i < attrNames.length; i++) {
                values.add(fo.getAttribute(attrNames[i]));
            }
        }
        result.put(name, fo);
        while (it.hasNext()) {
            name = (String) it.next();
            fo = (FileObject) files.get(name);
            if (fo == null) {
                if (!Arrays.equals(values.toArray(), Collections.nCopies(attrNames.length, null).toArray()))
                    continue;
            } else {
                int i;
                for (i = 0; i < attrNames.length; i++) {
                    Object value = values.get(i);
                    Object attr = fo.getAttribute(attrNames[i]);
                    if (!(value == null && attr == null ||
                          value != null && value.equals(attr)))
                        break;
                }
                if (i < attrNames.length) continue;
            }
            result.put(name, fo);
        }
        return values.toArray();
    }

    /** Remove the files for which the command is disabled */
    private static Table removeDisabled(FileStatusProvider statusProvider, Table files, VcsCommand cmd) {
        if (statusProvider == null) return files;
        String disabledStatus = (String) cmd.getProperty(VcsCommand.PROPERTY_DISABLED_ON_STATUS);
        if (disabledStatus == null) return files;
        Table remaining = new Table();
        for (Enumeration enum = files.keys(); enum.hasMoreElements(); ) {
            String name = (String) enum.nextElement();
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
        files = removeDisabled(fileSystem.getStatusProvider(), files, cmd);
        if (VcsCommand.NAME_REFRESH.equals(cmd.getName()) ||
            (VcsCommand.NAME_REFRESH + VcsCommand.NAME_SUFFIX_OFFLINE).equals(cmd.getName())) {
            ArrayList paths = new ArrayList();
            for (Iterator it = files.values().iterator(); it.hasNext(); ) {
                FileObject fo = (FileObject) it.next();
                if (fo == null) continue;
                String path = fo.getPackageName('/');
                if (!paths.contains(path)) {
                    doList(fileSystem, path);
                    paths.add(path);
                }
            }
        } else if (VcsCommand.NAME_REFRESH_RECURSIVELY.equals(cmd.getName()) ||
                   (VcsCommand.NAME_REFRESH_RECURSIVELY + VcsCommand.NAME_SUFFIX_OFFLINE).equals(cmd.getName())) {
            ArrayList paths = new ArrayList();
            for (Iterator it = files.values().iterator(); it.hasNext(); ) {
                FileObject fo = (FileObject) it.next();
                if (fo == null) continue;
                String path = fo.getPackageName('/');
                if (!paths.contains(path)) {
                    CommandExecutorSupport.doRefresh(fileSystem, path, true);
                    paths.add(path);
                }
            }
        } else if (files.size() > 0) {
            if (isExpert && !fileSystem.isExpertMode()) {
                if (additionalVars == null) additionalVars = new Hashtable();
                additionalVars.put(VcsFileSystem.VAR_CTRL_DOWN_IN_ACTION, Boolean.TRUE);
            }
            doCommand(files, cmd, additionalVars, fileSystem);
        }
    }
    
    /*
    protected void performCommand(final String cmdName, final Node[] nodes) {
        //System.out.println("performCommand("+cmdName+")");// on "+nodes.length+" nodes.");
        /* should not be used any more:
        if (cmdName.equals("KILL_ALL_CMDS")) {
            killAllCommands();
            return;
        }
         *//*
        VcsFileSystem fileSystem = (VcsFileSystem) this.fileSystem.get();
        final VcsCommand cmd = fileSystem.getCommand(cmdName);
        if (cmd == null) return;
        if (selectedFileObjects != null) {
            ArrayList allFiles = new ArrayList(selectedFileObjects);
            if (variablesForSelectedFiles != null) {
                Iterator it = variablesForSelectedFiles.keySet().iterator();
                while (it.hasNext()) {
                    Hashtable vars = (Hashtable) it.next();
                    Collection files = (Collection) variablesForSelectedFiles.get(vars);
                    performVcsCommand(cmd, fileSystem, files, CTRL_Down, vars);
                    allFiles.removeAll(files);
                }
                if (allFiles.size() > 0) {
                    performVcsCommand(cmd, fileSystem, allFiles, CTRL_Down);
                }
            } else {
                performVcsCommand(cmd, fileSystem, selectedFileObjects, CTRL_Down);
            }
        } else {
            ArrayList fileObjects = new ArrayList();
            Hashtable additionalVars = new Hashtable();
            for(int i = 0; i < nodes.length; i++) {
                //D.deb("nodes["+i+"]="+nodes[i]); // NOI18N
                if (nodes[i] instanceof VcsGroupNode) {
                    VcsGroupNode grNode = (VcsGroupNode) nodes[i];
                    additionalVars.put(Variables.GROUP_NAME, grNode.getDisplayName());
                    additionalVars.put(Variables.GROUP_DESCRIPTION, grNode.getShortDescription());
                    Enumeration children = nodes[i].getChildren().nodes();
                    while (children.hasMoreElements()) {
                        Node nd = (Node) children.nextElement();
                        DataObject dd = (DataObject) nd.getCookie(DataObject.class);
                        if (dd != null) fileObjects.addAll(dd.files());
                    }
                } else {
                    DataObject dd = (DataObject) (nodes[i].getCookie(DataObject.class));
                    if (dd != null) fileObjects.addAll(dd.files());
                }
            }
            performVcsCommand(cmd, fileSystem, fileObjects, CTRL_Down, additionalVars);
        }
        //String mimeType = null;
        //String path = "";
        /*
        if (selectedFileObjects != null) {
            addImportantFiles(selectedFileObjects, files, processAll, true);
        } else {
            for(int i = 0; i < nodes.length; i++) {
                //D.deb("nodes["+i+"]="+nodes[i]); // NOI18N
                DataObject dd = (DataObject) (nodes[i].getCookie(DataObject.class));
                if (dd != null) addImportantFiles(dd.files(), files, processAll, false);
                else continue;
            }
        }
         *//*
    }
            */
    /*
    public void performAction(Node[] nodes) {
        //D.deb("performAction()"); // NOI18N
        //System.out.println("performAction("+nodes+")");
    }
    
    private static RequestProcessor actionRequestProcesor;
    private static final Object ACTION_LOCK = new Object();

    public void actionPerformed(final java.awt.event.ActionEvent e){
        //D.deb("actionPerformed("+e+")"); // NOI18N
        //System.out.println("actionPerformed("+e+")");
        final String cmdName = e.getActionCommand();
        //D.deb("cmd="+cmd); // NOI18N
        Runnable cpr;
        if (selectedFileObjects != null) {
            cpr = new Runnable() {
                public void run() {
                    performCommand(cmdName, null);
                }
            };
        } else {
            final Node[] nodes = getActivatedNodes();
            cpr = new Runnable() {
                public void run() {
                    performCommand(cmdName, nodes);
                }
            };
        }
        synchronized (ACTION_LOCK) {
            if (actionRequestProcesor == null) {
                actionRequestProcesor = new RequestProcessor("Vcs Action Request Processor"); // NOI18N
            }
        }
        actionRequestProcesor.post(cpr);
        //new Thread(cpr, "Vcs Commands Performing Thread").start(); // NOI18N
    }

    private class CtrlMenuKeyListener implements javax.swing.event.MenuKeyListener {
        public void menuKeyTyped(javax.swing.event.MenuKeyEvent p1) {
        }
        public void menuKeyPressed(javax.swing.event.MenuKeyEvent p1) {
            boolean newCTRL_Down = "Ctrl".equals(p1.getKeyText(p1.getKeyCode())) || p1.isControlDown(); // NOI18N
//            System.out.println("key pressed=" + newCTRL_Down);
//            System.out.println("is down=" + p1.isControlDown());
            changeCtrlSigns(newCTRL_Down);
            CTRL_Down = newCTRL_Down;
        }
        public void menuKeyReleased(javax.swing.event.MenuKeyEvent p1) {
            boolean newCTRL_Down = "Ctrl".equals(p1.getKeyText(p1.getKeyCode())) || !p1.isControlDown(); // NOI18N
//            System.out.println("key Released=" + newCTRL_Down);
//            System.out.println("keykode=" + p1.getKeyText(p1.getKeyCode()));
            changeCtrlSigns(!newCTRL_Down);
            CTRL_Down = !newCTRL_Down;
        }
    }

    private void deselectedMenu() {
        changeCtrlSigns(false);
        CTRL_Down = false;
    }   

    private void changeCtrlSigns(boolean newValue) {
        if (newValue == CTRL_Down) return;
        Iterator it = switchableList.iterator();
        while (it.hasNext()) {
            JMenuItem item = (JMenuItem)it.next();
            String text = item.getText();
            if (newValue) {
                // do turn ctrl sign on
                if (!text.endsWith(advancedOptionsSign)) {text = text + advancedOptionsSign;}
            }   else { 
                // turn it off - ctrl released
                if (text.endsWith(advancedOptionsSign)) {text = text.substring(0, text.length() - advancedOptionsSign.length());}
            }    
            item.setText(text);
        }    
    }
    
    private static class CommandMenu extends JMenuPlus {

        private Node commandRoot;
        private boolean onRoot;
        private boolean onFile;
        private boolean onDir;
        private VcsFileSystem fileSystem;
        private Set statuses;
        private List switchableList;
        private String advancedOptionsSign;
        private boolean removeDisabled;
        private ActionListener listener;
        private boolean popupCreated = false;
        
        public CommandMenu(Node commandRoot, boolean onRoot, boolean onFile,
                           boolean onDir, VcsFileSystem fileSystem, Set statuses,
                           List switchableList, String advancedOptionsSign,
                           boolean removeDisabled, ActionListener listener) {
            super();
            this.commandRoot = commandRoot;
            this.onRoot = onRoot;
            this.onFile = onFile;
            this.onDir = onDir;
            this.fileSystem = fileSystem;
            this.statuses = statuses;
            this.switchableList = switchableList;
            this.advancedOptionsSign = advancedOptionsSign;
            this.removeDisabled = removeDisabled;
            this.listener = listener;
            VcsCommand cmd = (VcsCommand) commandRoot.getCookie(VcsCommand.class);
            if (cmd != null) {
                setText(cmd.getDisplayName());
                String mnemonic = (String) cmd.getProperty(VcsCommand.PROPERTY_LABEL_MNEMONIC);
                if (mnemonic != null && mnemonic.length() > 0) {
                    setMnemonic(mnemonic.charAt(0));
                }
            }
        }
        
        /** Overrides superclass method. Adds lazy popup menu creation
          * if it is necessary. *
        public JPopupMenu getPopupMenu() {
            if (!popupCreated) createPopup();
            return super.getPopupMenu();
        }
        
        private void createPopup() {
            boolean wasSeparator = false;
            boolean wasNullCommand = false;
            Hashtable variables = fileSystem.getVariablesAsHashtable();
            Children children = commandRoot.getChildren();
            for (Enumeration subnodes = children.nodes(); subnodes.hasMoreElements(); ) {
                Node child = (Node) subnodes.nextElement();
                VcsCommand cmd = (VcsCommand) child.getCookie(VcsCommand.class);
                if (cmd == null) {
                    // an extra check to not allow more separators, than appropriate
                    if (!wasSeparator || wasNullCommand) {
                        addSeparator();
                    }
                    wasSeparator = true;
                    wasNullCommand = true;
                    continue;
                }
                wasNullCommand = false;
                //System.out.println("VcsAction.addMenu(): cmd = "+cmd.getName());
                JMenuItem cmdMenu = getCommandMenuItem(cmd, onFile, onDir, onRoot,
                                                       fileSystem, variables, switchableList,
                                                       advancedOptionsSign, listener);
                if (cmdMenu == null) continue;
                boolean disabled = VcsUtilities.isSetContainedInQuotedStrings(
                (String) cmd.getProperty(VcsCommand.PROPERTY_DISABLED_ON_STATUS), statuses);
                //System.out.println("VcsAction: isSetContainedInQuotedStrings("+(String) cmd.getProperty(VcsCommand.PROPERTY_DISABLED_ON_STATUS)+
                //                   ", "+VcsUtilities.arrayToString((String[]) statuses.toArray(new String[0]))+") = "+disabled);
                if (disabled && removeDisabled) continue;
                wasSeparator = false;
                JMenuItem item;
                if (!child.isLeaf()) {
                    JMenu submenu;
                    submenu = new VcsAction.CommandMenu(child, onRoot, onFile,
                                                        onDir, fileSystem, statuses,
                                                        switchableList, advancedOptionsSign,
                                                        removeDisabled, listener);
                    add(submenu);
                    item = submenu;
                } else {
                    item = cmdMenu;
                    //                item.addMenuKeyListener(ctrlListener);
                    add(item);
                }
                if (disabled) {
                    item.setEnabled(false);
                }
            }
            popupCreated = true;
        }

    }
     */

}

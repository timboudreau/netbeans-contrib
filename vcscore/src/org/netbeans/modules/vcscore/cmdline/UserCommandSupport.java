/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.cmdline;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.openide.TopManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.util.UserCancelException;

import org.netbeans.api.vcs.commands.CommandTask;
import org.netbeans.api.vcs.commands.Command;

import org.netbeans.api.vcs.commands.AddCommand;
import org.netbeans.api.vcs.commands.CheckInCommand;
import org.netbeans.api.vcs.commands.CheckOutCommand;
//import org.netbeans.api.vcs.commands.DiffCommand;
//import org.netbeans.api.vcs.commands.HistoryCommand;
import org.netbeans.api.vcs.commands.RemoveCommand;

import org.netbeans.spi.vcs.commands.CommandSupport;
import org.netbeans.spi.vcs.commands.CommandTaskSupport;

import org.netbeans.modules.vcscore.DirReaderListener;
import org.netbeans.modules.vcscore.FileReaderListener;
import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.cache.CacheDir;
import org.netbeans.modules.vcscore.cache.CacheFile;
import org.netbeans.modules.vcscore.cache.CacheHandler;
import org.netbeans.modules.vcscore.cache.FileSystemCache;
import org.netbeans.modules.vcscore.caching.FileCacheProvider;
import org.netbeans.modules.vcscore.commands.ActionCommandSupport;
import org.netbeans.modules.vcscore.commands.CommandCustomizationSupport;
import org.netbeans.modules.vcscore.commands.TextOutputListener;
import org.netbeans.modules.vcscore.commands.TextErrorListener;
import org.netbeans.modules.vcscore.commands.RegexOutputListener;
import org.netbeans.modules.vcscore.commands.RegexErrorListener;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;
import org.netbeans.modules.vcscore.commands.VcsCommandIO;
import org.netbeans.modules.vcscore.commands.VcsCommandVisualizer;
import org.netbeans.modules.vcscore.commands.VcsDescribedCommand;
import org.netbeans.modules.vcscore.util.Table;
import org.netbeans.modules.vcscore.util.VariableInputDialog;
import org.netbeans.modules.vcscore.util.VariableValueAdjustment;
import org.netbeans.modules.vcscore.util.VcsUtilities;

/**
 * The adapter from UserCommand to CommandSupport. This class is used to
 * transfer the VcsCommand approach to the new org.netbeans.api.vcs.commands.Command
 * approach.
 * @author  Martin Entlicher
 */
public class UserCommandSupport extends CommandSupport implements java.security.PrivilegedAction,
                                                                  ActionCommandSupport {
    
    private static final String PROPERTY_PARSED_ATTR_NAMES = VcsCommand.PROP_NAME_FOR_INTERNAL_USE_ONLY + "FOAttributesNamesParsed"; // NOI18N
    private static final String PROPERTY_PARSED_ATTR_NEMPTY_VARS = VcsCommand.PROP_NAME_FOR_INTERNAL_USE_ONLY + "FOAttributesNotEmptyVars"; // NOI18N
    private static final String PROPERTY_PARSED_ATTR_VALUES_VARS = VcsCommand.PROP_NAME_FOR_INTERNAL_USE_ONLY + "FOAttributesValuesVars"; // NOI18N
    
    private UserCommand cmd;
    private VcsFileSystem fileSystem;
    private String displayName;
    /** A flag of whether the command associated with this clon of
     * UserCommandSupport was already customized or not, so that we know whether
     * a customization needs to be done before the execution. */
    //private boolean isCommandCustomized = false;
    
    /** Creates a new instance of UserCommandSupport */
    public UserCommandSupport(UserCommand cmd, VcsFileSystem fileSystem) {
        super(getClassesForCommand(cmd));
        this.cmd = cmd;
        this.fileSystem = fileSystem;
        this.displayName = getDisplayName(cmd, fileSystem);
    }
    
    private static Class[] getClassesForCommand(UserCommand cmd) {
        List classes = new ArrayList();
        classes.add(VcsDescribedCommand.class);
        classes.add(CustomizationStatus.class);
        if (cmd.NAME_GENERIC_ADD.equals(cmd.getName())) {
            classes.add(AddCommand.class);
        } else if (cmd.NAME_GENERIC_CHECKIN.equals(cmd.getName())) {
            classes.add(CheckInCommand.class);
        } else if (cmd.NAME_GENERIC_CHECKOUT.equals(cmd.getName())) {
            classes.add(CheckOutCommand.class);
        //} else if (cmd.NAME_GENERIC_DIFF.equals(cmd.getName())) {
        //    classes.add(DiffCommand.class);
        //} else if (cmd.NAME_GENERIC_HISTORY.equals(cmd.getName())) {
        //    classes.add(HistoryCommand.class);
        } else if (cmd.NAME_GENERIC_REMOVE.equals(cmd.getName())) {
            classes.add(RemoveCommand.class);
        }
        return (Class[]) classes.toArray(new Class[classes.size()]);
    }
    
    private static String getDisplayName(UserCommand cmd, VcsFileSystem fileSystem) {
        String label = cmd.getDisplayName();
        if (label != null) {
            label = Variables.expand(fileSystem.getVariablesAsHashtable(), label, false);
            String mnemonic = (String) cmd.getProperty(VcsCommand.PROPERTY_LABEL_MNEMONIC);
            if (mnemonic != null && mnemonic.length() > 0) {
                char mnemonicChar = mnemonic.charAt(0);
                int mnemonicIndex = label.indexOf(mnemonicChar);
                if (mnemonicIndex >= 0) {
                    label = label.substring(0, mnemonicIndex) + "&" + label.substring(mnemonicIndex);
                }
            }
        }
        return label;
    }
    
    /**
     * Get the UserCommand associated with this support.
     */
    public UserCommand getVcsCommand() {
        return cmd;
    }
    
    /**
     * Get the VCS filesystem, that is associated with this support.
     */
    public VcsFileSystem getVcsFileSystem() {
        return fileSystem;
    }
    
    /**
     * Get the name of the command.
     */
    public String getName() {
        return cmd.getName();
    }
    
    /**
     * Get the display name of the command. It will be visible on the popup menu under this name.
     * When <code>null</code>, the command will not be visible on the popup menu.
     */
    public String getDisplayName() {
        return displayName;
    }
    
    /*
     * Get the mnemonic displayed for that command. The mnemonic will be set
     * on the menu item of this command.
     * @return The mnemonic character or <code>null</code> when no mnemonic is needed.
     *
    public char getDisplayedMnemonic() {
        String mnemonic = (String) cmd.getProperty(VcsCommand.PROPERTY_LABEL_MNEMONIC);
        if (mnemonic != null && mnemonic.length() > 0) {
            return mnemonic.charAt(0);
        } else {
            return '\0';
        }
    }
     */
    
    /**
     * Whether the command supports an expert mode. The command should provide
     * a more complex customizer and/or output if in expert mode. If the
     * command does not differentiate expert mode, it should declare, that
     * it does not have an expert mode.
     * @return true If the command differentiate expert mode, false otherwise
     */
    public boolean hasExpertMode() {
        return VcsCommandIO.getBooleanProperty(cmd, VcsCommand.PROPERTY_SUPPORTS_ADVANCED_MODE);
    }
    
    VcsFileSystem getFileSystem() {
        return fileSystem;
    }
    
    protected CommandTaskSupport createTask(Command command) {
        if (!(command instanceof VcsDescribedCommand)) return null;
        //if (!isCommandCustomized) doCustomization(false, command);
        if (!((CustomizationStatus) command).isAlreadyCustomizedUserCommand()) {
            doCustomization(false, (VcsDescribedCommand) command);
        }
        VcsDescribedCommand dCommand = (VcsDescribedCommand) command;
        if (dCommand.getNextCommand() != null) {
            WrappingCommandTask wt = new WrappingCommandTask(this, dCommand);
            wt.runTasks();
            return wt;
        } else {
        //Hashtable vars = fileSystem.getVariablesAsHashtable();
        //vars.putAll(dCommand.getAdditionalVariables());
        //ExecuteCommand ec = new ExecuteCommand(fileSystem, cmd, vars, dCommand.getPreferredExec());
            return new UserCommandTask(this, dCommand);//, ec);
        }
    }
    
    DirReaderListener getAttachedDirReaderListener(Command command) {
        EventListener[] listeners = getListeners(DirReaderListener.class, command);
        return (listeners != null && listeners.length > 0) ? (DirReaderListener) listeners[0] : null;
    }
    
    /* implemented in UserCommandTask instead!
    protected boolean canExecute(CommandTask task) {
    }
     */
    
    /**
     * Perform the actual execution of the command from the provided info.
     */
    protected int execute(CommandTask task) {
        if (task instanceof WrappingCommandTask) return ((WrappingCommandTask) task).waitForTasks();
        Command command = getCommand(task);
        if (!(command instanceof VcsDescribedCommand)) return task.STATUS_FAILED;
        if (!(task instanceof UserCommandTask)) return task.STATUS_FAILED;
        UserCommandTask userTask = (UserCommandTask) task;
        if (userTask.willSpawnRefresh()) {
            userTask.spawnRefresh();
            return task.STATUS_SUCCEEDED;
        }
        VcsDescribedCommand dCommand = (VcsDescribedCommand) command;
        //Hashtable vars = fileSystem.getVariablesAsHashtable();
        //vars.putAll(dCommand.getAdditionalVariables());
        VcsCommandExecutor ec = userTask.getExecutor();//new ExecuteCommand(fileSystem, cmd, vars, dCommand.getPreferredExec());
        //if (ec == null) return task.STATUS_FAILED; ec must NOT be null !
        java.util.EventListener[] listeners = getListeners(TextOutputListener.class, command);
        if (listeners != null) {
            for (int i = 0; i < listeners.length; i++) {
                ec.addTextOutputListener((TextOutputListener) listeners[i]);
            }
        }
        listeners = getListeners(TextErrorListener.class, command);
        if (listeners != null) {
            for (int i = 0; i < listeners.length; i++) {
                ec.addTextErrorListener((TextErrorListener) listeners[i]);
            }
        }
        listeners = getListeners(RegexOutputListener.class, command);
        if (listeners != null) {
            for (int i = 0; i < listeners.length; i++) {
                ec.addRegexOutputListener((RegexOutputListener) listeners[i]);
            }
        }
        listeners = getListeners(RegexErrorListener.class, command);
        if (listeners != null) {
            for (int i = 0; i < listeners.length; i++) {
                ec.addRegexErrorListener((RegexErrorListener) listeners[i]);
            }
        }
        listeners = getListeners(FileReaderListener.class, command);
        if (listeners != null) {
            for (int i = 0; i < listeners.length; i++) {
                ec.addFileReaderListener((FileReaderListener) listeners[i]);
            }
        }
        Map additionalVariables = dCommand.getAdditionalVariables();
        Hashtable vars = ec.getVariables();
        if (additionalVariables != null) {
            vars.putAll(additionalVariables);
        }
        ec.preprocessCommand(cmd, vars, dCommand.getPreferredExec());
        VcsCommandVisualizer visualizer = ec.getVisualizer();
        if (visualizer != null) {
            if (!visualizer.openAfterCommandFinish()) visualizer.open();
        }
        if (VcsCommandIO.getBooleanProperty(ec.getCommand(), VcsCommand.PROPERTY_DISPLAY_PLAIN_OUTPUT)) {
            visualizer = userTask.getVisualizer(true);
            if (visualizer != null) visualizer.open();
        }
        try {
            ec.run();
        } finally {
            visualizer = ec.getVisualizer();
            if (visualizer != null) {
                visualizer.setExitStatus(ec.getExitStatus());
                if (visualizer.openAfterCommandFinish()) visualizer.open();
            }
        }
        return ec.getExitStatus();
    }
    
    /**
     * Find, whether this command can act on a set of files.
     * @param files The array of files to inspect
     * @return an array of files the command can act on or <code>null</code> when
     * it can not act on any file listed.
     */
    public FileObject[] getApplicableFiles(FileObject[] files) {
        // If the FS is offline, the command must end with _OFFLINE
        // If the FS is not offline, search for the corresponding offline command
        boolean offLine;
        if ((offLine = fileSystem.isOffLine()) != getName().endsWith(VcsCommand.NAME_SUFFIX_OFFLINE)) {
            if (offLine && fileSystem.getCommand(getName() + VcsCommand.NAME_SUFFIX_OFFLINE) != null) {
                return null;
            }
            if (!offLine) return null;
        }
        // If the command can run only on some number of revisions, return null
        // if no revision is specified.
        if (VcsCommandIO.getIntegerPropertyAssumeZero(cmd, VcsCommand.PROPERTY_NUM_REVISIONS) != 0) {
            Command cmd = getCommand();
            if (cmd != null) {
                Map vars = ((VcsDescribedCommand) cmd).getAdditionalVariables();
                if (vars == null || vars.get("REVISION") == null) {
                    return null;
                }
            } else {
                return null;
            }
        }
        files = VcsUtilities.convertFileObjects(files);
        FileObject[] appFiles = CommandCustomizationSupport.getApplicableFiles(fileSystem, cmd, files);
        //System.out.println("getApplicableFiles("+cmd+", "+new ArrayList(Arrays.asList(files))+") = "+
        //                   ((appFiles == null) ? null : new ArrayList(Arrays.asList(appFiles))));
        //Thread.dumpStack();
        return appFiles;
        //return files;
    }
    
    protected Object clone() throws CloneNotSupportedException {
        UserCommandSupport clone = new UserCommandSupport(cmd, fileSystem);
        return clone;
    }
    
    /**
     * Initialize the command after it's created. This allows the implementator
     * to preset some initial values to the command before it's customized.
     * When sb. is just creates a command through VcsManager.createCommand(..)
     * they will get this customized command.
     * @param cmd The command to be customized.
     */
    protected void initializeCommand(Command cmd) {
        super.initializeCommand(cmd);
        VcsDescribedCommand vcmd = (VcsDescribedCommand) cmd;
        vcmd.setVcsCommand(this.cmd);
        FileSystemCache cache = CacheHandler.getInstance().getCache(fileSystem.getCacheIdStr());
        if (cache instanceof FileReaderListener) {
            vcmd.addFileReaderListener((FileReaderListener) cache);
        }
    }
    
    /**
     * Create the customizer for the command. This uses a hack through the PrivilegedAction.
     */
    public Object run() {
        Command cmd = getCommand();
        if (!(cmd instanceof VcsDescribedCommand)) {
            throw new IllegalArgumentException("Command "+cmd+" is not an instance of VcsDescribedCommand!");
        }
        return doCustomization(true, (VcsDescribedCommand) cmd);
    }
    
    private static final String QUOTING = "${QUOTE}"; // NOI18N
    
    /**
     * Perform the basic customization and preprocessing of the command.
     * This needs to be done even if a GUI customizer is not desired.
     */
    private Object doCustomization(boolean doCreateCustomizer, VcsDescribedCommand cmd) {
        //isCommandCustomized = true;
        Table files = getFilesToActOn(cmd);
        //System.out.println("\ndoCustomization("+doCreateCustomizer+", "+cmd.getVcsCommand()+"), files = "+files);
        if (files.size() == 0) {
            // In case, that sb. executes a command, that is either partially customized
            // or does not need any files. No variables will be set from files then.
            // This will happen when e.g.:
            // VcsCommandExecutor vce = fileSystem.getVcsFactory().getCommandExecutor(logCmd, vars);
            // fileSystem.getCommandsPool().startExecutor(vce, fileSystem);
            files = null;
        }
        if (files != null && VcsCommandIO.getBooleanPropertyAssumeDefault(this.cmd, VcsCommand.PROPERTY_NEEDS_HIERARCHICAL_ORDER)) {
            files = createHierarchicalOrder(files);
        }
        boolean cmdCanRunOnMultipleFiles = VcsCommandIO.getBooleanPropertyAssumeDefault(this.cmd, VcsCommand.PROPERTY_RUN_ON_MULTIPLE_FILES);
        boolean cmdCanRunOnMultipleFilesInFolder = VcsCommandIO.getBooleanPropertyAssumeDefault(this.cmd, VcsCommand.PROPERTY_RUN_ON_MULTIPLE_FILES_IN_FOLDER);
        VariableValueAdjustment valueAdjustment = fileSystem.getVarValueAdjustment();
        FileCacheProvider cacheProvider = fileSystem.getCacheProvider();
        Object obj = doCustomization(doCreateCustomizer, null, cmd, files, cacheProvider,
                                     valueAdjustment, cmdCanRunOnMultipleFiles,
                                     cmdCanRunOnMultipleFilesInFolder);
        //System.out.println("AFTER doCustomization("+doCreateCustomizer+", "+cmd.getVcsCommand()+"), files = "+files+", MODULE = "+cmd.getAdditionalVariables().get("MODULE")+", DIR = "+cmd.getAdditionalVariables().get("DIR"));
        return obj;
    }
        
    private Object doCustomization(boolean doCreateCustomizer,
                                   UserCommandCustomizer customizer,
                                   VcsDescribedCommand cmd,
                                   Table files, FileCacheProvider cacheProvider,
                                   VariableValueAdjustment valueAdjustment,
                                   boolean cmdCanRunOnMultipleFiles,
                                   boolean cmdCanRunOnMultipleFilesInFolder) {
        //System.out.println("\ndoCustomization("+doCreateCustomizer+", "+customizer+", "+cmd+", "+files+", "+cmdCanRunOnMultipleFiles+", "+cmdCanRunOnMultipleFilesInFolder+")");
        boolean forEachFile[] = null;
        Hashtable vars = fileSystem.getVariablesAsHashtable();
        Map additionalVars = cmd.getAdditionalVariables();
        if (additionalVars != null) vars.putAll(additionalVars);
        if (cmd.isExpertMode() && !fileSystem.isExpertMode()) {
            vars.put(VcsFileSystem.VAR_CTRL_DOWN_IN_ACTION, Boolean.TRUE);
        }
        if (files != null && files.size() > 1) {
            forEachFile = new boolean[] { true };
        }
        return doCustomization(doCreateCustomizer, customizer, cmd, files, cacheProvider,
                               valueAdjustment, vars, forEachFile,
                               cmdCanRunOnMultipleFiles, cmdCanRunOnMultipleFilesInFolder);
    }
    
    private Object doCustomization(boolean doCreateCustomizer,
                                   UserCommandCustomizer customizer,
                                   VcsDescribedCommand cmd,
                                   Table files, FileCacheProvider cacheProvider,
                                   VariableValueAdjustment valueAdjustment,
                                   Hashtable vars, boolean[] forEachFile,
                                   boolean cmdCanRunOnMultipleFiles,
                                   boolean cmdCanRunOnMultipleFilesInFolder) {
        //System.out.println("\ndoCustomization("+doCreateCustomizer+", "+customizer+", "+cmd+", "+files+", "+forEachFile+", "+cmdCanRunOnMultipleFiles+", "+cmdCanRunOnMultipleFilesInFolder+")");
        //Object customizer = null;
        VcsCommand vcsCmd = cmd.getVcsCommand();
        Table subFiles;
        if (files != null) {
            subFiles = setupRestrictedFileMap(files, vars, vcsCmd);
            setVariables(subFiles, vars, QUOTING, valueAdjustment, cacheProvider,
                         fileSystem.getRelativeMountPoint(), true);
        } else {
            subFiles = null;
        }
        //System.out.println("subFiles = "+subFiles+", files = "+files+", MODULE = "+vars.get("MODULE")+", DIR = "+vars.get("DIR"));
        //Hashtable vars = fileSystem.getVariablesAsHashtable();
        //System.out.println("\nVARS for cmd = "+cmd+" ARE:"+vars+"\n");
        String newExec = CommandCustomizationSupport.preCustomize(fileSystem, vcsCmd, vars);
        Object finalCustomizer = null;
        if (newExec != null && doCreateCustomizer) {
            finalCustomizer = createCustomizer(customizer, newExec, vars, forEachFile,
                                               cmd, files, cacheProvider, valueAdjustment,
                                               cmdCanRunOnMultipleFiles,
                                               cmdCanRunOnMultipleFilesInFolder);
            if (finalCustomizer instanceof UserCommandCustomizer) {
                customizer = (UserCommandCustomizer) finalCustomizer;
            } else customizer = null;
        }
        if (newExec != null) cmd.setPreferredExec(newExec);
        cmd.setAdditionalVariables(vars);
        //System.out.println("subFiles = "+subFiles+", files = "+files+", MODULE = "+cmd.getAdditionalVariables().get("MODULE")+", DIR = "+cmd.getAdditionalVariables().get("DIR"));
        if (finalCustomizer == null && files != null) {
            VcsDescribedCommand lastCmd = cmd;
            if (!cmdCanRunOnMultipleFiles || cmdCanRunOnMultipleFilesInFolder) {
                lastCmd = createNextCustomizedCommand(cmd, subFiles, cacheProvider,
                                                      valueAdjustment, vars,
                                                      cmdCanRunOnMultipleFiles,
                                                      cmdCanRunOnMultipleFilesInFolder);
            }
            for (Iterator it = subFiles.keySet().iterator(); it.hasNext(); ) {
                files.remove(it.next());
            }
            // If there is no customizer, so let's continue with the rest of the files
            if (files.size() > 0) {
                VcsDescribedCommand nextCmd = createNextCommand(files, lastCmd);
                // Do not attempt to create a customizer again if it was already null
                doCustomization(false, null, nextCmd, files, cacheProvider,
                                valueAdjustment, new Hashtable(vars), forEachFile,
                                cmdCanRunOnMultipleFiles, cmdCanRunOnMultipleFilesInFolder);
            }
        }
        ((CustomizationStatus) cmd).setAlreadyCustomizedUserCommand(true);
        //System.out.println("\nUserCommandSupport.doCustomization("+doCreateCustomizer+") = "+customizer);
        return finalCustomizer;
    }
        
    private VcsDescribedCommand createNextCustomizedCommand(VcsDescribedCommand cmd,
                                                            Table files,
                                                            FileCacheProvider cacheProvider,
                                                            VariableValueAdjustment valueAdjustment,
                                                            Hashtable vars,
                                                            boolean cmdCanRunOnMultipleFiles,
                                                            boolean cmdCanRunOnMultipleFilesInFolder) {
        //System.out.println("createNextCustomizedCommand("+cmd+", "+files+")");
        Table subFiles = setupRestrictedFileMap(files, cmdCanRunOnMultipleFiles,
                                                cmdCanRunOnMultipleFilesInFolder);
        setVariables(subFiles, vars, QUOTING, valueAdjustment, cacheProvider,
                     fileSystem.getRelativeMountPoint(), true);
        cmd.setAdditionalVariables(vars);
        // Suppose, that the command is already preprocessed.
        //System.out.println("RestrictedFileMap = "+subFiles+", files = "+files+", MODULE = "+cmd.getAdditionalVariables().get("MODULE")+", DIR = "+cmd.getAdditionalVariables().get("DIR"));
        //System.out.println("\nVARS for cmd = "+cmd+" ARE:"+vars+"\n");
        if (files.size() != subFiles.size()) {
            Table remaining = new Table();
            for (Enumeration en = files.keys(); en.hasMoreElements(); ) {
                Object singleFile = en.nextElement();
                if (!subFiles.containsKey(singleFile)) {
                    remaining.put(singleFile, files.get(singleFile));
                }
            }
            VcsDescribedCommand nextCmd = createNextCommand(remaining, cmd);
            Hashtable newVars = new Hashtable(vars);
            VcsDescribedCommand nextCustomizedCommand =
                   createNextCustomizedCommand(nextCmd, remaining, cacheProvider,
                                               valueAdjustment, newVars,
                                               cmdCanRunOnMultipleFiles,
                                               cmdCanRunOnMultipleFilesInFolder);
            String newExec = CommandCustomizationSupport.preCustomize(fileSystem, nextCmd.getVcsCommand(), newVars);
            if (newExec != null) nextCmd.setPreferredExec(newExec);
            nextCmd.setAdditionalVariables(newVars);
            return nextCustomizedCommand;
        }
        //System.out.println("  return "+cmd);
        return cmd;
    }
    
    /**
     * Create a new command acting on the given files with the basic properties
     * inherited from the old command. It also sets the new command as the next
     * for the old one.
     */
    private VcsDescribedCommand createNextCommand(Table files, VcsDescribedCommand oldCommand) {
        Command command = createCommand();
        if (!(command instanceof VcsDescribedCommand)) {
            throw new IllegalArgumentException("Command "+command+" is not an instance of VcsDescribedCommand!");
        }
        setCommandFilesFromTable(command, files, fileSystem);
        command.setExpertMode(oldCommand.isExpertMode());
        command.setGUIMode(oldCommand.isGUIMode());
        oldCommand.setNextCommand(command);
        return (VcsDescribedCommand) command;
    }
    
    /** Return the table of file names relative to filesystem and associated
     * FileObjects.
     */
    private Table getFilesToActOn(Command cmd) {
        Table files = new Table();
        FileObject[] fos = cmd.getFiles();
        if (fos != null) {
            for (int i = 0; i < fos.length; i++) {
                files.put(fos[i].getPath(), fos[i]);
            }
        }
        if (cmd instanceof VcsDescribedCommand) {
            java.io.File[] diskFiles = ((VcsDescribedCommand) cmd).getDiskFiles();
            if (diskFiles != null) {
                String root = fileSystem.getFile("").getAbsolutePath();
                for (int i = 0; i < diskFiles.length; i++) {
                    String path = diskFiles[0].getAbsolutePath();
                    if (path.indexOf(root) == 0) {
                        path = path.substring(root.length());
                        while (path.startsWith(java.io.File.separator)) path = path.substring(1);
                    }
                    files.put(path.replace(java.io.File.separatorChar, '/'), null);
                }
            }
        }
        return files;
    }
    
    private Object createCustomizer(UserCommandCustomizer customizer,
                                    String newExec, final Hashtable vars,
                                    final boolean[] forEachFile,
                                    final VcsDescribedCommand command,
                                    final Table files,
                                    final FileCacheProvider cacheProvider,
                                    final VariableValueAdjustment valueAdjustment,
                                    final boolean cmdCanRunOnMultipleFiles,
                                    final boolean cmdCanRunOnMultipleFilesInFolder) {
        StringBuffer title = new StringBuffer();
        final VariableInputDialog dlg;
        try {
            dlg = CommandCustomizationSupport.createInputDialog(fileSystem, newExec, vars, cmd, forEachFile, title);
        } catch (UserCancelException ucex) {
            return ucex;
        }
        //System.out.println("\ncreateCustomizer("+customizer+", "+files+", "+forEachFile+"), dlg = "+dlg);
        if (dlg == null) return null;
        if (customizer == null) customizer = new UserCommandCustomizer();
        final UserCommandCustomizer finalCustomizer = customizer;
        customizer.setCommand(command, dlg, fileSystem, title.toString());
        dlg.addCloseListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                boolean isPromptForEachFile = dlg.getPromptForEachFile();
                if (dlg.isValidInput() && forEachFile != null) {
                    forEachFile[0] = isPromptForEachFile;
                    fileSystem.setPromptForVarsForEachFile(forEachFile[0]);
                }
                //System.out.println("\n!!close listener: isPromptForEachFile = "+isPromptForEachFile);
                if (files == null) return ;
                if (isPromptForEachFile) {
                    Object singleFile = files.keys().nextElement();
                    Table subFiles = new Table();
                    subFiles.put(singleFile, files.get(singleFile));
                    setVariables(subFiles, vars, QUOTING, valueAdjustment, cacheProvider,
                                 fileSystem.getRelativeMountPoint(), true);
                    command.setAdditionalVariables(vars);
                    //System.out.println("RestrictedFileMap = "+subFiles+", files = "+files+", MODULE = "+command.getAdditionalVariables().get("MODULE")+", DIR = "+command.getAdditionalVariables().get("DIR"));
                    //System.out.println("\nVARS for cmd = "+command+" ARE:"+vars+"\n");
                    VcsDescribedCommand nextCmd = createNextCommand(files, command);
                    files.remove(singleFile);
                    doCustomization(true, finalCustomizer, nextCmd, files, cacheProvider,
                                    valueAdjustment, cmdCanRunOnMultipleFiles,
                                    cmdCanRunOnMultipleFilesInFolder);
                } else {
                    VcsCommand vcsCmd = command.getVcsCommand();
                    Table subFiles = setupRestrictedFileMap(files, vars, vcsCmd);
                    VcsDescribedCommand lastCmd = command;
                    if (!cmdCanRunOnMultipleFiles || cmdCanRunOnMultipleFilesInFolder) {
                        lastCmd = createNextCustomizedCommand(command, subFiles, cacheProvider,
                                                              valueAdjustment, vars,
                                                              cmdCanRunOnMultipleFiles,
                                                              cmdCanRunOnMultipleFilesInFolder);
                    }
                    for (Iterator it = subFiles.keySet().iterator(); it.hasNext(); ) {
                        files.remove(it.next());
                    }
                    // I'm customized, but I have to setup commands for the rest of the files.
                    if (files.size() > 0) {
                        VcsDescribedCommand nextCmd = createNextCommand(files, lastCmd);
                        // Do not attempt to create a customizer again
                        doCustomization(false, null, nextCmd, files, cacheProvider,
                                        valueAdjustment, new Hashtable(vars), forEachFile,
                                        cmdCanRunOnMultipleFiles, cmdCanRunOnMultipleFilesInFolder);
                    }
                }
            }
        });
        //System.out.println("createCustomizer() = "+customizer);
        return customizer;
    }
    
    /**
     * Get the class of the action.
     */
    public Class getActionClass() {
        Class actionClass = (Class) cmd.getProperty(VcsCommand.PROP_NAME_FOR_INTERNAL_USE_ONLY + "generalCommandActionClass"); // NOI18N
        if (actionClass == null) {
            Object actionClassNameObj = cmd.getProperty(VcsCommand.PROPERTY_GENERAL_COMMAND_ACTION_CLASS_NAME);
            if (actionClassNameObj instanceof String) {
                String actionClassName = (String) actionClassNameObj;
                try {
                    actionClass = Class.forName(actionClassName, false,
                                                TopManager.getDefault().currentClassLoader());
                } catch (ClassNotFoundException e) {
                    TopManager.getDefault().notifyException(
                        TopManager.getDefault().getErrorManager().annotate(e,
                            NbBundle.getMessage(UserCommandSupport.class, "EXC_CouldNotFindAction",
                            actionClassName)));
                }
            }
            if (actionClass != null) cmd.setProperty(VcsCommand.PROP_NAME_FOR_INTERNAL_USE_ONLY + "generalCommandActionClass", actionClass); // NOI18N
        }
        return actionClass;
    }
    
    /**
     * Get the display name of the action.
     */
    public String getActionDisplayName() {
        return (String) cmd.getProperty(VcsCommand.PROPERTY_GENERAL_COMMAND_ACTION_DISPLAY_NAME);
    }
    
    
    // Setup stuff:
    
    /**
     * Set the files as arguments to the command. All FileObjects from the table
     * are set via setFiles() method, all other are converted to java.io.Files
     * through fileSystem.getFile(name) and set to the command via setDiskFiles()
     * method.
     * @param command The command to set the files on.
     * @param files The table of file names and associated FileObjects.
     * @param fileSystem The filesystem to get the java.io.Files from.
     */
    public static void setCommandFilesFromTable(Command command, Table files, VcsFileSystem fileSystem) {
        ArrayList diskFiles = new ArrayList();
        ArrayList foFiles = new ArrayList();
        for (Iterator it = files.keySet().iterator(); it.hasNext(); ) {
            String name = (String) it.next();
            FileObject fo = (FileObject) files.get(name);
            if (fo != null) {
                foFiles.add(fo);
            } else {
                diskFiles.add(fileSystem.getFile(name));
            }
        }
        FileObject[] fos = (FileObject[]) foFiles.toArray(new FileObject[foFiles.size()]);
        command.setFiles(fos);
        if (command instanceof VcsDescribedCommand) {
            VcsDescribedCommand dcmd = (VcsDescribedCommand) command;
            if (diskFiles.size() > 0) {
                dcmd.setDiskFiles((java.io.File[]) diskFiles.toArray(new java.io.File[diskFiles.size()]));
            }
        }
    }
    
    /** Reorder the table of files by the path hierarchical order.
     * @param files the table of pairs of files and file objects
     * @return the reordered table
     */
    private static Table createHierarchicalOrder(Table files) {
        TreeMap sorted = new TreeMap(files);
        Table sortedFiles = new Table();
        for (Iterator it = sorted.keySet().iterator(); it.hasNext(); ) {
            Object key = it.next();
            sortedFiles.put(key, files.get(key));
        }
        return sortedFiles;
    }
    
    private static Table setupRestrictedFileMap(Table files, boolean cmdCanRunOnMultipleFiles,
                                                boolean cmdCanRunOnMultipleFilesInFolder) {
        Table subFiles = new Table();
        if (!cmdCanRunOnMultipleFiles && !cmdCanRunOnMultipleFilesInFolder) {
            Object singleFile = files.keys().nextElement();
            subFiles.put(singleFile, files.get(singleFile));
        } else if (cmdCanRunOnMultipleFilesInFolder) {
            Enumeration keys = files.keys();
            String file = (String) keys.nextElement();
            subFiles.put(file, files.get(file));
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
                    subFiles.put(file, files.get(file));
                }
            }
        }
        return subFiles;
    }

    private static Table setupRestrictedFileMap(Table files, Hashtable vars, VcsCommand cmd) {
        String[] attrsToVars = (String[]) cmd.getProperty(VcsCommand.PROPERTY_LOAD_ATTRS_TO_VARS);
        if (attrsToVars != null) {
            files = getAttributeRestrictedFileMap(files, vars, cmd, attrsToVars);
        }
        if (Boolean.TRUE.equals(cmd.getProperty(VcsCommand.PROPERTY_DISTINGUISH_BINARY_FILES))) {
            files = getBinaryRestrictedFileMap(files, vars, cmd);
        }
        return files;
    }
    
    private static Table getAttributeRestrictedFileMap(Table files, Hashtable vars, VcsCommand cmd, String[] attrsToVars) {
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
            subFiles = setVarsFromAttrs(files, vars, attrNames, attrNonNullVars, attrValueVars);
        } else {
            subFiles = files;
        }
        return subFiles;
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
     */
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
    
    private static Table getBinaryRestrictedFileMap(Table files, Hashtable vars, VcsCommand cmd) {
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
        vars.put("PROCESSING_BINARY_FILES", isBinary ? Boolean.TRUE.toString() : ""); // NOI18N
        return restrictedFiles;
    }
    
    private static boolean isFOBinary(FileObject fo) {
        String mimeType = fo.getMIMEType();
        return !mimeType.startsWith("text") && !"content/unknown".equals(mimeType); // NOI18N
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
     */
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
    
    /**
     * Get the file attribute values.
     * @param result the table filled with files, which have the same attribute values
     * @param files the table of files
     * @param attrNames the array of file attribute names
     * @return the array of attribute values
     */
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
     */
    private static void setVariables(Table files, Hashtable vars, String quoting,
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
         */
    }
    
    /**
     * Find the greatest parent folder of files.
     */
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
     * This interface represents the customization status of the command.
     */
    private static interface CustomizationStatus extends Command {
        
        public boolean isAlreadyCustomizedUserCommand();
        
        public void setAlreadyCustomizedUserCommand(boolean customized);
        
    }
}

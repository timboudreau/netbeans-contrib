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
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JCheckBox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.WeakHashMap;

import org.openide.NotifyDescriptor;
import org.openide.DialogDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileStateInvalidException;
import org.openide.util.UserCancelException;

import org.netbeans.modules.vcscore.VcsFileSystem;
//import org.netbeans.modules.vcscore.VcsAction;
import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.RetrievingDialog;
import org.netbeans.modules.vcscore.VcsAttributes;
import org.netbeans.modules.vcscore.VcsConfigVariable;
import org.netbeans.modules.vcscore.caching.FileCacheProvider;
import org.netbeans.modules.vcscore.caching.FileStatusProvider;
import org.netbeans.modules.vcscore.cmdline.UserCommandSupport;
import org.netbeans.modules.vcscore.cmdline.exec.StructuredExec;
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
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;

/**
 * This class contains a support for VCS commands customization.
 *
 * @author  Martin Entlicher
 */
public class CommandCustomizationSupport extends Object {
    
    /**
     * The name of the variable, where the global input descriptor is stored.
     */
    public static final String GLOBAL_INPUT_DESCRIPTOR = "GLOBAL_INPUT_DESCRIPTOR";
    /**
     * The name of the variable, where the global input expression is stored. This expression
     * will be inserted to the execution string instead of ${USER_GLOBAL_PARAM}
     */
    public static final String GLOBAL_INPUT_EXPRESSION = "GLOBAL_INPUT_EXPRESSION";
    
    /**
     * The name of the variable for the global additional parameters.
     */
    private static final String USER_GLOBAL_PARAM = "USER_GLOBAL_PARAM";
    /**
     * The name of the variable for the local additional parameters.
     */
    private static final String USER_PARAM = "USER_PARAM";
    
    public static final String INPUT_DESCRIPTOR_PARSED = VcsCommand.PROP_NAME_FOR_INTERNAL_USE_ONLY + "_INPUT_DESCRIPTOR_PARSED";
        
    private static final String VAR_INPUT_MULTIPLE_FILES_TITLE_APPEND = " ...";
    private static final String VAR_INPUT_FILE_SEPARATOR = " - ";

    /** Creates new CommandCustomizationSupport */
    private CommandCustomizationSupport() {
    }
    
    /*
    public static int preprocessCommand(VcsFileSystem fileSystem, VcsCommandExecutor vce, Hashtable vars) {
        return preprocessCommand(fileSystem, vce, vars, CommandsPool.PREPROCESS_DONE, null);
    }
     */
    
    /**
     * Find out, the number of important files among these paths.
     * @param paths the files paths delimited by double File.separator
     * @param ps the path separator
     * @return the number of important files
     */
    private static int numImportant(VcsFileSystem fileSystem, String paths, String ps) {
        //System.out.println("numImportant("+paths+", "+ps+")");
        if (paths == null) return 0; // Just for robustness
        int num = 0;
        String delim;
        if (ps != null) {
            delim = ps+ps;
        } else {
            delim = java.io.File.separator + java.io.File.separator;
        }
        VariableValueAdjustment varValueAdjust = fileSystem.getVarValueAdjustment();
        int begin = 0;
        int end = paths.indexOf(delim);
        if (end < 0) end = paths.length();
        while (true) {
            String path = paths.substring(begin, end);
            //System.out.println("  path = "+path);
            path = varValueAdjust.revertAdjustedVarValue(path);
            //System.out.println("  rev. = "+path);
            if (fileSystem.isImportant(path)) num++;
            //System.out.println("  isImportant = "+fileSystem.isImportant(path));
            begin = end + delim.length();
            if (begin > paths.length()) break;
            end = paths.indexOf(delim, begin);
            if (end < 0) end = paths.length();
        }
        return num;
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
                    res.put(ff.getPath(), ff);
                    continue;
                }
                if (!doNotTestFS && ff.getFileSystem() != fileSystem)
                    continue;
            } catch (FileStateInvalidException exc) {
                continue;
            }
            String fileName = ff.getPath();
            //VcsFile file = fileSystem.getCache().getFile(fileName);
            //D.deb("file = "+file+" for "+fileName);
            //if (file == null || file.isImportant()) {
            if (all || fileSystem == null || fileSystem.isImportant(fileName)) {
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
    private static Table removeDisabled(VcsFileSystem fileSystem,
                                        Table files, VcsCommand cmd) {
        FileStatusProvider statusProvider = fileSystem.getStatusProvider();
        if (statusProvider == null) return files;
        String disabledStatus = (String) cmd.getProperty(VcsCommand.PROPERTY_DISABLED_ON_STATUS);
        if (disabledStatus != null) {
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
            files = remaining;
        }
        boolean disabledWhenNotLocked = VcsCommandIO.getBooleanProperty(cmd, VcsCommand.PROPERTY_DISABLED_WHEN_NOT_LOCKED);
        String disabledWhenNotLockedConditionedStr = (String) cmd.getProperty(VcsCommand.PROPERTY_DISABLED_WHEN_NOT_LOCKED+"Conditioned");
        if (disabledWhenNotLocked || disabledWhenNotLockedConditionedStr != null) {
            Table remaining = new Table();
            Hashtable vars = fileSystem.getVariablesAsHashtable();
            String currentLocker = (String) vars.get(VcsFileSystem.VAR_LOCKER_USER_NAME);
            if (currentLocker != null) {
                currentLocker = Variables.expand(vars, currentLocker, false);
            }
            if (disabledWhenNotLockedConditionedStr != null) {
                for (Enumeration enum = files.keys(); enum.hasMoreElements(); ) {
                    String name = (String) enum.nextElement();
                    Table varFiles = new Table();
                    varFiles.put(name, files.get(name));
                    Hashtable vvars = new Hashtable(vars);
                    UserCommandSupport.setVariables(varFiles, vvars, fileSystem.getVarValueAdjustment(),
                                                    fileSystem.getCacheProvider(),
                                                    fileSystem.getRelativeMountPoint(), true);
                    String disabledWhenNotLockedConditionedExp = Variables.expand(vvars, disabledWhenNotLockedConditionedStr, false);
                    disabledWhenNotLocked = "true".equalsIgnoreCase(disabledWhenNotLockedConditionedExp);
                    if (disabledWhenNotLocked) {
                        String locker = statusProvider.getFileLocker(name);
                        if (VcsFileSystem.lockerMatch(locker, currentLocker)) {
                            remaining.put(name, files.get(name));
                        }
                    } else {
                        remaining.put(name, files.get(name));
                    }
                }
            } else {
                for (Enumeration enum = files.keys(); enum.hasMoreElements(); ) {
                    String name = (String) enum.nextElement();
                    String locker = statusProvider.getFileLocker(name);
                    if (VcsFileSystem.lockerMatch(locker, currentLocker)) {
                        remaining.put(name, files.get(name));
                    }
                }
            }
            files = remaining;
        }
        return files;
    }
    
    public static FileObject[] getApplicableFiles(CommandExecutionContext executionContext, VcsCommand cmd, FileObject[] files) {
        VcsFileSystem fileSystem;
        if (executionContext instanceof VcsFileSystem) {
            fileSystem = (VcsFileSystem) executionContext;
        } else {
            fileSystem = null;
        }
        boolean processAll = fileSystem != null && (VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_PROCESS_ALL_FILES)
                                                    || fileSystem.isProcessUnimportantFiles());
        Collection fileObjects = new ArrayList();
        boolean isOnFiles = false;
        boolean isOnDirs = false;
        boolean isOnRoot = false;
        for (int i = 0; i < files.length; i++) {
            fileObjects.add(files[i]);
            if (files[i].isFolder()) isOnDirs = true;
            else isOnFiles = true;
            if (files[i].getPath().length() == 0) isOnRoot = true;
        }
        if (isOnRoot) isOnDirs = false;
        if (isOnDirs && !VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_ON_DIR)) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFolder()) fileObjects.remove(files[i]);
            }
        }
        if (isOnFiles && !VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_ON_FILE)) {
            for (int i = 0; i < files.length; i++) {
                if (!files[i].isFolder()) fileObjects.remove(files[i]);
            }
        }
        if (isOnRoot && !VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_ON_ROOT)) {
            for (int i = 0; i < files.length; i++) {
                if (files[i].getPath().length() == 0) fileObjects.remove(files[i]);
            }
        }
        if (fileObjects.size() > 0) {
            if (VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_HIDDEN)) {
                fileObjects.clear();
            }
            String hiddenTestExpression = (String) cmd.getProperty(VcsCommand.PROPERTY_HIDDEN_TEST_EXPRESSION);
            if (hiddenTestExpression != null) {
                Hashtable variables = executionContext.getVariablesAsHashtable();
                if (Variables.expand(variables, hiddenTestExpression, false).trim().length() > 0) {
                    fileObjects.clear();
                }
            }
        }
        if (fileObjects.size() == 0) return (fileSystem != null) ? null : new FileObject[0];
        //boolean refreshDone = false;
        Table filesTable = new Table();
        addImportantFiles(fileObjects, filesTable, processAll, fileSystem, false);
        if (fileSystem != null) {
            filesTable = removeDisabled(fileSystem, filesTable, cmd);
        }
        if (filesTable.size() == 0) return (fileSystem != null) ? null : new FileObject[0];
        FileObject[] applFiles = new FileObject[filesTable.size()];
        int i = 0;
        for (Iterator it = filesTable.keySet().iterator(); it.hasNext(); ) {
            String path = (String) it.next();
            FileObject file = (FileObject) filesTable.get(path);
            if (file == null) {
                file = new NonExistentFileObject(fileSystem, path);
            }
            applFiles[i++] = file;
        }
        return applFiles;
    }
    
    private static String processConfirmation(String confirmation, Hashtable vars,
                                              CommandExecutionContext executionContext) throws UserCancelException {
        confirmation = Variables.expand(vars, confirmation, true);
        PreCommandPerformer cmdPerf = new PreCommandPerformer(executionContext, vars);
        ArrayList confCommandsOutput = new ArrayList();
        confirmation = cmdPerf.process(confirmation, confCommandsOutput);
        vars.put("CONFIRMATION_MSG", confirmation);
        boolean confCommandsSuccess = true;
        if (confCommandsOutput.size() > 0) {
            for (Iterator it = confCommandsOutput.iterator(); it.hasNext(); ) {
                confCommandsSuccess = confCommandsSuccess && ((Boolean) it.next()).booleanValue();
            }
        }
        if (confCommandsSuccess) {
            vars.put("CONFIRMATION_SUCCESS", Boolean.TRUE.toString());
        } else {
            vars.remove("CONFIRMATION_SUCCESS");
        }
        return confirmation;
    }
    
    /**
     * Perform the pre-customization of a command. After this it's necessary to
     * call {@link #preCustomizeExec} or {@link #preCustomizeStructuredExec}
     * @return <code>false</code> when the precustomization was cancelled,
     *         <code>true</code> otherwise.
     */
    public static boolean preCustomize(CommandExecutionContext executionContext, VcsCommand cmd, Hashtable vars) {
        Object confObj = cmd.getProperty(VcsCommand.PROPERTY_CONFIRMATION_MSG);
        String confirmation = (confObj == null) ? "" : (String) confObj; //cmd.getConfirmationMsg();
        String fullName = (String) vars.get("PATH");
        String paths = (String) vars.get("PATHS");
        boolean confirmed = false;
        String pathSeparator = (String) vars.get("PS");
        if (pathSeparator != null) {
            pathSeparator = Variables.expand(vars, pathSeparator, false);
        } else {
            pathSeparator = java.io.File.separator;
        }
        VcsFileSystem fileSystem;
        if (executionContext instanceof VcsFileSystem) {
            fileSystem = (VcsFileSystem) executionContext;
        } else {
            fileSystem = null;
        }
        if ((fileSystem != null &&
             (fullName == null || fileSystem.isImportant(fullName)))
            || executionContext != null) {
            
            if (fileSystem != null) vars.put("NUM_IMPORTANT_FILES",
                                             ""+numImportant(fileSystem, paths, pathSeparator));
            try {
                confirmation = processConfirmation(confirmation, vars, executionContext);
            } catch (UserCancelException cancelExc) {
                return false;
            }
            confirmed = true;
        //} else {
        //    confirmation = null;
        }
        if (confirmed && confirmation.length() > 0) {
            if (!NotifyDescriptor.Confirmation.YES_OPTION.equals (
                    DialogDisplayer.getDefault ().notify (new NotifyDescriptor.Confirmation (
                        confirmation, NotifyDescriptor.Confirmation.YES_NO_OPTION)))) { // NOI18N
                return false; // The command is cancelled for that file
            }
        }
        // II. Then filll output from pre commands
        //     preCustomizeExec() or preCustomizeStructuredExec should be called
        return true;
    }
    
    /**
     * Perform the pre-customization of a command's execution string. Should be
     * called after {@link #preCustomize}.
     * @return the new execution string of the command or <code>null</code>
     *         when the precustomization was cancelled.
     */
    public static String preCustomizeExec(CommandExecutionContext executionContext, VcsCommand cmd, Hashtable vars) {
        String exec;
        if (executionContext != null) {
            PreCommandPerformer cmdPerf = new PreCommandPerformer(executionContext, vars);
            try {
                exec = cmdPerf.process((String) cmd.getProperty(VcsCommand.PROPERTY_EXEC));
            } catch (UserCancelException cancelExc) {
                return null;
            }
        } else {
            exec = (String) cmd.getProperty(VcsCommand.PROPERTY_EXEC);
        }
        return exec;
    }
    
    /**
     * Perform the pre-customization of a command's structured execution string.
     * Should be called after {@link #preCustomize}.
     * @return the new structured execution property of the command or <code>null</code>
     *         when the precustomization was cancelled.
     */
    public static StructuredExec preCustomizeStructuredExec(CommandExecutionContext executionContext, VcsCommand cmd, Hashtable vars) {
        StructuredExec exec = (StructuredExec) cmd.getProperty(VcsCommand.PROPERTY_EXEC_STRUCTURED);
        if (exec == null) return null;
        if (executionContext != null) {
            PreCommandPerformer cmdPerf = new PreCommandPerformer(executionContext, vars);
            StructuredExec.Argument[] args = exec.getArguments();
            String w = null;
            String exe;
            StructuredExec.Argument[] as = new StructuredExec.Argument[args.length];
            try {
                if (exec.getWorking() != null) {
                    w = cmdPerf.process(exec.getWorking().getPath());
                }
                exe = cmdPerf.process(exec.getExecutable());
                for (int i = 0; i < args.length; i++) {
                    String arg = cmdPerf.process(args[i].getArgument());
                    as[i] = new StructuredExec.Argument(arg, args[i].isLine());
                }
            } catch (UserCancelException cancelExc) {
                return null;
            }
            exec = new StructuredExec((w != null) ? new java.io.File(w) : null, exe, as);
        }
        return exec;
    }
    
    /**
     * Pre process the command. Ask for the confirmation, execute any precommands,
     * prompt the user for input variables.
     *
    public static int preprocessCommand(VcsFileSystem fileSystem, VcsCommandExecutor vce,
                                        Hashtable vars, boolean[] askForEachFile) {
        VcsCommand cmd = vce.getCommand();
        // I. First check the confirmation:
        Object confObj = cmd.getProperty(VcsCommand.PROPERTY_CONFIRMATION_MSG);
        String confirmation = (confObj == null) ? "" : (String) confObj; //cmd.getConfirmationMsg();
        String fullName = (String) vars.get("PATH");
        String paths = (String) vars.get("PATHS");
        boolean confirmed = false;
        String pathSeparator = (String) vars.get("PS");
        pathSeparator = Variables.expand(vars, pathSeparator, false);
        if (fileSystem != null &&
            (fullName == null || fileSystem.isImportant(fullName))) {
            
            vars.put("NUM_IMPORTANT_FILES", ""+numImportant(fileSystem, paths, pathSeparator));
            try {
                confirmation = processConfirmation(confirmation, vars, fileSystem);
            } catch (UserCancelException cancelExc) {
                return CommandsPool.PREPROCESS_CANCELLED;
            }
            confirmed = true;
        //} else {
        //    confirmation = null;
        }
        if (confirmed && confirmation.length() > 0) {
            if (!NotifyDescriptor.Confirmation.YES_OPTION.equals (
                    TopManager.getDefault ().notify (new NotifyDescriptor.Confirmation (
                        confirmation, NotifyDescriptor.Confirmation.YES_NO_OPTION)))) { // NOI18N
                return CommandsPool.PREPROCESS_CANCELLED; // The command is cancelled for that file
            }
        }
        // II. Then filll output from pre commands:
        String exec;
        if (fileSystem != null) {
            PreCommandPerformer cmdPerf = new PreCommandPerformer(fileSystem, vars);
            try {
                exec = cmdPerf.process((String) cmd.getProperty(VcsCommand.PROPERTY_EXEC));
            } catch (UserCancelException cancelExc) {
                return CommandsPool.PREPROCESS_CANCELLED;
            }
        } else {
            exec = (String) cmd.getProperty(VcsCommand.PROPERTY_EXEC);
        }
        exec = insertGlobalOptions(exec, vars);
        // III. Ask for the variable input
        if (fileSystem != null && !promptForVariables(fileSystem, exec, vars, cmd, askForEachFile)) {
            return CommandsPool.PREPROCESS_CANCELLED; // The command is cancelled for that file
        }
        // Ask for the confirmation again, if the preprocessing was done, but there is an important file
        if (fileSystem != null && !(askForEachFile != null && askForEachFile[0])) {
            int numImp = numImportant(fileSystem, paths, pathSeparator);
            if (!confirmed && numImp > 0) {
                vars.put("NUM_IMPORTANT_FILES", ""+numImp);
                try {
                    confirmation = processConfirmation(confirmation, vars, fileSystem);
                } catch (UserCancelException cancelExc) {
                    return CommandsPool.PREPROCESS_CANCELLED;
                }
                if (confirmation.length() > 0) {
                    if (!NotifyDescriptor.Confirmation.YES_OPTION.equals (
                            TopManager.getDefault ().notify (new NotifyDescriptor.Confirmation (
                                confirmation, NotifyDescriptor.Confirmation.YES_NO_OPTION)))) { // NOI18N
                        return CommandsPool.PREPROCESS_CANCELLED; // The command is cancelled for that file
                    }
                }
            }
        }
        // IV. Perform the default variable expansion
        //exec = Variables.expand(vars, exec, true); // NO: - moved to ExecuteCommand. Each command executor have to take care of it
        //vce.updateExec(exec);
        // V. Allow a custom preprocessing
        exec = vce.preprocessCommand(cmd, vars, exec);
        if (askForEachFile != null && askForEachFile[0]) {
            return CommandsPool.PREPROCESS_NEXT_FILE;
        } else {
            return CommandsPool.PREPROCESS_DONE;
        }
    }
     */
    
    /**
     * Insert the global options into the structured execution string and
     * return the new StructuredExec with the filled global options.
     */
    public static StructuredExec insertGlobalOptions(StructuredExec exec, Hashtable vars) {
        if (exec == null) return null;
        StructuredExec.Argument[] args = exec.getArguments();
        String w = null;
        String exe = exec.getExecutable();
        StructuredExec.Argument[] as = new StructuredExec.Argument[args.length];
        if (exec.getWorking() != null) {
            w = exec.getWorking().getPath();
            w = insertGlobalOptions(w, vars);
        }
        exe = insertGlobalOptions(exe, vars);
        for (int i = 0; i < args.length; i++) {
            String arg = args[i].getArgument();
            arg = insertGlobalOptions(arg, vars);
            as[i] = new StructuredExec.Argument(arg, args[i].isLine());
        }
        exec = new StructuredExec((w != null) ? new java.io.File(w) : null, exe, as);
        return exec;
    }
    
    /**
     * Insert the global options into the execution string and
     * return the new exec with the filled global options.
     */
    public static String insertGlobalOptions(String exec, Hashtable vars) {
        if (vars.get(GLOBAL_INPUT_DESCRIPTOR) != null) {
            String glInput = (String) vars.get(GLOBAL_INPUT_EXPRESSION);
            if (glInput != null) {
                String search = "${"+USER_GLOBAL_PARAM+"}";
                int pos = 0;
                int index;
                while ((index = exec.indexOf(search, pos)) > 0) {
                    exec = exec.substring(0, index) + glInput + exec.substring(index + search.length());
                    pos = index + search.length();
                }
                synchronized (vars) {
                    vars.put(USER_GLOBAL_PARAM, glInput); // Put it as a variable, so that it can be used in sub-commands.
                }
            }
        }
        return exec;
    }
    
    private static Component createNotificationDesign(final String text,
                                                      final JCheckBox checkBox) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        JLabel textLabel = new JLabel(text);
        textLabel.setForeground(java.awt.Color.black);
        panel.add(textLabel, BorderLayout.CENTER);
        if (checkBox != null) panel.add(checkBox, BorderLayout.SOUTH);
        panel.getAccessibleContext().setAccessibleDescription(g("DLG_Notification_acsd"));
        return panel;
    }
    
    public static void commandNotification(final VcsCommandExecutor vce,
                                           String notification,
                                           final CommandExecutionContext executionContext) {
        final String notification1 = Variables.expand(vce.getVariables(), notification, false);
        if (notification1 == null || notification1.trim().length() == 0) return ;
        org.openide.util.RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                NotifyDescriptor msg = new NotifyDescriptor.Message(notification1);
                JCheckBox checkBox;
                if (executionContext != null) {
                    checkBox = new JCheckBox(g("DLG_DoNotNotify"));
                    checkBox.setMnemonic(g("DLG_DoNotNotify_mnc").charAt(0));
                    checkBox.getAccessibleContext().setAccessibleDescription(g("DLG_DoNotNotify_acsd"));
                } else checkBox = null;
                msg.setMessage(createNotificationDesign(notification1, checkBox));
                DialogDisplayer.getDefault().notify(msg);
                if (checkBox != null && checkBox.isSelected()) {
                    executionContext.setCommandNotification(false);
                    DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(
                        g("DLG_CanBeEnabled")));
                }
            }
        });
    }
    
    private static Collection getAllFilesAssociatedWith(VcsFileSystem fileSystem, Collection fileNames) {
        java.util.HashSet files = new java.util.HashSet();
        for (Iterator filesIt = fileNames.iterator(); filesIt.hasNext(); ) {
            String name = (String) filesIt.next();
            org.openide.filesystems.FileObject fo = fileSystem.findResource(name);
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
    
    /**
     * Find out which additional user parameters prompt the use for.
     * @return The table of parameter labels for the user to input, one for each parameter
     *         and default values.
     */
    private static Table needPromptForUserParams(CommandExecutionContext executionContext, String exec,
                                                 Hashtable vars, Hashtable varNames,
                                                 Hashtable userParamsIndexes, VcsCommand cmd,
                                                 boolean acceptUserParams) {
        Table results = new Table();
        String search = "${"+USER_GLOBAL_PARAM;
        int pos = 0;
        int index;
        String[] userParamsLabels = executionContext.getUserParamsLabels();
        String[] userParams = executionContext.getUserParams();
        String[] userLocalParamsLabels = executionContext.getUserLocalParamsLabels();
        while((index = exec.indexOf(search, pos)) >= 0) {
            int varBegin = index + 2;
            index += search.length();
            char cnum = exec.charAt(index);
            int num = 1;
            if (Character.isDigit(cnum)) {
                num = Character.digit(cnum, 10);
                index++;
            }
            num--;
            int varEnd = VcsUtilities.getPairIndex(exec, index, '{', '}');
            if (varEnd < 0) {
                pos = index; //TODO: wrong command syntax: '}' is missing
                continue;
            }
            String varName = exec.substring(varBegin, varEnd);
            if (vars.get(varName) == null) { // Do this only when the variable is not yet defined.
                // Otherwise the user would be prompted again for the same variables by sub-commands.
                String defaultParam = "";
                if (exec.charAt(index) == '(') {
                    index++;
                    int index2 = VcsUtilities.getPairIndex(exec, index, '(', ')');
                    if (index2 > 0) defaultParam = exec.substring(index, index2);
                }
                if (acceptUserParams && userParamsLabels != null) {
                    if (num >= userParamsLabels.length) num = userParamsLabels.length - 1;
                    if (userParams[num] != null) defaultParam = userParams[num];
                    results.put(userParamsLabels[num], defaultParam);
                    varNames.put(varName, userParamsLabels[num]);
                    userParamsIndexes.put(varName, new Integer(num));
                } else {
                    vars.put(varName, defaultParam);
                }
            }
            pos = varEnd;
        }
        search = "${"+USER_PARAM;
        pos = 0;
        while((index = exec.indexOf(search, pos)) >= 0) {
            int varBegin = index + 2;
            index += search.length();
            char cnum = exec.charAt(index);
            int num = 1;
            if (Character.isDigit(cnum)) {
                num = Character.digit(cnum, 10);
                index++;
            }
            num--;
            int varEnd = VcsUtilities.getPairIndex(exec, index, '{', '}');
            if (varEnd < 0) {
                pos = index; //TODO: wrong command syntax: '}' is missing
                continue;
            }
            String varName = exec.substring(varBegin, varEnd);
            if (vars.get(varName) == null) { // Do this only when the variable is not yet defined.
                // Otherwise the user would be prompted again for the same variables by sub-commands.
                String defaultParam = "";
                if (exec.charAt(index) == '(') {
                    index++;
                    int index2 = VcsUtilities.getPairIndex(exec, index, '(', ')');
                    if (index2 > 0) defaultParam = exec.substring(index, index2);
                }
                if (acceptUserParams && userLocalParamsLabels != null) {
                    String[] cmdUserParams = (String[]) cmd.getProperty(VcsCommand.PROPERTY_USER_PARAMS);
                    if (cmdUserParams == null) cmdUserParams = new String[userLocalParamsLabels.length];
                    cmd.setProperty(VcsCommand.PROPERTY_USER_PARAMS, cmdUserParams);
                    if (num >= userLocalParamsLabels.length) num = userLocalParamsLabels.length - 1;
                    if (cmdUserParams[num] != null) defaultParam = cmdUserParams[num];
                    results.put(userLocalParamsLabels[num], defaultParam);
                    varNames.put(varName, userLocalParamsLabels[num]);
                    userParamsIndexes.put(varName, new Integer(-num - 1));
                } else {
                    vars.put(varName, defaultParam);
                }
            }
            pos = varEnd;
        }
        return results;
    }
    
    private static boolean needPromptForPR(String name, String exec, Hashtable vars){
        //D.deb("needPromptFor('"+name+"','"+exec+"')"); // NOI18N
        boolean result=false;
        String oldPassword= (String) vars.get("PASSWORD"); // NOI18N
        vars.put("PASSWORD", ""); // NOI18N
        String oldReason= (String) vars.get("REASON"); // NOI18N
        vars.put("REASON", ""); // NOI18N

        String test="variable_must_be_prompt_for"; // NOI18N
        vars.put(name,test);
        String s = Variables.expand(vars, exec, false);
        result = (s.indexOf(test) >= 0) ? true : false ;

        if (oldPassword != null) {
            vars.put("PASSWORD", oldPassword); // NOI18N
        } else {
            vars.remove("PASSWORD"); // NOI18N
        }
        if (oldReason != null) {
            vars.put("REASON", oldReason); // NOI18N
        } else {
            vars.remove("REASON"); // NOI18N
        }

        return result ;
    }
    
    private static void addComponentsWithPrecommands(VariableInputComponent component,
                                                     ArrayList componentsWithPrecommands) {
        if (component.needsPreCommandPerform()) componentsWithPrecommands.add(component);
        VariableInputComponent[] components = component.subComponents();
        if (components != null) {
            for (int i = 0; i < components.length; i++) {
                addComponentsWithPrecommands(components[i], componentsWithPrecommands);
            }
        }    
    }

    private static void processPrecommands(CommandExecutionContext executionContext, Hashtable vars,
                                           VariableInputDescriptor inputDescriptor) throws UserCancelException {
        VariableInputComponent[] components = inputDescriptor.components();
        ArrayList componentsWithPrecommands = new ArrayList();
        for (int i = 0; i < components.length; i++) {
            addComponentsWithPrecommands(components[i], componentsWithPrecommands);
        }
        if (componentsWithPrecommands.size() > 0) {
            String[] defVals = new String[componentsWithPrecommands.size()];
            for (int i = 0; i < defVals.length; i++) {
                defVals[i] = ((VariableInputComponent) componentsWithPrecommands.get(i)).getDefaultValue();
            }
            PreCommandPerformer cmdPerf = new PreCommandPerformer(executionContext, vars);
            String[] values = cmdPerf.process(defVals);
            for (int i = 0; i < values.length; i++) {
                ((VariableInputComponent) componentsWithPrecommands.get(i)).setValue(values[i]);
            }
        }
    }
    
    private static java.util.List getComponentsToPreprocess(VariableInputDescriptor inputDescriptor) {
        VariableInputComponent[] components = inputDescriptor.components();
        ArrayList componentsWithPrecommands = new ArrayList();
        for (int i = 0; i < components.length; i++) {
            addComponentsWithPrecommands(components[i], componentsWithPrecommands);
        }
        return componentsWithPrecommands;
    }
    
    private static void doPromptForPasswordIfNecessary(final CommandExecutionContext executionContext,
                                                       final String exec,
                                                       final Hashtable vars) throws UserCancelException {
        synchronized (vars) {
            if (exec != null && needPromptForPR("PASSWORD", exec, vars)) { // NOI18N
                String password;
                synchronized (promptLock) { // disable the possibility, that the user
                    // will be prompted multiple times at once by concurrenly running commands
                    password = executionContext.getPassword();
                    if (password == null) {
                        String description = executionContext.getPasswordDescription();
                        NotifyDescriptorInputPassword nd;
                        if (description == null) {
                            nd = new NotifyDescriptorInputPassword (g("MSG_Password"), g("MSG_Password")); // NOI18N
                        } else {
                            nd = new NotifyDescriptorInputPassword (g("MSG_Password"), g("TITL_Password"), description); // NOI18N
                        }
                        if (NotifyDescriptor.OK_OPTION.equals (DialogDisplayer.getDefault ().notify (nd))) {
                            password = nd.getInputText ();
                        } else {
                            executionContext.setPassword(null);
                            throw new UserCancelException();
                        }
                        executionContext.setPassword(password);
                    }
                }
                vars.put("PASSWORD", password); // NOI18N
            /* Do not change forEachFile, if the command is successful it will not ask any more */
            }
        }
    }
    
    /**
     * Setup some necessary variables, but do not present any GUI - the command
     * does not wish to be customized. The only exception is a prompt for password.
     * This method just sets the password (and prompt for it if it's not set).
     */
    public static void setupUncustomizedCommand(final CommandExecutionContext executionContext,
                                                final String exec, final Hashtable vars,
                                                final VcsCommand cmd) throws UserCancelException {
        doPromptForPasswordIfNecessary(executionContext, exec, vars);
    }
    
    /** The table of FS and its global descriptor string. */
    private static Map globalInputStrs = Collections.synchronizedMap(new WeakHashMap());
    /** The table of FS and its parsed global descriptor */
    private static Map globalInputDescrs = Collections.synchronizedMap(new WeakHashMap());
    
    private static final Object promptLock = new Object();
    
    public static VariableInputDialog createInputDialog(final CommandExecutionContext executionContext,
                                                        String exec, final Hashtable vars,
                                                        final VcsCommand cmd,
                                                        boolean[] forEachFile,
                                                        StringBuffer retTitle) throws UserCancelException {
        VariableInputDescriptor inputDescriptor = (VariableInputDescriptor) cmd.getProperty(INPUT_DESCRIPTOR_PARSED);
        if (inputDescriptor == null) {
            String inputDescriptorStr = (String) cmd.getProperty(VcsCommand.PROPERTY_INPUT_DESCRIPTOR);
            if (inputDescriptorStr != null) {
                // Perform the variable expansion to be able to use variables there
                //System.out.println("promptForVariables(): inputDescriptorStr = "+inputDescriptorStr);
                //inputDescriptorStr = Variables.expand(vars, inputDescriptorStr, true);
                //System.out.println("FILES_IS_FOLDER = '"+vars.get("FILES_IS_FOLDER")+"'");
                //System.out.println("promptForVariables(): after expand: inputDescriptorStr = "+inputDescriptorStr);
                //PreCommandPerformer cmdPerf = new PreCommandPerformer(fileSystem, vars);
                //inputDescriptorStr = cmdPerf.process(inputDescriptorStr);
                try {
                    inputDescriptor = VariableInputDescriptor.parseItems(inputDescriptorStr);
                } catch (VariableInputFormatException exc) {
                    ErrorManager.getDefault().notify(exc);
                    throw new UserCancelException();
                }
                inputDescriptor.setValuesAsDefault();
                cmd.setProperty(INPUT_DESCRIPTOR_PARSED, inputDescriptor);
            }
        }
        synchronized (vars) {
            doPromptForPasswordIfNecessary(executionContext, exec, vars);
            if (forEachFile == null || forEachFile[0] == true) {
                final String[] userParams = executionContext.getUserParams();
                final Hashtable userParamsVarNames = new Hashtable(); // Variable names of prompt for additional parameters
                final Hashtable userParamsIndexes = new Hashtable();

                String ctrlDown = (String) vars.get(VcsFileSystem.VAR_CTRL_DOWN_IN_ACTION);
                boolean expertCondition = /*executionContext.isExpertMode() || */(ctrlDown != null && ctrlDown.length() > 0);
                boolean acceptUserParams = executionContext.isAcceptUserParams() || (ctrlDown != null && ctrlDown.length() > 0);
                Table userParamsPromptLabels;
                if (exec == null) {
                    userParamsPromptLabels = new Table();
                } else {
                    userParamsPromptLabels = needPromptForUserParams(executionContext, exec, vars, userParamsVarNames,
                                                                     userParamsIndexes, cmd, acceptUserParams);
                }
                /*
                createTempPromptFiles(promptFile);
                if (prompt != null && prompt.size() > 0 || ask != null && ask.size() > 0 ||
                promptFile.size() > 0 || userParamsPromptLabels.size() > 0) {
                    */
                if (inputDescriptor != null && showInputDescriptor(inputDescriptor, expertCondition, vars)
                    || userParamsPromptLabels.size() > 0) {
                        
                    String file = (String) vars.get("FILE"); // NOI18N
                    // provide a copy of variables for easy use and modification,
                    // since I have the original variables locked.
                    final Hashtable dlgVars = new Hashtable(vars);
                    final VariableInputDialog dlg = new VariableInputDialog(new String[] { file }, inputDescriptor, expertCondition, dlgVars);
                    if (inputDescriptor != null && inputDescriptor.getHelpID() != null) {
                        dlg.putClientProperty("helpID", inputDescriptor.getHelpID());
                    }
                    dlg.setExecutionContext(executionContext, dlgVars);
                    if (cmd.getDisplayName() != null) {
                        dlg.setCmdName(cmd.getDisplayName());
                    } else {
                        dlg.setCmdName(cmd.getName());
                    }
                    if (expertCondition) {
                        if (exec != null) dlg.setExec(exec);
                    }
                    if (inputDescriptor != null) {
                        dlg.setComponentsToPreprocess(getComponentsToPreprocess(inputDescriptor));
                    }
                    final String globalInputStr = (String) vars.get(GLOBAL_INPUT_DESCRIPTOR);
                    String globalInputStrStored = (String) globalInputStrs.get(executionContext);
                    VariableInputDescriptor globalInputDescriptor = null;
                    if (globalInputStr != null) {
                        if (!globalInputStr.equals(globalInputStrStored)) {
                            try {
                                globalInputDescriptor = VariableInputDescriptor.parseItems(globalInputStr);
                            } catch (VariableInputFormatException exc) {
                                ErrorManager.getDefault().notify(exc);
                                return null;
                            }
                            globalInputStrs.put(executionContext, globalInputStr);
                            globalInputDescrs.put(executionContext, globalInputDescriptor);
                            globalInputDescriptor.setValuesAsDefault();
                        } else {
                            globalInputDescriptor = (VariableInputDescriptor) globalInputDescrs.get(executionContext);
                        }
                    }
                    dlg.setGlobalInput(globalInputDescriptor);
                    dlg.setUserParamsPromptLabels(userParamsPromptLabels, (String) cmd.getProperty(VcsCommand.PROPERTY_ADVANCED_NAME));
                    if (executionContext instanceof VariableInputDialog.FilePromptDocumentListener) {
                        dlg.setFilePromptDocumentListener((VariableInputDialog.FilePromptDocumentListener) executionContext, cmd);
                    }
                    if (forEachFile == null) dlg.showPromptEach(false);
                    else dlg.setPromptEach(executionContext.isPromptForVarsForEachFile());
                    String title = (inputDescriptor != null) ? inputDescriptor.getLabel() : null;
                    if (title == null) {
                        /*
                        title = java.text.MessageFormat.format(
                            org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.titleWithName"),
                            new Object[] { cmd.getDisplayName() }
                        );
                         */
                        title = cmd.getDisplayName();
                    }
                    if (file != null) title += VAR_INPUT_FILE_SEPARATOR + file;
                    String multipleFiles = (String) vars.get("MULTIPLE_FILES");
                    if (multipleFiles != null && multipleFiles.length() > 0) title += VAR_INPUT_MULTIPLE_FILES_TITLE_APPEND;
                    //if (retTitle.length() > 0) {
                        retTitle.replace(0, retTitle.length(), title);
                    //} else {
                    //    retTitle.append(title);
                    //}
                    final VariableInputDescriptor dlgInputDescriptor = inputDescriptor;
                    final VariableInputDescriptor dlgGlobalInputDescriptor = globalInputDescriptor;
                    dlg.addCloseListener(new ActionListener() {
                        public void actionPerformed(ActionEvent evt) {
                            if (dlg.isValidInput()) {
                                dlg.processActions();
                                // put the dialog's variables back with all necessary modifications done.
                                vars.clear();
                                vars.putAll(dlgVars);
                                if (dlgInputDescriptor != null) {
                                    dlgInputDescriptor.addValuesToHistory();
                                }
                                if (dlgGlobalInputDescriptor != null) {
                                    dlgGlobalInputDescriptor.addValuesToHistory();
                                    // Now I need to remember default values
                                    dlgGlobalInputDescriptor.setDefaultValues();
                                    String globalVIDString = dlgGlobalInputDescriptor.getStringInputItems();
                                    //System.out.println("CCS: old GlobalVID was :\n"+globalInputStr);
                                    //System.out.println("CCS: have new GlobalVID:\n"+globalVIDString);
                                    if (!globalVIDString.equals(globalInputStr)) {
                                        Vector fsVars = executionContext.getVariables();
                                        for (Iterator it = fsVars.iterator(); it.hasNext(); ) {
                                            VcsConfigVariable var = (VcsConfigVariable) it.next();
                                            if (GLOBAL_INPUT_DESCRIPTOR.equals(var.getName())) {
                                                var.setValue(globalVIDString);
                                                break;
                                            }
                                        }
                                        executionContext.setVariables(fsVars);
                                    }
                                }
                                Hashtable valuesTable = dlg.getUserParamsValuesTable();
                                for (Enumeration enum = userParamsVarNames.keys(); enum.hasMoreElements(); ) {
                                    String varName = (String) enum.nextElement();
                                    //System.out.println("varName = "+varName+", label = "+userParamsVarNames.get(varName));
                                    String value = (String) valuesTable.get(userParamsVarNames.get(varName));
                                    vars.put(varName, value);
                                    int index = ((Integer) userParamsIndexes.get(varName)).intValue();
                                    if (index >= 0) userParams[index] = value;
                                    else {
                                        String[] cmdUserParams = (String[]) cmd.getProperty(VcsCommand.PROPERTY_USER_PARAMS);
                                        cmdUserParams[-index - 1] = value;
                                        cmd.setProperty(VcsCommand.PROPERTY_USER_PARAMS, cmdUserParams);
                                    }
                                    //D.deb("put("+varName+", "+valuesTable.get(userParamsVarNames.get(varName))+")");
                                }
                                executionContext.setUserParams(userParams);
                                /*
                                if (forEachFile != null) {
                                    forEachFile[0] = dlg.getPromptForEachFile();
                                    fileSystem.setPromptForVarsForEachFile(forEachFile[0]);
                                }
                                 */
                            }
                        }
                    });
                    return dlg;
                } else {
                    if (inputDescriptor != null && showInputDescriptor(inputDescriptor, true, vars)) {
                        VariableInputComponent[] components = inputDescriptor.components();
                        for (int i = 0; i < components.length; i++) {
                            String var = components[i].getVariable();
                            String value = components[i].getDefaultValue();
                            if (value != null) vars.put(var, value);
                        }
                        String globalInputStr = (String) vars.get(GLOBAL_INPUT_DESCRIPTOR);
                        String globalInputStrStored = (String) globalInputStrs.get(executionContext);
                        if (globalInputStr != null) {
                            VariableInputDescriptor globalInputDescriptor;
                            if (!globalInputStr.equals(globalInputStrStored)) {
                                try {
                                    globalInputDescriptor = VariableInputDescriptor.parseItems(globalInputStr);
                                } catch (VariableInputFormatException exc) {
                                    ErrorManager.getDefault().notify(exc);
                                    return null;
                                }
                                globalInputStrs.put(executionContext, globalInputStr);
                                globalInputDescrs.put(executionContext, globalInputDescriptor);
                            } else {
                                globalInputDescriptor = (VariableInputDescriptor) globalInputDescrs.get(executionContext);
                            }
                            //dlg.setGlobalInput(globalInputDescriptor);
                            components = globalInputDescriptor.components();
                            for (int i = 0; i < components.length; i++) {
                                String var = components[i].getVariable();
                                String value = components[i].getDefaultValue();
                                if (value != null) vars.put(var, value);
                            }
                        }
                    }
                    if (forEachFile != null) {
                        forEachFile[0] = false;
                    }
                }
            }
            return null;
        }
    }
    
    /**
     * Ask the user for the value of some variables.
     * @param inputDescriptor the descriptor of variable input components
     * @param vars the variables
     * @param cmd the command
     * @param forEachFile whether to ask for these variables for each file being processed
     * @return true if all variables were entered, false otherways
     *
    public static boolean promptForVariables(CommandExecutionContext executionContext, String exec,
                                             Hashtable vars, VcsCommand cmd, boolean[] forEachFile) {
        VariableInputDescriptor inputDescriptor = (VariableInputDescriptor) cmd.getProperty(INPUT_DESCRIPTOR_PARSED);
        if (inputDescriptor == null) {
            String inputDescriptorStr = (String) cmd.getProperty(VcsCommand.PROPERTY_INPUT_DESCRIPTOR);
            if (inputDescriptorStr != null) {
                // Perform the variable expansion to be able to use variables there
                //System.out.println("promptForVariables(): inputDescriptorStr = "+inputDescriptorStr);
                //inputDescriptorStr = Variables.expand(vars, inputDescriptorStr, true);
                //System.out.println("FILES_IS_FOLDER = '"+vars.get("FILES_IS_FOLDER")+"'");
                //System.out.println("promptForVariables(): after expand: inputDescriptorStr = "+inputDescriptorStr);
                //PreCommandPerformer cmdPerf = new PreCommandPerformer(fileSystem, vars);
                //inputDescriptorStr = cmdPerf.process(inputDescriptorStr);
                try {
                    inputDescriptor = VariableInputDescriptor.parseItems(inputDescriptorStr);
                } catch (VariableInputFormatException exc) {
                    ErrorManager.getDefault().notify(exc);
                    return false;
                }
                inputDescriptor.setValuesAsDefault();
                cmd.setProperty(INPUT_DESCRIPTOR_PARSED, inputDescriptor);
            }
        }
        if (inputDescriptor != null) {
            try {
                processPrecommands(executionContext, vars, inputDescriptor);
            } catch (UserCancelException cancelExc) {
                return false;
            }
        }
        synchronized (vars) {
            if (needPromptForPR("PASSWORD", exec, vars)) { // NOI18N
                String password;
                synchronized (promptLock) { // disable the possibility, that the user
                    // will be prompted multiple times at once by concurrenly running commands
                    password = executionContext.getPassword();
                    if (password == null) {
                        NotifyDescriptorInputPassword nd = new NotifyDescriptorInputPassword (g("MSG_Password"), g("MSG_Password")); // NOI18N
                        if (NotifyDescriptor.OK_OPTION.equals (DialogDisplayer.getDefault ().notify (nd))) {
                            password = nd.getInputText ();
                        } else {
                            executionContext.setPassword(null);
                            return false;
                        }
                        executionContext.setPassword(password);
                    }
                }
                vars.put("PASSWORD", password); // NOI18N
            /* Do not change forEachFile, if the command is successful it will not ask any more *//*
            }
            if (forEachFile == null || forEachFile[0] == true) {
                String[] userParams = executionContext.getUserParams();
                Hashtable userParamsVarNames = new Hashtable(); // Variable names of prompt for additional parameters
                Hashtable userParamsIndexes = new Hashtable();

                String ctrlDown = (String) vars.get(VcsFileSystem.VAR_CTRL_DOWN_IN_ACTION);
                boolean expertCondition = /*executionContext.isExpertMode() ||*//* (ctrlDown != null && ctrlDown.length() > 0);
                boolean acceptUserParams = executionContext.isAcceptUserParams() || (ctrlDown != null && ctrlDown.length() > 0);
                Table userParamsPromptLabels = needPromptForUserParams(executionContext, exec, vars, userParamsVarNames,
                                                                       userParamsIndexes, cmd, acceptUserParams);
                /*
                createTempPromptFiles(promptFile);
                if (prompt != null && prompt.size() > 0 || ask != null && ask.size() > 0 ||
                promptFile.size() > 0 || userParamsPromptLabels.size() > 0) {
                    *//*
                if (inputDescriptor != null && showInputDescriptor(inputDescriptor, expertCondition, vars)
                    || userParamsPromptLabels.size() > 0) {
                        
                    String file = (String) vars.get("FILE"); // NOI18N
                    // provide a copy of variables for easy use and modification,
                    // since I have the original variables locked.
                    Hashtable dlgVars = new Hashtable(vars);
                    VariableInputDialog dlg = new VariableInputDialog(new String[] { file }, inputDescriptor, expertCondition, dlgVars);
                    dlg.setExecutionContext(executionContext, dlgVars);
                    if (cmd.getDisplayName() != null) {
                        dlg.setCmdName(cmd.getDisplayName());
                    } else {
                        dlg.setCmdName(cmd.getName());
                    }
                    if (expertCondition) {
                        if (exec != null) dlg.setExec(exec);
                    }
                    String globalInputStr = (String) vars.get(GLOBAL_INPUT_DESCRIPTOR);
                    String globalInputStrStored = (String) globalInputStrs.get(executionContext);
                    VariableInputDescriptor globalInputDescriptor = null;
                    if (globalInputStr != null) {
                        if (!globalInputStr.equals(globalInputStrStored)) {
                            try {
                                globalInputDescriptor = VariableInputDescriptor.parseItems(globalInputStr);
                            } catch (VariableInputFormatException exc) {
                                ErrorManager.getDefault().notify(exc);
                                return false;
                            }
                            globalInputStrs.put(executionContext, globalInputStr);
                            globalInputDescrs.put(executionContext, globalInputDescriptor);
                            globalInputDescriptor.setValuesAsDefault();
                        } else {
                            globalInputDescriptor = (VariableInputDescriptor) globalInputDescrs.get(executionContext);
                        }
                    }
                    dlg.setGlobalInput(globalInputDescriptor);
                    dlg.setUserParamsPromptLabels(userParamsPromptLabels, (String) cmd.getProperty(VcsCommand.PROPERTY_ADVANCED_NAME));
                    if (executionContext instanceof VariableInputDialog.FilePromptDocumentListener) {
                        dlg.setFilePromptDocumentListener((VariableInputDialog.FilePromptDocumentListener) executionContext, cmd);
                    }
                    if (forEachFile == null) dlg.showPromptEach(false);
                    else dlg.setPromptEach(executionContext.isPromptForVarsForEachFile());
                    String title = (inputDescriptor != null) ? inputDescriptor.getLabel() : null;
                    if (title == null) {
                        /*
                        title = java.text.MessageFormat.format(
                            org.openide.util.NbBundle.getBundle(VariableInputDialog.class).getString("VariableInputDialog.titleWithName"),
                            new Object[] { cmd.getDisplayName() }
                        );
                         *//*
                        title = cmd.getDisplayName();
                    }
                    title += VAR_INPUT_FILE_SEPARATOR + file;
                    String multipleFiles = (String) vars.get("MULTIPLE_FILES");
                    if (multipleFiles != null && multipleFiles.length() > 0) title += VAR_INPUT_MULTIPLE_FILES_TITLE_APPEND;
                    DialogDescriptor dialogDescriptor = new DialogDescriptor(dlg, title, true, dlg.getActionListener());
                    dialogDescriptor.setClosingOptions(new Object[] { NotifyDescriptor.CANCEL_OPTION });
                    final Dialog dialog = DialogDisplayer.getDefault().createDialog(dialogDescriptor);
                    dlg.addCloseListener(new java.awt.event.ActionListener() {
                        public void actionPerformed(java.awt.event.ActionEvent ev) {
                            dialog.dispose();
                        }
                    });
                    synchronized (promptLock) {
                        dialog.setVisible(true);
                    }
                    if (dlg.isValidInput()) {
                        dlg.processActions();
                        // put the dialog's variables back with all necessary modifications done.
                        vars.clear();
                        vars.putAll(dlgVars);
                        if (inputDescriptor != null) {
                            inputDescriptor.addValuesToHistory();
                        }
                        if (globalInputDescriptor != null) {
                            globalInputDescriptor.addValuesToHistory();
                        }
                        Hashtable valuesTable = dlg.getUserParamsValuesTable();
                        for (Enumeration enum = userParamsVarNames.keys(); enum.hasMoreElements(); ) {
                            String varName = (String) enum.nextElement();
                            //System.out.println("varName = "+varName+", label = "+userParamsVarNames.get(varName));
                            String value = (String) valuesTable.get(userParamsVarNames.get(varName));
                            vars.put(varName, value);
                            int index = ((Integer) userParamsIndexes.get(varName)).intValue();
                            if (index >= 0) userParams[index] = value;
                            else {
                                String[] cmdUserParams = (String[]) cmd.getProperty(VcsCommand.PROPERTY_USER_PARAMS);
                                cmdUserParams[-index - 1] = value;
                                cmd.setProperty(VcsCommand.PROPERTY_USER_PARAMS, cmdUserParams);
                            }
                            //D.deb("put("+varName+", "+valuesTable.get(userParamsVarNames.get(varName))+")");
                        }
                        executionContext.setUserParams(userParams);
                        if (forEachFile != null) {
                            forEachFile[0] = dlg.getPromptForEachFile();
                            executionContext.setPromptForVarsForEachFile(forEachFile[0]);
                        }
                    } else return false;
                } else {
                    if (inputDescriptor != null && showInputDescriptor(inputDescriptor, true, vars)) {
                        VariableInputComponent[] components = inputDescriptor.components();
                        for (int i = 0; i < components.length; i++) {
                            String var = components[i].getVariable();
                            String value = components[i].getDefaultValue();
                            if (value != null) vars.put(var, value);
                        }
                        String globalInputStr = (String) vars.get(GLOBAL_INPUT_DESCRIPTOR);
                        String globalInputStrStored = (String) globalInputStrs.get(executionContext);
                        if (globalInputStr != null) {
                            VariableInputDescriptor globalInputDescriptor;
                            if (!globalInputStr.equals(globalInputStrStored)) {
                                try {
                                    globalInputDescriptor = VariableInputDescriptor.parseItems(globalInputStr);
                                } catch (VariableInputFormatException exc) {
                                    ErrorManager.getDefault().notify(exc);
                                    return false;
                                }
                                globalInputStrs.put(executionContext, globalInputStr);
                                globalInputDescrs.put(executionContext, globalInputDescriptor);
                            } else {
                                globalInputDescriptor = (VariableInputDescriptor) globalInputDescrs.get(executionContext);
                            }
                            //dlg.setGlobalInput(globalInputDescriptor);
                            components = globalInputDescriptor.components();
                            for (int i = 0; i < components.length; i++) {
                                String var = components[i].getVariable();
                                String value = components[i].getDefaultValue();
                                if (value != null) vars.put(var, value);
                            }
                        }
                    }
                    if (forEachFile != null) {
                        forEachFile[0] = false;
                    }
                }
            }
            return true;
        }
    }
                            */
    
    private static boolean showInputDescriptor(VariableInputDescriptor inputDescriptor, boolean isExpertMode, Map vars) {
        VariableInputComponent[] inputComponents = inputDescriptor.components();
        if (inputComponents.length == 0) return false;
        for (int i = 0; i < inputComponents.length; i++) {
            if (VariableInputComponent.isVarConditionMatch(inputComponents[i].getVarConditions(), vars) &&
                (inputComponents[i].isExpert() && isExpertMode ||
                 (!inputComponents[i].isExpert()
                  && inputComponents[i].getComponent() != VariableInputDescriptor.INPUT_GLOBAL)))
                return true;
        }
        return false;
    }


    private static String g(String s) {
        return org.openide.util.NbBundle.getBundle(CommandCustomizationSupport.class).getString(s);
    }
    
    /**
     * A dummy FileObject, that represents a non-existent FileObject
     * -- FileObject, that does not exist in the FileSystem !!!
     * This FO can not be find in any FS through findResource() or any other method.
     * The only purpose of this FO is to hold the path, that is used for VCS
     * commands execution.
     */
    private static final class NonExistentFileObject extends FileObject {
        
        private String path;
        private String name;
        private org.openide.filesystems.FileSystem fileSystem;
        
        public NonExistentFileObject(org.openide.filesystems.FileSystem fs, String path) {
            this.fileSystem = fs;
            this.path = path;
            int i = path.lastIndexOf('/');
            if (i >= 0) {
                name = path.substring(i);
            } else {
                name = path;
            }
        }
        
        public void addFileChangeListener(org.openide.filesystems.FileChangeListener fcl) {
            // It's not possible to listen on non-existent FileObject
        }
        
        public FileObject createData(String name, String ext) throws java.io.IOException {
            throw new java.io.IOException("It's not possible to create data inside non-existent file object.");
        }
        
        public FileObject createFolder(String name) throws java.io.IOException {
            throw new java.io.IOException("It's not possible to create folder inside non-existent file object.");
        }
        
        public void delete(org.openide.filesystems.FileLock lock) throws java.io.IOException {
            // non-existing file is already deleted
        }
        
        public Object getAttribute(String attrName) {
            return null; // no attributes
        }
        
        public Enumeration getAttributes() {
            return java.util.Collections.enumeration(java.util.Collections.EMPTY_SET);
        }
        
        public FileObject[] getChildren() {
            return new FileObject[0];
        }
        
        public String getExt() {
            int i = name.lastIndexOf ('.') + 1;
            /** period at first position is not considered as extension-separator */
            return i <= 1 || i == name.length ()  ? "" : name.substring (i); // NOI18N
        }
        
        public FileObject getFileObject(String name, String ext) {
            return null;
        }
        
        public org.openide.filesystems.FileSystem getFileSystem() throws org.openide.filesystems.FileStateInvalidException {
            return fileSystem;
        }
        
        public java.io.InputStream getInputStream() throws java.io.FileNotFoundException {
            throw new java.io.FileNotFoundException("File "+getPath()+" does not exist.");
        }
        
        public String getName() {
            int i = name.lastIndexOf ('.');
            /** period at first position is not considered as extension-separator */        
            return i <= 0 ? name : name.substring (0, i);
        }
        
        public java.io.OutputStream getOutputStream(org.openide.filesystems.FileLock lock) throws java.io.IOException {
            throw new java.io.FileNotFoundException("File "+getPath()+" does not exist.");
        }
        
        public FileObject getParent() {
            return null;
        }
        
        public String getPath() {
            return path;
        }
        
        public long getSize() {
            return 0;
        }
        
        public boolean isData() {
            return true;
        }
        
        public boolean isFolder() {
            return false;
        }
        
        public boolean isReadOnly() {
            return true;
        }
        
        public boolean isRoot() {
            return false;
        }
        
        public boolean isValid() {
            return true;
        }
        
        public java.util.Date lastModified() {
            return new java.util.Date(0);
        }
        
        public org.openide.filesystems.FileLock lock() throws java.io.IOException {
            throw new java.io.FileNotFoundException("File "+getPath()+" does not exist.");
        }
        
        public void removeFileChangeListener(org.openide.filesystems.FileChangeListener fcl) {
            // It's not possible to listen on non-existent FileObject
        }
        
        public void rename(org.openide.filesystems.FileLock lock, String name, String ext) throws java.io.IOException {
            throw new java.io.IOException("Non-existent file can not be renamed.");
        }
        
        public void setAttribute(String attrName, Object value) throws java.io.IOException {
            // silently ignore
        }
        
        public void setImportant(boolean b) {
            // silently ignore
        }
        
    }
}

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

import java.awt.Component;
import java.awt.BorderLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JCheckBox;
import java.awt.Dialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Hashtable;
import java.util.Enumeration;

import org.openide.TopManager;
import org.openide.NotifyDescriptor;
import org.openide.DialogDescriptor;
import org.openide.util.UserCancelException;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.VcsAction;
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

/**
 * This class contains a support for VCS commands execution.
 *
 * @author  Martin Entlicher
 */
public class CommandExecutorSupport extends Object {
    
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

    /** Creates new CommandExecutorSupport */
    private CommandExecutorSupport() {
    }
    
    /*
    public static int preprocessCommand(VcsFileSystem fileSystem, VcsCommandExecutor vce, Hashtable vars) {
        return preprocessCommand(fileSystem, vce, vars, CommandsPool.PREPROCESS_DONE, null);
    }
     */
    
    /**
     * Find out, the number of important files among these paths.
     * @param paths the files paths delimited by double File.separator
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
    
    private static String processConfirmation(String confirmation, Hashtable vars, VcsFileSystem fileSystem) throws UserCancelException {
        confirmation = Variables.expand(vars, confirmation, true);
        PreCommandPerformer cmdPerf = new PreCommandPerformer(fileSystem, vars);
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
     * Pre process the command. Ask for the confirmation, execute any precommands,
     * prompt the user for input variables.
     */
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
    
    private static String insertGlobalOptions(String exec, Hashtable vars) {
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
        return panel;
    }
    
    public static void commandNotification(final VcsCommandExecutor vce,
                                           String notification,
                                           final VcsFileSystem fileSystem) {
        final String notification1 = Variables.expand(vce.getVariables(), notification, false);
        org.openide.util.RequestProcessor.postRequest(new Runnable() {
            public void run() {
                NotifyDescriptor msg = new NotifyDescriptor.Message(notification1);
                JCheckBox checkBox;
                if (fileSystem != null) {
                    checkBox = new JCheckBox(g("DLG_DoNotNotify"));
                } else checkBox = null;
                msg.setMessage(createNotificationDesign(notification1, checkBox));
                TopManager.getDefault().notify(msg);
                if (checkBox != null && checkBox.isSelected()) {
                    fileSystem.setCommandNotification(false);
                    TopManager.getDefault().notify(new NotifyDescriptor.Message(
                        g("DLG_CanBeEnabled")));
                }
            }
        });
    }
    
    /**
     * Postprocess the command after it's execution.
     */
    public static void postprocessCommand(VcsFileSystem fileSystem, VcsCommandExecutor vce) {
        int exit = vce.getExitStatus();
        VcsCommand cmd = vce.getCommand();
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
        issuePostCommands(cmd, vce.getVariables(),
                          VcsCommandExecutor.SUCCEEDED == exit, fileSystem);
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
            String name = fo.getPackageNameExt('/', '.');
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
                                          boolean success, VcsFileSystem fileSystem) {
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
            VcsCommand c = fileSystem.getCommand(cmdNames[i]);
            if (c != null) {
                Hashtable cVars = new Hashtable(vars);
                VcsCommandExecutor vce = fileSystem.getVcsFactory().getCommandExecutor(c, cVars);
                fileSystem.getCommandsPool().preprocessCommand(vce, cVars, fileSystem);
                fileSystem.getCommandsPool().startExecutor(vce);
            }
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
    public static void doRefresh(VcsFileSystem fileSystem, VcsCommandExecutor vce) {
        doRefresh(fileSystem, vce, false);
    }
    
    /**
     * Performs an automatic refresh after the command finishes.
     */
    public static void doRefresh(VcsFileSystem fileSystem, VcsCommandExecutor vce, boolean foldersOnly) {
        VcsCommand cmd = vce.getCommand();
        //String dir = vce.getPath();
        //String file = "";
        boolean doRefreshCurrent = VcsCommandIO.getBooleanProperty(cmd, VcsCommand.PROPERTY_REFRESH_CURRENT_FOLDER);
        boolean doRefreshParent = VcsCommandIO.getBooleanProperty(cmd, VcsCommand.PROPERTY_REFRESH_PARENT_FOLDER);
        /*
        boolean doRefreshFiles = VcsCommandIO.getBooleanProperty(cmd, VcsCommand.PROPERTY_REFRESH_PROCESSED_FILES);
        if (doRefreshFiles) {
            doRefreshFiles(fileSystem, vce.getFiles());
        }
         */
        if (doRefreshCurrent || doRefreshParent) {
            Collection files = vce.getFiles();
            for(Iterator it = files.iterator(); it.hasNext(); ) {
                String fullPath = (String) it.next();
                String dir = VcsUtilities.getDirNamePart(fullPath);
                String file = VcsUtilities.getFileNamePart(fullPath);
                doRefresh(fileSystem, vce.getExec(), cmd, dir, file, foldersOnly,
                          doRefreshCurrent, doRefreshParent);
            }
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
                Thread t = new Thread(rd, "VCS Recursive Retrieving Thread - "+dirName); // NOI18N
                t.start();
            }
        } else {
            statusProvider.refreshDir(dirName); // NOI18N
        }
    }
    
    private static void doRefresh(VcsFileSystem fileSystem, String exec, VcsCommand cmd,
                                  String dir, String file, boolean foldersOnly,
                                  boolean doRefreshCurrent, boolean doRefreshParent) {
        FileCacheProvider cache = fileSystem.getCacheProvider();
        FileStatusProvider statusProvider = fileSystem.getStatusProvider();
        if (statusProvider == null) return; // No refresh without a status provider
        if((doRefreshCurrent || doRefreshParent) && fileSystem.getDoAutoRefresh(dir/*(String) vars.get("DIR")*/)) { // NOI18N
            //D.deb("Now refresh folder after CheckIn,CheckOut,Lock,Unlock... commands for convenience"); // NOI18N
            fileSystem.setAskIfDownloadRecursively(false); // do not ask if using auto refresh
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
            if (!foldersOnly || cache.isDir(refreshPath)) {
                doRefresh(fileSystem, refreshPath, rec);
            }
        }
        if (!(doRefreshCurrent || doRefreshParent)) fileSystem.removeNumDoAutoRefresh(dir); //(String)vars.get("DIR")); // NOI18N
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
                if (fo == null) fo = fileSystem.getVersioningFileSystem().findResource(files[i]);
                if (fo != null) {
                    RevisionEvent event = new RevisionEvent(fo);
                    event.setRevisionChangeID(whatChanged);
                    event.setChangedRevision(changedRevision);
                    fileSystem.fireRevisionsChanged(event);
                }
            }
        }
    }

    /*
    public static String preprocessCommand(VcsCommand cmd, Hashtable vars) {
    }
     */

    /**
     * Find out which additional user parameters prompt the use for.
     * @return The table of parameter labels for the user to input, one for each parameter
     *         and default values.
     */
    private static Table needPromptForUserParams(VcsFileSystem fileSystem, String exec,
                                                 Hashtable vars, Hashtable varNames,
                                                 Hashtable userParamsIndexes, VcsCommand cmd,
                                                 boolean acceptUserParams) {
        Table results = new Table();
        String search = "${"+USER_GLOBAL_PARAM;
        int pos = 0;
        int index;
        String[] userParamsLabels = fileSystem.getUserParamsLabels();
        String[] userParams = fileSystem.getUserParams();
        String[] userLocalParamsLabels = fileSystem.getUserLocalParamsLabels();
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

    private static void processPrecommands(VcsFileSystem fileSystem, Hashtable vars,
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
            PreCommandPerformer cmdPerf = new PreCommandPerformer(fileSystem, vars);
            String[] values = cmdPerf.process(defVals);
            for (int i = 0; i < values.length; i++) {
                ((VariableInputComponent) componentsWithPrecommands.get(i)).setValue(values[i]);
            }
        }
    }
    
    /** The table of FS and its global descriptor string. */
    private static Hashtable globalInputStrs = new Hashtable();
    /** The table of FS and its parsed global descriptor */
    private static Hashtable globalInputDescrs = new Hashtable();
    
    private static final Object promptLock = new Object();
    
    /**
     * Ask the user for the value of some variables.
     * @param inputDescriptor the descriptor of variable input components
     * @param vars the variables
     * @param cmd the command
     * @param forEachFile whether to ask for these variables for each file being processed
     * @return true if all variables were entered, false otherways
     */
    public static boolean promptForVariables(VcsFileSystem fileSystem, String exec,
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
                    TopManager.getDefault().notifyException(exc);
                    return false;
                }
                inputDescriptor.setValuesAsDefault();
                cmd.setProperty(INPUT_DESCRIPTOR_PARSED, inputDescriptor);
            }
        }
        if (inputDescriptor != null) {
            try {
                processPrecommands(fileSystem, vars, inputDescriptor);
            } catch (UserCancelException cancelExc) {
                return false;
            }
        }
        synchronized (vars) {
            if (needPromptForPR("PASSWORD", exec, vars)) { // NOI18N
                String password;
                synchronized (promptLock) { // disable the possibility, that the user
                    // will be prompted multiple times at once by concurrenly running commands
                    password = fileSystem.getPassword();
                    if (password == null) {
                        NotifyDescriptorInputPassword nd = new NotifyDescriptorInputPassword (g("MSG_Password"), g("MSG_Password")); // NOI18N
                        if (NotifyDescriptor.OK_OPTION.equals (TopManager.getDefault ().notify (nd))) {
                            password = nd.getInputText ();
                        } else {
                            fileSystem.setPassword(null);
                            return false;
                        }
                        fileSystem.setPassword(password);
                    }
                }
                vars.put("PASSWORD", password); // NOI18N
            /* Do not change forEachFile, if the command is successful it will not ask any more */
            }
            if (forEachFile == null || forEachFile[0] == true) {
                String[] userParams = fileSystem.getUserParams();
                Hashtable userParamsVarNames = new Hashtable(); // Variable names of prompt for additional parameters
                Hashtable userParamsIndexes = new Hashtable();

                Boolean ctrlDown = (Boolean)vars.get(VcsFileSystem.VAR_CTRL_DOWN_IN_ACTION);
                boolean expertCondition = fileSystem.isExpertMode() || (ctrlDown != null && ctrlDown.booleanValue() == true);
                boolean acceptUserParams = fileSystem.isAcceptUserParams() || (ctrlDown != null && ctrlDown.booleanValue() == true);
                Table userParamsPromptLabels = needPromptForUserParams(fileSystem, exec, vars, userParamsVarNames,
                                                                       userParamsIndexes, cmd, acceptUserParams);
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
                    Hashtable dlgVars = new Hashtable(vars);
                    VariableInputDialog dlg = new VariableInputDialog(new String[] { file }, inputDescriptor, expertCondition, dlgVars);
                    dlg.setVCSFileSystem(fileSystem, dlgVars);
                    if (cmd.getDisplayName() != null) {
                        dlg.setCmdName(cmd.getDisplayName());
                    } else {
                        dlg.setCmdName(cmd.getName());
                    }
                    if (expertCondition) {
                        if (exec != null) dlg.setExec(exec);
                    }
                    String globalInputStr = (String) vars.get(GLOBAL_INPUT_DESCRIPTOR);
                    String globalInputStrStored = (String) globalInputStrs.get(fileSystem);
                    VariableInputDescriptor globalInputDescriptor = null;
                    if (globalInputStr != null) {
                        if (!globalInputStr.equals(globalInputStrStored)) {
                            try {
                                globalInputDescriptor = VariableInputDescriptor.parseItems(globalInputStr);
                            } catch (VariableInputFormatException exc) {
                                TopManager.getDefault().notifyException(exc);
                                return false;
                            }
                            globalInputStrs.put(fileSystem, globalInputStr);
                            globalInputDescrs.put(fileSystem, globalInputDescriptor);
                            globalInputDescriptor.setValuesAsDefault();
                        } else {
                            globalInputDescriptor = (VariableInputDescriptor) globalInputDescrs.get(fileSystem);
                        }
                    }
                    dlg.setGlobalInput(globalInputDescriptor);
                    dlg.setUserParamsPromptLabels(userParamsPromptLabels, (String) cmd.getProperty(VcsCommand.PROPERTY_ADVANCED_NAME));
                    dlg.setFilePromptDocumentListener(fileSystem, cmd);
                    if (forEachFile == null) dlg.showPromptEach(false);
                    else dlg.setPromptEach(fileSystem.isPromptForVarsForEachFile());
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
                    title += VAR_INPUT_FILE_SEPARATOR + file;
                    String multipleFiles = (String) vars.get("MULTIPLE_FILES");
                    if (multipleFiles != null && multipleFiles.length() > 0) title += VAR_INPUT_MULTIPLE_FILES_TITLE_APPEND;
                    DialogDescriptor dialogDescriptor = new DialogDescriptor(dlg, title, true, dlg.getActionListener());
                    dialogDescriptor.setClosingOptions(new Object[] { NotifyDescriptor.CANCEL_OPTION });
                    final Dialog dialog = TopManager.getDefault().createDialog(dialogDescriptor);
                    dlg.setCloseListener(new java.awt.event.ActionListener() {
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
                        fileSystem.setUserParams(userParams);
                        if (forEachFile != null) {
                            forEachFile[0] = dlg.getPromptForEachFile();
                            fileSystem.setPromptForVarsForEachFile(forEachFile[0]);
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
                        String globalInputStrStored = (String) globalInputStrs.get(fileSystem);
                        if (globalInputStr != null) {
                            VariableInputDescriptor globalInputDescriptor;
                            if (!globalInputStr.equals(globalInputStrStored)) {
                                try {
                                    globalInputDescriptor = VariableInputDescriptor.parseItems(globalInputStr);
                                } catch (VariableInputFormatException exc) {
                                    TopManager.getDefault().notifyException(exc);
                                    return false;
                                }
                                globalInputStrs.put(fileSystem, globalInputStr);
                                globalInputDescrs.put(fileSystem, globalInputDescriptor);
                            } else {
                                globalInputDescriptor = (VariableInputDescriptor) globalInputDescrs.get(fileSystem);
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
        return org.openide.util.NbBundle.getBundle(CommandExecutorSupport.class).getString(s);
    }
    
}

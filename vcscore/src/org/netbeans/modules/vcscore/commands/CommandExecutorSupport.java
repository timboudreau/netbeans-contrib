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

import java.util.Collection;
import java.util.Iterator;
import java.util.Hashtable;
import java.util.Enumeration;

import org.openide.TopManager;
import org.openide.NotifyDescriptor;
import org.openide.DialogDescriptor;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.RetrievingDialog;
import org.netbeans.modules.vcscore.caching.FileCacheProvider;
import org.netbeans.modules.vcscore.caching.FileStatusProvider;
import org.netbeans.modules.vcscore.revision.RevisionListener;
import org.netbeans.modules.vcscore.util.VariableInputDescriptor;
import org.netbeans.modules.vcscore.util.VariableInputComponent;
import org.netbeans.modules.vcscore.util.VariableInputDialog;
import org.netbeans.modules.vcscore.util.VariableInputFormatException;
import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.netbeans.modules.vcscore.util.Table;
import org.netbeans.modules.vcscore.util.NotifyDescriptorInputPassword;

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
    private static int numImportant(VcsFileSystem fileSystem, String paths) {
        if (paths == null) return 0; // Just for robustness
        int num = 0;
        String delim = java.io.File.separator + java.io.File.separator;
        int begin = 0;
        int end = paths.indexOf(delim);
        while (end > 0) {
            String path = paths.substring(begin, end);
            if (fileSystem.isImportant(path)) num++;
            begin = end + delim.length();
            end = paths.indexOf(delim, begin);
        }
        return num;
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
        if (fileSystem.isImportant(fullName)) {
            String numFiles = (String) vars.get("NUM_FILES");
            vars.put("NUM_FILES", ""+numImportant(fileSystem, paths));
            confirmation = Variables.expand(vars, confirmation, true);
            PreCommandPerformer cmdPerf = new PreCommandPerformer(fileSystem, vars);
            confirmation = cmdPerf.process(confirmation);
            if (numFiles != null) vars.put("NUM_FILES", numFiles);
            confirmed = true;
        //} else {
        //    confirmation = null;
        }
        if (confirmed && confirmation.length() > 0) {
            if (NotifyDescriptor.Confirmation.NO_OPTION.equals (
                    TopManager.getDefault ().notify (new NotifyDescriptor.Confirmation (
                        confirmation, NotifyDescriptor.Confirmation.YES_NO_OPTION)))) { // NOI18N
                return CommandsPool.PREPROCESS_CANCELLED; // The command is cancelled for that file
            }
        }
        // II. Then filll output from pre commands:
        PreCommandPerformer cmdPerf = new PreCommandPerformer(fileSystem, vars);
        String exec = cmdPerf.process((String) cmd.getProperty(VcsCommand.PROPERTY_EXEC));
        exec = insertGlobalOptions(exec, vars);
        // III. Ask for the variable input
        if (!promptForVariables(fileSystem, exec, vars, cmd, askForEachFile)) {
            return CommandsPool.PREPROCESS_CANCELLED; // The command is cancelled for that file
        }
        // Ask for the confirmation again, if the preprocessing was done, but there is an important file
        if (!(askForEachFile != null && askForEachFile[0])) {
            int numImp = numImportant(fileSystem, paths);
            if (!confirmed && numImp > 0) {
                String numFiles = (String) vars.get("NUM_FILES");
                vars.put("NUM_FILES", ""+numImp);
                confirmation = Variables.expand(vars, confirmation, true);
                vars.put("NUM_FILES", numFiles);
                if (confirmation.length() > 0) {
                    if (NotifyDescriptor.Confirmation.NO_OPTION.equals (
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
                int index = exec.indexOf(search);
                if (index > 0) {
                    exec = exec.substring(0, index) + glInput + exec.substring(index + search.length());
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
        panel.add(checkBox, BorderLayout.SOUTH);
        return panel;
    }
    
    public static void commandNotification(final VcsCommandExecutor vce,
                                           String notification,
                                           final VcsFileSystem fileSystem) {
        notification = Variables.expand(vce.getVariables(), notification, false);
        NotifyDescriptor msg = new NotifyDescriptor.Message(notification);
        JCheckBox checkBox = new JCheckBox(g("DLG_DoNotNotify"));
        msg.setMessage(createNotificationDesign(notification, checkBox));
        TopManager.getDefault().notify(msg);
        if (checkBox.isSelected()) {
            fileSystem.setCommandNotification(false);
            TopManager.getDefault().notify(new NotifyDescriptor.Message(
                g("DLG_CanBeEnabled")));
        }
    }
    
    /**
     * Performs an automatic refresh after the command finishes.
     */
    public static void doRefresh(VcsFileSystem fileSystem, VcsCommandExecutor vce) {
        VcsCommand cmd = vce.getCommand();
        //String dir = vce.getPath();
        //String file = "";
        Collection files = vce.getFiles();
        for(Iterator it = files.iterator(); it.hasNext(); ) {
            String fullPath = (String) it.next();
            String dir = VcsUtilities.getDirNamePart(fullPath);
            String file = VcsUtilities.getFileNamePart(fullPath);
            doRefresh(fileSystem, vce.getExec(), cmd, dir, file);
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
            String patternMatch = (String) cmd.getProperty(VcsCommand.PROPERTY_REFRESH_RECURSIVELY_PATTERN_MATCHED);
            String patternUnmatch = (String) cmd.getProperty(VcsCommand.PROPERTY_REFRESH_RECURSIVELY_PATTERN_UNMATCHED);
            boolean rec = (exec != null && (cache == null || !cache.isFile(refreshPathFile))
                && (patternMatch != null && patternMatch.length() > 0 && exec.indexOf(patternMatch) >= 0
                    || patternUnmatch != null && patternUnmatch.length() > 0 && exec.indexOf(patternUnmatch) < 0));
            doRefresh(fileSystem, refreshPath, rec);
            /*
                VcsCommand listSub = fileSystem.getCommand(VcsCommand.NAME_REFRESH_RECURSIVELY);
                Object execList = (listSub != null) ? listSub.getProperty(VcsCommand.PROPERTY_EXEC) : null;
                if (execList != null && ((String) execList).trim().length() > 0) {
                    statusProvider.refreshDirRecursive(refreshPath);
                } else {
                    RetrievingDialog rd = new RetrievingDialog(fileSystem, refreshPath, new javax.swing.JFrame(), false);
                    VcsUtilities.centerWindow(rd);
                    Thread t = new Thread(rd, "VCS Recursive Retrieving Thread - "+refreshPath); // NOI18N
                    t.start();
                }
            } else {
                statusProvider.refreshDir(refreshPath);
            }
             */
        }
        if (!(doRefreshCurrent || doRefreshParent)) fileSystem.removeNumDoAutoRefresh(dir); //(String)vars.get("DIR")); // NOI18N
    }
    
    public static void checkRevisionChanges(VcsFileSystem fileSystem, VcsCommandExecutor vce) {
        int whatChanged = 0;
        Object changedInfo = null;
        VcsCommand cmd = vce.getCommand();
        if (VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_CHANGING_NUM_REVISIONS)) {
            whatChanged |= RevisionListener.NUM_REVISIONS_CHANGED;
        }
        if (VcsCommandIO.getBooleanPropertyAssumeDefault(cmd, VcsCommand.PROPERTY_CHANGING_REVISION)) {
            whatChanged |= RevisionListener.ONE_REVISION_CHANGED;
            Object varName = cmd.getProperty(VcsCommand.PROPERTY_CHANGED_REVISION_VAR_NAME);
            if (varName != null) changedInfo = vce.getVariables().get(varName);
        }
        if (whatChanged != 0) {
            String[] files = (String[]) vce.getFiles().toArray(new String[0]);
            for (int i = 0; i < files.length; i++) {
                org.openide.filesystems.FileObject fo = fileSystem.findFileObject(files[i]);
                if (fo != null) fileSystem.fireRevisionsChanged(whatChanged, fo, changedInfo);
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
                                                 Hashtable userParamsIndexes, VcsCommand cmd) {
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
            if (fileSystem.isAcceptUserParams() && userParamsLabels != null) {
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
            if (fileSystem.isAcceptUserParams() && userLocalParamsLabels != null) {
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
        String oldPassword=(String)vars.get("PASSWORD"); vars.put("PASSWORD",""); // NOI18N
        String oldReason=(String)vars.get("REASON"); vars.put("REASON",""); // NOI18N

        String test="variable_must_be_prompt_for"; // NOI18N
        vars.put(name,test);
        String s = Variables.expand(vars, exec, false);
        result = (s.indexOf(test) >= 0) ? true : false ;

        if (oldPassword != null) { vars.put("PASSWORD", oldPassword); } // NOI18N
        if (oldReason != null) { vars.put("REASON", oldReason); } // NOI18N

        return result ;
    }

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
        String inputDescriptorStr = (String) cmd.getProperty(VcsCommand.PROPERTY_INPUT_DESCRIPTOR);
        VariableInputDescriptor inputDescriptor = null;
        if (inputDescriptorStr != null) {
            // Perform the variable expansion to be able to use variables there
            //System.out.println("promptForVariables(): inputDescriptorStr = "+inputDescriptorStr);
            inputDescriptorStr = Variables.expand(vars, inputDescriptorStr, true);
            //System.out.println("FILES_IS_FOLDER = '"+vars.get("FILES_IS_FOLDER")+"'");
            //System.out.println("promptForVariables(): after expand: inputDescriptorStr = "+inputDescriptorStr);
            PreCommandPerformer cmdPerf = new PreCommandPerformer(fileSystem, vars);
            inputDescriptorStr = cmdPerf.process(inputDescriptorStr);
            try {
                inputDescriptor = VariableInputDescriptor.parseItems(inputDescriptorStr);
            } catch (VariableInputFormatException exc) {
                TopManager.getDefault().notifyException(exc);
                return false;
            }
        }
        synchronized (vars) {
            if (needPromptForPR("PASSWORD", exec, vars)) { // NOI18N
                String password = fileSystem.getPassword();
                if (password == null) {
                    password = ""; // NOI18N
                    NotifyDescriptorInputPassword nd = new NotifyDescriptorInputPassword (g("MSG_Password"), g("MSG_Password")); // NOI18N
                    if (NotifyDescriptor.OK_OPTION.equals (TopManager.getDefault ().notify (nd))) {
                        password = nd.getInputText ();
                    } else {
                        return false;
                    }
                    fileSystem.setPassword(password);
                }
                vars.put("PASSWORD", password); // NOI18N
            /* Do not change forEachFile, if the command is successful it will not ask any more */
            }
            if (forEachFile == null || forEachFile[0] == true) {
                String[] userParams = fileSystem.getUserParams();
                Hashtable userParamsVarNames = new Hashtable(); // Variable names of prompt for additional parameters
                Hashtable userParamsIndexes = new Hashtable();
                Table userParamsPromptLabels = needPromptForUserParams(fileSystem, exec, vars, userParamsVarNames, userParamsIndexes, cmd);
                /*
                createTempPromptFiles(promptFile);
                if (prompt != null && prompt.size() > 0 || ask != null && ask.size() > 0 ||
                promptFile.size() > 0 || userParamsPromptLabels.size() > 0) {
                    */
                if (inputDescriptor != null && showInputDescriptor(inputDescriptor, fileSystem.isExpertMode())
                    || userParamsPromptLabels.size() > 0) {
                        
                    String file = (String) vars.get("FILE"); // NOI18N
                    VariableInputDialog dlg = new VariableInputDialog(new String[] { file }, inputDescriptor, fileSystem.isExpertMode());
                    dlg.setVCSFileSystem(fileSystem, vars);
                    if (fileSystem.isExpertMode()) {
                        if (exec != null) dlg.setExec(exec);
                        String globalInputStr = (String) vars.get(GLOBAL_INPUT_DESCRIPTOR);
                        if (globalInputStr != null) {
                            VariableInputDescriptor globalInputDescriptor;
                            try {
                                globalInputDescriptor = VariableInputDescriptor.parseItems(globalInputStr);
                            } catch (VariableInputFormatException exc) {
                                TopManager.getDefault().notifyException(exc);
                                return false;
                            }
                            dlg.setGlobalInput(globalInputDescriptor);
                        }
                    }
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
                    dialog.setVisible(true);
                    if (dlg.isValidInput()) {
                        dlg.processActions();
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
                    if (forEachFile != null) {
                        forEachFile[0] = false;
                    }
                }
            }
            return true;
        }
    }
    
    private static boolean showInputDescriptor(VariableInputDescriptor inputDescriptor, boolean isExpertMode) {
        VariableInputComponent[] inputComponents = inputDescriptor.components();
        if (inputComponents.length == 0) return false;
        for (int i = 0; i < inputComponents.length; i++) {
            if (inputComponents[i].isExpert() && isExpertMode ||
                (!inputComponents[i].isExpert()
                 && inputComponents[i].getComponent() != VariableInputDescriptor.INPUT_GLOBAL))
                return true;
        }
        return false;
    }


    private static String g(String s) {
        return org.openide.util.NbBundle.getBundle(CommandExecutorSupport.class).getString(s);
    }
    
}

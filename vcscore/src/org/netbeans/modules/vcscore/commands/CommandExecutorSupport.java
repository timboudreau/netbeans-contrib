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

import java.util.Collection;
import java.util.Iterator;
import java.util.Hashtable;

import org.openide.TopManager;
import org.openide.NotifyDescriptor;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.RetrievingDialog;
import org.netbeans.modules.vcscore.caching.FileCacheProvider;
import org.netbeans.modules.vcscore.caching.FileStatusProvider;
import org.netbeans.modules.vcscore.revision.RevisionListener;
import org.netbeans.modules.vcscore.util.VcsUtilities;

/**
 * This class contains a support for VCS commands execution.
 *
 * @author  Martin Entlicher
 */
public class CommandExecutorSupport extends Object {

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
        PreCommandPerformer cmdPerf = new PreCommandPerformer(fileSystem, cmd, vars);
        String exec = cmdPerf.process();
        // III. Perform the default variable expansion
        //exec = Variables.expand(vars, exec, true);
        // III. Ask for the variable input
        /*
        boolean[] askForEachFile;
        if (fileNames == null || fileNames.length <= 1) {
            askForEachFile = null;
        } else {
            askForEachFile = new boolean[1];
            askForEachFile[0] = (CommandsPool.PREPROCESS_NEXT_FILE == lastPreprocessState);
        }
         */
        // III. Ask for the variable input
        if (!fileSystem.promptForVariables(exec, vars, cmd, askForEachFile)) {
            return CommandsPool.PREPROCESS_CANCELLED; // The command is cancelled for that file
        }
        /*
        // Have to remove multifile variables if has to execute on only one file:
        if (askForEachFile != null && askForEachFile[0]) {
            vars.put("PATHS", "");
            vars.put("QPATHS", "");
            vars.put("FILES", "");
        }
         */
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
        exec = Variables.expand(vars, exec, true);
        //vce.updateExec(exec);
        // V. Allow a custom preprocessing
        exec = vce.preprocessCommand(cmd, vars, exec);
        if (askForEachFile != null && askForEachFile[0]) {
            return CommandsPool.PREPROCESS_NEXT_FILE;
        } else {
            return CommandsPool.PREPROCESS_DONE;
        }
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

    private static String g(String s) {
        return org.openide.util.NbBundle.getBundle(CommandExecutorSupport.class).getString(s);
    }
    
}

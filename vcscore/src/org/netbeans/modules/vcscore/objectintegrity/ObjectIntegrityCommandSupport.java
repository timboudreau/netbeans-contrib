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

package org.netbeans.modules.vcscore.objectintegrity;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.openide.DialogDescriptor;
import org.openide.filesystems.FileObject;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;

import org.netbeans.api.vcs.VcsManager;
import org.netbeans.api.vcs.commands.AddCommand;
import org.netbeans.api.vcs.commands.CheckInCommand;
import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;

import org.netbeans.spi.vcs.commands.CommandSupport;

/**
 * The support for command, that takes care about the objects integrity.
 *
 * @author  Martin Entlicher
 */
public class ObjectIntegrityCommandSupport extends CommandSupport implements java.security.PrivilegedAction {
    
    /** Creates a new instance of ObjectIntegrityCommandSupport */
    public ObjectIntegrityCommandSupport() {
        super(ObjectIntegrityCommand.class);
    }
    
    /**
     * Perform the actual execution of the command from the provided info.
     * This method might be called multiple times and even concurrently
     * for a single CommandSupport instance. It's recommended not to
     * modify any variables from this instance object in this method.
     */
    protected int execute(CommandTask task) {
        ObjectIntegrityCommand cmd = (ObjectIntegrityCommand) getCommand(task);
        AddCommand addCmd = cmd.getAddCommand();
        addCmd.setExpertMode(cmd.isExpertMode());
        addCmd.setGUIMode(cmd.isGUIMode());
        FileObject[] filesToAdd = cmd.getFilesToAdd();
        if (filesToAdd == null) return CommandTask.STATUS_SUCCEEDED;
        addCmd.setFiles(filesToAdd);
        boolean customized = VcsManager.getDefault().showCustomizer(addCmd);
        if (customized) {
            CommandTask cmdTask = addCmd.execute();
            try {
                cmdTask.waitFinished(0);
                return cmdTask.getExitStatus();
            } catch (InterruptedException iex) {
                return CommandTask.STATUS_INTERRUPTED;
            }
        } else {
            return CommandTask.STATUS_INTERRUPTED;
        }
    }
    
    /** Find, whether this command can act on a set of files.
     * @param files The array of files to inspect
     * @return an array of files the command can act on or <code>null</code> when
     * it can not act on any file listed.
     */
    public FileObject[] getApplicableFiles(FileObject[] files) {
        //System.out.println("ObjectIntegrityCommandSupport.getApplicableFiles("+java.util.Arrays.asList(files)+")");
        ObjectIntegrityCommand cmd = (ObjectIntegrityCommand) getCommand();
        VcsObjectIntegritySupport integritySupport = cmd.getObjectIntegritySupport();
        if (integritySupport != null) {
            files = findFilesToAdd(integritySupport, files);
            if (files.length == 0) {
                files = null;
            }
        }
        //System.out.println("   filesToAdd = "+((files == null) ? null : java.util.Arrays.asList(files)));
        return files;
    }
    
    /** Get the display name of the command. It will be visible on the popup menu under this name.
     * When <code>null</code>, the command will not be visible on the popup menu.
     */
    public String getDisplayName() {
        return NbBundle.getMessage(ObjectIntegrityCommandSupport.class,
                                   "ObjectIntegrityCommand.name"); // NOI18N
    }
    
    /** Get the name of the command.
     */
    public String getName() {
        return "Object Integrity Helper";
    }
    
    /** Whether the command supports an expert mode. The command should provide
     * a more complex customizer and/or output if in expert mode. If the
     * command does not differentiate expert mode, it should declare, that
     * it does not have an expert mode.
     * @return true If the command differentiate expert mode, false otherwise
     */
    public boolean hasExpertMode() {
        return false;
    }
    
    protected Object clone() throws CloneNotSupportedException {
        return new ObjectIntegrityCommandSupport();//addCommand, checkInCommand);
    }
    
    /**
     * Create the customizer for the command. This uses a hack through the PrivilegedAction.
     * If the returned object is UserCancelException, the command is canceled.
     */
    public Object run() {
        ObjectIntegrityCommand cmd = (ObjectIntegrityCommand) getCommand();
        ObjectIntegrityPanel integrityPanel = new ObjectIntegrityPanel();
        FileObject[] files = cmd.getFiles();
        if (files == null) return null;//new org.openide.util.UserCancelException();
        String[] filePaths = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            filePaths[i] = files[i].getPath();
        }
        integrityPanel.setFilePaths(filePaths);
        final Object[] options = new Object[] {
            NbBundle.getMessage(ObjectIntegrityCommandSupport.class, "ObjectIntegrityCommand.btn.add"),
            NbBundle.getMessage(ObjectIntegrityCommandSupport.class, "ObjectIntegrityCommand.btn.skip"),
            DialogDescriptor.CANCEL_OPTION };
        DialogDescriptor dlgDescriptor = new DialogDescriptor(integrityPanel,
            NbBundle.getMessage(ObjectIntegrityCommandSupport.class, "ObjectIntegrityCommand.dlg.title"),
            true, options, options[0], DialogDescriptor.BOTTOM_ALIGN,
            new HelpCtx(ObjectIntegrityCommandSupport.class),
            new DialogActionListener(cmd, integrityPanel, options));
        dlgDescriptor.setClosingOptions(options); // All options are closing.
        return dlgDescriptor;
    }
    
    private static FileObject[] findFilesToAdd(VcsObjectIntegritySupport objectIntegritySupport,
                                               FileObject[] files) {
        List localFiles = new ArrayList();
        Map secondariesByPrimary = objectIntegritySupport.getObjectsWithLocalFiles();
        for (int i = 0; i < files.length; i++) {
            FileObject fo = files[i];
            if (fo.isFolder()) {
                addLocalsUnder(fo, secondariesByPrimary, objectIntegritySupport,
                               localFiles);
            } else {
                String path = fo.getPath();
                Set locals = (Set) secondariesByPrimary.get(path);
                if (locals != null) {
                    for (Iterator it = locals.iterator(); it.hasNext(); ) {
                        String localFile = (String) it.next();
                        FileObject local = objectIntegritySupport.findFileObject(localFile);
                        if (local != null) localFiles.add(local);
                    }
                }
            }
        }
        return (FileObject[]) localFiles.toArray(new FileObject[localFiles.size()]);
    }
    
    private static void addLocalsUnder(FileObject folder, Map secondariesByPrimary,
                                       VcsObjectIntegritySupport objectIntegritySupport,
                                       List localFiles) {
        String folderPath = folder.getPath();
        int length = folderPath.length();
        for (Iterator it = secondariesByPrimary.keySet().iterator(); it.hasNext(); ) {
            String primaryPath = (String) it.next();
            if (primaryPath.startsWith(folderPath) && primaryPath.length() > length && primaryPath.charAt(length) == '/') {
                Set locals = (Set) secondariesByPrimary.get(primaryPath);
                if (locals != null) {
                    for (Iterator lockIt = locals.iterator(); lockIt.hasNext(); ) {
                        String localFile = (String) lockIt.next();
                        FileObject local = objectIntegritySupport.findFileObject(localFile);
                        if (local != null) localFiles.add(local);
                    }
                }
            }
        }
    }
    
    
    private static final class DialogActionListener extends Object implements ActionListener {
        
        private ObjectIntegrityCommand cmd;
        private ObjectIntegrityPanel integrityPanel;
        private Object[] options;
        
        public DialogActionListener(ObjectIntegrityCommand cmd,
                                    ObjectIntegrityPanel integrityPanel,
                                    Object[] options) {
            this.cmd = cmd;
            this.integrityPanel = integrityPanel;
            this.options = options;
        }
        
        /** Invoked when an action occurs.
         */
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();
            if (options[0].equals(command)) { // Add
                String[] filePaths = integrityPanel.getSelectedFilePaths();
                if (filePaths.length == 0) {
                    cmd.setFilesToAdd(null);
                } else {
                    VcsObjectIntegritySupport vcsOIS = cmd.getObjectIntegritySupport();
                    FileObject[] files = new FileObject[filePaths.length];
                    for (int i = 0; i < filePaths.length; i++) {
                        files[i] = vcsOIS.findFileObject(filePaths[i]);
                    }
                    cmd.setFilesToAdd(files);
                }
            } else if (options[1].equals(command)) { // Skip
                cmd.setFilesToAdd(null);
            }
            String[] ignoredFiles = integrityPanel.getIgnoredFilePaths();
            if (ignoredFiles.length > 0) {
                VcsObjectIntegritySupport vcsOIS = cmd.getObjectIntegritySupport();
                vcsOIS.addIgnoredFiles(ignoredFiles);
            }
        }
        
    }
    
}

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
    
    /** The add command, that will be called to add files. */
    //private AddCommand addCommand;
    /** The check in command, that is used to retrieve some options. */
    //private CheckInCommand checkInCommand;
    
    /** Creates a new instance of ObjectIntegrityCommandSupport */
    public ObjectIntegrityCommandSupport(/*AddCommand addCommand,
                                         CheckInCommand checkInCommand*/) {
        super(ObjectIntegrityCommand.class);
        //this.addCommand = addCommand;
        //this.checkInCommand = checkInCommand;
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
        addCmd.setFiles(cmd.getFiles());
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
        ObjectIntegrityCommand cmd = (ObjectIntegrityCommand) getCommand();
        VcsObjectIntegritySupport integritySupport = cmd.getObjectIntegritySupport();
        files = findFilesToAdd(integritySupport, files);
        if (files.length == 0) return null;
        else return files;
    }
    
    /** Get the display name of the command. It will be visible on the popup menu under this name.
     * When <code>null</code>, the command will not be visible on the popup menu.
     */
    public String getDisplayName() {
        return NbBundle.getMessage(ObjectIntegrityCommandSupport.class,
                                   "ObjectIntegrityCommand.name");
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
        //integrityPanel.setCommand(cmd);
        FileObject[] files = cmd.getFiles();
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
            new DialogActionListener(cmd, integrityPanel));
        return dlgDescriptor;
    }
    
    /**
     * Returns a map of providers and the associated files. The associated
     * files are an array of FileObjects.
     *
    private static Map findCommandProvidersForFiles(FileObject[] files) {
        Map providers = new HashMap();
        for (int i = 0; i < files.length; i++) {
            FileObject fo = files[i];
            VcsCommandsProvider provider = VcsCommandsProvider.findProvider(fo);
            //System.out.println("  fo = "+fo+" provider = "+provider);
            if (provider != null) {
                if (providers.containsKey(provider)) {
                    List fileList = (List) providers.get(provider);
                    fileList.add(fo);
                } else {
                    List fileList = new ArrayList();
                    fileList.add(fo);
                    providers.put(provider, fileList);
                    //System.out.println("  put("+provider+", "+fileList+")");
                }
            }
        }
        for (Iterator it = providers.keySet().iterator(); it.hasNext(); ) {
            VcsCommandsProvider provider = (VcsCommandsProvider) it.next();
            List fileList = (List) providers.get(provider);
            FileObject[] fileArray = (FileObject[]) fileList.toArray(new FileObject[fileList.size()]);
            providers.put(provider, fileArray);
        }
        return providers;
    }
     */
    
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
        
        public DialogActionListener(ObjectIntegrityCommand cmd,
                                    ObjectIntegrityPanel integrityPanel) {
            this.cmd = cmd;
            this.integrityPanel = integrityPanel;
        }
        
        /** Invoked when an action occurs.
         */
        public void actionPerformed(ActionEvent e) {
        }
        
    }
    
}

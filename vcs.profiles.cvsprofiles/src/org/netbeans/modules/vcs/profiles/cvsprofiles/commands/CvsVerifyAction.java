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

package org.netbeans.modules.vcs.profiles.cvsprofiles.commands;

import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.HashSet;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.SwingUtilities;

import org.openide.DialogDescriptor;
import org.openide.TopManager;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.loaders.DataShadow;
import org.openide.util.NbBundle;
import org.openide.util.SharedClassObject;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.VcsAction;
import org.netbeans.modules.vcscore.actions.AddCommandAction;
import org.netbeans.modules.vcscore.actions.CommandActionSupporter;
import org.netbeans.modules.vcscore.actions.GeneralCommandAction;
import org.netbeans.modules.vcscore.actions.UpdateCommandAction;
import org.netbeans.modules.vcscore.caching.FileStatusProvider;
import org.netbeans.modules.vcscore.cmdline.ExecuteCommand;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.grouping.GroupUtils;
import org.netbeans.modules.vcscore.ui.*;
import org.netbeans.modules.vcscore.util.Table;

/**
 * The verification of CVS files in a group.
 *
 * @author  Martin Entlicher
 */
public class CvsVerifyAction extends java.lang.Object implements VcsAdditionalCommand {
    
    private static String UP_TO_DATE = "Up-to-date";
    private static String[] TO_UPDATE = { "Needs Checkout", "Needs Patch", "Needs Merge" };

    private ArrayList localFiles;
    private ArrayList outOfDateFiles;
    private ArrayList uptoDateFiles;
    private NotChangedFilesPanel ncfPanel;
    private ToAddFilesPanel taPanel;
    private ToUpdateFilesPanel tuPanel;

    private VcsFileSystem fileSystem = null;

    /** Creates new CvsVerifyAction */
    public CvsVerifyAction() {
    }

    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
    
    static List getFOs(VcsFileSystem fileSystem, Hashtable vars) {
        Collection files = ExecuteCommand.createProcessingFiles(fileSystem, vars);
        ArrayList fos = new ArrayList(files.size());
        for (Iterator fileIt = files.iterator(); fileIt.hasNext(); ) {
            String file = (String) fileIt.next();
            FileObject fo = fileSystem.findFileObject(file);
            if (fo != null) {
                fos.add(fo);
            }
        }
        return fos;
    }

    private void fillFilesByState(List fos) {
        FileStatusProvider statusProvider = fileSystem.getStatusProvider();
        if (statusProvider == null) {
            localFiles.addAll(fos);
        } else {
            HashSet toUpdate = new HashSet(Arrays.asList(TO_UPDATE));
            for (Iterator it = fos.iterator(); it.hasNext(); ) {
                FileObject fo = (FileObject) it.next();
                String file = fo.getPackageNameExt('/', '.');
                String status = statusProvider.getFileStatus(file);
                //System.out.println("fillFilesByState: file = '"+file+"', status = "+status);
                if (statusProvider.getLocalFileStatus().equals(status)) {
                    localFiles.add(fo);
                    //System.out.println("  is Local");
                } else if (toUpdate.contains(status)) {
                    outOfDateFiles.add(fo);
                    //System.out.println("  is Out Of Date");
                } else if (UP_TO_DATE.equals(status)) {
                    uptoDateFiles.add(fo);
                    //System.out.println("  is Up to date");
                } else {
                    //System.out.println("  is Unrecognized => should me modified or so.");
                }
            }
        }
    }
    
    static void refreshFilesState(String cmdName, VcsFileSystem fileSystem, Hashtable vars) throws InterruptedException {
        VcsCommand cmd = fileSystem.getCommand(cmdName);
        if (VcsCommandIO.getBooleanProperty(cmd, VcsCommand.PROPERTY_RUN_ON_MULTIPLE_FILES)) {
            VcsCommandExecutor vce = fileSystem.getVcsFactory().getCommandExecutor(cmd, vars);
            fileSystem.getCommandsPool().preprocessCommand(vce, new Hashtable(vars), fileSystem);
            fileSystem.getCommandsPool().startExecutor(vce);
            try {
                fileSystem.getCommandsPool().waitToFinish(vce);
            } catch (InterruptedException iexc) {
                fileSystem.getCommandsPool().kill(vce);
                throw iexc;
            }
        } else {
            List fos = getFOs(fileSystem, vars);
            Table files = new Table();
            for (Iterator it = fos.iterator(); it.hasNext(); ) {
                FileObject fo = (FileObject) it.next();
                files.put(fo.getPackageNameExt('/', '.'), fo);
            }
            VcsCommandExecutor[] execs = VcsAction.doCommand(files, cmd, null, fileSystem, null, null, null, null, true);
            CommandsPool cpool = fileSystem.getCommandsPool();
            for (int i = 0; i < execs.length; i++) {
                try {
                    cpool.waitToFinish(execs[i]);
                } catch (InterruptedException iexc) {
                    for (int j = i; j < execs.length; j++) {
                        cpool.kill(execs[j]);
                    }
                    throw iexc;
                }
            }
        }
    }

    /**
     * This method is used to execute the command.
     * @param vars the variables that can be passed to the command
     * @param args the command line parametres passed to it in properties
     * @param stdoutListener listener of the standard output of the command
     * @param stderrListener listener of the error output of the command
     * @param stdoutDataListener listener of the standard output of the command which
     *                          satisfies regex <CODE>dataRegex</CODE>
     * @param dataRegex the regular expression for parsing the standard output
     * @param stderrDataListener listener of the error output of the command which
     *                          satisfies regex <CODE>errorRegex</CODE>
     * @param errorRegex the regular expression for parsing the error output
     * @return true if the command was succesfull
     *        false if some error occured.
     */
    public boolean exec(Hashtable vars, String[] args,
                        CommandOutputListener stdoutListener, CommandOutputListener stderrListener,
                        CommandDataOutputListener stdoutDataListener, String dataRegex,
                        CommandDataOutputListener stderrDataListener, String errorRegex) {
        localFiles = new ArrayList();
        outOfDateFiles = new ArrayList();
        uptoDateFiles = new ArrayList();
        if (args.length > 0) {
            try {
                refreshFilesState(args[0], fileSystem, vars);
            } catch (InterruptedException iexc) {
                return false;
            }
        }
        List foList = getFOs(fileSystem, vars);
        fillFilesByState(foList);
        showDialog();
        return true;
    }
    
    private void showDialog() {
        VerifyGroupPanel panel = new VerifyGroupPanel();
        boolean nothing = true;
        if (localFiles.size() > 0) {
            taPanel = new ToAddFilesPanel(localFiles);
            panel.addPanel(taPanel, NbBundle.getBundle(CvsVerifyAction.class).getString("VcsVerifyAction.ToAdd"));
            nothing = false;
        }
        if (outOfDateFiles.size() > 0) {
            tuPanel = new ToUpdateFilesPanel(outOfDateFiles);
            panel.addPanel(tuPanel, NbBundle.getBundle(CvsVerifyAction.class).getString("CvsVerifyAction.ToUpdate"));
            nothing = false;
        }
        if (uptoDateFiles.size() > 0) {
            List dobjList = getDOForNotChanged(uptoDateFiles);
            if (dobjList.size() > 0) {
                ncfPanel = new NotChangedFilesPanel(dobjList);
                panel.addPanel(ncfPanel, NbBundle.getBundle(CvsVerifyAction.class).getString("VcsVerifyAction.NotChanged"));
                nothing = false;
            }
        }
        String title = NbBundle.getBundle(CvsVerifyAction.class).getString("VcsVerifyAction.title");
        DialogDescriptor dd = new DialogDescriptor(panel, title);
        dd.setHelpCtx(new org.openide.util.HelpCtx(VerifyGroupPanel.class));
        dd.setModal(false);
        if (nothing) {
            panel.setDescription(NbBundle.getBundle(CvsVerifyAction.class).getString("VcsVerifyAction.NoProblem"));
            JButton btnClose = new JButton(NbBundle.getBundle(CvsVerifyAction.class).getString("VcsVerifyAction.closeButton"));
            Object[] options = new Object[] {btnClose};
            dd.setOptions(options);
            dd.setClosingOptions(options);
        } else {
            panel.setDescription(NbBundle.getBundle(CvsVerifyAction.class).getString("VcsVerifyAction.ProblemsFound"));
            final JButton btnCorrect = new JButton(NbBundle.getBundle(CvsVerifyAction.class).getString("VcsVerifyAction.correctButton"));
            btnCorrect.getAccessibleContext().setAccessibleDescription(NbBundle.getBundle(CvsVerifyAction.class).getString("ACSD_VcsVerifyAction.correctButton"));
            Object[] options = new Object[] {btnCorrect, DialogDescriptor.CANCEL_OPTION};
            dd.setOptions(options);
            dd.setClosingOptions(options);
            dd.setButtonListener(new ActionListener() {
                public void actionPerformed(ActionEvent event) {
                    if (event.getSource().equals(btnCorrect)) {
                        correctGroup();
                        return;
                    }
                }
            });
        }
        final Dialog dial = TopManager.getDefault().createDialog(dd);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                dial.show();
            }  
        });        
    }
    
    private String getStatus(DataObject dobj) {
        String status = null;
        for (Iterator it = dobj.files().iterator(); it.hasNext(); ) {
            FileObject fo = (FileObject) it.next();
            if (!fileSystem.isProcessUnimportantFiles() &&
                !fileSystem.isImportant(fo.getPackageNameExt('/', '.'))) continue;
            String foStatus = fileSystem.getStatus(fo);
            if (status == null) {
                status = foStatus;
            } else if (!status.equals(foStatus)) {
                status += foStatus;
                break;
            }
        }
        return status;
    }

    private List getDOForNotChanged(List list) {
        Iterator it = list.iterator();
        HashSet dobjStat = new HashSet();
        while (it.hasNext()) {
            FileObject fo = (FileObject)it.next();
            DataObject dobj = null;
            try {
                dobj = DataObject.find(fo);
                //System.out.println("datablject=" + dobj.getName());
                String stat = getStatus(dobj);
                //System.out.println("dataobject's status=" + stat);
                if (UP_TO_DATE.equals(stat)) {
                    dobjStat.add(dobj);
                }
                
            } catch (DataObjectNotFoundException exc) {
                continue;
            }
        }
        List toReturn = new ArrayList(dobjStat.size());
        toReturn.addAll(dobjStat);
        return toReturn;
    }
   
    private void correctGroup() {
        if (ncfPanel != null) {
            performNCFCorrection();
        }
        if (tuPanel != null) {
            performTUCorrection();
        }
        if (taPanel != null) {
            performTACorrection();
        }
    }
    
    public void performTUCorrection() {
        List list = tuPanel.getFileObjects();
        if (list != null && list.size() != 0) {
            //VcsActionSupporter supp = ((CommandLineVcsFileSystem) fileSystem).getVcsActionSupporter() FsCommandFactory.getFsInstance().getSupporter();
            UpdateCommandAction act = (UpdateCommandAction) SharedClassObject.findObject(UpdateCommandAction.class, true);
            FileObject[] fos = new FileObject[list.size()];
            fos = (FileObject[]) list.toArray(fos);
            CommandActionSupporter supp = (CommandActionSupporter) fos[0].getAttribute(GeneralCommandAction.VCS_ACTION_ATTRIBUTE);
            supp.performAction(act, fos);
        }
    }
    
    public void performNCFCorrection() {
        List list = ncfPanel.getSelectedDataObjects();
        if (list != null && list.size() != 0) {
            Iterator it = list.iterator();
            while (it.hasNext()) {
                DataObject obj = (DataObject) it.next();
                DataShadow shadow = GroupUtils.findDOInGroups(obj);
                if (shadow != null) {
                    try {
                        shadow.delete();
                    } catch (java.io.IOException exc) {
                    }
                }
            }
        }
    }
    
    public void performTACorrection() {
        java.util.List foList = taPanel.getFileObjects();
        if (foList != null && foList.size() != 0) {
            //CommandActionSupporter supp = FsCommandFactory.getFsInstance().getSupporter();
            AddCommandAction act = (AddCommandAction) SharedClassObject.findObject(AddCommandAction.class, true);
            FileObject[] fos = new FileObject[foList.size()];
            fos = (FileObject[]) foList.toArray(fos);
            CommandActionSupporter supp = (CommandActionSupporter) fos[0].getAttribute(GeneralCommandAction.VCS_ACTION_ATTRIBUTE);
            supp.performAction(act, fos);
        }
    }
    
}

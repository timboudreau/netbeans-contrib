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

package org.netbeans.modules.vcs.profiles.pvcs.commands;

import java.io.File;
import java.util.Hashtable;

import org.openide.NotifyDescriptor;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.caching.FileStatusProvider;
import org.openide.DialogDisplayer;

/**
 * Find whether a writable copy exists before get command
 * and provide a warning if true.
 * @author  Martin Entlicher
 */
public class PvcsGetWithQuestions implements VcsAdditionalCommand {

    private VcsFileSystem fileSystem = null;
    /** Creates new PvcsGetWritableCheck */
    public PvcsGetWithQuestions() {
    }
    
    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    private boolean runCommand(String name, Hashtable vars, CommandOutputListener stderrNRListener) {
        VcsCommand cmd = fileSystem.getCommand(name);
        if (cmd == null) {
            stderrNRListener.outputLine("Uknown command '"+name+"'.");
            return false;
        }
        VcsCommandExecutor vce = fileSystem.getVcsFactory().getCommandExecutor(cmd, vars);
        fileSystem.getCommandsPool().preprocessCommand(vce, vars, fileSystem);
        fileSystem.getCommandsPool().startExecutor(vce, fileSystem);
        try {
            fileSystem.getCommandsPool().waitToFinish(vce);
        } catch (InterruptedException iexc) {
            fileSystem.getCommandsPool().kill(vce);
            return false;
        }
        return vce.getExitStatus() == VcsCommandExecutor.SUCCEEDED;
    }
    
    private boolean checkWritable(Hashtable vars) {
        String rootDir = (String) vars.get("ROOTDIR"); // NOI18N
        if (rootDir == null) {
            rootDir = "."; // NOI18N
        }
        String dir = (String) vars.get("DIR"); // NOI18N
        if (dir == null) {
            dir = ""; // NOI18N
        }
        String module = (String) vars.get("MODULE"); // NOI18N
        if (dir.equals("")) { // NOI18N
            dir = rootDir;
            if (module != null && module.length() > 0) {
                dir += File.separator + module;
            }
        } else {
            if (module == null) {
                dir = rootDir + File.separator + dir;
            } else {
                dir = rootDir + File.separator + module + File.separator + dir;
            }
        }
        if (dir.charAt(dir.length() - 1) == File.separatorChar)
            dir = dir.substring(0, dir.length() - 1);
        String file = (String) vars.get("FILE");
        dir = dir + File.separator + file;
        File filePath = new File(dir);
        if (filePath.canWrite()) {
            System.out.println("");
            Object confirmation = DialogDisplayer.getDefault().notify(
                new NotifyDescriptor.Confirmation(org.openide.util.NbBundle.getMessage(
                    PvcsGetWithQuestions.class, "Get_Writable_Confirmation", file)));
            if (!NotifyDescriptor.OK_OPTION.equals(confirmation)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean checkLocked(Hashtable vars, String getArguments) {
        if (getArguments != null && getArguments.indexOf("-l") >= 0) {
            String file = (String) vars.get("FILE");
            String path = (String) vars.get("PATH");
            FileStatusProvider statusProvider = fileSystem.getStatusProvider();
            if (statusProvider != null) {
                String locker = statusProvider.getFileLocker(path);
                if (locker != null && locker.length() > 0) {
                    Object confirmation = DialogDisplayer.getDefault().notify(
                        new NotifyDescriptor.Confirmation(org.openide.util.NbBundle.getMessage(
                            PvcsGetWithQuestions.class, "Get_Lock_Confirmation", file, locker)));
                    if (!NotifyDescriptor.OK_OPTION.equals(confirmation)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Expect the PVCS get command as the first argument.
     */
    public boolean exec(Hashtable vars, String[] args,
                        CommandOutputListener stdoutNRListener, CommandOutputListener stderrNRListener,
                        CommandDataOutputListener stdoutListener, String dataRegex,
                        CommandDataOutputListener stderrListener, String errorRegex) {
        if (!checkWritable(vars)) return true;
        if (!checkLocked(vars, args.length > 1 ? args[1] : null)) return true;
        boolean state = runCommand(args[0], vars, stderrNRListener);
        return state;
    }
}

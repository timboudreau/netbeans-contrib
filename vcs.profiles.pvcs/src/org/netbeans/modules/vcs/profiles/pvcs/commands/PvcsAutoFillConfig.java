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

package org.netbeans.modules.vcs.profiles.pvcs.commands;

import java.util.Hashtable;

import org.netbeans.modules.vcscore.VcsAction;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.util.Table;

/**
 * Automatic fill of configuration for PVCS
 * @author  Martin Entlicher
 */
public class PvcsAutoFillConfig extends Object implements VcsAdditionalCommand,
                                                          CommandDataOutputListener {
    
    private static final String ERROR = " [Error]"; // NOI18N
    
    private VcsFileSystem fileSystem;
    private boolean failed;
    private String work;
    
    /** Creates new PvcsAutoFillConfig */
    public PvcsAutoFillConfig() {
    }

    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
    
    public boolean exec(Hashtable vars, String[] args,
                        CommandOutputListener stdoutNRListener, CommandOutputListener stderrNRListener,
                        CommandDataOutputListener stdoutListener, String dataRegex,
                        CommandDataOutputListener stderrListener, String errorRegex) {
        
        if (args.length < 1) {
            stderrNRListener.outputLine("A get work location command name expected as an argument.");
            return false;
        }
        VcsCommand cmd = fileSystem.getCommand(args[0]);
        if (cmd == null) {
            stderrNRListener.outputLine("Unknown command '"+args[0]+"'.");
            return false;
        }
        String projectDB = (String) vars.get("PROJECT_DB");
        if (projectDB == null || projectDB.length() == 0) return true;
        Table dummyFiles = new Table();
        dummyFiles.put("foo.txt", null);
        work = null;
        failed = false;
        VcsCommandExecutor[] execs = VcsAction.doCommand(dummyFiles, cmd, null, fileSystem,
                                                         null, null, this, null);
        try {
            for (int i = 0; i < execs.length; i++) {
                fileSystem.getCommandsPool().waitToFinish(execs[i]);
            }
        } catch (InterruptedException intrEx) {
        }
        if (failed || execs[0].getExitStatus() != VcsCommandExecutor.SUCCEEDED) work = null;
        if (work != null) {
            try {
                fileSystem.setRootDirectory(new java.io.File(work));
                vars.put("ROOTDIR", work);
            } catch (java.beans.PropertyVetoException pvex) {
            } catch (java.io.IOException ioex) {}
        }
        return true;
    }
    
    /**
     * This method is called, with elements of the output data.
     * @param elements the elements of output data.
     */
    public void outputData(String[] elements) {
        if (elements[0] == null || elements[0].length() == 0) return ;
        if (elements[0].indexOf(ERROR) >= 0) {
            failed = true;
            return ;
        }
        work = elements[0];
    }
    
}

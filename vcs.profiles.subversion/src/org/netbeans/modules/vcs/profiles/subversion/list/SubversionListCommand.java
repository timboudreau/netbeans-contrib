/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.profiles.subversion.list;

import java.io.File;
import java.util.Hashtable;

import org.netbeans.modules.vcs.profiles.list.AbstractListCommand;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.VcsCommand;

/**
 * Subversion LIST command wrapper.
 *
 * @author  Martin Entlicher
 */
public class SubversionListCommand extends AbstractListCommand {
    
    private VcsFileSystem fileSystem;
    private String dir;
    
    /** Creates a new instance of SubversionListCommand */
    public SubversionListCommand() {
    }
    
    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
        super.setFileSystem(fileSystem);
    }

    private void initDir(Hashtable vars) {
        String rootDir = (String) vars.get("ROOTDIR"); // NOI18N
        if (rootDir == null) {
            rootDir = "."; // NOI18N
        }
        this.dir = (String) vars.get("DIR"); // NOI18N
        if (this.dir == null) {
            this.dir = ""; // NOI18N
        }
        String module = (String) vars.get("MODULE"); // NOI18N
        //D.deb("rootDir = "+rootDir+", module = "+module+", dir = "+dir); // NOI18N
        if (dir.equals("")) { // NOI18N
            dir=rootDir;
            if (module != null && module.length() > 0) {
                dir += File.separator + module;
            }
        } else {
            if (module == null || module.length() == 0) {
                dir = rootDir + File.separator + dir;
            } else {
                dir = rootDir + File.separator + module + File.separator + dir;
            }
        }
        if (dir.charAt(dir.length() - 1) == File.separatorChar)
            dir = dir.substring(0, dir.length() - 1);
    }

    public boolean list(Hashtable vars, String[] args, Hashtable filesByName,
                        CommandOutputListener stdoutNRListener, CommandOutputListener stderrNRListener,
                        CommandDataOutputListener stdoutListener, String dataRegex,
                        CommandDataOutputListener stderrListener, String errorRegex) {
        this.stdoutNRListener = stdoutNRListener;
        this.stderrNRListener = stderrNRListener;
        this.stdoutListener = stdoutListener;
        this.stderrListener = stderrListener;
        this.dataRegex = dataRegex;
        this.errorRegex = errorRegex;
        this.filesByName = filesByName;
        if (args.length < 1) {
            stderrNRListener.outputLine("Expecting list command as an argument!"); //NOI18N
            return false;
        }
        initDir(vars);
        VcsCommand listCmd = fileSystem.getCommand(args[0]);
        if (listCmd == null) {
            stderrNRListener.outputLine("Unknown Command: "+args[1]); //NOI18N
            return false;
        }
        try {
            runCommand(vars, args[0], this, stderrListener);
        } catch (InterruptedException iexc) {
            return false;
        }
        return !shouldFail;
    }
    
    public void outputData(String[] elements) {
        if (elements.length <= 9) {
            return; // Too short output
        }
        File file = new File(dir, elements[9]);
        if (file.isDirectory()) {
            elements[9] += "/"; // NOI18N
        }
        filesByName.put(elements[9], elements);
        stdoutListener.outputData(elements);
    }
    
}

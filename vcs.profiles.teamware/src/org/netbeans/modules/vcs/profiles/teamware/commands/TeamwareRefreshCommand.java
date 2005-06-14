/*
 * Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is the Teamware module.
 * The Initial Developer of the Original Code is Sun Microsystems, Inc.
 * Portions created by Sun Microsystems, Inc. are Copyright (C) 2004.
 * All Rights Reserved.
 * 
 * Contributor(s): Daniel Blaukopf.
 */

package org.netbeans.modules.vcs.profiles.teamware.commands;

import java.io.File;
import java.util.Hashtable;

import org.netbeans.modules.vcscore.cmdline.VcsListCommand;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;

public class TeamwareRefreshCommand extends VcsListCommand {
    
    public boolean list(Hashtable vars, String[] args,
        Hashtable filesByName,
        CommandOutputListener stdoutListener,
        CommandOutputListener stderrListener,
        CommandDataOutputListener stdoutDataListener,
        String dataRegex,
        CommandDataOutputListener stderrDataListener,
        String errorRegex) {
            
        File dir = TeamwareSupport.getDir(vars);
        File[] files = TeamwareRefreshSupport.listFilesInDir(dir);
        File sccsDir = new File(dir, "SCCS");
        for (int i = 0 ; i < files.length; i++) {
            String[] data = TeamwareRefreshSupport.listFile(files[i],
                sccsDir, stderrListener);
            if (data != null
                && !data[0].equals("Ignored")
                && !data[0].equals("Local")) {
                stdoutListener.outputLine(dir + File.separator + data[1] + " [" + data[0] + "]");
                filesByName.put(data[1], data);
            }
        }
        return true;
    }
    
    
}

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


import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

public class TeamwareCommitCommand implements VcsAdditionalCommand {
    
    public boolean exec(Hashtable vars, String[] args,
                        CommandOutputListener stdout,
                        CommandOutputListener stderr,
                        CommandDataOutputListener stdoutDataListener, String dataRegex,
                        CommandDataOutputListener stderrDataListener, String errorRegex) {

        String rootDir = (String) vars.get("ROOTDIR");
        String module = (String) vars.get("MODULE");
        String dirName = (String) vars.get("DIR");
        File root = new File(rootDir);
        File dir = (module != null) ? new File(root, module) : root;
        if (dirName != null) {
            dir = new File(dir, dirName);
        }
        String file = (String) vars.get("FILE");
        
        String message =
             NbBundle.getMessage(TeamwareDiffCommand.class, "CI_Comments");
        String title = NbBundle.getMessage(TeamwareDiffCommand.class, "TITLE");
        NotifyDescriptor.InputLine input =
            new NotifyDescriptor.InputLine(message, title);
        Integer result = (Integer) DialogDisplayer.getDefault().notify(input);
        if (result.intValue() == 0) {
            String[] argv = {
                (String) vars.get("SCCS"),
                "delget",
                "-y" + input.getInputText(),
                file
            };
            return TeamwareSupport.exec(dir, argv, stdout, stderr);
        } else {
            return true;
        }
    }
    
}

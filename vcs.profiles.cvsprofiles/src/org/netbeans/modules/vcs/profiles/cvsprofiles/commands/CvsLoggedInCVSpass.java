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

package org.netbeans.modules.vcs.profiles.cvsprofiles.commands;

import java.util.Hashtable;

import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.cmdline.*;

import org.netbeans.modules.vcs.profiles.cvsprofiles.commands.passwd.CVSPasswd;

/**
 * This class is used just to check, whether the user is logged in .cvspass file.
 *
 * @author  Martin Entlicher
 */
public class CvsLoggedInCVSpass implements VcsAdditionalCommand {

    public boolean exec(Hashtable vars, String[] args,
                        CommandOutputListener stdoutNRListener, CommandOutputListener stderrNRListener,
                        CommandDataOutputListener stdoutListener, String dataRegex,
                        CommandDataOutputListener stderrListener, String errorRegex) {

        String connectStr = (String) vars.get("CVSROOT");
        if (args.length > 0) {
            connectStr = args[0];
        }
        connectStr = Variables.expand(vars, connectStr, false);
        boolean loggedIn = false;
        CVSPasswd pasFile = new CVSPasswd((String)null);
        pasFile.loadPassFile();
        //System.out.println("CvsLoggedInCVSpass: connectStr = '"+connectStr+"'");
        //System.out.println("  pasFile = "+pasFile);
        //PasswdEntry entry = pasFile.find(connectStr);
        String portStr = (String) vars.get("ENVIRONMENT_VAR_CVS_CLIENT_PORT");
        int port = 0;
        if (portStr != null) {
            try {
                port = Integer.parseInt(portStr);
            } catch (NumberFormatException nfex) {}
        }
        try {
            loggedIn = pasFile.find(connectStr, port) != null;
        } catch (IllegalArgumentException iaex) {
            loggedIn = false;
        }
        //System.out.println("  loggedIn = "+loggedIn);
        if (loggedIn) {
            vars.put("USER_IS_LOGGED_IN", "true");
        } else {
            vars.put("USER_IS_LOGGED_IN", "");
        }
        vars.remove("BUILT-IN"); // Not to alter that variable
        return loggedIn;
    }
}


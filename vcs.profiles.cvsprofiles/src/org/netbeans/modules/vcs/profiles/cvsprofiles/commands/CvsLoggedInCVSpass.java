/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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
        if (connectStr == null) {
            stderrNRListener.outputLine("Variable 'CVSROOT' is not defined. Can not verify login state.");
            return false;
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
        String loggedInText = (String) vars.get("LOGGED_IN_TEXT");
        vars.clear(); // Not to alter other variables than that we want to set.
        vars.put("LOGGED_IN_TEXT", loggedInText);
        if (loggedIn) {
            vars.put("USER_IS_LOGGED_IN", "true");
        } else {
            vars.put("USER_IS_LOGGED_IN", "");
        }
        return true;
    }
}


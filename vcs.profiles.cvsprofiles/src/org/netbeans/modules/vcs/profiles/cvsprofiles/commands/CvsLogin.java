/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.profiles.cvsprofiles.commands;

import java.awt.Dialog;
import java.util.*;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.cmdline.*;

import org.netbeans.modules.vcs.profiles.cvsprofiles.commands.passwd.LoginPanel;

/**
 * This class is used as a CVS login command.
 * @author  Martin Entlicher
 */
public class CvsLogin implements VcsAdditionalCommand {

    private VcsFileSystem fileSystem = null;

    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public boolean exec(Hashtable vars, String[] args,
                        CommandOutputListener stdoutNRListener, CommandOutputListener stderrNRListener,
                        CommandDataOutputListener stdoutListener, String dataRegex,
                        CommandDataOutputListener stderrListener, String errorRegex) {

        String serverName = (String) vars.get("CVS_SERVER");
        String userName = (String) vars.get("CVS_USERNAME");
        String cvsRoot = (String) vars.get("CVS_REPOSITORY");
        /*
        if (fileSystem instanceof CvsFileSystem) {
            CvsFileSystem cvsFileSystem = (CvsFileSystem) fileSystem;
         */
        LoginPanel login = new LoginPanel();
        login.setPserverName(serverName);
        String connectStr = ":pserver:" + userName + "@" + serverName + ":" + cvsRoot;
        String portStr = (String) vars.get("ENVIRONMENT_VAR_CVS_CLIENT_PORT");
        int port = 0;
        if (portStr != null) {
            try {
                port = Integer.parseInt(portStr);
            } catch (NumberFormatException nfex) {}
        }
        login.setConnectString(connectStr);
        login.setPort(port);
        Dialog logDialog = login.createDialog(fileSystem);
        logDialog.show();
        
        boolean loginCancelled = !login.isOffline();
        boolean isLoggedIn = login.isLoggedIn();
        if (!isLoggedIn && !loginCancelled) {
            fileSystem.setOffLine(true); // set offline mode
        } else if (isLoggedIn) {
            fileSystem.setOffLine(false); // unset offline mode
        }
        return isLoggedIn || loginCancelled;     
    }
}


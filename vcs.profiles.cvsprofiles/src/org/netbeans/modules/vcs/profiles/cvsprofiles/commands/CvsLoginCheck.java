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

import java.util.Hashtable;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.cmdline.*;

import org.netbeans.modules.vcs.profiles.cvsprofiles.commands.passwd.CVSPasswd;

/**
 * This class is used as a CVS login command.
 * @author  Martin Entlicher
 */
public class CvsLoginCheck implements VcsAdditionalCommand {

    private VcsFileSystem fileSystem = null;

    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public boolean exec(Hashtable vars, String[] args,
                        CommandOutputListener stdoutNRListener, CommandOutputListener stderrNRListener,
                        CommandDataOutputListener stdoutListener, String dataRegex,
                        CommandDataOutputListener stderrListener, String errorRegex) {

        String connectStr = (String) vars.get("CONNECT_STR");
        String password = (String) vars.get("PASSWORD");
        /*
        if (fileSystem instanceof CvsFileSystem) {
            CvsFileSystem cvsFileSystem = (CvsFileSystem) fileSystem;
         */
        StringBuffer message = new StringBuffer();
        boolean loggedIn = false;
        CVSPasswd pasFile = new CVSPasswd((String)null);
        try {
            pasFile.loadPassFile();
            pasFile.remove(connectStr);
            pasFile.add(connectStr, password);
            pasFile.savePassFile();
            loggedIn = CVSPasswd.checkLogin(fileSystem, message);
        } catch (java.net.UnknownHostException exc) {
            stderrNRListener.outputLine(
                org.openide.util.NbBundle.getBundle(
                    vcs.commands.passwd.CvsLoginDialog.class).getString("LoginDialog.unknownHost"));
            return false;
        } catch (java.io.IOException exc) {
            stderrNRListener.outputLine(
                org.openide.util.NbBundle.getBundle(
                    vcs.commands.passwd.CvsLoginDialog.class).getString("LoginDialog.connectionIOError"));
            return false;
        } finally {
            if (!loggedIn) {
                pasFile.remove(connectStr);
                pasFile.savePassFile();
            }
        }
        stderrNRListener.outputLine(
            org.openide.util.NbBundle.getBundle(
                vcs.commands.passwd.CvsLoginDialog.class).getString("LoginDialog.status.failed") + " "+ message);
        return loggedIn;
    }
}


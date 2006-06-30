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

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.cmdline.UserCommand;
import org.netbeans.modules.vcscore.util.*;

/**
 * This class retrieves which watch actions are currently set.
 * Returns three elements on the data output:
 * <br>1) "true" if edit action is being watched, "false" otherwise
 * <br>2) "true" if unedit action is being watched, "false" otherwise
 * <br>3) "true" if commit action is being watched, "false" otherwise
 *
 * @author  Martin Entlicher
 */
public class CvsWatchStatus extends Object implements VcsAdditionalCommand {

    private static final String EDIT = "\tedit";
    private static final String UNEDIT = "\tunedit";
    private static final String COMMIT = "\tcommit";

    private VcsFileSystem fileSystem = null;
    /** Creates new CvsWatchStatus */
    public CvsWatchStatus() {
    }
    
    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    public boolean exec(Hashtable vars, String[] args,
                        CommandOutputListener stdoutNRListener, CommandOutputListener stderrNRListener,
                        CommandDataOutputListener stdoutListener, String dataRegex,
                        CommandDataOutputListener stderrListener, String errorRegex) {
        if (args.length < 1) {
            stderrNRListener.outputLine("The cvs watchers command is expected as an argument");
            return false;
        }
        final StringBuffer buff = new StringBuffer();
        VcsCommand cmd = fileSystem.getCommand(args[0]);
        String userName = (String) vars.get("CVS_USERNAME");
        if (userName == null || userName.length() == 0) {
            userName = System.getProperty("user.name");
        }
        if (userName != null) cmd.setProperty(UserCommand.PROPERTY_DATA_REGEX, "(^.*"+userName+".*$)");
        else cmd.setProperty(UserCommand.PROPERTY_DATA_REGEX, null);
        VcsCommandExecutor vce = fileSystem.getVcsFactory().getCommandExecutor(cmd, vars);
        vce.addDataOutputListener(new CommandDataOutputListener() {
            public void outputData(String[] elements) {
                buff.append(elements[0]);
            }
        });
        fileSystem.getCommandsPool().startExecutor(vce, fileSystem);
        try {
            fileSystem.getCommandsPool().waitToFinish(vce);
        } catch (InterruptedException iexc) {
            fileSystem.getCommandsPool().kill(vce);
            Thread.currentThread().interrupt();
        }
        String[] elements = new String[3];
        String watched = buff.toString();
        elements[0] = watched.indexOf(EDIT) > 0 ? "true" : "false"; // NOI18N
        elements[1] = watched.indexOf(UNEDIT) > 0 ? "true" : "false"; // NOI18N
        elements[2] = watched.indexOf(COMMIT) > 0 ? "true" : "false"; // NOI18N
        stdoutListener.outputData(elements);
        return true;
    }
}

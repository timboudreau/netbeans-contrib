/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
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

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


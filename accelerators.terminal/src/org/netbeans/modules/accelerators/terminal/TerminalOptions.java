/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2008 Sun Microsystems, Inc. All rights reserved.
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
 * The Original Software is the Accelerators module.
 * The Initial Developer of the Original Software is Andrei Badea.
 * Portions Copyright 2005-2006 Andrei Badea.
 * All Rights Reserved.
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
 *
 * Contributor(s): Andrei Badea
 */

package org.netbeans.modules.accelerators.terminal;

import org.openide.options.SystemOption;
import org.openide.util.NbBundle;
import org.openide.util.SharedClassObject;
import org.openide.util.Utilities;

/**
 *
 * @author Andrei Badea
 */
public class TerminalOptions extends SystemOption {

    private static final long serialVersionUID = -3596174768571288758L;

    public static final String PROP_TERMINAL_COMMAND = "terminalCommand"; // NOI18N

    private static final String SYSTEM_TERMINAL_COMMAND = "accelerators.terminal.terminalCommand"; // NOI18N

    public static TerminalOptions getInstance() {
        return (TerminalOptions) SharedClassObject.findObject(TerminalOptions.class, true);
    }
    
    public String displayName() {
        return NbBundle.getMessage(TerminalOptions.class, "LBL_TerminalOptions");
    }
    
    public String getTerminalCommand() {
        String result = getUserDefined();
        if (result != null) {
            return result;
        }
        result = (String) getProperty(PROP_TERMINAL_COMMAND);
        if (result != null) {
            return result;
        } else if (Utilities.isWindows()) {
            // use command.com on win9x, cmd.exe on other
            if ((Utilities.getOperatingSystem() & (Utilities.OS_WIN95 | Utilities.OS_WIN98)) != 0) {
                return "command.com /c start command.com"; // NOI18N
            } else {
                return "cmd.exe /c start cmd.exe"; // NOI18N
            }
        } else if (Utilities.isMac()) {
            return "/Applications/Utilities/Terminal.app/Contents/MacOS/Terminal"; // NOI18N
        } else if (System.getProperty("Env-GNOME_DESKTOP_SESSION_ID") != null) { // NOI18N
            return "gnome-terminal"; // NOI18N
        } else if (Utilities.isUnix()) {
            return "xterm"; // NOI18N
        } else {
            // ???
            return null;
        }
    }
    
    public void setTerminalCommand(String command) {
        putProperty(PROP_TERMINAL_COMMAND, command, true);
    }

    private String getUserDefined() {
        return System.getProperty(SYSTEM_TERMINAL_COMMAND);
    }
}

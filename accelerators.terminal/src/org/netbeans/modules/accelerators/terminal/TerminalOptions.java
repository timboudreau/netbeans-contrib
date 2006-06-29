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
 * The Original Software is the Accelerators module.
 * The Initial Developer of the Original Software is Andrei Badea.
 * Portions Copyright 2005-2006 Andrei Badea.
 * All Rights Reserved.
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

    public static TerminalOptions getInstance() {
        return (TerminalOptions) SharedClassObject.findObject(TerminalOptions.class, true);
    }
    
    public String displayName() {
        return NbBundle.getMessage(TerminalOptions.class, "LBL_TerminalOptions");
    }
    
    public String getTerminalCommand() {
        String result = (String) getProperty(PROP_TERMINAL_COMMAND);
        if (result != null) {
            return result;
        } else if (Utilities.isWindows()) {
            // use command.com on win9x, cmd.exe on other
            if ((Utilities.getOperatingSystem() & (Utilities.OS_WIN95 | Utilities.OS_WIN98)) != 0) {
                return "command.com /c start command.com"; // NOI18N
            } else {
                return "cmd.exe /c start cmd.exe"; // NOI18N
            }
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
}

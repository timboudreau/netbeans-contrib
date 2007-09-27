/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is the Accelerators module.
 * The Initial Developer of the Original Code is Andrei Badea.
 * Portions Copyright 2005-2006 Andrei Badea.
 * All Rights Reserved.

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 2, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 2] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 2 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 2 code and therefore, elected the GPL
Version 2 license, then the option applies only if the new code is
made subject to such option by the copyright holder.
 *
 * Contributor(s): Andrei Badea
 */

package org.netbeans.modules.accelerators;

import org.openide.options.SystemOption;
import org.openide.util.NbBundle;
import org.openide.util.SharedClassObject;
import org.openide.util.Utilities;

/**
 *
 * @author Andrei Badea
 */
public class AcceleratorsOptions extends SystemOption {

    private static final long serialVersionUID = -3596174768571288758L;

    public static final String PROP_TERMINAL_COMMAND = "terminalCommand"; // NOI18N
    public static final String PROP_FILE_SEARCH_CASE_SENSITIVE = "fileSearchCaseSensitive"; // NOI18N
    
    public static AcceleratorsOptions getInstance() {
        return (AcceleratorsOptions) SharedClassObject.findObject(AcceleratorsOptions.class, true);
    }
    
    public String displayName() {
        return NbBundle.getMessage(AcceleratorsOptions.class, "LBL_AcceleratorsOptions");
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
    
    public boolean getFileSearchCaseSensitive() {
        Boolean result = (Boolean)getProperty(PROP_FILE_SEARCH_CASE_SENSITIVE);
        return (result != null) ? result.booleanValue() : false;
    }
    
    public void setFileSearchCaseSensitive(boolean fileSearchCaseSensitive) {
        putProperty(PROP_FILE_SEARCH_CASE_SENSITIVE, Boolean.valueOf(fileSearchCaseSensitive), true);
    }
}

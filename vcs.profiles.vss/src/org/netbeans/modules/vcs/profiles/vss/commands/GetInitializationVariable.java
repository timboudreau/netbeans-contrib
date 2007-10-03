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

package org.netbeans.modules.vcs.profiles.vss.commands;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;

/**
 * Get the value of an initialization variable.
 *
 * @author  Martin Entlicher
 */
public class GetInitializationVariable extends Object implements VcsAdditionalCommand {
    
    public static final String SRCSAFE_INI = "srcsafe.ini"; // NOI18N
    public static final String SS_INI = "ss.ini"; // NOI18N
    public static final String USERS_TXT = "Users_Txt"; // NOI18N

    /** Creates new GetInitializationVariable */
    public GetInitializationVariable() {
    }

    /**
     * This method is used to execute the command.
     * @param vars the variables that can be passed to the command
     * @param args the command line parametres passed to it in properties
     * @param stdoutListener listener of the standard output of the command
     * @param stderrListener listener of the error output of the command
     * @param stdoutDataListener listener of the standard output of the command which
     *                          satisfies regex <CODE>dataRegex</CODE>
     * @param dataRegex the regular expression for parsing the standard output
     * @param stderrDataListener listener of the error output of the command which
     *                          satisfies regex <CODE>errorRegex</CODE>
     * @param errorRegex the regular expression for parsing the error output
     * @return true if the command was succesfull
     *         false if some error occured.
     */
    public boolean exec(final Hashtable vars, final String[] args,
                        final CommandOutputListener stdoutListener, final CommandOutputListener stderrListener,
                        final CommandDataOutputListener stdoutDataListener, final String dataRegex,
                        final CommandDataOutputListener stderrDataListener, final String errorRegex) {
        if (args.length < 1) {
            stderrListener.outputLine("Expecting the name of the variable as an argument.");
            return false;
        }
        String varName = args[0];
        String ssDir = (String) vars.get("ENVIRONMENT_VAR_SSDIR"); // NOI18N
        String userName = (String) vars.get("USER_NAME"); // NOI18N
        if (userName == null || userName.length() == 0) {
            userName = System.getProperty("user.name");
        }
        String value = null;
        boolean status = true;
        try {
            value = getVariable(ssDir, userName, varName);
        } catch (IOException ioex) {
            stderrListener.outputLine(ioex.getLocalizedMessage());
            status = false;
        }
        /*
        String varName = args[0];
        String value = null;
        String ssDir = (String) vars.get("ENVIRONMENT_VAR_SSDIR"); // NOI18N
        //String initFile = Variables.expand(vars, "${ENVIRONMENT_VAR_SSDIR}${PS}"+SRCSAFE_INI, false);
        File initFile = new File(ssDir, SRCSAFE_INI);
        String[] usersTxtPtr = new String[] { null };
        boolean status = true;
        try {
            value = readValue(initFile, varName, usersTxtPtr);
        } catch (IOException ioex) {
            stderrListener.outputLine(ioex.getLocalizedMessage());
            status = false;
        }
        if (usersTxtPtr[0] != null) {
            String userName = (String) vars.get("USER_NAME"); // NOI18N
            if (userName == null || userName.length() == 0) {
                userName = System.getProperty("user.name");
            }
            try {
                File ssIni = getSSIniFile(ssDir, usersTxtPtr[0], userName);
                if (ssIni != null) {
                    String userValue = readValue(ssIni, varName, null);
                    if (userValue != null) value = userValue;
                }
            } catch (IOException ioex) {
                stderrListener.outputLine(ioex.getLocalizedMessage());
                status = false;
            }
        }
         */
        if (value != null) {
            stdoutListener.outputLine(varName + " = " + value);
            stdoutDataListener.outputData(new String[] { value });
        }
        return status;
    }
    
    /**
     * Get the value of an initialization variable.
     * @param ssDir the value of <code>SSDIR</code> - the location of VSS database
     * @param userName The current user name.
     * @param varName The name of the variable to read.
     * @return The value of the variable, or <code>null</code> when the variable was not found.
     * @throws IOException When an I/O error occurs.
     */
    public static String getVariable(String ssDir, String userName, String varName) throws IOException {
        File initFile = new File(ssDir, SRCSAFE_INI);
        String[] usersTxtPtr = new String[] { null };
        String value = readValue(initFile, varName, usersTxtPtr);
        //System.out.println("Value of '"+varName+"' from '"+initFile.getAbsolutePath()+"' = '"+value+"'");
        if (usersTxtPtr[0] != null) {
            File ssIni = getSSIniFile(ssDir, usersTxtPtr[0], userName);
            if (ssIni != null) {
                String userValue = readValue(ssIni, varName, null);
                //System.out.println("Value of '"+varName+"' from '"+ssIni.getAbsolutePath()+"' = '"+userValue+"'");
                if (userValue != null) value = userValue;
            } else {
                throw new IOException("ss.ini file not found for user "+userName);
            }
        }
        return value;
    }
    
    /**
     * Read a value of an initialization and/or location of <code>Users.txt</code> file.
     * @param file The file name. It's expected that it is either <code>srcsafe.ini</code> or <code>ss.ini</code> file.
     * @param varName The name of the variable to read, or <code>null</code>,
     *                in case that no variable value is desired.
     * @param usersTxtPtr The "pointer" to a String with the location of Users.txt file,
     *                    which is returned. Can be <code>null</code>.
     * @return The value of the variable, or <code>null</code> when the variable was not found.
     * @throws IOException When an I/O error occurs.
     */
    public static String readValue(File file, String varName, String[] usersTxtPtr) throws IOException {
        String value = null;
        BufferedReader r = new BufferedReader(new FileReader(file));
        boolean haveVar = varName == null;
        boolean haveUsersTxt = usersTxtPtr == null;
        try {
            do {
                String line = r.readLine();
                if (line == null) break;
                if (line.startsWith("[$") && line.endsWith("]")) {
                    // No variables are defined after these projects
                    break;
                }
                if (!haveVar && line.startsWith(varName)) {
                    value = getValueFromLine(varName.length(), line);
                    if (value == null) continue;
                    haveVar = true;
                }
                if (!haveUsersTxt && line.startsWith(USERS_TXT)) {
                    usersTxtPtr[0] = getValueFromLine(USERS_TXT.length(), line);
                    if (usersTxtPtr[0] == null) continue;
                    haveUsersTxt = true;
                }
            } while (!haveVar || !haveUsersTxt);
        } finally {
            r.close();
        }
        return value;
    }
    
    /**
     * Get the <code>ss.ini</code> file.
     * @param ssDir the value of <code>SSDIR</code> - the location of VSS database
     * @param usersTxt The path to <code>Users.txt</code> file. Can be relative to <code>ssDir</code>.
     * @param userName The current user name.
     * @return The <code>ss.ini</code> file.
     * @throws IOException When an I/O error occurs.
     */
    public static File getSSIniFile(String ssDir, String usersTxt, String userName) throws IOException {
        File usersTxtFile = new File(usersTxt);
        if (!usersTxtFile.isAbsolute()) {
            usersTxtFile = new File(ssDir, usersTxt);
        }
        BufferedReader r = new BufferedReader(new FileReader(usersTxtFile));
        int userNameLength = userName.length();
        try {
            do {
                String line = r.readLine();
                if (line == null) break;
                if (line.length() > userNameLength &&
                    line.substring(0, userNameLength).equalsIgnoreCase(userName)) {
                    
                    String value = getValueFromLine(userNameLength, line);
                    if (value == null) continue;
                    File ssIni = new File(value);
                    if (!ssIni.isAbsolute()) {
                        ssIni = new File(ssDir, ssIni.getPath());
                    }
                    return ssIni;
                }
            } while (true);
        } finally {
            r.close();
        }
        return null;
    }
    
    /**
     * Get the variable value from line <code>&lt;var name&gt; = &lt;var value&gt;</code>
     * @param name Variable name
     * @param line The line <code>&lt;var name&gt; = &lt;var value&gt;</code>
     * @return The variable value, or <code>null</code> when the variable of that name was not found.
     */
    public static String getValueFromLine(int index, String line) {
        int n = line.length();
        while (index < n && Character.isWhitespace(line.charAt(index))) index++;
        if (index >= n || line.charAt(index++) != '=') return null;
        while (index < n && Character.isWhitespace(line.charAt(index))) index++;
        return (index < n) ? line.substring(index).trim() : "";
    }
    
}

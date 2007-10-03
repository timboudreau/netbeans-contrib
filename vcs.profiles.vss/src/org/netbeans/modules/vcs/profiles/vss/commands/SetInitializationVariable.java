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

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;

/**
 * Set a value of an initialization variable.
 *
 * @author  Martin Entlicher
 */
public class SetInitializationVariable extends Object implements VcsAdditionalCommand {

    /** Creates new SetInitializationVariable */
    public SetInitializationVariable() {
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
        if (args.length < 2) {
            stderrListener.outputLine("Expecting the name of the variable and it's value as arguments.");
            return false;
        }
        String varName = args[0];
        String value = args[1];
        String ssDir = (String) vars.get("ENVIRONMENT_VAR_SSDIR"); // NOI18N
        File initFile = new File(ssDir, GetInitializationVariable.SRCSAFE_INI);
        String[] usersTxtPtr = new String[] { null };
        boolean status = true;
        try {
            GetInitializationVariable.readValue(initFile, null, usersTxtPtr);
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
                File ssIni = GetInitializationVariable.getSSIniFile(ssDir, usersTxtPtr[0], userName);
                stdoutListener.outputLine("ss.ini: "+((ssIni == null) ? null : ssIni.getAbsolutePath()));
                stdoutListener.outputLine(varName + " = " + value);
                if (ssIni != null) {
                    writeValue(ssIni, varName, value);
                } else {
                    stderrListener.outputLine("ss.ini file not found for user "+userName);
                    status = false;
                }
            } catch (IOException ioex) {
                stderrListener.outputLine(ioex.getLocalizedMessage());
                status = false;
            }
        }
        return status;
    }
    
    private static void writeValue(File ssIni, String varName, String value) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(ssIni, "rw");
        byte[] remainingBytes = null;
        long varPos = -1L;
        long pos;
        try {
            do {
                pos = raf.getFilePointer();
                String line = raf.readLine();
                if (line == null) break;
                if (line.startsWith("[$") && line.endsWith("]")) {
                    // No variables are defined after these projects
                    // We have to add the variable definition before them!
                    varPos = pos;
                    remainingBytes = new byte[(int) (raf.length() - pos)];
                    raf.seek(pos);
                    raf.readFully(remainingBytes);
                    break;
                }
                if (line.startsWith(varName)) {
                    String origValue = GetInitializationVariable.getValueFromLine(varName.length(), line);
                    if (value.equals(origValue)) return ;
                    if (origValue != null) {
                        varPos = pos;
                        remainingBytes = new byte[(int) (raf.length() - raf.getFilePointer())];
                        raf.readFully(remainingBytes);
                        break;
                    }
                }
            } while (true);
            if (varPos >= 0) raf.seek(varPos);
            raf.write(new String(varName + " = " + value + "\r\n").getBytes());
            if (remainingBytes != null) {
                raf.write(remainingBytes);
            }
        } finally {
            raf.close();
        }
    }
    /* A test
    public static void main(String[] args) {
        if (args.length < 3) {
            System.err.println("Too few arguments. Expecting file name, var name, var value.");
            return ;
        }
        try {
            writeValue(new File(args[0]), args[1], args[2]);
        } catch (IOException ioex) {
            ioex.printStackTrace();
        }
    }
     */
}

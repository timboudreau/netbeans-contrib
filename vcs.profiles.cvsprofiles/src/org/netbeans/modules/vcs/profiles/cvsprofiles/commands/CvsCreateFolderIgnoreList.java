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

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.StringTokenizer;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.util.VcsUtilities;

/**
 *
 * @author  Martin Entlicher
 */
public class CvsCreateFolderIgnoreList extends Object implements VcsAdditionalCommand {

    private VcsFileSystem fileSystem;
    
    /** Creates new CvsCreateFolderIgnoreList */
    public CvsCreateFolderIgnoreList() {
    }

    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
    
    private File getIgnoreFile(Hashtable vars) {
        String rootDir = (String) vars.get("ROOTDIR"); // NOI18N
        if (rootDir == null) {
            rootDir = "."; // NOI18N
        }
        String dir = (String) vars.get("DIR"); // NOI18N
        if (dir == null) {
            dir = ""; // NOI18N
        }
        String module = (String) vars.get("MODULE"); // NOI18N
        if (dir.equals("")) { // NOI18N
            dir = rootDir;
            if (module != null && module.length() > 0) {
                dir += File.separator + module;
            }
        } else {
            if (module == null) {
                dir = rootDir + File.separator + dir;
            } else {
                dir = rootDir + File.separator + module + File.separator + dir;
            }
        }
        if (dir.charAt(dir.length() - 1) == File.separatorChar)
            dir = dir.substring(0, dir.length() - 1);
        String file = (String) vars.get("FILE");
        dir = dir + File.separator + file;
        File filePath = new File(dir, ".cvsignore");  // NOI18N
        if (filePath.canRead()) {
            return filePath;
        } else {
            return null;
        }
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
     *        false if some error occured.
     */
    public boolean exec(Hashtable vars, String[] args,
                        CommandOutputListener stdoutListener, CommandOutputListener stderrListener,
                        CommandDataOutputListener stdoutDataListener, String dataRegex,
                        CommandDataOutputListener stderrDataListener, String errorRegex) {

        assert false : "Replaced by list.CvsFileAttributeProvider IgnoreList.ID";  // NOI18N

        String parentIgnoreList = (String) vars.get("PARENT_IGNORE_LIST");
        String[] parentIgnoreListItems = VcsUtilities.getQuotedStrings(parentIgnoreList);
        ArrayList ignoreList = new ArrayList(Arrays.asList(parentIgnoreListItems));
        File file = getIgnoreFile(vars);
        if (file != null) {
            addFileIgnoreList(file, ignoreList);
        }
        CvsCreateInitialIgnoreList.returnIgnoreList(ignoreList, stdoutDataListener);
        return true;
    }

    /**
     *
     * @param file existing and readable .cvsignore file
     * @param ignoreList
     */
    public static void addFileIgnoreList(File file, ArrayList ignoreList) {
        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line = null;
            while ((line = in.readLine()) != null) {
                StringTokenizer tk = new StringTokenizer(line);
                while (tk.hasMoreTokens()) {
                    String element = tk.nextToken().trim();
                    if (element.length() ==0) {
                        continue;
                    } else if ("!".equals(element)) {
                        ignoreList.clear();
                        continue;
                    } else {
                        ignoreList.add(element);
                    }
                }
            }
        } catch (IOException e) {/*skip file, if can not be read*/}
        finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException nestedIOException) {}
            }
        }
    }
}

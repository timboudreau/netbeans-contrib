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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.cmdline.UserCommand;
import org.netbeans.modules.vcscore.util.*;

/**
 * The wrapper for scheduled add. There's a problem, that when the remove is scheduled
 * ("cvs rm -f" is called), the file is removed and marked as [Locally Removed].
 * Then when the file reappears and the removal is cancelled by a schedule for add,
 * the "cvs add" command fails, because the file exists. It should be enough to change
 * CVS/Entries to remove "-" from the revision number.
 * @author  Martin Entlicher
 */
public class CvsScheduledAdd implements VcsAdditionalCommand {

    private VcsFileSystem fileSystem = null;
    /** Creates new CvsScheduledAdd */
    public CvsScheduledAdd() {
    }

    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    private File getDir(Hashtable vars) {
        //this.cmd = VcsUtilities.array2string(args);

        String rootDir = (String) vars.get("ROOTDIR"); // NOI18N
        if (rootDir == null) {
            rootDir = "."; // NOI18N
            //vars.put("ROOTDIR","."); // NOI18N
        }
        String dir = (String) vars.get("DIR"); // NOI18N
        if (dir == null) {
            dir = ""; // NOI18N
            //vars.put("DIR","."); // NOI18N
        }
        String module = (String) vars.get("MODULE"); // NOI18N
        //D.deb("rootDir = "+rootDir+", module = "+module+", dir = "+dir); // NOI18N
        if (dir.equals("")) { // NOI18N
            dir=rootDir;
            if (module != null && module.length() > 0) dir += File.separator + module;
        } else {
            if (module == null)
                dir=rootDir+File.separator+dir;
            else
                dir=rootDir+File.separator+module+File.separator+dir;
        }
        if (dir.charAt(dir.length() - 1) == File.separatorChar)
            dir = dir.substring(0, dir.length() - 1);
        //dir += (String) vars.get("FILE");
        return new File(dir);
    }

    private boolean readdRemovedFile(Hashtable vars) {
        File wdir = getDir(vars);
        String file = (String) vars.get("FILE");
        if (!(new File(wdir, file)).exists()) return false;
        //System.out.println("readdRemovedFile() file = "+file);
        File entries = new File(wdir, "CVS/Entries");
        //System.out.println("readdRemovedFile(): entries = "+entries);
        if (entries.exists() && entries.canRead() && entries.canWrite()) {
            ArrayList entriesLines = new ArrayList();
            int fileIndex = -1;
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(entries));
                String line;
                while ((line = reader.readLine()) != null) {
                    entriesLines.add(line);
                    //System.out.println("line = "+line);
                    //if (line.length() > (file.length() + 2)) System.out.println("line.charAt("+(file.length() + 1)+") = "+line.charAt(file.length() + 1));
                    if (line.startsWith("/") &&
                        line.substring(1).startsWith(file) &&
                        line.length() > (file.length() + 2) &&
                        line.charAt(file.length() + 1) == '/') {
                        fileIndex = entriesLines.size() - 1;
                    }
                }
            } catch (FileNotFoundException fnfExc) {
                //fnfExc.printStackTrace();
                return false;
            } catch (IOException ioExc) {
                //ioExc.printStackTrace();
                return false;
            } finally {
                try {
                    if (reader != null) reader.close();
                } catch (IOException exc) {}
            }
            //System.out.println("fileIndex = "+fileIndex);
            if (fileIndex < 0) return false;
            String lineToChange = (String) entriesLines.get(fileIndex);
            //System.out.println("lineToChange = "+lineToChange+", lineToChange.charAt("+(file.length() + 2)+") = "+lineToChange.charAt(file.length() + 2));
            if (!(lineToChange.charAt(file.length() + 2) == '-')) return false;
            lineToChange = lineToChange.substring(0, file.length() + 2) + lineToChange.substring(file.length() + 3);
            entriesLines.set(fileIndex, lineToChange);
            File entriesNb = new File(wdir, "CVS/Entries.nb");
            BufferedWriter writer = null;
            try {
                entriesNb.createNewFile();
                writer = new BufferedWriter(new FileWriter(entriesNb));
                for (Iterator it = entriesLines.iterator(); it.hasNext(); ) {
                    writer.write((String) it.next());
                    writer.newLine();
                }
            } catch (IOException ioExc) {
                //ioExc.printStackTrace();
                return false;
            } finally {
                try {
                    if (writer != null) writer.close();
                } catch (IOException exc) {}
            }
            if (entries.delete()) {
                return entriesNb.renameTo(entries);
            } else return false;
        } else return false;
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
                        CommandOutputListener stdoutNRListener, CommandOutputListener stderrNRListener,
                        CommandDataOutputListener stdoutListener, String dataRegex,
                        CommandDataOutputListener stderrListener, String errorRegex) {
        if (args.length < 1) {
            stderrNRListener.outputLine("The cvs add command is expected as an argument");
            return false;
        }
        if (readdRemovedFile(vars)) return true;
        VcsCommand cmd = fileSystem.getCommand(args[0]);
        VcsCommandExecutor vce = fileSystem.getVcsFactory().getCommandExecutor(cmd, vars);
        fileSystem.getCommandsPool().startExecutor(vce);
        try {
            fileSystem.getCommandsPool().waitToFinish(vce);
        } catch (InterruptedException iexc) {
            fileSystem.getCommandsPool().kill(vce);
            return false;
        }
        return vce.getExitStatus() == VcsCommandExecutor.SUCCEEDED;
    }
    
}

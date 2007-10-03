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

import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.cmdline.ExecuteCommand;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;

/**
 * This command assures, that when one deletes a local folder, which have
 * it's item in the parent's entries, it is removed from the entries, so that
 * recursive commands will work correctly.
 *
 * @author  Martin Entlicher
 */
public class CvsDeleteFolder extends Object implements VcsAdditionalCommand {

    private static final String ENTRIES = "CVS/Entries";
    
    private VcsFileSystem fileSystem;

    /** Creates new CvsDeleteFolder */
    public CvsDeleteFolder() {
    }

    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
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
        Collection files = ExecuteCommand.createProcessingFiles(fileSystem, vars);
        for (Iterator it = files.iterator(); it.hasNext(); ) {
            removeFolderFromEntries((String) it.next());
        }
        return true;
    }
    
    private void removeFolderFromEntries(String folderName) {
        File folder = fileSystem.getFile(folderName);
        File parent = folder.getParentFile();
        File entries = new File(parent, ENTRIES);
        if (entries.exists()) {
            List entriesContent = loadEntries(entries);
            String name = folder.getName();
            if (removeEntriesFolder(name, entriesContent)) {
                writeEntries(entries, entriesContent);
            }
        }
    }
    
    private boolean removeEntriesFolder(String name, List entriesContent) {
        String pattern = "D/"+name+"/";
        for (Iterator it = entriesContent.iterator(); it.hasNext(); ) {
            String line = (String) it.next();
            if (line.startsWith(pattern)) {
                entriesContent.remove(line);
                return true;
            }
        }
        return false;
    }
    
    private static List loadEntries(File entries) {
        ArrayList entriesFiles = new ArrayList();
        if (entries.exists() && entries.canRead() && entries.canWrite()) {
            int fileIndex = -1;
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(entries));
                String line;
                while ((line = reader.readLine()) != null) {
                    entriesFiles.add(line);
                }
            } catch (FileNotFoundException fnfExc) {
                // ignore
            } catch (IOException ioExc) {
                // ignore
            } finally {
                try {
                    if (reader != null) reader.close();
                } catch (IOException exc) {}
            }
        }
        return entriesFiles;
    }
    
    private static void writeEntries(File entries, List entriesFiles) {
        if (entries.exists() && entries.canRead() && entries.canWrite()) {
            int fileIndex = -1;
            BufferedWriter writer = null;
            File entriesCopy = new File(entries.getAbsolutePath()+".rmdir");
            boolean errors = false;
            try {
                entriesCopy.createNewFile();
                writer = new BufferedWriter(new FileWriter(entriesCopy));
                for (Iterator it = entriesFiles.iterator(); it.hasNext(); ) {
                    String line = (String) it.next();
                    writer.write(line + "\n");
                }
            } catch (FileNotFoundException fnfExc) {
                errors = true;
            } catch (IOException ioExc) {
                errors = true;
            } finally {
                try {
                    if (writer != null) writer.close();
                } catch (IOException exc) {
                    errors = true;
                }
            }
            if (!errors) {
                entries.delete();
                entriesCopy.renameTo(entries);
            }
        }
    }
    
}

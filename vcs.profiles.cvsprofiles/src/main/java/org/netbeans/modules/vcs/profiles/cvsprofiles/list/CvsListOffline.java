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

package org.netbeans.modules.vcs.profiles.cvsprofiles.list;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.cmdline.VcsListCommand;

import org.openide.ErrorManager;

/**
 * The offline refresh of CVS folder
 * @author  Martin Entlicher
 */
public class CvsListOffline extends VcsListCommand {

    static final String CVS_DIRNAME = "CVS"; // NOI18N
    static final String[] CVS_DIRCONTENT = {"Entries", "Repository", "Root"}; // NOI18N
    
    private static final String ENTRIES_LOG = "Entries.Log"; // NOI18N

    private static final String DUMMY_TIMESTAMP = "dummy timestamp"; //NOI18N

    private static final String MERGE_TIMESTAMP = "Result of merge"; //NOI18N

    private static final String CONFLICT_IN_DATE = "+"; //NOI18N

    private static final String INITIAL_TIMESTAMP = "Initial"; //NOI18N

    private String rootDir=null;
    private String dir=null;

    private CommandOutputListener stdoutNRListener = null;
    private CommandOutputListener stderrNRListener = null;
    private CommandDataOutputListener stderrListener = null;
    private CommandDataOutputListener stdoutListener = null;
    
    private HashMap entriesByFiles = null;

    /** Creates new CvsListOffline */
    public CvsListOffline() {
    }

    private void initVars(Hashtable vars, String[] args) {
        //this.cmd = VcsUtilities.array2string(args);

        this.rootDir = (String) vars.get("ROOTDIR"); // NOI18N
        if (this.rootDir == null) {
            this.rootDir = "."; // NOI18N
            //vars.put("ROOTDIR","."); // NOI18N
        }
        this.dir = (String) vars.get("DIR"); // NOI18N
        if (this.dir == null) {
            this.dir = ""; // NOI18N
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
        //D.deb("dir="+dir); // NOI18N
    }

    /**
     * Add local directories with no status information.
     * @param filesByName the files container
     */
    private void addLocalFiles(Hashtable filesByName){
        File d = new File(dir);
        String[] files = d.list();
        if (files != null) {
            for(int i = 0; i < files.length; i++) {
                String[] fileStatuses = new String[7];
                fileStatuses[1] = ""; // NOI18N
                String fileName = files[i];
                //D.deb("fileName="+fileName);

                File dirfile = new File(d + File.separator + fileName);
                if (dirfile.isDirectory()) {
                    fileName += "/"; // NOI18N
                } else {
                    fillCVSFileStatus(d, fileName, filesByName);
                }
                if (fileName.equals(CVS_DIRNAME + "/")) { // NOI18N
                    continue;
                }

                if (!CvsListCommand.isCVSDirectory(dirfile)) continue;
                if (filesByName.get(fileName) == null) {
                    //D.deb("adding "+fileName);
                    //System.out.println("addLocalFiles(): '"+fileName+"'");
                    fileStatuses[0] = fileName;
                    fileStatuses[5] = CvsListCommand.findStickyOfDir(dirfile);
                    filesByName.put(fileName, fileStatuses);
                    if (stdoutListener != null) stdoutListener.outputData(fileStatuses);
                }
            }
        }
    }
    
    private void addRemoteFiles(Hashtable filesByName) {
        File d = new File(dir);
        getEntry(d, "test"); // This will initialize entries if they are not yet loaded
        if (entriesByFiles != null) {
            for (Iterator it = entriesByFiles.keySet().iterator(); it.hasNext(); ) {
                String fileName = (String) it.next();
                if (!new File(d, fileName).exists()) {
                    fillCVSFileStatus(d, fileName, filesByName);
                }
            }
        }
    }
    
    private void fillCVSFileStatus(File dir, String fileName, Hashtable filesByName) {
        String entry = getEntry(dir, fileName);
        if (entry == null) return ;
        String[] entryItems = parseEntry(entry);
        String revision = entryItems[1];
        //System.out.println("revision = "+revision);
        String status;
        if (revision.startsWith("-")) {
            status = "Locally Removed";
        } else if (revision.equals("0")) {
            status = "Locally Added";
        } else {
            status = getStatusFromTime(entryItems[2], new File(dir, fileName));
        }
        String sticky = "";
        if (entryItems.length > 4) {
            sticky = entryItems[4];
            if (sticky.length() > 0) sticky = sticky.substring(1, sticky.length());
        }
        String[] elements = new String[7];
        elements[0] = fileName;
        elements[1] = status;
        elements[2] = revision;
        elements[3] = "";
        elements[4] = "";
        elements[5] = sticky;
        elements[6] = "";
        filesByName.put(fileName, elements);
        if (stdoutListener != null) stdoutListener.outputData(elements);
    }
    
    public static String getStatusFromTime(String cvsDateStr, File realFile) {
        if (cvsDateStr.indexOf(CONFLICT_IN_DATE) >= 0) {
            return "File had conflicts on merge";
        }
        if (cvsDateStr.startsWith(MERGE_TIMESTAMP)) {
            return "Locally Modified"; // Result of a merge, but no conflicts.
        }
        if (cvsDateStr.startsWith(DUMMY_TIMESTAMP)) {
            return "Locally Added";
        }
        if (cvsDateStr.startsWith(INITIAL_TIMESTAMP)) {
            return "Locally Added";
        }
        if (!realFile.exists()) {
            return "Needs Checkout";
        }
        DateFormat cvsDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss yyyy", Locale.US); //NOI18N
        cvsDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+0000")); //NOI18N
        Date cvsDate;
        try {
            cvsDate = cvsDateFormat.parse(cvsDateStr);
        } catch (ParseException pExc) {
            IllegalArgumentException iaex = new IllegalArgumentException("Unparsable date: "+cvsDateStr);
            iaex.initCause(pExc);
            ErrorManager.getDefault().notify(ErrorManager.EXCEPTION, iaex);
            return "Error Date";
        }
        Date fileDate = new Date(realFile.lastModified());
        // WORKAROUND 4860999 Compare only whole seconds an dignore hour diffs
        if (((cvsDate.getTime()/1000 - fileDate.getTime()/1000) % (60*60)) == 0) {
            return "Up-to-date";
        } else {
            return "Locally Modified";
        }
    }
    
    /**
     * Parse the Entry line.
     * @param entry one line of Entries file.
     * @return the <code>String[5]</code> array of elements of this entry.
     */
    public static String[] parseEntry(String entry) {
        //System.out.println("parseEntry("+entry+")");
        StringTokenizer tokens = new StringTokenizer(entry, "/", true);
        ArrayList elements = new ArrayList();
        String lastToken = null;
        while (tokens.hasMoreTokens()) {
            String entryElement = tokens.nextToken();
            if ("/".equals(entryElement) && "/".equals(lastToken)) {
                elements.add("");
                //System.out.println("  element ''");
            } else if (!"/".equals(entryElement)) {
                elements.add(entryElement);
                //System.out.println("  element '"+entryElement+"'");
            }
            lastToken = entryElement;
        }
        String[] ret = (String[]) elements.toArray(new String[5]);
        if (ret[4] == null) ret[4] = "";
        return ret;
    }
    
    private String getEntry(File dir, String file) {
        //System.out.println("getEntry("+dir+", "+file+")");
        if (entriesByFiles == null) {
            File entriesFile = new File(dir, "CVS/Entries");
            //System.out.println("entriesFile = "+entriesFile);
            if (!entriesFile.exists() || !entriesFile.canRead()) return null;
            List entries = loadEntries(entriesFile);
            //System.out.println("entries = "+entries);
            entriesByFiles = createEntriesByFiles(entries);
        }
        if (entriesByFiles == null) return null;
        return (String) entriesByFiles.get(file);
    }
    
    /**
     * Create the map of file names and associated entries.
     * @param entriesFile the list of Strings which represent the Entries file.
     * @return map of file names and associated entries strings.
     */
    public static HashMap createEntriesByFiles(List entriesFile) {
        if (entriesFile == null) return null;
        HashMap entriesByFiles = new HashMap();
        for (Iterator entryIt = entriesFile.iterator(); entryIt.hasNext(); ) {
            String entry = (String) entryIt.next();
            if (entry.startsWith("/")) {
                int index = entry.indexOf("/", 1);
                if (index > 0) {
                    String file = entry.substring(1, index);
                    entriesByFiles.put(file, entry);
                }
            }
        }
        return entriesByFiles;
    }

    /**
     * Load file entries from the Entries file. Folder entries are ignored.
     * @param entries the Entries file
     * @return the list of Strings which represent the Entries file.
     */
    public static List loadEntries(File entries) {
        ArrayList entriesFiles = new ArrayList();
        if (entries.exists() && entries.canRead()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(entries));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("/")) {
                        entriesFiles.add(line);
                        //int end = line.indexOf('/', 1);
                        //if (end > 0) entriesFiles.add(line.substring(1, end));
                    }
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
        File folder = entries.getParentFile();
        if (folder != null) {
            File log = new File(folder, ENTRIES_LOG);
            if (log.exists() && log.canRead()) {
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new FileReader(log));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        if (line.startsWith("A /")) {
                            entriesFiles.add(line.substring(2));
                        } else if (line.startsWith("R /")) {
                            entriesFiles.remove(line.substring(2));
                        }
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
        }
        return entriesFiles;
    }
    
    /**
     * List files of CVS Repository.
     * @param vars Variables used by the command
     * @param args Command-line arguments
     * filesByName listing of files with statuses
     * @param stdoutNRListener listener of the standard output of the command
     * @param stderrNRListener listener of the error output of the command
     * @param stdoutListener listener of the standard output of the command which
     *                       satisfies regex <CODE>dataRegex</CODE>
     * @param dataRegex the regular expression for parsing the standard output
     * @param stderrListener listener of the error output of the command which
     *                       satisfies regex <CODE>errorRegex</CODE>
     * @param errorRegex the regular expression for parsing the error output
     */
    public boolean list(Hashtable vars, String[] args, Hashtable filesByName,
                        CommandOutputListener stdoutNRListener, CommandOutputListener stderrNRListener,
                        CommandDataOutputListener stdoutListener, String dataRegex,
                        CommandDataOutputListener stderrListener, String errorRegex) {

        this.stdoutNRListener = stdoutNRListener;
        this.stderrNRListener = stderrNRListener;
        this.stderrListener = stderrListener;
        this.stdoutListener = stdoutListener;
        //this.dataRegex = dataRegex;
        //this.errorRegex = errorRegex;
        initVars(vars, args);
        addLocalFiles(filesByName);
        addRemoteFiles(filesByName);
        return true;
    }
}

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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.turbo.Turbo;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.cmdline.ExecuteCommand;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;

import org.netbeans.modules.vcs.profiles.cvsprofiles.list.CvsListCommand;
import org.netbeans.modules.vcs.profiles.cvsprofiles.list.CvsListOffline;
import org.openide.filesystems.FileObject;

/**
 * This class assures the correct file status refresh after cvs update and cvs checkout commands.
 * It runs the command passed in as an argument and after it finish, it refresh status of
 * only changed files from CVS/Entries files. This kind of refresh does not require any
 * server connection.
 *
 * @author  Martin Entlicher
 */
public class CvsUpdate extends Object implements VcsAdditionalCommand {

    private static final String CVS_ENTRIES = "CVS"+File.separator+"Entries"; // NOI18N
    private static final String CONTAINS_DIFFERENCES = "already contains the differences"; // NOI18N
    
    private VcsFileSystem fileSystem = null;

    /**
     * A map of folders and corresponfing map of file names and Entries Strings.
     */
    private HashMap cachedEntries = new HashMap();
    
    /**
     * The full path of the command's working directory. The updated files
     * are relative to this directory.
     */
    private String workingFullPath;
    
    /**
     * The relative path of the command's working directory to the root of the
     * filesystem.
     */
    private String relativeWorking;
    
    /**
     * List of refreshed files for each folder.
     */
    Map refreshedFilesByFolders = new HashMap();
    
    /**
     * Whether we should refresh the processed files.
     * This will turn to false if the update will not make any changes.
     */
    private boolean doRefresh = true;
    
    /** Creates new CvsUpdate */
    public CvsUpdate() {
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
                        CommandOutputListener stdoutListener,
                        CommandOutputListener stderrListener,
                        final CommandDataOutputListener stdoutDataListener, String dataRegex,
                        final CommandDataOutputListener stderrDataListener, String errorRegex) {
        if (args.length < 2 || "-n".equals(args[0]) && args.length < 3) {
            stderrListener.outputLine("The current command's working dir and cvs update OR cvs checkout commands are expected as arguments.\n"+ // NOI18N
                                      "Possibly -n might be passed as the first argument not to do any subsequent refresh."); // NOI18N
            return false;
        }
        if ("-n".equals(args[0])) {
            doRefresh = false;
            String[] args1 = new String[args.length - 1];
            System.arraycopy(args, 1, args1, 0, args1.length);
            args = args1;
        }
        final Collection processingFiles = ExecuteCommand.createProcessingFiles(fileSystem, vars);

        final String rootDir = Variables.expand(vars, (String) vars.get(args[0]), false);
        final String rootDirCutted = (rootDir.endsWith(File.separator)) ?
                                      rootDir.substring(0, rootDir.length() - 1) :
                                      rootDir;
        // The workingFullPath is used just to know where one should cut the file
        // path to have it relative to working dir (or whatever is specified in args[0])
        workingFullPath = rootDirCutted;
        relativeWorking = (String) vars.get("COMMON_PARENT"); // NOI18N
        if (relativeWorking == null) relativeWorking = ""; // NOI18N
        final List filesBuff = new ArrayList();
        final List filesStatusBuff = new ArrayList();
        final ArrayList foldersBuff = new ArrayList();
        final ArrayList removedFiles = new ArrayList(); // Files removed by the update.
        final String[] lastFileFolder = { null }; // The folder of the last updated file.
        VcsCommand cmd = fileSystem.getCommand(args[1]);
        VcsCommandExecutor vce = fileSystem.getVcsFactory().getCommandExecutor(cmd, vars);
        if (doRefresh) {
          vce.addDataOutputListener(new CommandDataOutputListener() {
            public void outputData(String[] data) {
                if (data != null && data.length > 1) {
                    String status;
                    String fileName;
                    if (CONTAINS_DIFFERENCES.equals(data[1])) {
                        status = "U"; // NOI18N
                        fileName = data[0];
                    } else {
                        status = data[0];
                        fileName = data[1];
                    }
                    int index = fileName.lastIndexOf('/');
                    String name;
                    String folder;
                    if (index < 0) {
                        name = fileName;
                        folder = "";
                    } else {
                        name = fileName.substring(index + 1);
                        folder = fileName.substring(0, index);
                    }
                    if (!folder.equals(lastFileFolder[0])) {
                        if (lastFileFolder[0] != null) {
                            sendUpdatedFiles((rootDirCutted + File.separator + lastFileFolder[0]).
                                              replace('/', File.separatorChar),
                                             lastFileFolder[0], filesBuff,
                                             filesStatusBuff, stdoutDataListener,
                                             processingFiles);
                            filesBuff.clear();
                            filesStatusBuff.clear();
                        }
                        lastFileFolder[0] = folder;
                    }
                    filesBuff.add(name);
                    filesStatusBuff.add(status);
                    //filesStatusBuff.add(status);
                    //String file = (rootDir + File.separator + fileName).
                    //              replace('/', File.separatorChar);
                    //filesBuff.add(file);
                    //sendUpdatedFile(file, status, stdoutDataListener);
                }
            }
          });
          vce.addDataErrorOutputListener(new CommandDataOutputListener() {
            public void outputData(String[] data) {
                if (data != null && data.length > 0 && data[0] != null) {
                    String file;
                    if (".".equals(data[0]) || data[0].length() == 0) { // NOI18N
                        file = rootDir;
                    } else {
                        file = rootDirCutted + File.separator + data[0];
                    }
                    file = file.replace('/', File.separatorChar);
                    if (data.length > 1 && data[1].length() > 0) {
                        removedFiles.add(file);
                        //sendRemovedFile(file, stdoutDataListener);
                    } else {
                        foldersBuff.add(file);
                        /*
                        Potreba refreshnout cely adresar, ve ktery je "file" folder!
                        Nebo alespon zjistit je-li adresar zapsan v diskove cachi
                        a refreshnout cely adresar jen neni-li zapsan.
                        sendUpdatedFolder(file, stdoutDataListener);
                         */
                    }
                }
            }
          });
        }
        fileSystem.getCommandsPool().startExecutor(vce, fileSystem);
        try {
            fileSystem.getCommandsPool().waitToFinish(vce);
        } catch (InterruptedException iexc) {
            fileSystem.getCommandsPool().kill(vce);
            Thread.currentThread().interrupt();
        }
        if (doRefresh) {
            if (lastFileFolder[0] != null) {
                sendUpdatedFiles((rootDirCutted + File.separator + lastFileFolder[0]).
                                  replace('/', File.separatorChar),
                                 lastFileFolder[0], filesBuff, filesStatusBuff,
                                 stdoutDataListener, processingFiles);
                filesBuff.clear();
                filesStatusBuff.clear();
            }
            cachedEntries.clear(); // The Entries probably has changed
            //sendUpdatedFiles(filesBuff, filesStatusBuff, foldersBuff, stdoutDataListener);
            sendUpdatedFolders(foldersBuff, stdoutDataListener);
            sendRemovedFiles(removedFiles, stdoutDataListener);
            sendUpdateProcessedFiles(processingFiles, stdoutDataListener);


            // Covers following case: a file have disappeared in repository
            // and update have removed it locally. children must be updated.
            // removed folders are unreported so cannot be catched by above sends

            Iterator it = processingFiles.iterator();
            while (it.hasNext()) {
                String processingFile = (String) it.next();
                FileObject fo = fileSystem.findResource(processingFile);
                if (fo != null && fo.isFolder()) {
                    FileObject parent = fo.getParent();
                    if (parent != null) {
                        parent.refresh();
                    }
                }
            }
        }
        if (vce.getExitStatus() == VcsCommandExecutor.INTERRUPTED) {
            Thread.currentThread().interrupt(); // Set itself as interrupted when the sub-command was interrupted.
        }
        return VcsCommandExecutor.SUCCEEDED == vce.getExitStatus();
    }
    
    private void sendUpdatedFiles(String folderName, String relativeFolderName,
                                  List files, List states,
                                  CommandDataOutputListener stdoutDataListener,
                                  Collection processingFiles) {
        if (relativeFolderName.length() == 0) {
            relativeFolderName = relativeWorking;
        } else if (relativeWorking.length() != 0) {
            relativeFolderName = relativeWorking + '/' + relativeFolderName;
        }
        //System.out.println("sendUpdatedFiles("+folderName+", "+relativeFolderName+", "+files+", "+states+")");
        int i = 0;
        File folder = new File(folderName);
        HashSet filesInFolder = (HashSet) refreshedFilesByFolders.get(folder);
        if (filesInFolder == null) {
            filesInFolder = new HashSet();
            refreshedFilesByFolders.put(folder, filesInFolder);
        }
        Map entriesMap = (Map) cachedEntries.get(folder);
        if (entriesMap == null) {
            File entriesFile = new File(folder, CVS_ENTRIES);
            entriesMap = CvsListOffline.createEntriesByFiles(
                             CvsListOffline.loadEntries(entriesFile));
            cachedEntries.put(folder, entriesMap);
        }
        for (Iterator it = files.iterator(); it.hasNext(); i++) {
            String fileName = (String) it.next();
            File file = new File(folder, fileName);
            filesInFolder.add(fileName);
            String entry = (String) entriesMap.get(fileName);
            if (entry == null) { // Didn't find the entry. The file should not exist. What can I do then?
                //System.err.println("DIDN'T found file '"+fileName+"' in "+new File(folder, CVS_ENTRIES));
                continue;
            }
            String[] entryElements = CvsListOffline.parseEntry(entry);
            sendFileRefresh(file, (String) states.get(i), entryElements, stdoutDataListener);
        }
        // We can update the rest of the files only when the processing files
        // contains the whole folder.
        boolean updateFolder = processingFiles.contains(relativeFolderName);
        if (!updateFolder) {
            for (Iterator it = processingFiles.iterator(); it.hasNext(); ) {
                String pFile = (String) it.next();
                if (relativeFolderName.indexOf(pFile) == 0 && relativeFolderName.charAt(pFile.length()) == '/') {
                    // We'return updating a parent of this folder
                    updateFolder = true;
                    break;
                }
            }
        }
        if (updateFolder) {
            for (Iterator entryIt = entriesMap.keySet().iterator(); entryIt.hasNext(); ) {
                String fileName = (String) entryIt.next();
                if (filesInFolder.contains(fileName)) continue;
                File file = new File(folder, fileName);
                String entry = (String) entriesMap.get(fileName);
                sendFileRefresh(file, null, CvsListOffline.parseEntry(entry), stdoutDataListener);
            }
        }
    }
    private void sendUpdatedFolders(List folders,
                                    CommandDataOutputListener stdoutDataListener) {
        for (Iterator it = folders.iterator(); it.hasNext(); ) {
            File folder = new File((String) it.next());
            sendFolderRefresh(folder, stdoutDataListener);
            Set refreshedFiles = (Set) refreshedFilesByFolders.get(folder);
            if (refreshedFiles == null) {
                refreshedFiles = new HashSet();
                refreshedFilesByFolders.put(folder, refreshedFiles);
                Map entriesMap = (Map) cachedEntries.get(folder);
                if (entriesMap == null) {
                    File entriesFile = new File(folder, CVS_ENTRIES);
                    entriesMap = CvsListOffline.createEntriesByFiles(
                                     CvsListOffline.loadEntries(entriesFile));
                    cachedEntries.put(folder, entriesMap);
                }
                for (Iterator entryIt = entriesMap.keySet().iterator(); entryIt.hasNext(); ) {
                    String fileName = (String) entryIt.next();
                    //if (refreshedFiles.contains(fileName)) continue;
                    File file = new File(folder, fileName);
                    String entry = (String) entriesMap.get(fileName);
                    sendFileRefresh(file, null, CvsListOffline.parseEntry(entry), stdoutDataListener);
                }
            }
        }
    }
    
    private void sendUpdateProcessedFiles(Collection processingFiles, CommandDataOutputListener stdoutDataListener) {
        for (Iterator it = processingFiles.iterator(); it.hasNext(); ) {
            File file = fileSystem.getFile((String) it.next());
            if (file.isFile()) {
                File folder = file.getParentFile();
                Set refreshedFiles = (Set) refreshedFilesByFolders.get(folder);
                if (refreshedFiles == null) refreshedFiles = Collections.EMPTY_SET;
                String fileName = file.getName();
                if (refreshedFiles.contains(fileName)) continue;
                Map entriesMap = (Map) cachedEntries.get(folder);
                if (entriesMap == null) {
                    File entriesFile = new File(folder, CVS_ENTRIES);
                    entriesMap = CvsListOffline.createEntriesByFiles(
                                     CvsListOffline.loadEntries(entriesFile));
                    cachedEntries.put(folder, entriesMap);
                }
                String entry = (String) entriesMap.get(fileName);
                if (entry != null) {
                    sendFileRefresh(file, null, CvsListOffline.parseEntry(entry), stdoutDataListener);
                }
            }
        }
    }
    
    private void sendFolderRefresh(File folder, CommandDataOutputListener stdoutDataListener) {
        if (CvsListCommand.isCVSDirectory(folder)) {
            String[] statuses = new String[8];
            String filePath = folder.getAbsolutePath();
            if (filePath.length() > workingFullPath.length()) {
                filePath = filePath.substring(workingFullPath.length() + 1);
            } else {
                return ;
            }
            filePath = filePath.replace(File.separatorChar, '/');
            if (".".equals(filePath)) return ;
            statuses[0] = filePath;// + "/";
            statuses[1] = "";
            for (int i = 1; i < 8; i++) statuses[i] = "";
            statuses[5] = CvsListCommand.findStickyOfDir(folder);
            statuses[7] = null;
            //filesByName.put(fileName, fileStatuses);
            stdoutDataListener.outputData(statuses);
        }
    }
    
    private void sendFileRefresh(File file, String status, String[] entryElements,
                                 CommandDataOutputListener stdoutDataListener) {
        String[] statuses = new String[8];
        String filePath = file.getAbsolutePath().substring(workingFullPath.length() + 1);
        filePath = filePath.replace(File.separatorChar, '/');
        statuses[0] = filePath;
        statuses[2] = entryElements[1];
        String sticky = "";
        if (entryElements.length > 4) {
            sticky = entryElements[4];
            if (sticky.length() > 0) sticky = sticky.substring(1, sticky.length());
        }
        statuses[3] = "";
        statuses[4] = "";
        statuses[5] = sticky;
        statuses[6] = "";
        statuses[7] = null;
        if (status != null) {
            if ("U".equals(status) || "P".equals(status)) {
                statuses[1] = "Up-to-date";
            } else if ("A".equals(status)) {
                statuses[1] = "Locally Added";
            } else if ("R".equals(status)) {
                statuses[1] = "Locally Removed";
            } else if ("M".equals(status)) {
                statuses[1] = "Locally Modified";
            } else if ("C".equals(status)) {
                statuses[1] = "File had conflicts on merge";
            } else {
                statuses[1] = CvsListOffline.getStatusFromTime(entryElements[2], file);
            }
        } else {
            //statuses[1] = null;
            if (statuses[2].startsWith("-")) { // Negative revision
                statuses[1] = "Locally Removed";
            } else if (statuses[2].equals("0")) { // Zero revision
                statuses[1] = "Locally Added";
            } else {
                statuses[1] = "Up-to-date";//CvsListOffline.getStatusFromTime(entryElements[2], file);
                // The file must be up-to-date when it was not mentioned by the update command!
                // See issue #29885 to see why we can not rely on time comparisons.
            }
        }
        stdoutDataListener.outputData(statuses);
    }
    
    private void sendRemovedFiles(List removedFiles,
                                  CommandDataOutputListener stdoutDataListener) {
        for (Iterator it = removedFiles.iterator(); it.hasNext(); ) {
            String file = (String) it.next();
            String filePath = file.substring(workingFullPath.length() + 1);
            filePath = filePath.replace(File.separatorChar, '/');
            String[] statuses = new String[8];
            //statuses[0] = filePath;
            statuses[7] = filePath;
            stdoutDataListener.outputData(statuses);
        }
    }
    
}

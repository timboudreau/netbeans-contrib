/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
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
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.cmdline.ExecuteCommand;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;

import org.netbeans.modules.vcs.profiles.cvsprofiles.list.CvsListCommand;
import org.netbeans.modules.vcs.profiles.cvsprofiles.list.CvsListOffline;

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
     * The filesystem root folder (work dir + rel. mountpoint)
     */
    private String fsRootDir;
    
    /**
     * The original file revisions before the update is executed.
     */
    //private Map fileRevisions;
    
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
                        CommandDataOutputListener stdoutDataListener, String dataRegex,
                        CommandDataOutputListener stderrDataListener, String errorRegex) {
        
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
        Collection processingFiles = ExecuteCommand.createProcessingFiles(fileSystem, vars);
        fsRootDir = fileSystem.getRootDirectory().getAbsolutePath();
        //collectRevisions(processingFiles, fsRootDir);
        Map foldersByProcessingFiles = null;
        if (doRefresh) {
            foldersByProcessingFiles = getFoldersByProcessingFiles(processingFiles);
        }

        final String rootDir = Variables.expand(vars, (String) vars.get(args[0]), false); // NOI18N
        final ArrayList filesBuff = new ArrayList();
        final ArrayList filesStatusBuff = new ArrayList();
        final ArrayList foldersBuff = new ArrayList();
        final ArrayList removedFiles = new ArrayList(); // Files removed by the update.
        VcsCommand cmd = fileSystem.getCommand(args[1]);
        VcsCommandExecutor vce = fileSystem.getVcsFactory().getCommandExecutor(cmd, vars);
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
                    filesStatusBuff.add(status);
                    String file = (rootDir + File.separator + fileName).
                                  replace('/', File.separatorChar);
                    filesBuff.add(file);
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
                        file = rootDir + File.separator + data[0];
                    }
                    file = file.replace('/', File.separatorChar);
                    if (data.length > 1 && data[1].length() > 0) {
                        removedFiles.add(file);
                    } else {
                        foldersBuff.add(file);
                    }
                }
            }
        });
        fileSystem.getCommandsPool().startExecutor(vce, fileSystem);
        try {
            fileSystem.getCommandsPool().waitToFinish(vce);
        } catch (InterruptedException iexc) {
            fileSystem.getCommandsPool().kill(vce);
            Thread.currentThread().interrupt();
        }
        if (doRefresh) {
            cachedEntries.clear(); // The Entries probably has changed
            sendUpdatedFiles(filesBuff, filesStatusBuff, foldersBuff, stdoutDataListener);
            sendRemovedFiles(removedFiles, stdoutDataListener);
            sendUpdateProcessedFiles(processingFiles, stdoutDataListener);
            sendRemovedFolders(foldersByProcessingFiles,
                               getFoldersByProcessingFiles(processingFiles),
                               stdoutDataListener);
        }
        return VcsCommandExecutor.SUCCEEDED == vce.getExitStatus();
    }
    
    /*
    private Map collectRevisions(Collection processingFiles, String fsRootDir) {
        fileRevisions = new HashMap();
        for (Iterator it = processingFiles.iterator(); it.hasNext(); ) {
            String file = fsRootDir + File.separator + (String) it.next();
            file.replace('/', File.separatorChar);
            addFileRevisions(new File(file));
        }
        return fileRevisions;
    }
    
    private void addFileRevisions(File file) {
        if (file.isFile()) {
            File parent = file.getParentFile();
            Map entriesMap = (Map) cachedEntries.get(parent);
            if (entriesMap == null) {
                File entriesFile = new File(parent, CVS_ENTRIES);
                entriesMap = CvsListOffline.createEntriesByFiles(
                                 CvsListOffline.loadEntries(entriesFile));
                cachedEntries.put(parent, entriesMap);
            }
            String fileName = file.getName();
            String entry = (String) entriesMap.get(fileName);
            String[] entryElements = CvsListOffline.parseEntry(entry);
            if (entryElements.length > 1) {
                String revision = entryElements[1];
                fileRevisions.put(file, revision);
            }
        } else {
            Map entriesMap = (Map) cachedEntries.get(file);
            if (entriesMap == null) {
                File entriesFile = new File(file, CVS_ENTRIES);
                entriesMap = CvsListOffline.createEntriesByFiles(
                                 CvsListOffline.loadEntries(entriesFile));
                cachedEntries.put(file, entriesMap);
            }
            for (Iterator it = entriesMap.keySet().iterator(); it.hasNext(); ) {
                String fileName = (String) it.next();
                File theFile = new File(file, fileName);
                String entry = (String) entriesMap.get(fileName);
                String[] entryElements = CvsListOffline.parseEntry(entry);
                if (entryElements.length > 1) {
                    String revision = entryElements[1];
                    fileRevisions.put(theFile, revision);
                }
            }
            File[] subFiles = file.listFiles();
            if (subFiles != null) {
                for (int i = 0; i < subFiles.length; i++) {
                    if (subFiles[i].isDirectory()) {
                        addFileRevisions(subFiles[i]);
                    }
                }
            }
        }
    }
     */
    
    /** Create a map of collection of folders for each processing file */
    private Map getFoldersByProcessingFiles(Collection processingFiles) {
        HashMap map = new HashMap();
        for (Iterator it = processingFiles.iterator(); it.hasNext(); ) {
            String processingFile = (String) it.next();
            String file = fsRootDir + File.separator + processingFile;
            file.replace('/', File.separatorChar);
            map.put(processingFile, getFoldersUnder(new File(file)));
        }
        return map;
    }
    
    /** Collect all folders under a specified file. */
    private static List getFoldersUnder(File file) {
        ArrayList folders = new ArrayList();
        if (file.isDirectory() && !"CVS".equalsIgnoreCase(file.getName())) {
            folders.add(file.getAbsolutePath());
            File[] subFolders = file.listFiles();//new java.io.FileFilter() {
            if (subFolders != null) {
                for (int i = 0; i < subFolders.length; i++) {
                    if (subFolders[i].isDirectory()) {
                        folders.addAll(getFoldersUnder(subFolders[i]));
                    }
                }
            }
        }
        return folders;
    }
    
    private void sendUpdatedFiles(ArrayList files, ArrayList states, ArrayList folders,
                                  CommandDataOutputListener stdoutDataListener) {
        int i = 0;
        for (Iterator it = files.iterator(); it.hasNext(); i++) {
            File file = new File((String) it.next());
            File folder = file.getParentFile();
            Map entriesMap = (Map) cachedEntries.get(folder);
            if (entriesMap == null) {
                File entriesFile = new File(folder, CVS_ENTRIES);
                entriesMap = CvsListOffline.createEntriesByFiles(
                                 CvsListOffline.loadEntries(entriesFile));
                cachedEntries.put(folder, entriesMap);
            }
            String fileName = file.getName();
            HashSet filesInFolder = (HashSet) refreshedFilesByFolders.get(folder);
            if (filesInFolder == null) {
                filesInFolder = new HashSet();
                refreshedFilesByFolders.put(folder, filesInFolder);
            }
            filesInFolder.add(fileName);
            //String revision = (String) fileRevisions.get(file);
            //boolean changed = revision == null;
            String entry = (String) entriesMap.get(fileName);
            if (entry == null) { // Didn't find the entry. The file should not exist. What can I do then?
                continue;
            }
            String[] entryElements = CvsListOffline.parseEntry(entry);
            /*
            if (revision != null) {
                if (entryElements.length > 1) {
                    changed = !revision.equals(entryElements[1]);
                }
            }
             */
            //if (changed) {
                sendFileRefresh(file, (String) states.get(i), entryElements, stdoutDataListener);
                /*
            } else {
                // Updated a file, but it's revision didn't change ??
                // Strange. We probably run with the global "-n" switch - do not make changes.
                // If no changes, we'll do no refresh!
                doRefresh = false;
                return ;
            }
                 */
        }
        for (Iterator it = folders.iterator(); it.hasNext(); ) {
            File folder = new File((String) it.next());
            sendFolderRefresh(folder, stdoutDataListener);
            Set refreshedFiles = (Set) refreshedFilesByFolders.get(folder);
            if (refreshedFiles == null) refreshedFiles = Collections.EMPTY_SET;
            Map entriesMap = (Map) cachedEntries.get(folder);
            if (entriesMap == null) {
                File entriesFile = new File(folder, CVS_ENTRIES);
                entriesMap = CvsListOffline.createEntriesByFiles(
                                 CvsListOffline.loadEntries(entriesFile));
                cachedEntries.put(folder, entriesMap);
            }
            for (Iterator entryIt = entriesMap.keySet().iterator(); entryIt.hasNext(); ) {
                String fileName = (String) entryIt.next();
                if (refreshedFiles.contains(fileName)) continue;
                File file = new File(folder, fileName);
                String entry = (String) entriesMap.get(fileName);
                sendFileRefresh(file, null, CvsListOffline.parseEntry(entry), stdoutDataListener);
            }
        }
        /*
            Map entriesMap = (Map) cachedEntries.get(folder);
            if (entriesMap == null) {
                File entriesFile = new File(folder, CVS_ENTRIES);
                entriesMap = CvsListOffline.createEntriesByFiles(
                                 CvsListOffline.loadEntries(entriesFile));
                cachedEntries.put(folder, entriesMap);
            }
            for (Iterator filesIt = entriesMap.keySet().iterator(); filesIt.hasNext(); ) {
                String fileName = (String) filesIt.next();
                if (files.contains(fileName)) { // The file might be changed.
                    File file = new File(folder, fileName);
                    String revision = (String) fileRevisions.get(file);
                    boolean changed = revision == null;
                    String entry = (String) entriesMap.get(fileName);
                    String[] entryElements = CvsListOffline.parseEntry(entry);
                    if (revision != null) {
                        if (entryElements.length > 1) {
                            changed = !revision.equals(entryElements[1]);
                        }
                    }
                    if (changed) {
                        int index = files.indexOf(fileName);
                        String status = null;
                        if (files.lastIndexOf(fileName) == index) {
                            status = (String) states.get(index);
                        }
                        sendFileRefresh(file, status, entryElements, stdoutDataListener);
                    }
                }
            }
        }
         */
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
            if (filePath.length() > fsRootDir.length()) {
                filePath = filePath.substring(fsRootDir.length() + 1);
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
        String filePath = file.getAbsolutePath().substring(fsRootDir.length() + 1);
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
                statuses[1] = CvsListOffline.getStatusFromTime(entryElements[2], file);
            }
        }
        stdoutDataListener.outputData(statuses);
    }
    
    private void sendRemovedFiles(List removedFiles,
                                  CommandDataOutputListener stdoutDataListener) {
        for (Iterator it = removedFiles.iterator(); it.hasNext(); ) {
            String file = (String) it.next();
            String filePath = file.substring(fsRootDir.length() + 1);
            filePath = filePath.replace(File.separatorChar, '/');
            String[] statuses = new String[8];
            //statuses[0] = filePath;
            statuses[7] = filePath;
            stdoutDataListener.outputData(statuses);
        }
    }
    
    private void sendRemovedFolders(Map oldFoldersMap, Map newFoldersMap,
                                    CommandDataOutputListener stdoutDataListener) {
        for (Iterator it = oldFoldersMap.keySet().iterator(); it.hasNext(); ) {
            String processedFile = (String) it.next();
            List oldFolders = (List) oldFoldersMap.get(processedFile);
            List newFolders = (List) newFoldersMap.get(processedFile);
            if (newFolders != null) {
                oldFolders.removeAll(newFolders);
                // We should not fire changes for removed children.
                // This would cause creation of their removed parents.
                removeChildren(oldFolders);
                sendRemovedFiles(oldFolders, stdoutDataListener);
            }
        }
    }
    
    /** Remove all children from this collection of folders. It's expected, that
     * children always follow their parents. */
    private static void removeChildren(List folders) {
        String lastFolder = null;
        for (int i = 0; i < folders.size(); i++) {
            String folder = (String) folders.get(i);
            if (lastFolder != null) {
                if (folder.startsWith(lastFolder)) {
                    folders.remove(folder);
                    i--;
                    continue;
                }
            }
            lastFolder = folder;
        }
    }
    
}

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

package org.netbeans.modules.vcs.profiles.vss.list;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.io.*;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.cmdline.UserCommand;
import org.netbeans.modules.vcscore.util.*;

import org.netbeans.modules.vcs.profiles.list.AbstractListCommand;

/**
 * List command for VSS.
 * @author  Martin Entlicher
 */
public class VssListCommand extends AbstractListCommand {

    private Debug E=new Debug("VssList", true);
    private Debug D=E;
    
    private static final String PROJECT_BEGIN = "$/"; // NOI18N
    private static final String STATUS_MISSING = "Missing"; // NOI18N
    private static final String STATUS_CURRENT = "Current"; // NOI18N
    private static final String STATUS_LOCALLY_MODIFIED = "Locally Modified"; // NOI18N
    private static final String LOCAL_FILES = "Local files not in the current project:"; // NOI18N
    private static final String SOURCE_SAFE_FILES = "SourceSafe files not in the current folder:"; // NOI18N
    private static final String DIFFERENT_FILES = "SourceSafe files different from local files:"; // NOI18N
    private static final String IGNORED_FILE = "vssver.scc"; // NOI18N

    private static final int STATUS_POSITION = 19;
    
    private String dir=null; //, rootDir=null;
    private String relDir = null;
    private String[] args=null;
    private volatile String[] statuses = null;
    private Hashtable vars = null;
    private VcsFileSystem fileSystem = null;
    private HashSet currentFiles = null;
    private HashSet missingFiles = null;
    private HashSet differentFiles = null;

    /** Creates new VssListCommand */
    public VssListCommand() {
    }

    public void setFileSystem(VcsFileSystem fileSystem) {
        super.setFileSystem(fileSystem);
        this.fileSystem = fileSystem;
    }

    private void initDir(Hashtable vars) {
        String rootDir = (String) vars.get("ROOTDIR");
        if (rootDir == null) {
            rootDir = ".";
            vars.put("ROOTDIR",".");
        }
        this.dir = (String) vars.get("DIR");
        if (this.dir == null) {
            this.dir = "";
            vars.put("DIR","");
        }
        String module = (String) vars.get("MODULE"); // NOI18N
        //D.deb("rootDir = "+rootDir+", module = "+module+", dir = "+dir); // NOI18N
        relDir = new String(dir);
        if (dir.equals("")) { // NOI18N
            dir=rootDir;
            if (module != null && module.length() > 0) {
                dir += File.separator + module;
                relDir = new String(module);
            }
        } else {
            if (module == null)
                dir=rootDir+File.separator+dir;
            else {
                dir=rootDir+File.separator+module+File.separator+dir;
                relDir = new String(module+File.separator+dir);
            }
        }
        if (dir.charAt(dir.length() - 1) == File.separatorChar)
            dir = dir.substring(0, dir.length() - 1);
        D.deb("dir="+dir);
    }

    private boolean runCommand(Hashtable vars, String cmdName) throws InterruptedException {
        return runCommand(vars, cmdName, null);
    }
    
    private boolean runCommand(Hashtable vars, String cmdName, final boolean[] errMatch) throws InterruptedException {
        VcsCommand cmd = fileSystem.getCommand(cmdName);
        VcsCommandExecutor ec = fileSystem.getVcsFactory().getCommandExecutor(cmd, vars);
        ec.addDataOutputListener(this);
        if (errMatch != null && errMatch.length > 0) {
            ec.addDataErrorOutputListener(new CommandDataOutputListener() {
                public void outputData(String[] data) {
                    if (data != null) errMatch[0] = true;
                }
            });
        }
        fileSystem.getCommandsPool().preprocessCommand(ec, vars, fileSystem);
        fileSystem.getCommandsPool().startExecutor(ec);
        try {
            fileSystem.getCommandsPool().waitToFinish(ec);
        } catch (InterruptedException iexc) {
            fileSystem.getCommandsPool().kill(ec);
            throw iexc;
        }
        return (ec.getExitStatus() == VcsCommandExecutor.SUCCEEDED);
    }
    
    /**
     * List files of VSS Repository.
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
        this.stdoutListener = stdoutListener;
        this.stderrListener = stderrListener;
        this.dataRegex = dataRegex;
        this.errorRegex = errorRegex;
        this.args = args;
        this.vars = new Hashtable(vars);
        this.filesByName = filesByName;
        if (args.length < 2) {
            if (stderrNRListener != null) stderrNRListener.outputLine("Bad number of arguments. "+
                                                                      "Expecting two arguments: directory reader and status reader.\n"+
                                                                      "Directory status reader is an optional third argument to improve "+
                                                                      "performance on large directories.");
            return false;
        }
        initVars(this.vars);
        initDir(this.vars);
        readLocalFiles(dir);
        missingFiles = new HashSet();
        differentFiles = new HashSet();
        //parseCommands();
        boolean[] errMatch = new boolean[1];
        try {
            runCommand(this.vars, args[0], errMatch);
        } catch (InterruptedException iexc) {
            return false;
        }
        if (!errMatch[0]) {
            flushLastFile();
            currentFiles.removeAll(differentFiles);
            try {
                fillFilesByName();
            } catch (InterruptedException iexc) {
                return false;
            }
        }

        return !errMatch[0];
    }
    
    private void fillFilesByName() throws InterruptedException {
        /*
        System.out.println("fillFilesByName():\n"+
                           "currentFiles = "+VcsUtilities.arrayToString((String[]) currentFiles.toArray(new String[0]))+
                           "\nmissingFiles = "+VcsUtilities.arrayToString((String[]) missingFiles.toArray(new String[0]))+
                           "\ndifferentFiles = "+VcsUtilities.arrayToString((String[]) differentFiles.toArray(new String[0]))+
                           "\n");
         */
        // At first try to run ss status once for the whole folder and retrieve
        // status of files distinguishable at first 19 characters
        if (args.length > 2) fillPossibleFilesAtOnce();
        // If some files differ at characters after 19, we need to precess them
        // file-by-file
        fillFilesByName(currentFiles, STATUS_CURRENT);
        fillFilesByName(missingFiles, STATUS_MISSING);
        fillFilesByName(differentFiles, STATUS_LOCALLY_MODIFIED);
    }
    
    private void fillPossibleFilesAtOnce() throws InterruptedException {
        HashMap distinguishable = new HashMap();
        HashSet undistinguishable = new HashSet();
        HashSet allFiles = (currentFiles != null) ? new HashSet(currentFiles) : new HashSet();
        if (missingFiles != null) allFiles.addAll(missingFiles);
        if (differentFiles != null) allFiles.addAll(differentFiles);
        for (Iterator it = allFiles.iterator(); it.hasNext(); ) {
            String file = (String) it.next();
            String pattern;
            if (file.length() <= STATUS_POSITION) {
                pattern = file;
            } else {
                pattern = file.substring(0, STATUS_POSITION);
            }
            if (distinguishable.containsKey(pattern)) {
                // This pattern was already processed => it's not distinguishable any more
                distinguishable.remove(pattern);
                undistinguishable.add(file);
            } else if (!undistinguishable.contains(file)) {
                distinguishable.put(pattern, file);
            }
        }
        if (distinguishable.size() > 2) { // We have to have a reasonable number of files to take care about
            fillFromFolderStatus(distinguishable);
        }
    }
    
    private void fillFromFolderStatus(final Map distinguishable) throws InterruptedException {
        VcsCommand cmd = fileSystem.getCommand(args[2]);
        final Set processedFiles = Collections.synchronizedSet(new HashSet());
        if (cmd != null) {
            Hashtable cmdVars = new Hashtable(vars);
            VcsCommandExecutor vce = fileSystem.getVcsFactory().getCommandExecutor(cmd, cmdVars);
            vce.addDataOutputListener(new CommandDataOutputListener() {
                public void outputData(String[] elements) {
                    if (elements != null) {
                        if (elements[0].indexOf(PROJECT_BEGIN) == 0) return ; // skip the $/... folder
                        int index = Math.min(STATUS_POSITION + 1, elements[0].length());
                        int index2 = elements[0].indexOf("  ", index);
                        if (index2 < 0) index2 = elements[0].length();
                        if (index < index2) {
                            String pattern = elements[0].substring(0, STATUS_POSITION).trim();
                            String file = (String) distinguishable.get(pattern);
                            if (file != null) {
                                String[] statuses = new String[3];
                                statuses[0] = file;
                                if (currentFiles != null && currentFiles.contains(file)) {
                                    statuses[1] = STATUS_CURRENT;
                                } else if (missingFiles != null && missingFiles.contains(file)) {
                                    statuses[1] = STATUS_MISSING;
                                } else {
                                    statuses[1] = STATUS_LOCALLY_MODIFIED;
                                }
                                statuses[2] = elements[0].substring(index, index2).trim();
                                filesByName.put(statuses[0], statuses);
                                stdoutListener.outputData(statuses);
                                processedFiles.add(file);
                            }
                        }
                    }
                }
            });
            fileSystem.getCommandsPool().preprocessCommand(vce, cmdVars, fileSystem);
            fileSystem.getCommandsPool().startExecutor(vce);
            try {
                fileSystem.getCommandsPool().waitToFinish(vce);
            } catch (InterruptedException iexc) {
                fileSystem.getCommandsPool().kill(vce);
                throw iexc;
            }
            Collection distinguishableFiles = distinguishable.values();
            Set noStatusFiles = new HashSet(distinguishableFiles);
            noStatusFiles.removeAll(processedFiles);
            for (Iterator it = noStatusFiles.iterator(); it.hasNext(); ) {
                String file = (String) it.next();
                String[] statuses = new String[3];
                statuses[0] = file;
                if (currentFiles != null && currentFiles.contains(file)) {
                    statuses[1] = STATUS_CURRENT;
                } else if (missingFiles != null && missingFiles.contains(file)) {
                    statuses[1] = STATUS_MISSING;
                } else {
                    statuses[1] = STATUS_LOCALLY_MODIFIED;
                }
                statuses[2] = "";
                filesByName.put(statuses[0], statuses);
                stdoutListener.outputData(statuses);
            }
            if (currentFiles != null) {
                currentFiles.removeAll(distinguishableFiles);
            }
            if (missingFiles != null) {
                missingFiles.removeAll(distinguishableFiles);
            }
            if (differentFiles != null) {
                differentFiles.removeAll(distinguishableFiles);
            }
        }
    }
    
    private void fillFilesByName(Set files, String status) throws InterruptedException {
        if (files == null) return ;
        for (Iterator fileIt = files.iterator(); fileIt.hasNext(); ) {
            String file = (String) fileIt.next();
            statuses = new String[3];
            statuses[0] = file;
            statuses[1] = status;
            VcsCommand cmd = fileSystem.getCommand(args[1]);
            if (cmd != null) {
                Hashtable varsCmd = new Hashtable(vars);
                varsCmd.put("FILE", file);
                //cmd.setProperty(UserCommand.PROPERTY_DATA_REGEX, "^"+file.substring(0, Math.min(STATUS_POSITION, file.length()))+" (.*$)");
                statuses[2] = null;
                VcsCommandExecutor vce = fileSystem.getVcsFactory().getCommandExecutor(cmd, varsCmd);
                vce.addDataOutputListener(new CommandDataOutputListener() {
                    public void outputData(String[] elements2) {
                        if (elements2 != null) {
                            //D.deb(" ****  status match = "+VcsUtilities.arrayToString(elements));
                            if (elements2[0].indexOf(PROJECT_BEGIN) == 0) return ; // skip the $/... folder
                            addStatuses(elements2);
                        }
                    }
                });
                fileSystem.getCommandsPool().preprocessCommand(vce, varsCmd, fileSystem);
                fileSystem.getCommandsPool().startExecutor(vce);
                try {
                    fileSystem.getCommandsPool().waitToFinish(vce);
                } catch (InterruptedException iexc) {
                    fileSystem.getCommandsPool().kill(vce);
                    throw iexc;
                }
            } else statuses[2] = "";
            filesByName.put(statuses[0], statuses);
            stdoutListener.outputData(statuses);
        }
    }
    
    private void readLocalFiles(String dir) {
        File fileDir = new File(dir);
        currentFiles = new HashSet();
        String[] subFiles = fileDir.list();
        if (subFiles == null) return ;
        File ignoredFile = new File(dir, IGNORED_FILE);
        for (int i = 0; i < subFiles.length; i++) {
            File file = new File(dir, subFiles[i]);
            if (file.isFile() && file.compareTo(ignoredFile) != 0) {
                currentFiles.add(subFiles[i]);
            }
        }
    }

    private void addStatuses(String[] elements) {
        D.deb(" !!!!!!!!!!  adding statuses "+VcsUtilities.arrayToString(elements));
        /*
        for (int i = 1; i < Math.min(elements.length, statuses.length); i++)
          statuses[i] = elements[i];
        */
        if (statuses[2] != null) return ; // The status is already set (it can be called more than once with some garbage then)
        int fileIndex = statuses[0].lastIndexOf('/');
        if (fileIndex < 0) fileIndex = 0;
        else fileIndex++;
        String file = statuses[0].substring(fileIndex);
        if (file.length() <= STATUS_POSITION) {
            if (!elements[0].startsWith(file)) {
                statuses[2] = "";
                // The element does not start with the file name
                return ;
            }
        } else {
            if (!file.startsWith(elements[0].substring(0, STATUS_POSITION))) {
                statuses[2] = "";
                // The element does not start with the file name
                return ;
            }
        }
        int index = Math.min(STATUS_POSITION + 1, elements[0].length());
        int index2 = elements[0].indexOf("  ", index);
        if (index2 < 0) index2 = elements[0].length();
        if (index < index2) {
            statuses[2] = elements[0].substring(index, index2).trim();
        } else {
            statuses[2] = "";
        }
    }
    
    private int findWhiteSpace(String str) {
        return findWhiteSpace(str, 0);
    }
    
    private int findWhiteSpace(String str, int index) {
        for (int i = index; i < str.length(); i++) {
            if (Character.isWhitespace(str.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    private String getFileFromRow(String row) {
        int index;
        //System.out.println("getFileFromRow("+row+")");
        for (index = findWhiteSpace(row); index >= 0 && index < row.length(); index = findWhiteSpace(row, index + 1)) {
            String file = row.substring(0, index).trim();
            //System.out.println("    test file = '"+file+"', contains = "+currentFiles.contains(file));
            if (currentFiles.contains(file)) break;
        }
        //System.out.println("   have CVS file = "+((index > 0 && index < row.length()) ? row.substring(0, index) : row));
        if (index > 0 && index < row.length()) return row.substring(0, index);
        else return row;
    }
    
    private boolean gettingFolders = true;
    private boolean gettingLocalFiles = false;
    private boolean gettingSourceSafeFiles = false;
    private boolean gettingDifferentFiles = false;
    private String lastFileName = "";
    
    private void removeLocalFiles() {
        //System.out.println("removeLocalFiles: "+lastFileName);
        if (lastFileName == null) return ;
        while (lastFileName.length() > 0) {
            String fileName = getFileFromRow(lastFileName);
            //System.out.println("file From Row = '"+fileName+"'");
            currentFiles.remove(fileName.trim());
            lastFileName = lastFileName.substring(fileName.length());
        }
    }
    
    private void flushLastFile() {
        if (lastFileName == null || lastFileName.length() == 0) return ;
        if (gettingSourceSafeFiles) {
            missingFiles.add(lastFileName.trim());
            lastFileName = "";
        } else if (gettingDifferentFiles) {
            differentFiles.add(lastFileName.trim());
            lastFileName = "";
        } else if (gettingLocalFiles) {
            removeLocalFiles();
        }
    }
    
    /** Parse the output of "ss dir -F- && ss diff" commands
     * ss dir -F- gives the subfolders in the given folder
     * ss diff gives the differences between the current folder and the repository.
     */
    public void outputData(String[] elements) {
        String line = elements[0];
        //System.out.println("outputData("+line+")");
        if (line == null) return;
        String file = line.trim();
        if (LOCAL_FILES.equals(file)) {
            //System.out.println("LOCAL_FILES");
            gettingFolders = false;
            gettingLocalFiles = true;
        } else if (SOURCE_SAFE_FILES.equals(file)) {
            //System.out.println("SOURCE_SAFE_FILES");
            gettingFolders = false;
            if (gettingLocalFiles) {
                removeLocalFiles();
                gettingLocalFiles = false;
            }
            gettingSourceSafeFiles = true;
        } else if (DIFFERENT_FILES.equals(file)) {
            //System.out.println("DIFFERENT_FILES");
            gettingFolders = false;
            if (gettingLocalFiles) {
                removeLocalFiles();
                gettingLocalFiles = false;
            }
            flushLastFile();
            gettingSourceSafeFiles = false;
            gettingDifferentFiles = true;
        } else if (gettingFolders) {
            if(!file.startsWith(PROJECT_BEGIN) && file.startsWith("$")) {
                String fname = file.substring(1, file.length());
                File f = new File(dir + File.separator + fname);
                statuses = new String[3];
                statuses[0] = fname + "/";
                if (f.exists()) statuses[1] = STATUS_CURRENT;
                else            statuses[1] = STATUS_MISSING;
                filesByName.put(statuses[0], statuses);
                stdoutListener.outputData(statuses);
            }
        } else if (gettingLocalFiles) {
            lastFileName += line;
        } else if (gettingSourceSafeFiles || gettingDifferentFiles) {
            if (line.startsWith("  ")) {
                flushLastFile();
            }
            lastFileName += line;
        }
    }
    
}

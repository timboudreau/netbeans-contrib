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

package org.netbeans.modules.vcs.profiles.pvcs.list;

import java.io.*;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
//import java.util.*;
//import java.beans.*;
//import java.text.*;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.util.*;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;

import org.netbeans.modules.vcs.profiles.list.AbstractListCommand;

/** PVCS list command wrapper
 * 
 * @author Martin Entlicher
 */

public class PvcsListCommand extends AbstractListCommand {
    private Debug E=new Debug("PvcsListComand",true); // NOI18N
    private Debug D=E;

    private String dir=null;
    private String pathToSkip = null;
    
    private VcsFileSystem fileSystem;
    private String[] fileStatuses = null;
    private Map archivesByNames;
    private Map workFilesByNames;
    
    //private String rootDir=null;
    //private String pvcsRoot=null;
    
    /*
    //private String configFile=null;
    //private String cmd=null;
    private StringBuffer dataBuffer=new StringBuffer(4096);
    //private static final String[] archiveFiles = {"*-arc","*.??v"};
    private static final String workFile = "Workfile:";
    private static final String locks = "Locks:";
    private static final String locksSeparator = " : ";
     */
    private static final String ENTITY_TYPE = "EntityType="; // NOI18N
    private static final String ENTITY_PROJECT = "Project"; // NOI18N
    private static final String ENTITY_VERSIONED_FILE = "VersionedFile"; // NOI18N
    private static final String ENTITY_WORK_PATH = "WorkPath="; // NOI18N
    private static final String ENTITY_NAME = "Name="; // NOI18N
    private static final String ARCHIVE_PATH = "ArchivePath="; // NOI18N
    private static final String ARCHIVE_LOCK_INFO = "Archive:LockInfo=["; // NOI18N
    private static final String LOCKS_SEPARATOR = " : "; // NOI18N
    
    private static final String MISSING_STATUS = "Missing"; // NOI18N
    private static final String CURRENT_STATUS = "Current"; // NOI18N
    private static final String MODIFIED_STATUS = "Locally Modified"; // NOI18N

    //-------------------------------------------
    public PvcsListCommand() {
    }

    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
        super.setFileSystem(fileSystem);
    }

    private void initDir(Hashtable vars) {
        String rootDir = (String) vars.get("ROOTDIR"); // NOI18N
        if (rootDir == null) {
            rootDir = "."; // NOI18N
        }
        this.dir = (String) vars.get("DIR"); // NOI18N
        if (this.dir == null) {
            this.dir = ""; // NOI18N
        }
        String module = (String) vars.get("MODULE"); // NOI18N
        //D.deb("rootDir = "+rootDir+", module = "+module+", dir = "+dir); // NOI18N
        if (dir.equals("")) { // NOI18N
            dir=rootDir;
            if (module != null && module.length() > 0) {
                dir += File.separator + module;
                pathToSkip = module;
            } else {
                pathToSkip = "";
            }
        } else {
            if (module == null || module.length() == 0) {
                pathToSkip = dir;
                dir = rootDir + File.separator + dir;
            } else {
                pathToSkip = module + File.separator + dir;
                dir = rootDir + File.separator + module + File.separator + dir;
            }
        }
        if (dir.charAt(dir.length() - 1) == File.separatorChar)
            dir = dir.substring(0, dir.length() - 1);
        while (pathToSkip.endsWith(File.separator)) pathToSkip = pathToSkip.substring(0, pathToSkip.length() - 1);
        pathToSkip = "/" + pathToSkip.replace(File.separatorChar, '/');
        //System.out.println("pathToSkip = "+pathToSkip);
        //D.deb("dir = "+dir); // NOI18N
    }


    /**
     * List files of StarTeam Repository.
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
        this.dataRegex = dataRegex;
        this.errorRegex = errorRegex;
        this.filesByName = filesByName;
        initVars(vars);
        initDir(vars);
        if (args.length < 2) {
            stderrNRListener.outputLine("Expecting list and diff commands as arguments!"); //NOI18N
            return false;
        }
        VcsCommand diffCmd = fileSystem.getCommand(args[1]);
        if (diffCmd == null) {
            stderrNRListener.outputLine("Unknown Command: "+args[1]); //NOI18N
            return false;
        }
        archivesByNames = new HashMap();
        workFilesByNames = new HashMap();
        try {
            runCommand(vars, args[0], false);
        } catch (InterruptedException iexc) {
            return false;
        }
        //fillHashtable(filesByName);
        //addDirs(filesByName);
        if (fileStatuses != null) filesByName.put(fileStatuses[0], fileStatuses);
        findFilesStatus(filesByName, diffCmd, vars);
        if (stdoutListener != null) {
            for (Iterator it = filesByName.values().iterator(); it.hasNext(); ) {
                stdoutListener.outputData((String[]) it.next());
            }
        }
        return filesByName.size() > 0 || !shouldFail;
    }

    /*
    private void fillHashtable(Hashtable filesByName) {
        String data=new String(dataBuffer);
        int pos=0;
        int index=0;
        while((index = data.indexOf(workFile, pos)) >= 0) {
            index += workFile.length();
            int nextIndex = data.indexOf('\n', index);
            if (nextIndex < 0) nextIndex = data.length();
            String name = data.substring(index, nextIndex).trim();
            index = data.indexOf(locks, nextIndex);
            if (index < 0) break;
            index += locks.length();
            nextIndex = data.indexOf('\n', index);
            if (nextIndex < 0) nextIndex = data.length();
            String lock = data.substring(index, nextIndex).trim();
            int revIndex = lock.indexOf(locksSeparator);
            String revision = "";
            if (revIndex > 0) {
                revision = lock.substring(revIndex + locksSeparator.length());
                lock = lock.substring(0, revIndex);
            }
            String[] fileStatuses = new String[4];
            fileStatuses[0] = name;
            fileStatuses[1] = missingStatus;
            fileStatuses[2] = lock;
            fileStatuses[3] = revision;
            filesByName.put(name, fileStatuses);
            pos = nextIndex;
        }
    }
     */

    //-------------------------------------------
    /**
     * Add directories from archive with status "Missing" and check for files and directories
     * in the working directory, if they are present, change the status to "Current".
     */
    private void addDirs(Hashtable filesByName) {
        /*
        File d = new File(pvcsRoot);
        File[] files = d.listFiles();
        if (files != null) {
            for(int i=0;i<files.length;i++){
                String[] fileStatuses = new String[4];
                fileStatuses[1] = missingStatus;
                fileStatuses[2] = "";
                fileStatuses[3] = "";
                File file=files[i];
                if (file.isDirectory()) {
                    String fileName = file.getName()+"/"; // NOI18N
                    if(filesByName.get(fileName) == null) {
                        fileStatuses[0] = fileName;
                        filesByName.put(fileName, fileStatuses);
                    }
                }
            }
        }
         */
        File d = new File(dir);
        File[] files = d.listFiles();
        if (files != null) {
            String[] fileStatuses = null;
            for(int i=0;i<files.length;i++){
                File file=files[i];
                String fileName = file.getName();
                if (file.isDirectory()) fileName += "/"; // NOI18N
                if ((fileStatuses = (String[]) filesByName.get(fileName)) != null) {
                    fileStatuses[1] = CURRENT_STATUS;
                    //filesByName.put(fileName, fileStatuses);
                }
            }
        }
    }
    
    private void findFilesStatus(Map filesByName, VcsCommand diffCmd, Hashtable vars) {
        for (Iterator it = workFilesByNames.keySet().iterator(); it.hasNext(); ) {
            String name = (String) it.next();
            String[] fileStatuses = (String[]) filesByName.get(name);
            Hashtable cmdVars = new Hashtable(vars);
            cmdVars.put("WORKFILE", workFilesByNames.get(name));
            cmdVars.put("ARCHIVE", archivesByNames.get(name));
            VcsCommandExecutor ec = fileSystem.getVcsFactory().getCommandExecutor(diffCmd, cmdVars);
            fileSystem.getCommandsPool().startExecutor(ec, fileSystem);
            try {
                fileSystem.getCommandsPool().waitToFinish(ec);
            } catch (InterruptedException iexc) {
                fileSystem.getCommandsPool().kill(ec);
                //throw iexc;
            }
            if (ec.getExitStatus() != VcsCommandExecutor.SUCCEEDED) {
                fileStatuses[1] = MODIFIED_STATUS;
            }
        }
    }

    private boolean file = false;
    private boolean folder = false;
    private String lastFile = null;
    private boolean skipNextName = false;
    
    /** Called with the line of LIST command output */
    public void outputData(String[] elements) {
        D.deb("match("+elements[0]+")"); // NOI18N
        System.out.println("match("+elements[0]+")");
        if (elements[0] != null) {
            if (pathToSkip.equals(elements[0])) skipNextName = true;
            if (elements[0].startsWith(ENTITY_TYPE)) {
                String entityType = elements[0].substring(ENTITY_TYPE.length());
                file = ENTITY_VERSIONED_FILE.equals(entityType);
                folder = ENTITY_PROJECT.equals(entityType);
            }
            if (elements[0].startsWith(ENTITY_NAME)) {
                if (fileStatuses != null) filesByName.put(fileStatuses[0], fileStatuses);
                fileStatuses = null;
                if (skipNextName) {
                    skipNextName = false;
                    return ;
                }
                String name = elements[0].substring(ENTITY_NAME.length());
                File file = new File(dir, name);
                if (folder) name += "/";
                fileStatuses = new String[5];
                fileStatuses[0] = name.intern();
                if (file.exists()) {
                    fileStatuses[1] = CURRENT_STATUS;
                    lastFile = file.getAbsolutePath();
                } else {
                    fileStatuses[1] = MISSING_STATUS;
                    lastFile = null;
                }
            }
            if (elements[0].startsWith(ARCHIVE_PATH)) {
                if (file && lastFile != null && fileStatuses != null) {
                    workFilesByNames.put(fileStatuses[0], lastFile);
                    String archive = elements[0].substring(ARCHIVE_PATH.length());
                    archivesByNames.put(fileStatuses[0], archive);
                    fileStatuses[4] = archive;
                }
            }
            if (elements[0].startsWith(ARCHIVE_LOCK_INFO) && fileStatuses != null) {
                String lockInfo = elements[0].substring(ARCHIVE_LOCK_INFO.length());
                int index = lockInfo.indexOf(LOCKS_SEPARATOR);
                int index2 = lockInfo.indexOf(LOCKS_SEPARATOR, index + 1);
                if (index2 < 0) return ;
                String revision = lockInfo.substring(index + LOCKS_SEPARATOR.length(), index2).intern();
                index = index2;
                index2 = lockInfo.indexOf(LOCKS_SEPARATOR, index + 1);
                String locker = lockInfo.substring(index + LOCKS_SEPARATOR.length(), index2).intern();
                fileStatuses[2] = locker;
                fileStatuses[3] = revision;
                fileStatuses[4] = (String) archivesByNames.get(fileStatuses[0]);
            }
        }
        //dataBuffer.append(elements[0]+"\n");
    }

}

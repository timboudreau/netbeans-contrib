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
import java.util.Hashtable;
//import java.util.*;
//import java.beans.*;
//import java.text.*;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.util.*;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;

import org.netbeans.modules.vcs.profiles.list.AbstractListCommand;

/** PVCS list command wrapper
 * 
 * @author Martin Entlicher
 */

public class PvcsListCommand extends AbstractListCommand {
    private Debug E=new Debug("PvcsListComand",true); // NOI18N
    private Debug D=E;

    private String dir=null;
    //private String rootDir=null;
    private String pvcsRoot=null;
    //private String configFile=null;
    //private String cmd=null;
    private StringBuffer dataBuffer=new StringBuffer(4096);
    //private static final String[] archiveFiles = {"*-arc","*.??v"};
    private static final String workFile = "Workfile:";
    private static final String locks = "Locks:";
    private static final String locksSeparator = " : ";
    private static final String missingStatus = "Missing";
    private static final String currentStatus = "Current";

    //-------------------------------------------
    public PvcsListCommand(/*String[] args*/){
        /*
        this.dir=System.getProperty("DIR","");
        this.pvcsRoot=System.getProperty("PVCSROOT","");
        this.rootDir=System.getProperty("ROOTDIR","."/*pvcsRoot+File.separator+workDir*//*);
        pvcsRoot+=File.separator+archiveDir;
        this.configFile=System.getProperty("VCSCONFIG","vcs.cfg");
        if (configFile.indexOf(File.separator) < 0)
        configFile=rootDir+File.separator+configFile;
        this.cmd="vlog -C${CONFIGFILE} ${FILE}";
        */
    }

    public void setFileSystem(VcsFileSystem fileSystem) {
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
        D.deb("rootDir = "+rootDir+", module = "+module+", dir = "+dir); // NOI18N
        pvcsRoot = (String) vars.get("PVCSROOT"); // NOI18N
        if (pvcsRoot == null) {
            pvcsRoot = "."; // NOI18N
        }
        if (dir.equals("")) { // NOI18N
            dir=rootDir;
            if (module != null && module.length() > 0) {
                dir += File.separator + module;
                pvcsRoot += File.separator + module;
            }
        } else {
            if (module == null) {
                pvcsRoot += File.separator + dir;
                dir = rootDir + File.separator + dir;
            } else {
                pvcsRoot += File.separator + module + File.separator + dir;
                dir = rootDir + File.separator + module + File.separator + dir;
            }
        }
        if (dir.charAt(dir.length() - 1) == File.separatorChar)
            dir = dir.substring(0, dir.length() - 1);
        D.deb("dir = "+dir); // NOI18N
        D.deb("pvcsRoot = "+pvcsRoot); // NOI18N
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
        try {
            runCommand(vars, args, false);
        } catch (InterruptedException iexc) {
            return false;
        }
        fillHashtable(filesByName);
        addDirs(filesByName);
        return filesByName.size() > 0 || !shouldFail;
    }

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

    //-------------------------------------------
    /**
     * Add directories from archive with status "Missing" and check for files and directories
     * in the working directory, if they are present, change the status to "Current".
     */
    private void addDirs(Hashtable filesByName) {
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
        d = new File(dir);
        files = d.listFiles();
        if (files != null) {
            String[] fileStatuses = null;
            for(int i=0;i<files.length;i++){
                File file=files[i];
                String fileName = file.getName();
                if (file.isDirectory()) fileName += "/"; // NOI18N
                if ((fileStatuses = (String[]) filesByName.get(fileName)) != null) {
                    fileStatuses[1] = currentStatus;
                    //filesByName.put(fileName, fileStatuses);
                }
            }
        }
    }

    //-------------------------------------------
    public void outputData(String[] elements) {
        D.deb("match("+elements[0]+")"); // NOI18N
        dataBuffer.append(elements[0]+"\n");
    }

}

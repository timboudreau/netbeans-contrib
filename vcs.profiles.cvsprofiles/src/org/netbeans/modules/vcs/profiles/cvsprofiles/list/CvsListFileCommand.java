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

package org.netbeans.modules.vcs.profiles.cvsprofiles.list;

import java.io.*;
import java.util.*;
import java.beans.*;
import java.text.*;

import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.caching.VcsCacheFile;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.util.*;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;

/**
 * List file command for CVS.
 * @author  Martin Entlicher
 */
public class CvsListFileCommand extends Object implements VcsAdditionalCommand, CommandDataOutputListener  {

    //static final String CVS_DIRNAME = "CVS"; // NOI18N
    //static final String[] CVS_DIRCONTENT = {"Entries", "Repository", "Root"}; // NOI18N
    //static final String CVS_FILE_TAG = "Tag"; // NOI18N
    //static final String[] EXAMINING_STRS = {"status: Examining", "server: Examining"}; // NOI18N
    //static final String MATCH_FILE = "File:"; // NOI18N
    //static final String MATCH_STATUS = "Status:"; // NOI18N
    //static final String MATCH_REVISION = "Working revision:"; // NOI18N
    //static final String MATCH_STICKY_TAG = "Sticky Tag:"; // NOI18N
    //static final String MATCH_STICKY_DATE = "Sticky Date:"; // NOI18N
    //static final String MATCH_STICKY_OPTIONS = "Sticky Options:"; // NOI18N
    //static final String STICKY_NONE = "(none)"; // NOI18N
    //static final String STATUS_UNKNOWN = "Unknown"; // NOI18N
    //static final String FILE_SEPARATOR = "===================================="; // NOI18N
    
    //static final String LOG_WORKING_FILE = "Working file:"; // NOI18N
    //static final String LOG_LOCKS = "locks:"; // NOI18N

    private Debug E=new Debug("CvsListCommand",true); // NOI18N
    private Debug D=E;

    private VcsFileSystem fileSystem = null;

    protected CommandOutputListener stdoutNRListener = null;
    protected CommandOutputListener stderrNRListener = null;
    protected CommandDataOutputListener stdoutListener = null;
    protected CommandDataOutputListener stderrListener = null;

    protected String dataRegex = null;
    protected String errorRegex = null;
    protected String input = null;

    protected boolean shouldFail=false;
    
    private String dir=null;

    private Vector files=new Vector(30);

    private StringBuffer dataBuffer=new StringBuffer(4096);

    /** Creates new CvsListCommand */
    public CvsListFileCommand() {
    }

    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }

    /**
     * Initialize <code>DIR</code> variable and copy some values from <code>vars</code>
     * to local variables.
     * @param vars the variables passed from the VCS filesystem
     */
    protected void initVars(Hashtable vars) {
        String dataRegex = (String) vars.get("DATAREGEX");
        if (dataRegex != null) this.dataRegex = dataRegex;
        String errorRegex = (String) vars.get("ERRORREGEX");
        if (errorRegex != null) this.errorRegex = errorRegex;
        D.deb("dataRegex = "+dataRegex+", errorRegex = "+errorRegex);
        this.input = (String) vars.get("INPUT");
        if (this.input == null) this.input = "";
        //this.timeout = ((Long) vars.get("TIMEOUT")).longValue();
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
        D.deb("dir="+dir); // NOI18N
    }

    /*
    private boolean isCVSDir() {
        File d=new File(dir);
        String[] files=d.list();
        if (files != null) {
            for(int i = 0; i < files.length; i++)
                if (files[i].equals(CVS_DIRNAME)) return true;
        }
        shouldFail=true ;
        return false;
    }
     */

    //------------------------------------------
    private boolean furtherExamining(String data, int index) {
        while(Character.isWhitespace(data.charAt(index))) index++;
        return (data.charAt(index) == '.');
    }

    /** Fill the data information into the hash table containing files
     * and their status attributes.
     */
    private void fillHashtableFromStatus(Hashtable filesByName) {
        String data=new String(dataBuffer);
        //String examiningStr = EXAMINING_STRS[0];

        int pos=0;
        /* I expect file listing in the form: File: <filename> Status: <status>
         * There has to be info line about examining directories.
         * I.e. Regex: "^(File:.*Status:.*$)|( *Working revision:.*$)|( *Sticky .*$)|(cvs status.*)|(cvs server.*)"
         */
        int fileIndex;
        /*
        int examIndex = -1;
        for (int i = 0; i < EXAMINING_STRS.length; i++) {
            examIndex = data.indexOf(EXAMINING_STRS[i], pos);
            if (examIndex >= 0) {
                examiningStr = EXAMINING_STRS[i];
                break;
            }
        }
        boolean examining = true;
        if (examIndex < 0) {
            if (!shouldFail) E.err("Warning: No examining info from cvs status command !");
            examining = false;
        } else {
            examining = furtherExamining(data, examIndex += examiningStr.length());
            examIndex = data.indexOf(examiningStr, examIndex);
        }
         */
        fileIndex = data.indexOf(CvsListCommand.MATCH_FILE, pos);
        while(/* examining && */fileIndex >=0 ){
            int statusIndex = data.indexOf(CvsListCommand.MATCH_STATUS, fileIndex);
            int endFileIndex = data.indexOf(CvsListCommand.FILE_SEPARATOR, statusIndex);
            if (endFileIndex < 0) endFileIndex = data.length() - 1;
            fileIndex += CvsListCommand.MATCH_FILE.length();
            String fileName=data.substring(fileIndex,statusIndex).trim();
            int i=-1;
            if ((i=fileName.indexOf("no file")) >=0  ){ // NOI18N
                fileName=fileName.substring(i+7).trim();
            }
            int[] index = new int[] { statusIndex };
            String fileStatus = CvsListCommand.getAttribute(data, CvsListCommand.MATCH_STATUS, index);
            if (fileStatus == null) {
                fileStatus = CvsListCommand.STATUS_UNKNOWN;
            }
            String fileRevision = CvsListCommand.getAttribute(data, CvsListCommand.MATCH_REVISION, index);
            String fileDate = "";
            String fileTime = "";
            if (fileRevision == null) {
                fileRevision = "";
            } else {
                String revInfo = fileRevision;
                int endRevIndex = fileRevision.indexOf(" ");
                int endRevIndex1 = fileRevision.indexOf("\t");
                if (endRevIndex1 >= 0 && endRevIndex1 < endRevIndex) endRevIndex = endRevIndex1;
                if (endRevIndex < 0) endRevIndex = revInfo.length();
                fileRevision = revInfo.substring(0, endRevIndex);
                revInfo = revInfo.substring(endRevIndex).trim();
            }
            String fileStickyTag = CvsListCommand.getAttribute(data, CvsListCommand.MATCH_STICKY_TAG, index);
            if (fileStickyTag == null || index[0] > endFileIndex
                || CvsListCommand.STICKY_NONE.equals(fileStickyTag)) fileStickyTag = "";
            else {
                int spaceIndex = fileStickyTag.indexOf(" ");
                if (spaceIndex > 0) fileStickyTag = fileStickyTag.substring(0, spaceIndex);
            }
            String fileStickyDate = CvsListCommand.getAttribute(data, CvsListCommand.MATCH_STICKY_DATE, index);
            if (fileStickyDate == null || index[0] > endFileIndex
                || CvsListCommand.STICKY_NONE.equals(fileStickyDate)) fileStickyDate = "";
            String fileSticky = (fileStickyTag + " " + fileStickyDate).trim();
            D.deb("fillHashTable: "+"fileName="+fileName+", fileStatus="+fileStatus); // NOI18N
            
            String[] fileStatuses = new String[7];
            fileStatuses[0] = fileName;
            fileStatuses[1] = fileStatus;
            fileStatuses[2] = fileRevision;
            fileStatuses[3] = fileTime;
            fileStatuses[4] = fileDate;
            fileStatuses[5] = fileSticky;
            fileStatuses[6] = "";
            filesByName.put(fileName,fileStatuses);
            //if (stdoutListener != null) stdoutListener.outputData(fileStatuses);
            pos = endFileIndex;
            fileIndex = data.indexOf(CvsListCommand.MATCH_FILE, pos);
            /*
            if (examIndex > 0 && examIndex < fileIndex) {
                examining = furtherExamining(data, examIndex += examiningStr.length());
                examIndex = data.indexOf(examiningStr, examIndex);
            }
             */
        }
    }
    
    /** Fill the data information into the hash table containing files
     * and their status attributes.
     */
    private void fillHashtableFromLog(Hashtable filesByName) {
        String data = new String(dataBuffer);

        int pos=0;
        int fileIndex;
        while ((fileIndex = data.indexOf(CvsListCommand.LOG_WORKING_FILE, pos)) > 0) {
            fileIndex += CvsListCommand.LOG_WORKING_FILE.length();
            int eolIndex = data.indexOf('\n', fileIndex);
            if (eolIndex < 0) break;
            String fileName = data.substring(fileIndex, eolIndex).trim();
            String[] statuses = (String[]) filesByName.get(fileName);
            if (statuses == null) {
                statuses = new String[7];
                statuses[0] = fileName;
                statuses[1] = VcsCacheFile.STATUS_DEAD;
                filesByName.put(fileName, statuses);
            }
            pos = eolIndex;
            String revision = statuses[2];
            if (revision != null && revision.length() > 0) {
                String lockers = "";
                int lockIndex = data.indexOf(CvsListCommand.LOG_LOCKS, pos);
                if (lockIndex > 0) {
                    pos = lockIndex;
                    int lockerIndex;
                    eolIndex = data.indexOf('\n', lockIndex);
                    while(eolIndex > 0 && (lockerIndex = data.indexOf('\t', eolIndex)) == eolIndex + 1) {
                        eolIndex = data.indexOf('\n', lockerIndex);
                        if (eolIndex < 0) break;
                        String locker = data.substring(lockerIndex, eolIndex).trim();
                        int lockedRevisionIndex = locker.indexOf(':');
                        if (lockedRevisionIndex < 0) {
                            lockers += ((lockers.length() > 0) ? ", " : "") + locker;
                        } else {
                            String lockedRevision = locker.substring(lockedRevisionIndex + 1).trim();
                            if (revision.equals(lockedRevision)) {
                                locker = locker.substring(0, lockedRevisionIndex).trim();
                                lockers += ((lockers.length() > 0) ? ", " : "") + locker;
                            }
                        }
                    }
                }
                statuses[6] = lockers;
            }
            //if (stdoutListener != null) stdoutListener.outputData(statuses);
        }
    }
    
    private void postFiles(Hashtable filesByName) {
        for (Iterator elIt = filesByName.values().iterator(); elIt.hasNext(); ) {
            String[] elements = (String[]) elIt.next();
            stdoutListener.outputData(elements);
        }
    }
    
    /**
    * Test if the directory was checked out by CVS or not.
    * @param dir the directory name to test
    * @return <code>true</code> if the directory was created by CVS, <code>false</code> if not.
    *
    private static boolean isCVSDirectory(File dir) {
        File subdir = new File(dir, CVS_DIRNAME);
        if (!subdir.isDirectory()) return false;
        for(int i = 0; i < CVS_DIRCONTENT.length; i++) {
            File cvsFile = new File(subdir, CVS_DIRCONTENT[i]);
            if (!cvsFile.isFile()) return false;
        }
        return true;
    }
     */

    /**
     * Add local directories with no status information.
     * @param filesByName the files container
     *
    private void addLocalFiles(Hashtable filesByName){
        File d=new File(dir);
        String[] files=d.list();
        if (files != null) {
            for(int i=0;i<files.length;i++){
                String[] fileStatuses = new String[7];
                fileStatuses[1] = ""; // NOI18N
                String fileName=files[i];
                //D.deb("fileName="+fileName);

                File dirfile = new File(d+File.separator+fileName);
                if( dirfile.isDirectory() ){
                    fileName+="/"; // NOI18N
                } else continue;
                if( fileName.equals(CVS_DIRNAME+"/") ){ // NOI18N
                    continue;
                }

                if (!isCVSDirectory(dirfile)) continue;
                if (filesByName.get(fileName) == null) {
                    D.deb("adding "+fileName);
                    //System.out.println("addLocalFiles(): '"+fileName+"'");
                    fileStatuses[0] = fileName;
                    fileStatuses[5] = CvsListCommand.findStickyOfDir(dirfile);
                    filesByName.put(fileName, fileStatuses);
                    if (stdoutListener != null) stdoutListener.outputData(fileStatuses);
                }
            }
        }
    }
     */

    /**
     * Run the LIST command given by its name.
     * @param vars the variables passed from the VCS filesystem
     * @param cmdName the LIST command to execute
     * @param addErrOut whether to add error output to the output listener
     */
    protected void runCommand(Hashtable vars, String cmdName, boolean addErrOut) throws InterruptedException {
        runCommand(vars, cmdName, this, (addErrOut) ? this : null);
    }

    /**
     * Run the LIST command given by its name.
     * @param vars the variables passed from the VCS filesystem
     * @param cmdName the LIST command to execute
     * @param addErrOut whether to add error output to the output listener
     */
    protected void runCommand(Hashtable vars, String cmdName, CommandDataOutputListener dataOutputListener,
                              CommandDataOutputListener errorOutputListener) throws InterruptedException {
        VcsCommand cmd = fileSystem.getCommand(cmdName);
        if (cmd == null) return ;
        // The user should be warned by the wrapper class and not the command itself.
        cmd.setProperty(VcsCommand.PROPERTY_IGNORE_FAIL, new Boolean(true));
        VcsCommandExecutor ec = fileSystem.getVcsFactory().getCommandExecutor(cmd, vars);
        if (dataOutputListener != null) ec.addDataOutputListener(dataOutputListener);
        if (errorOutputListener != null) ec.addDataErrorOutputListener(errorOutputListener);
        ec.addOutputListener(stdoutNRListener);
        ec.addErrorOutputListener(stderrNRListener);
        fileSystem.getCommandsPool().startExecutor(ec, fileSystem);
        try {
            fileSystem.getCommandsPool().waitToFinish(ec);
        } catch (InterruptedException iexc) {
            fileSystem.getCommandsPool().kill(ec);
            throw iexc;
        }
        if (ec.getExitStatus() != VcsCommandExecutor.SUCCEEDED) {
            //E.err("exec failed "+ec.getExitStatus());
            shouldFail=true;
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

        this.stdoutNRListener = stdoutListener;
        this.stderrNRListener = stderrListener;
        this.stdoutListener = stdoutDataListener;
        this.stderrListener = stderrDataListener;
        this.dataRegex = dataRegex;
        this.errorRegex = errorRegex;
        if (stdoutListener == null) return true; // No one listen to me !
        initVars(vars);
        initDir(vars);
        if (args.length < 2) {
            stderrNRListener.outputLine("Expecting two commands as arguments!"); //NOI18N
            return false;
        }
        String statusCmd = args[0];
        String logCmd = args[1];
        try {
            runCommand(vars, statusCmd, true);
        } catch (InterruptedException iexc) {
            return false;
        }
        Hashtable filesByName = new Hashtable();
        /*if (!shouldFail)*/ fillHashtableFromStatus(filesByName);
        String showDeadFiles = (String) vars.get(Variables.SHOW_DEAD_FILES);
        if (!shouldFail && showDeadFiles != null && showDeadFiles.trim().length() > 0) {
            dataBuffer.delete(0, dataBuffer.length());
            try {
                runCommand(vars, logCmd, true);
            } catch (InterruptedException iexc) {
                return false;
            }
            fillHashtableFromLog(filesByName);
        }
        postFiles(filesByName);
        //addLocalFiles(filesByName);
        return !shouldFail;
    }

    public void outputData(String[] elements) {
        dataBuffer.append(elements[0]+"\n");
    }
    
}

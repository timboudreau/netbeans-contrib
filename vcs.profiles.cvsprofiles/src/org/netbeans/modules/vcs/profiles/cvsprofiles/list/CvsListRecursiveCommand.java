/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.profiles.cvsprofiles.list;

import java.io.*;
import java.util.*;

import org.openide.util.RequestProcessor;

import org.apache.regexp.*;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.VcsDirContainer;
import org.netbeans.modules.vcscore.caching.VcsCacheFile;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.cmdline.VcsListRecursiveCommand;
import org.netbeans.modules.vcscore.cmdline.UserCommand;
import org.netbeans.modules.vcscore.util.*;

/**
 * This class performes the recursive refresh of a directory tree by execution
 * only one CVS command recursively.
 *
 * @author  Martin Entlicher
 */
public class CvsListRecursiveCommand extends VcsListRecursiveCommand {//implements CommandDataOutputListener {

    private Debug E=new Debug("CvsListRecursiveCommand",true); // NOI18N
    private Debug D=E;

    private static final String ATTIC = "Attic"; // NOI18N
    private static final String[] EMPTY_DIR = {""}; // NOI18N
    private static final String MATCH_REPOSITORY_REVISION = "Repository revision:"; // NOI18N
    private static final String REPOSITORY_PATH = CvsListCommand.CVS_DIRNAME + File.separator
                                                  + "Repository"; // NOI18N
    private static final String[] ABSOLUTE_REPOSITORY_REGEXS = { "^/.*$", "^[a-zA-Z]:\\.*" };

    private String rootDir = null;
    private String dir = null; // The local dir being refreshed.
    private String dirPath = null;
    //private String cmd = null;
    private String cvsRoot = null;
    private String cvsRepository = null;
    private String relMount = null;

    private boolean shouldFail = false;

    private StringBuffer statusDataBuffer=new StringBuffer(4096);
    private StringBuffer logDataBuffer=new StringBuffer(4096);
    private CommandOutputListener stdoutNRListener = null;
    private CommandOutputListener stderrNRListener = null;
    private CommandDataOutputListener stdoutListener = null;
    private CommandDataOutputListener stderrListener = null;

    private String dataRegex = null;
    private String errorRegex = null;
    //private String input = null;
    private Vector examiningPaths = new Vector();
    private Hashtable workReposPaths = new Hashtable();
    private int fsRootPathLength = 0;
    private String lastPathConverted = null;
    private boolean lastPathFileDependent = false;
    private ArrayList lastWorkingPaths = null;
    private HashMap unknownPathFiles = new HashMap();
    private VcsFileSystem fileSystem = null;
    
    /** Creates new CvsListRecursiveCommand */
    public CvsListRecursiveCommand() {
    }

    private void initVars(Hashtable vars/*, String[] args*/) {
        //this.cmd = VcsUtilities.array2string(args);

        this.rootDir = (String) vars.get("ROOTDIR"); // NOI18N
        if (this.rootDir == null) {
            this.rootDir = "."; // NOI18N
            //vars.put("ROOTDIR","."); // NOI18N
        }
        this.cvsRepository = (String) vars.get("CVS_REPOSITORY");
        if (this.cvsRepository == null) {
            this.cvsRepository = "";
        }
        //cvsRepository = cvsRepository.replace('\\', '/');
        this.dir = (String) vars.get("DIR"); // NOI18N
        if (this.dir == null) {
            this.dir = ""; // NOI18N
            //vars.put("DIR","."); // NOI18N
        }
        String commonParent = (String) vars.get("COMMON_PARENT");
        if (commonParent != null && commonParent.length() > 0) {
            this.dirPath = new String(commonParent.replace(java.io.File.separatorChar, '/') + "/" + this.dir.replace(java.io.File.separatorChar, '/'));
        } else {
            this.dirPath = new String(dir.replace(java.io.File.separatorChar, '/')); // I have to be sure that I make new object
        }
        String module = (String) vars.get("MODULE"); // NOI18N
        String wholeModule = module;
        if (module != null && commonParent != null && module.endsWith(commonParent)) {
            module = module.substring(0, module.length() - commonParent.length());
            while (module.endsWith("/") || module.endsWith(java.io.File.separator)) {
                module = module.substring(0, module.length() - 1);
            }
        }
        D.deb("rootDir = "+rootDir+", module = "+module+", dir = "+dir); // NOI18N
        if (dir.equals("")) { // NOI18N
            dir=rootDir;
            this.fsRootPathLength = rootDir.length();
            if (wholeModule != null && wholeModule.length() > 0) {
                dir += File.separator + wholeModule;
            }
            if (module != null && module.length() > 0) {
                this.fsRootPathLength += (File.separator + module).length();
            }
        } else {
            if (wholeModule == null || wholeModule.length() == 0) {
                dir=rootDir+File.separator+dir;
            } else {
                dir=rootDir+File.separator+wholeModule+File.separator+dir;
            }
            if (module == null || module.length() == 0) {
                this.fsRootPathLength = rootDir.length();
            } else {
                this.fsRootPathLength = (rootDir+File.separator+module).length();
            }
        }
        this.fsRootPathLength++;
        if (module.length() > 0) this.relMount = "/"+module.replace('\\', '/');
        else relMount = "";
        if (dir.charAt(dir.length() - 1) == File.separatorChar)
            dir = dir.substring(0, dir.length() - 1);
        D.deb("dir="+dir); // NOI18N

        String dataRegex = (String) vars.get("DATAREGEX"); // NOI18N
        if (dataRegex != null) this.dataRegex = dataRegex;
        String errorRegex = (String) vars.get("ERRORREGEX"); // NOI18N
        if (errorRegex != null) this.errorRegex = errorRegex;
        D.deb("dataRegex = "+dataRegex+", errorRegex = "+errorRegex); // NOI18N
        //this.input = (String) vars.get("INPUT"); // NOI18N
        //if (this.input == null) this.input = "Cancel/n"; // NOI18N
    }

    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
    
    private boolean isAbsoluteRepository(String line) {
        try {
            for (int i = 0; i < ABSOLUTE_REPOSITORY_REGEXS.length; i++) {
                RE pattern = new RE(ABSOLUTE_REPOSITORY_REGEXS[i]);
                if (pattern.match(line)) return true;
            }
        } catch (RESyntaxException exc) {
            org.openide.ErrorManager.getDefault().notify(exc);
        }
        return false;
    }

    private void addRepositoryPath(String localDir, File repository) {
        FileInputStream in = null;
        try {
            in = new FileInputStream(repository);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line = reader.readLine();
            if (line != null && line.length() > 0) {
                String localPath = "";
                if (localDir.length() > fsRootPathLength) localPath = localDir.substring(fsRootPathLength);
                //int index = line.indexOf(cvsRepository);
                if (!isAbsoluteRepository(line)) {
                    line = cvsRepository + "/" + line; // Get the full path to the repository
                }
                if (line.endsWith(".")) line = line.substring(0, line.length() - 1);
                if (line.endsWith(File.separator)) line = line.substring(0, line.length() - 1);
                workReposPaths.put(localPath.replace(File.separatorChar, '/'), line);
                //System.out.println("for localDir = "+localDir+"\n  put("+localPath+", "+line+")");
            }
        } catch (FileNotFoundException fnfexc) {
            // Ignored
        } catch (IOException ioexc) {
            // Ignored
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException exc) {
                    // Ignored
                }
            }
        }
    }
    
    private void getRepositoryPaths(File dir) {
        File repository = new File(dir, REPOSITORY_PATH);
        addRepositoryPath(dir.getAbsolutePath(), repository);
        File[] subDirs = dir.listFiles(new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory();
            }
        });
        if (subDirs == null) return ;
        for (int i = 0; i < subDirs.length; i++) {
            getRepositoryPaths(subDirs[i]);
        }
    }
    
    //-----------------------------------
    private VcsCommandExecutor runStatusCommand(Hashtable vars, String cmdName) {
        //String prepared = Variables.expand(vars, cmd, true);

        //D.deb("prepared = "+prepared); // NOI18N
        //D.deb("DIR = '"+(String) vars.get("DIR")+"'"+", dir = '"+this.dir+"'"); // NOI18N
        VcsCommand cmd = fileSystem.getCommand(cmdName);
        /*
        UserCommand cmd = new UserCommand();
        cmd.setName("LIST_SUB_CMD");
        cmd.setProperty(VcsCommand.PROPERTY_EXEC, prepared);
        cmd.setProperty(UserCommand.PROPERTY_DATA_REGEX, dataRegex);
        cmd.setProperty(UserCommand.PROPERTY_ERROR_REGEX, errorRegex);
        // The user should be warned by the wrapper class and not the command itself.
        cmd.setProperty(VcsCommand.PROPERTY_IGNORE_FAIL, Boolean.TRUE);
         */
        VcsCommandExecutor ec = fileSystem.getVcsFactory().getCommandExecutor(cmd, vars);
        ec.addDataOutputListener(new CommandDataOutputListener() {
            public void outputData(String[] elements) {
                statusDataBuffer.append(elements[0]+"\n"); // NOI18N
            }
        });
        ec.addDataErrorOutputListener(new CommandDataOutputListener() {
                                          public void outputData(String[] elements) {
                                              if (elements[0] == null || elements[0].length() == 0) return;
                                              int index = -1;
                                              for(int i = 0; i < CvsListCommand.EXAMINING_STRS.length; i++) {
                                                  //D.deb("Comparing elements[0] = "+elements[0]+" to examining = "+examiningStrs[i]);
                                                  index = elements[0].indexOf(CvsListCommand.EXAMINING_STRS[i]);
                                                  if (index >= 0) {
                                                      index += CvsListCommand.EXAMINING_STRS[i].length();
                                                      break;
                                                  }
                                                  D.deb("Comp. unsuccessfull");
                                              }
                                              if (index >= 0) {
                                                  while (index < elements[0].length() && Character.isWhitespace(elements[0].charAt(index))) index++;
                                                  String path = elements[0].substring(index);
                                                  if (path.equals(".")) path = "";
                                                  D.deb("Got examining: "+path);
                                                  examiningPaths.add(path);
                                              }
                                          }
        });
        //ec.addOutputListener(stdoutNRListener);
        ec.addErrorOutputListener(stderrNRListener);
        fileSystem.getCommandsPool().startExecutor(ec, fileSystem);
        return ec;
        /*
        fileSystem.getCommandsPool().waitToFinish(ec);
        if (ec.getExitStatus() != VcsCommandExecutor.SUCCEEDED) {
            //E.err("exec failed "+ec.getExitStatus());
            shouldFail = true;
        }
         */
    }

    private VcsCommandExecutor runLogCommand(Hashtable vars, String cmdName) {
        VcsCommand cmd = fileSystem.getCommand(cmdName);
        VcsCommandExecutor ec = fileSystem.getVcsFactory().getCommandExecutor(cmd, vars);
        ec.addDataOutputListener(new CommandDataOutputListener() {
            public void outputData(String[] elements) {
                logDataBuffer.append(elements[0]+"\n"); // NOI18N
            }
        });
        ec.addErrorOutputListener(stderrNRListener);
        fileSystem.getCommandsPool().startExecutor(ec, fileSystem);
        return ec;
    }
    
    /**
     * Get the path of file from the output information at given index.
     * @param data the output data
     * @param index the index to the file information
     */
    private String[] getFilePaths(String data, int index, String fileName) {
        //D.deb("getFilePath("+data.substring(index, Math.min(index + 100, data.length()))+", "+index+", "+fileName+")");
        int begin = index;
        while(Character.isWhitespace(data.charAt(begin))) begin++; // skip the space
        while(!Character.isWhitespace(data.charAt(begin))) begin++; // skip the revision number
        while(Character.isWhitespace(data.charAt(begin))) begin++; // skip the space
        int end = data.indexOf('\n', begin);
        //D.deb("end = "+end);
        if (end < 0) return null;
        String path = data.substring(begin, end);
        //D.deb("getFilePath(): path = "+path);
        int nameIndex = path.lastIndexOf('/');
        //D.deb("nameIndex = "+nameIndex);
        if (nameIndex < 0) return null;
        if (nameIndex == 0) return (cvsRepository.length() > 0) ? null : EMPTY_DIR;
        path = path.substring(0, nameIndex); //.replace('\\', '/'); // Because of Windoze unexpectable behavior
        if (path.endsWith(ATTIC)) path = path.substring(0, path.length() - ATTIC.length() - 1);
        index = path.indexOf(cvsRepository/*+relMount*/);
        //D.deb("path = "+path+", path.indexOf("+cvsRepository+") = "+index);
        if (index < 0) return null;
        //D.deb("getFilePath(): path = "+path+", index = "+index+", cvsRepository = "+cvsRepository);
        if (path.length() <= cvsRepository.length())
            return EMPTY_DIR;
        else {
            //path = path.substring(index + cvsRepository.length() + 1);
            ArrayList myWorkings = new ArrayList();
            Iterator keysIt = workReposPaths.keySet().iterator();
            while (keysIt.hasNext()) {
                String workPath = (String) keysIt.next();
                String repPath = (String) workReposPaths.get(workPath);
                if (path.equals(repPath)) {
                    myWorkings.add(workPath);
                }
            }
            return (String[]) myWorkings.toArray(new String[0]);
        }
    }

    
    /**
     * Get files and their statuses from the command output.
     * @param filesByNameCont the container of files.
     */
    private void fillHashtableFromStatus(VcsDirContainer filesByNameCont) {
        String data=new String(statusDataBuffer);
        Hashtable filesByName = new Hashtable();
        VcsDirContainer filesByNameContPath = filesByNameCont;
        String last_filePath = filesByNameContPath.getPath();
        //D.deb("fillHashtable(): last_filePath = '"+last_filePath+"'");
        int pos=0;
        /* I expect file listing in the form: File: <filename> Status: <status>
         * Followed by Repository Revision: <revision path>
         * I suppose that revision path is the same as the working path.
         * (Regex ^(File:.*Status:.*$)|(Repository Revision.*)) 
         */
        filesByNameCont.setPath(dirPath);
        filesByNameCont.setElement(filesByName);
        //D.deb("At the beginning have dirPath = "+dirPath);
        while(pos < data.length()) {
            //int examIndex = getExaminingInfo(data, pos);
            int fileIndex = data.indexOf(CvsListCommand.MATCH_FILE, pos);
            int statusIndex = data.indexOf(CvsListCommand.MATCH_STATUS, pos);
            if (fileIndex < 0 || statusIndex < 0) {
                pos = data.length();
                continue;
            }
            int endFileIndex = data.indexOf(CvsListCommand.FILE_SEPARATOR, statusIndex);
            if (endFileIndex < 0) endFileIndex = data.length() - 1;
            int nextIndex=data.indexOf("\n",statusIndex); // NOI18N
            if (nextIndex < 0) {
                nextIndex = data.length()-1;
            }
            //D.deb("fillHashtable: fileIndex = "+fileIndex+", statusIndex = "+statusIndex); // NOI18N
            fileIndex += CvsListCommand.MATCH_FILE.length();
            String fileName=data.substring(fileIndex,statusIndex).trim();
            int i=-1;
            if( (i=fileName.indexOf("no file")) >=0  ){ // NOI18N
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
            int repositoryIndex = data.indexOf(MATCH_REPOSITORY_REVISION, statusIndex);
            if (repositoryIndex < 0) {
                pos = data.length();
                continue;
            }
            repositoryIndex += MATCH_REPOSITORY_REVISION.length();
            String[] filePaths = getFilePaths(data, repositoryIndex, fileName);
            //D.deb("fillHashtable(): have filePaths = "+VcsUtilities.arrayToString(filePaths));
            //System.out.println("fillHashtable(): have filePaths = "+VcsUtilities.arrayToString(filePaths));
            if (filePaths != null && (filePaths.length > 1 || (filePaths.length == 1 && !filePaths[0].equals(last_filePath)))) {
                int len = filePaths.length;
                for(int j = 0; j < len; j++) {
                    VcsDirContainer parent = filesByNameCont.getParent(filePaths[j]);
                    if (parent != null) filesByNameContPath = parent.addSubdir(filePaths[j]);
                    else filesByNameContPath = filesByNameCont.addSubdirRecursive(filePaths[j]);
                    if (filesByNameContPath == null) continue;
                    //D.deb("parent = "+parent+((parent == null) ? "" : " path = "+parent.getPath()));
                    addDirName(filePaths[j], filesByNameCont);
                    filesByName = (Hashtable) filesByNameContPath.getElement();
                    if (filesByName == null) {
                        filesByName = new Hashtable();
                        filesByNameContPath.setElement(filesByName);
                    }
                    //D.deb("created new Container with path: "+filePaths[j]);
                    //System.out.println("created new Container with path: "+filePaths[j]);
                    String[] fileStatuses = new String[7];
                    fileStatuses[0] = fileName;
                    fileStatuses[1] = fileStatus;
                    fileStatuses[2] = fileRevision;
                    fileStatuses[3] = fileTime;
                    fileStatuses[4] = fileDate;
                    fileStatuses[5] = fileSticky;
                    fileStatuses[6] = ""; // the locker will be filled in fillHashtableFromLog()
                    filesByName.put(fileName, fileStatuses);
                    if (stdoutListener != null) stdoutListener.outputData(fileStatuses);
                }
                if (len == 1) last_filePath = filePaths[0];
            } else if (filePaths != null) {
                String[] fileStatuses = new String[7];
                fileStatuses[0] = fileName;
                fileStatuses[1] = fileStatus;
                fileStatuses[2] = fileRevision;
                fileStatuses[3] = fileTime;
                fileStatuses[4] = fileDate;
                fileStatuses[5] = fileSticky;
                fileStatuses[6] = ""; // the locker will be filled in fillHashtableFromLog()
                //System.out.println("put("+fileName+", "+fileStatus+")");
                filesByName.put(fileName, fileStatuses);
                if (stdoutListener != null) stdoutListener.outputData(fileStatuses);
            } else { // the file path was not found (e.g. Locally Added)
                String[] fileStatuses = new String[7];
                fileStatuses[0] = fileName;
                fileStatuses[1] = fileStatus;
                fileStatuses[2] = fileRevision;
                fileStatuses[3] = fileTime;
                fileStatuses[4] = fileDate;
                fileStatuses[5] = fileSticky;
                fileStatuses[6] = ""; // the locker will be filled in fillHashtableFromLog()
                HashSet unknownFiles = (HashSet) unknownPathFiles.get(fileName);
                if (unknownFiles == null) {
                    unknownFiles = new HashSet();
                    unknownPathFiles.put(fileName, unknownFiles);
                }
                unknownFiles.add(fileStatuses);
                //System.out.println("UnknownFile: "+VcsUtilities.arrayToQuotedStrings(fileStatuses));
            }
            pos = repositoryIndex;
        }
    }

    /**
     * Get files and their statuses from the log command output.
     * @param filesByNameCont the container of files.
     */
    private void fillHashtableFromLog(VcsDirContainer filesByNameCont) {
        String data = new String(logDataBuffer);
        Hashtable filesByName = new Hashtable();
        int pos=0;
        String last_filePath = "";
        VcsDirContainer filesByNameContPath = filesByNameCont;
        while(pos < data.length()) {
            //int examIndex = getExaminingInfo(data, pos);
            int fileIndex = data.indexOf(CvsListCommand.LOG_WORKING_FILE, pos);
            if (fileIndex < 0) {
                pos = data.length();
                continue;
            }
            fileIndex += CvsListCommand.LOG_WORKING_FILE.length();
            int eolIndex = data.indexOf('\n', fileIndex);
            if (eolIndex < 0) break;
            String fileFullPath = data.substring(fileIndex, eolIndex).trim();
            String filePath;
            String fileName;
            int index = fileFullPath.lastIndexOf('/');
            if (index < 0) {
                filePath = "";
                fileName = fileFullPath;
            } else {
                filePath = fileFullPath.substring(0, index);
                fileName = fileFullPath.substring(index + 1);
            }
            //StringTokenizer pathTokens = new StringTokenizer(fileName, "/");
            //String[] filePaths = new String[pathTokens.countTokens() - 1];
            //for (int i = 0; i < filePaths.length && pathTokens.hasMoreTokens(); i++) {
            //    filePaths[i] = pathTokens.nextToken();
            //}
            //String fileName = pathTokens.nextToken();
            if (!filePath.equals(last_filePath)) {//filePaths.length > 1 || (filePaths.length == 1 && !filePaths[0].equals(last_filePath))) {
                //int len = filePaths.length;
                //for(int j = 0; j < len; j++) {
                VcsDirContainer parent = filesByNameCont.getParent(filePath);
                if (parent != null) filesByNameContPath = parent.addSubdir(filePath);
                else filesByNameContPath = filesByNameCont.addSubdirRecursive(filePath);
                if (filesByNameContPath == null) continue;
                //D.deb("parent = "+parent+((parent == null) ? "" : " path = "+parent.getPath()));
                addDirName(filePath, filesByNameCont);
                filesByName = (Hashtable) filesByNameContPath.getElement();
                if (filesByName == null) {
                    filesByName = new Hashtable();
                    filesByNameContPath.setElement(filesByName);
                }
            }
            //D.deb("created new Container with path: "+filePaths[j]);
            //System.out.println("created new Container with path: "+filePaths[j]);
            String[] fileStatuses = (String[]) filesByName.get(fileName);
            if (fileStatuses == null) {
                fileStatuses = new String[7];
                fileStatuses[0] = fileName;
                fileStatuses[1] = VcsCacheFile.STATUS_DEAD;
                filesByName.put(fileName, fileStatuses);
            }
            pos = eolIndex;
            String revision = fileStatuses[2];
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
                fileStatuses[6] = lockers;
            }
            if (stdoutListener != null) stdoutListener.outputData(fileStatuses);
        }
    }
    
    /**
     * Add the directory name to the proper container. Process the directory path recursively if necessary.
     * @param filePath the directory full path
     */
    private void addDirName(String filePath, VcsDirContainer filesByNameCont) {
        //D.deb("addDirName("+filePath+", "+filesByNameCont+"), filesByNameCont.path = "+filesByNameCont.getPath());
        if (filePath.length() == 0) return;
        String[] fileStatuses = new String[7];
        String dirName = VcsUtilities.getFileNamePart(filePath) + "/";
        String dirPath = VcsUtilities.getDirNamePart(filePath);
        //D.deb("dirName = "+dirName+", dirPath = "+dirPath);
        fileStatuses[0] = dirName;
        fileStatuses[1] = "";
        fileStatuses[5] = CvsListCommand.findStickyOfDir(fileSystem.getFile(/*new File(*/filePath/*.replace('/', File.separatorChar)*/));
        VcsDirContainer dirParent = filesByNameCont.getContainerWithPath(dirPath);
        //D.deb("parent = "+dirParent+", path = "+((dirParent != null) ? dirParent.getPath() : null));
        if (dirParent == null /*|| dirParent == filesByNameCont*/) {
            // parent is somehere out => don't care about this case
            return;
        } else {
            Hashtable filesByName = (Hashtable) dirParent.getElement();
            //D.deb("Adding dir '"+dirName+"' to container with path "+dirParent.getPath());
            if (filesByName == null) {
                filesByName = new Hashtable();
                dirParent.setElement(filesByName);
            } else {
                if (filesByName.get(dirName) != null) return; // the directory is already there
            }
            if (stdoutListener != null) stdoutListener.outputData(fileStatuses);
            filesByName.put(dirName, fileStatuses);
            if (dirParent == filesByNameCont) return;
            addDirName(dirPath, filesByNameCont); // We have to ensure that all subdirectories are there
        }
    }

    private void addLocalFolders(VcsDirContainer filesByNameCont) {
        Iterator keysIt = workReposPaths.keySet().iterator();
        //System.out.println("addLocalFolders("+filesByNameCont.getPath()+")");
        String rootPath = filesByNameCont.getPath();
        int rootPathLength = rootPath.length();
        while (keysIt.hasNext()) {
            String path = (String) keysIt.next();
            if (path.length() < rootPathLength) continue;
            VcsDirContainer filesByName = filesByNameCont.addSubdirRecursive(path);
            VcsDirContainer parent = filesByNameCont.getParent(path);
            if (filesByName != null && path.length() > rootPathLength) {
                //VcsDirContainer parent = filesByName.getParent();
                //System.out.println("addDirName("+path+", "+parent+"("+((parent != null) ? parent.getPath() : "")+"))");
                if (parent != null) addDirName(path, parent);
            }
            if (unknownPathFiles.size() > 0) {
                Hashtable knownFiles = (Hashtable) filesByName.getElement();
                if (knownFiles == null) {
                    knownFiles = new Hashtable();
                    filesByName.setElement(knownFiles);
                }
                File localDir = fileSystem.getFile(path);
                String[] files = localDir.list();
                if (files != null) {
                    HashMap entriesByFiles = null;
                    for (int i = 0; i < files.length && unknownPathFiles.size() > 0; i++) {
                        HashSet unknownFiles = (HashSet) unknownPathFiles.get(files[i]);
                        if (unknownFiles != null) {
                            if (knownFiles.get(files[i]) != null) {
                                // It's already known. The unknown file might be in a different folder
                                continue;
                            }
                            //System.out.println("file: "+files[i]+", unknownFiles.size() = "+unknownFiles.size());
                            if (unknownFiles.size() == 1) { // cool, we've just one file there
                                String[] fileStatuses = (String[]) unknownFiles.iterator().next();
                                knownFiles.put(files[i], fileStatuses);
                                unknownPathFiles.remove(files[i]);
                                if (stdoutListener != null) stdoutListener.outputData(fileStatuses);
                            } else {
                                // We have more candidates for this position.
                                if (entriesByFiles == null) {
                                    entriesByFiles = CvsListOffline.createEntriesByFiles(
                                        CvsListOffline.loadEntries(new File(localDir, "/CVS/Entries")));
                                    //System.out.println("  entriesByFiles("+localDir+", "+"/CVS/Entries"+") = "+entriesByFiles);
                                }
                                String entry = (String) entriesByFiles.get(files[i]);
                                if (entry == null) {
                                    // The unknown file does not belong here!
                                    continue;
                                }
                                String[] entryItems = CvsListOffline.parseEntry(entry);
                                String sticky = "";
                                if (entryItems.length > 4) {
                                    sticky = entryItems[4];
                                    if (sticky.length() > 0) sticky = sticky.substring(1, sticky.length());
                                }
                                for (Iterator it = unknownFiles.iterator(); it.hasNext(); ) {
                                    String[] fileStatuses = (String[]) it.next();
                                    if ("Locally Added".equals(fileStatuses[1]) &&
                                        sticky.equals(fileStatuses[5])) {
                                        unknownFiles.remove(fileStatuses);
                                        knownFiles.put(files[i], fileStatuses);
                                        if (stdoutListener != null) stdoutListener.outputData(fileStatuses);
                                        break;
                                    }
                                }
                            }
                    }
                    }
                }
            }
        }
    }
    
    /**
     * List files of CVS Repository recursively.
     * @param vars Variables used by the command
     * @param args Command-line arguments
     * @param filesByNameCont listing of files with statuses. For each directory there is a <code>Hashtable</code>
     *                        with files.
     * @param stdoutNRListener listener of the standard output of the command
     * @param stderrNRListener listener of the error output of the command
     * @param stdoutListener listener of the standard output of the command which
     *                       satisfies regex <CODE>dataRegex</CODE>
     * @param dataRegex the regular expression for parsing the standard output
     * @param stderrListener listener of the error output of the command which
     *                       satisfies regex <CODE>errorRegex</CODE>
     * @param errorRegex the regular expression for parsing the error output
     */
    public boolean listRecursively(Hashtable vars, String[] args, VcsDirContainer filesByNameCont,
                                   CommandOutputListener stdoutNRListener,
                                   CommandOutputListener stderrNRListener,
                                   CommandDataOutputListener stdoutListener, String dataRegex,
                                   CommandDataOutputListener stderrListener, String errorRegex) {

        this.stdoutNRListener = stdoutNRListener;
        this.stderrNRListener = stderrNRListener;
        this.stdoutListener = stdoutListener;
        this.stderrListener = stderrListener;
        this.dataRegex = dataRegex;
        this.errorRegex = errorRegex;
        if (args.length < 2) {
            stderrNRListener.outputLine("Expecting two commands as arguments!"); //NOI18N
            return false;
        }
        String statusCmd = args[0];
        String logCmd = args[1];
        initVars(vars);//, args);
        /*
        this.filesByNameCont = filesByNameCont;
        this.filesByNameContPath = filesByNameCont;
        this.filesByName = new Hashtable();
        */
        //getModulesPaths(vars);
        //Thread reposPathThread = new Thread("CVS_LIST_SUB_Repositories_Retrieval") { // NOI18N
        RequestProcessor.Task reposPathTask = RequestProcessor.getDefault().post(new Runnable() {
            public void run() {
                getRepositoryPaths(new File(dir));
            }
        });
        //reposPathThread.start();
        VcsCommandExecutor statusExecutor = runStatusCommand(vars, statusCmd);
        VcsCommandExecutor logExecutor = null;
        String showDeadFilesValue = (String) vars.get(Variables.SHOW_DEAD_FILES);
        boolean showDeadFiles = (showDeadFilesValue != null) && (showDeadFilesValue.length() > 0);
        boolean interrupted = false;
        if (showDeadFiles) {
            logExecutor = runLogCommand(vars, logCmd);
            try {
                fileSystem.getCommandsPool().waitToFinish(logExecutor);
            } catch (InterruptedException iexc) {
                fileSystem.getCommandsPool().kill(logExecutor);
                interrupted = true;
            }
        }
        try {
            fileSystem.getCommandsPool().waitToFinish(statusExecutor);
        } catch (InterruptedException iexc) {
            fileSystem.getCommandsPool().kill(statusExecutor);
            interrupted = true;
        }
        if (interrupted) {
            shouldFail = true;
        } else if (statusExecutor.getExitStatus() != VcsCommandExecutor.SUCCEEDED ||
            (logExecutor != null && logExecutor.getExitStatus() != VcsCommandExecutor.SUCCEEDED)) {
            //E.err("exec failed "+ec.getExitStatus());
            shouldFail = true;
        }

        if (!interrupted) {
            //try {
                reposPathTask.waitFinished(); // THE PROCESS CAN NOT BE KILLED HERE!
                //reposPathThread.join();
            //} catch (InterruptedException intrexc) {
                // Ignore, what can I do.
            //}
        } else {
            reposPathTask.cancel();
            //reposPathThread.interrupt();
        }
        /*if (!shouldFail)*/ fillHashtableFromStatus(filesByNameCont);
        fillHashtableFromLog(filesByNameCont);
        addLocalFolders(filesByNameCont);
        //addLocalFiles(dir, filesByNameCont);
        return !shouldFail;
    }

}

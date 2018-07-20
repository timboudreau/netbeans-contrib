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

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.netbeans.modules.vcs.profiles.cvsprofiles.commands.Validators;
import org.openide.ErrorManager;

import org.openide.util.RequestProcessor;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.VcsDirContainer;
import org.netbeans.modules.vcscore.turbo.Statuses;
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
public class CvsListRecursiveCommand extends VcsListRecursiveCommand implements CommandDataOutputListener {

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

    private CommandOutputListener stdoutNRListener = null;
    private CommandOutputListener stderrNRListener = null;
    private CommandDataOutputListener stdoutListener = null;
    private CommandDataOutputListener stderrListener = null;

    private String dataRegex = null;
    private String errorRegex = null;
    private Hashtable workReposPaths = new Hashtable();
    private int fsRootPathLength = 0;
    private String lastPathConverted = null;
    private boolean lastPathFileDependent = false;
    private ArrayList lastWorkingPaths = null;
    private HashMap unknownPathFiles = new HashMap();
    private VcsFileSystem fileSystem = null;
    private Pattern[] absoluteRepositoryRegexs;
    /** The container of all files by their names */
    private VcsDirContainer filesByNameCont;
    
    /** Creates new CvsListRecursiveCommand */
    public CvsListRecursiveCommand() {
        absoluteRepositoryRegexs = new Pattern[ABSOLUTE_REPOSITORY_REGEXS.length];
        try {
            for (int i = 0; i < absoluteRepositoryRegexs.length; i++) {
                absoluteRepositoryRegexs[i] = Pattern.compile(ABSOLUTE_REPOSITORY_REGEXS[i]);
            }
        } catch (PatternSyntaxException exc) {
            org.openide.ErrorManager.getDefault().notify(exc);
            absoluteRepositoryRegexs = new Pattern[0];
        }
    }

    private void initVars(Hashtable vars) {
        this.rootDir = (String) vars.get("ROOTDIR"); // NOI18N
        if (this.rootDir == null) {
            this.rootDir = "."; // NOI18N
        }
        this.cvsRepository = (String) vars.get("CVS_REPOSITORY");
        if (this.cvsRepository == null) {
            this.cvsRepository = "";
        }
        this.dir = (String) vars.get("DIR"); // NOI18N
        if (this.dir == null) {
            this.dir = ""; // NOI18N
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
        boolean rootPathEndsWithFileSeparator = false;
        if (dir.equals("")) { // NOI18N
            dir=rootDir;
            this.fsRootPathLength = rootDir.length();
            if (wholeModule != null && wholeModule.length() > 0) {
                dir += File.separator + wholeModule;
            }
            if (module != null && module.length() > 0) {
                this.fsRootPathLength += (File.separator + module).length();
            } else {
                rootPathEndsWithFileSeparator = rootDir.endsWith(File.separator);
            }
        } else {
            if (wholeModule == null || wholeModule.length() == 0) {
                dir=rootDir+File.separator+dir;
            } else {
                dir=rootDir+File.separator+wholeModule+File.separator+dir;
            }
            if (module == null || module.length() == 0) {
                this.fsRootPathLength = rootDir.length();
                rootPathEndsWithFileSeparator = rootDir.endsWith(File.separator);
            } else {
                this.fsRootPathLength = (rootDir+File.separator+module).length();
            }
        }
        if (!rootPathEndsWithFileSeparator) this.fsRootPathLength++;
        if (module.length() > 0) this.relMount = "/"+module.replace('\\', '/');
        else relMount = "";
        if (dir.charAt(dir.length() - 1) == File.separatorChar)
            dir = dir.substring(0, dir.length() - 1);

        String dataRegex = (String) vars.get("DATAREGEX"); // NOI18N
        if (dataRegex != null) this.dataRegex = dataRegex;
        String errorRegex = (String) vars.get("ERRORREGEX"); // NOI18N
        if (errorRegex != null) this.errorRegex = errorRegex;
    }

    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
    
    private boolean isAbsoluteRepository(String line) {
        for (int i = 0; i < absoluteRepositoryRegexs.length; i++) {
            if (absoluteRepositoryRegexs[i].matcher(line).matches()) {
                return true;
            }
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
        VcsCommand cmd = fileSystem.getCommand(cmdName);
        VcsCommandExecutor ec = fileSystem.getVcsFactory().getCommandExecutor(cmd, vars);
        ec.addDataOutputListener(this);
        ec.addErrorOutputListener(stderrNRListener);
        fileSystem.getCommandsPool().startExecutor(ec, fileSystem);
        return ec;
    }

    private VcsCommandExecutor runLogCommand(Hashtable vars, String cmdName) {
        VcsCommand cmd = fileSystem.getCommand(cmdName);
        VcsCommandExecutor ec = fileSystem.getVcsFactory().getCommandExecutor(cmd, vars);
        ec.addDataOutputListener(new CommandDataOutputListener() {
            public void outputData(String[] elements) {
                outputLogLine(elements[0]);
                //logDataBuffer.append(elements[0]+"\n"); // NOI18N
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
    private String[] getFilePaths(String repositoryPath, String fileName) {
        int nameIndex = repositoryPath.lastIndexOf('/');
        if (nameIndex < 0) return null;
        if (nameIndex == 0) return (cvsRepository.length() > 0) ? null : EMPTY_DIR;
        repositoryPath = repositoryPath.substring(0, nameIndex); //.replace('\\', '/'); // Because of Windoze unexpectable behavior
        if (repositoryPath.endsWith(ATTIC)) repositoryPath = repositoryPath.substring(0, repositoryPath.length() - ATTIC.length() - 1);
        int index = repositoryPath.indexOf(cvsRepository);
        if (index < 0) return null;
        if (repositoryPath.length() <= cvsRepository.length())
            return EMPTY_DIR;
        else {
            //path = path.substring(index + cvsRepository.length() + 1);
            ArrayList myWorkings = new ArrayList();
            Iterator keysIt = workReposPaths.keySet().iterator();
            while (keysIt.hasNext()) {
                String workPath = (String) keysIt.next();
                String repPath = (String) workReposPaths.get(workPath);
                if (repositoryPath.equals(repPath)) {
                    myWorkings.add(workPath);
                }
            }
            return (String[]) myWorkings.toArray(new String[0]);
        }
    }

    
    /**
     * Add the directory name to the proper container. Process the directory path recursively if necessary.
     * @param filePath the directory full path
     */
    private void addDirName(String filePath, VcsDirContainer filesByNameCont) {
        if (filePath.length() == 0) return;
        String[] fileStatuses = new String[7];
        String dirName = VcsUtilities.getFileNamePart(filePath) + "/";
        String dirPath = VcsUtilities.getDirNamePart(filePath);
        fileStatuses[0] = dirName;
        fileStatuses[1] = "";
        fileStatuses[5] = CvsListCommand.findStickyOfDir(fileSystem.getFile(/*new File(*/filePath/*.replace('/', File.separatorChar)*/));
        VcsDirContainer dirParent = filesByNameCont.getContainerWithPath(dirPath);
        if (dirParent == null /*|| dirParent == filesByNameCont*/) {
            // parent is somehere out => don't care about this case
            return;
        } else {
            Hashtable filesByName = (Hashtable) dirParent.getElement();
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
        this.filesByNameCont = filesByNameCont;
        if (args.length < 2) {
            stderrNRListener.outputLine("Expecting two commands as arguments!"); //NOI18N
            return false;
        }
        String statusCmd = args[0];
        String logCmd = args[1];
        initVars(vars);

        // Call getRepositoryPaths only when we do not have the streams merged
        // in the correct order
        boolean needToGetRepositoryPaths = !Validators.canHaveOutputStreamsMergedCorrectly(vars);
        //System.out.println("CvsListRecursiveCommand: needToGetRepositoryPaths = "+needToGetRepositoryPaths);
        if (needToGetRepositoryPaths) {
            getRepositoryPaths(new File(dir));
        }
        boolean interrupted = false;
        VcsCommandExecutor statusExecutor = runStatusCommand(vars, statusCmd);
        try {
            fileSystem.getCommandsPool().waitToFinish(statusExecutor);
        } catch (InterruptedException iexc) {
            fileSystem.getCommandsPool().kill(statusExecutor);
            interrupted = true;
            shouldFail = true;
        }
        flushLastFile();
        if (statusExecutor.getExitStatus() != VcsCommandExecutor.SUCCEEDED) {
            shouldFail = true;
        }
        VcsCommandExecutor logExecutor = null;
        String showDeadFilesValue = (String) vars.get(Variables.SHOW_DEAD_FILES);
        boolean showDeadFiles = (showDeadFilesValue != null) && (showDeadFilesValue.length() > 0);
        if (!shouldFail && showDeadFiles) {
            logExecutor = runLogCommand(vars, logCmd);
            try {
                fileSystem.getCommandsPool().waitToFinish(logExecutor);
            } catch (InterruptedException iexc) {
                fileSystem.getCommandsPool().kill(logExecutor);
                interrupted = true;
                shouldFail = true;
            }
        }
        if (logExecutor != null && logExecutor.getExitStatus() != VcsCommandExecutor.SUCCEEDED) {
            shouldFail = true;
        }
        addLocalFolders(filesByNameCont);
        return !shouldFail;
    }
    
    /** The map of files by name in the directory currently being processed. */
    private Hashtable lastFilesByName;
    private String lastFileName;
    private String lastStatus;
    private String lastRevision;
    private String lastSticky;
    private boolean haveExaminingPaths = false;
    
    /**
     * Output from status.
     */
    public void outputData(String[] elements) {
        if (elements == null || elements.length == 0) return ;
        String line = elements[0].trim();
        int examiningIndex = line.indexOf(CvsListCommand.EXAMINING_STR);
        if (examiningIndex >= 0) {
            flushLastFile();
            String path;
            String relativeDirectory = line.substring(examiningIndex + CvsListCommand.EXAMINING_STR.length()).trim();
            if (".".equals(relativeDirectory) || relativeDirectory.length() == 0) {
                path = dirPath;
            } else {
                if (dirPath.length() > 0) {
                    path = dirPath + "/" + relativeDirectory;
                } else {
                    path = relativeDirectory;
                }
            }
            VcsDirContainer filesByNameContDir = filesByNameCont.getContainerWithPath(path);
            if (filesByNameContDir == null) {
                filesByNameContDir = filesByNameCont.addSubdirRecursive(path);
            }
            if (filesByNameContDir == null) {
                ErrorManager.getDefault().log(ErrorManager.INFORMATIONAL, "CvsListRecursiveCommand: Bad path encountered: '"+path+"', line = "+line);
                return ;
            }
            if (path != dirPath) addDirName(path, filesByNameCont);
            Hashtable filesByName = (Hashtable) filesByNameContDir.getElement();
            if (filesByName == null) {
                filesByName = new Hashtable();
                filesByNameContDir.setElement(filesByName);
            }
            lastFilesByName = filesByName;
            haveExaminingPaths = true;
            assert path.equals(filesByNameContDir.getPath()) : "Path = '"+path+"', filesByNameContDir.getPath() = '"+filesByNameContDir.getPath()+"'";
        }
        else if (line.startsWith(CvsListCommand.MATCH_FILE)) {
            flushLastFile();
            int statusIndex = line.indexOf(CvsListCommand.MATCH_STATUS);
            if (statusIndex < 0) {
                ErrorManager.getDefault().log(ErrorManager.INFORMATIONAL, "CvsListRecursiveCommand: A false File: & Status: line encountered: '"+line+"'");
                return ;
            }
            String fileName = line.substring(CvsListCommand.MATCH_FILE.length(), statusIndex).trim();
            String status = line.substring(statusIndex + CvsListCommand.MATCH_STATUS.length()).trim().intern();
            int i;
            if ((i = fileName.indexOf("no file")) >= 0) { // NOI18N
                fileName = fileName.substring(i+7).trim();
            }
            lastFileName = fileName;
            lastStatus = status;
        }
        else if (line.startsWith(CvsListCommand.MATCH_REVISION)) {
            int startRev = CvsListCommand.MATCH_REVISION.length();
            while (startRev < line.length() && Character.isWhitespace(line.charAt(startRev))) startRev++;
            int endRev = startRev + 1;
            while (endRev < line.length() && !Character.isWhitespace(line.charAt(endRev))) endRev++;
            lastRevision = line.substring(startRev, endRev).intern();
        }
        else if (!haveExaminingPaths && line.startsWith(MATCH_REPOSITORY_REVISION)) {
            int repositoryIndex = MATCH_REPOSITORY_REVISION.length();
            while(Character.isWhitespace(line.charAt(repositoryIndex))) repositoryIndex++; // skip the space
            while(!Character.isWhitespace(line.charAt(repositoryIndex))) repositoryIndex++; // skip the revision number
            while(Character.isWhitespace(line.charAt(repositoryIndex))) repositoryIndex++; // skip the space
            String repositoryPath = line.substring(repositoryIndex);
            String[] filePaths = getFilePaths(repositoryPath, lastFileName);
            lastFilesByName = guessRightLastContainer(filePaths);
        }
        else if (line.startsWith(CvsListCommand.MATCH_STICKY_TAG)) {
            String stickyTag = line.substring(CvsListCommand.MATCH_STICKY_TAG.length()).trim();
            if (CvsListCommand.STICKY_NONE.equals(stickyTag)) stickyTag = ""; // NOI18N
            int end = 0;
            while (end < stickyTag.length() && !Character.isWhitespace(stickyTag.charAt(end))) end++;
            if (end < stickyTag.length()) {
                stickyTag = stickyTag.substring(0, end);
            }
            lastSticky = stickyTag.intern();
        }
        else if (line.startsWith(CvsListCommand.MATCH_STICKY_DATE)) {
            String stickyDate = line.substring(CvsListCommand.MATCH_STICKY_DATE.length()).trim();
            if (CvsListCommand.STICKY_NONE.equals(stickyDate)) stickyDate = ""; // NOI18N
            if (lastSticky.length() != 0 && stickyDate.length() != 0) {
                lastSticky = (lastSticky + " " + stickyDate).intern();
            } else if (stickyDate.length() != 0) {
                lastSticky = stickyDate.intern();
            }
        }
    }
    
    private String last_filePath;
    
    /** Guess the appropriate container for one of the file paths.
     * @return the element (filesByName) of the container guessed. */
    private Hashtable guessRightLastContainer(String[] filePaths) {
        Hashtable filesByName = null;
        if (filePaths != null && filePaths.length == 1 && filePaths[0].equals(last_filePath)) {
            return lastFilesByName;
        }
        if (filePaths != null) {
            int len = filePaths.length;
            for(int j = 0; j < len; j++) {
                VcsDirContainer parent;
                if (filePaths[j].length() == 0) {
                    parent = null;
                } else {
                    parent = filesByNameCont.getParent(filePaths[j]);
                }
                VcsDirContainer filesByNameContPath;
                if (parent != null) filesByNameContPath = parent.addSubdir(filePaths[j]);
                else filesByNameContPath = filesByNameCont.addSubdirRecursive(filePaths[j]);
                if (filesByNameContPath == null) continue;
                addDirName(filePaths[j], filesByNameCont);
                filesByName = (Hashtable) filesByNameContPath.getElement();
                if (filesByName == null) {
                    filesByName = new Hashtable();
                    filesByNameContPath.setElement(filesByName);
                }
            }
            if (len == 1) last_filePath = filePaths[0];
        } else { // the file path was not found (e.g. Locally Added)
            String[] fileStatuses = new String[7];
            fileStatuses[0] = lastFileName;
            fileStatuses[1] = lastStatus;
            fileStatuses[2] = lastRevision;
            fileStatuses[3] = "";
            fileStatuses[4] = "";
            fileStatuses[5] = lastSticky;
            fileStatuses[6] = ""; // the locker will be filled in fillHashtableFromLog()
            HashSet unknownFiles = (HashSet) unknownPathFiles.get(lastFileName);
            if (unknownFiles == null) {
                unknownFiles = new HashSet();
                unknownPathFiles.put(lastFileName, unknownFiles);
            }
            unknownFiles.add(fileStatuses);
        }
        return filesByName;
    }

    private void flushLastFile() {
        if (lastFilesByName != null && lastFileName != null) {
            String[] fileStatuses = new String[7];
            fileStatuses[0] = lastFileName;
            fileStatuses[1] = lastStatus;
            fileStatuses[2] = lastRevision;
            fileStatuses[3] = ""; // No Time
            fileStatuses[4] = ""; // No Date
            fileStatuses[5] = lastSticky;
            fileStatuses[6] = ""; // the locker will be filled in fillHashtableFromLog()
            lastFilesByName.put(lastFileName, fileStatuses);
            if (stdoutListener != null) stdoutListener.outputData(fileStatuses);
            lastFileName = null;
            //System.out.println("  Flushing "+java.util.Arrays.asList(fileStatuses));
        }
    }
    
    private String[] lastLogFileStatuses;
    private boolean logReadingLocks;
    private StringBuffer lastLocks = new StringBuffer();
    
    /** The line from cvs log */
    private void outputLogLine(String line) {
        if (line.startsWith(CvsListCommand.LOG_WORKING_FILE)) {
            String filePath = line.substring(CvsListCommand.LOG_WORKING_FILE.length()).trim();
            int index = filePath.lastIndexOf('/');
            if (index < 0) {
                index = filePath.lastIndexOf('\\');
            }
            String path;
            String fileName;
            if (index > 0) {
                path = filePath.substring(0, index);
                fileName = filePath.substring(index + 1);
            } else {
                path = "";
                fileName = filePath;
            }
            VcsDirContainer filesByNameContDir = filesByNameCont.getParent(path);
            if (filesByNameContDir != null) {
                Hashtable filesByName = (Hashtable) filesByNameContDir.getElement();
                if (filesByName != null) {
                    String[] fileStatuses = (String[]) filesByName.get(fileName);
                    if (fileStatuses == null) {
                        fileStatuses = new String[7];
                        fileStatuses[0] = fileName;
                        fileStatuses[1] = Statuses.STATUS_DEAD;
                        filesByName.put(fileName, fileStatuses);
                    }
                    lastLogFileStatuses = fileStatuses;
                }
            }
        } else if (line.startsWith(CvsListCommand.LOG_LOCKS)) {
            logReadingLocks = true;
        } else if (logReadingLocks) {
            if (line.length() > 0 && Character.isWhitespace(line.charAt(0))) {
                lastLocks.append(line.trim() + "\n");
            } else {
                if (lastLogFileStatuses != null && lastLocks.length() > 0) {
                    lastLogFileStatuses[6] = getLockers(lastLocks.toString(), lastLogFileStatuses[2]);
                    lastLocks.delete(0, lastLocks.length());
                }
                logReadingLocks = false;
                lastLogFileStatuses = null;
            }
        }
    }
    
    private String getLockers(String data, String revision) {
        String lockers = "";
        int pos = 0;
        int lockerIndex;
        int eolIndex = data.indexOf('\n');
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
        return lockers;
    }
    
}

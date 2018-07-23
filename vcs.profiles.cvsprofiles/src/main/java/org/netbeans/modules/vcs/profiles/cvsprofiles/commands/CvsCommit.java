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

import java.lang.reflect.*;
import java.io.*;
import java.util.*;
import org.netbeans.api.vcs.VcsManager;
import org.netbeans.api.vcs.commands.Command;
import org.netbeans.lib.cvsclient.CVSRoot;

import org.netbeans.modules.vcs.profiles.cvsprofiles.list.CvsListOffline;
import org.netbeans.modules.vcs.profiles.cvsprofiles.list.StatusFilePathsBuilder;
import org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.commit.CommitInformation;
import org.netbeans.modules.vcscore.util.VariableValueAdjustment;

import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;

import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.VcsAction;
import org.netbeans.modules.vcscore.commands.*;
import org.netbeans.modules.vcscore.cmdline.ExecuteCommand;
import org.netbeans.modules.vcscore.cmdline.UserCommand;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.util.Table;
import org.netbeans.modules.vcscore.util.VcsUtilities;
import org.netbeans.spi.vcs.commands.CommandSupport;
import org.openide.ErrorManager;

/**
 * The cvs commit command wrapper. This class ensures, that only files, that should
 * be committed with the current template are actually committed. If files with
 * different template are to be committed, the commit is executed multiple times.
 *
 * @author  Martin Entlicher
 */
public class CvsCommit extends Object implements VcsAdditionalCommand {

    private static final String COMMITTING = "CVS: Committing in";
    private static final String PRE_FILE = "CVS: \t";

    private static final String TEMP_FILE_PREFIX = "tempCommit";
    private static final String TEMP_FILE_SUFFIX = "output";
    
    private static final String REPOSITORY_FILE_PATTERN = ",v  <-- "; // NOI18N
    private static final String NEW_REVISION = "new revision: "; // NOI18N
    private static final String PREVIOUS_REVISION = "; previous revision: "; // NOI18N
    private static final String INITIAL_REVISION = "initial revision: "; //NOI18N
    private static final String DELETED_REVISION = "delete"; //NOI18N
    
    private VcsFileSystem fileSystem = null;
    
    private FileStatusUpdater fileStatusUpdater;

    /** Creates new CvsCommit */
    public CvsCommit() {
    }

    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
    
    private static ArrayList getFilePaths(String commonParent, String paths, char ps) {
        ArrayList files = new ArrayList();
        if (paths != null && paths.length() > 0) {
            int len = paths.length();
            int begin = 0;
            do {
                int index = paths.indexOf(""+ps + ps, begin);
                if (index < 0) index = len;
                String file = paths.substring(begin, index);
                if (commonParent != null && commonParent.length() > 0) {
                    file = commonParent + "/" + file;
                }
                files.add(file.replace(ps, '/'));
                begin = index + 2;
            } while (begin < len);
        }
        return files;
    }

    static List getCommitedFiles(String fsRoot, String relativePath,
                                 String template, char ps) {
        //System.out.println("getCommitedFiles("+template+")");
        ArrayList list = new ArrayList();
        int beginPath = template.indexOf(COMMITTING);
        //System.out.println("beginPath = "+beginPath);
        Map cachedEntries = new HashMap();
    
        File root = new File(fsRoot);
        if (beginPath < 0) beginPath = template.length();
        do {
            String path;
            int eol;
            if (beginPath < template.length()) {
                beginPath += COMMITTING.length();
                eol = template.indexOf('\n', beginPath);
                path = template.substring(beginPath, eol).trim();
                path = path.replace(File.separatorChar, '/');
                //System.out.println("path = "+path);
                if (".".equals(path)) path = "";
                beginPath = template.indexOf(COMMITTING, eol);
                if (beginPath < 0) beginPath = template.length();
            } else {
                path = "";
                eol = 0;
            }
            if (relativePath != null) {
                path = relativePath + ((path.length() > 0) ? "/" + path : "");
            }
            do {
                int beginFile = template.indexOf(PRE_FILE, eol);
                //System.out.println("begin file = "+beginFile);
                if (beginFile < 0 || beginFile > beginPath) break;
                eol = template.indexOf('\n', beginFile);
                beginFile += PRE_FILE.length();
                String files = template.substring(beginFile, eol).trim();
                files = files.replace(File.separatorChar, '/');
                addFiles(list, root, path, files, ps, cachedEntries);
                //list.add(path + "/" + file);
            } while (true);
        } while (beginPath < template.length());
        return list;
    }
    
    private static void addFiles(List list, File root, String path, String files,
                                 char ps, Map cachedEntries) {
        int begin = 0;
        int end = files.indexOf(' ');
        if (end < 0) end = files.length();
        while (begin < end) {
            String name;
            if (end < files.length()) {
                name = getFileFromRow(files.substring(begin), root, path, ps, cachedEntries);
                end = begin + name.length();
            } else {
                name = files.substring(begin, end);
            }
            //System.out.println("addFiles(): name = "+name);
            String file = (path.length() == 0) ? name : path + "/" + name;
            list.add(file);
            //System.out.println("  adding file = '"+file+"'");
            begin = end + 1;
            if (end < files.length()) {
                end = files.indexOf(' ', end + 1);
                if (end < 0) end = files.length();
            }
        }
    }
    
    private static String getFileFromRow(String row, File root, String path,
                                         char ps, Map cachedEntries) {
        int index;
        //System.out.println("getFileFromRow("+row+")");
        for (index = row.indexOf(' '); index > 0 && index < row.length(); index = row.indexOf(' ', index + 1)) {
            String file = row.substring(0, index);
            int sepIndex = file.lastIndexOf('/');
            if (sepIndex > 0) {
                String fileName = file.substring(sepIndex + 1);
                String filePath = file.substring(0, sepIndex);
                if (isCVSFile(root, path + "/" + filePath, fileName, ps, cachedEntries)) break;
            } else {
                //System.out.println(" file = "+file);
                if (isCVSFile(root, path, file, ps, cachedEntries)) break;
                //System.out.println("   is not CVS file!");
            }
        }
        //System.out.println("   have CVS file = "+((index > 0 && index < row.length()) ? row.substring(0, index) : row));
        if (index > 0 && index < row.length()) return row.substring(0, index);
        else return row;
    }
    
    private static boolean isCVSFile(File root, String path, String name,
                                     char ps, Map cachedEntries) {
        path = path.replace('/', ps);
        File dir = new File(root, path);
        File entries = new File(dir, "CVS/Entries");
        if (!entries.exists() || !entries.canRead()) return true;
        return isFileInEntries(name, entries, cachedEntries);
    }
    
    private static boolean isFileInEntries(String name, File entries, Map cachedEntries) {
        List files = (List) cachedEntries.get(entries);
        if (files == null) {
            files = loadEntries(entries);
            cachedEntries.put(entries, files);
        }
        return files.contains(name);
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
                    if (line.startsWith("/")) {
                        int end = line.indexOf('/', 1);
                        if (end > 0) entriesFiles.add(line.substring(1, end));
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
        return entriesFiles;
    }
    
    //private void setProcessingFiles(ArrayList files, Hashtable vars) {
    //}

    private Table getFiles(List filesList) {
        Table files = new Table();
        for (Iterator it = filesList.iterator(); it.hasNext(); ) {
            String file = (String) it.next();
            if (".".equals(file)) file = ""; // cvs ci . will fail on Windows !!
            files.put(file, fileSystem.findResource(file));
        }
        return files;
    }

    /**
     * Run the commit and return the appropriate task IDs.
     */
    private long[] doCommit(CommandsPool cpool, VcsCommand cmdCommit, List filesList,
                            Hashtable vars, CommandOutputListener stderrNRListener,
                            CommandDataOutputListener stderrListener,
                            VcsCommandExecutor[][] executorsRet) {
        Table files = getFiles(filesList);
        VcsCommandExecutor[] executors = VcsAction.doCommand(files, cmdCommit, vars, fileSystem, fileStatusUpdater, stderrNRListener, null, stderrListener);
        //VcsCommandExecutor commit = fileSystem.getVcsFactory().getCommandExecutor(cmdCommit, vars);
        //int preprocessStatus = cpool.preprocessCommand(commit, vars);
        //cpool.startExecutor(commit);
        //return preprocessStatus;
        long[] IDs = new long[executors.length];
        for (int i = 0; i < executors.length; i++) {
            IDs[i] = cpool.getCommandID(executors[i]);
        }
        executorsRet[0] = executors;
        return IDs;
    }
    
    public boolean exec(Hashtable vars, String[] args,
                        CommandOutputListener stdoutNRListener, CommandOutputListener stderrNRListener,
                        CommandDataOutputListener stdoutListener, String dataRegex,
                        CommandDataOutputListener stderrListener, String errorRegex) {
        if (args.length < 1) {
            stderrNRListener.outputLine("The cvs commit command is expected as an arguments");
            return false;
        }
        String psStr = (String) vars.get("PS");
        if (psStr != null) {
            psStr = Variables.expand(vars, psStr, true);
        }
        char ps = (psStr == null || psStr.length() < 1) ? java.io.File.pathSeparatorChar : psStr.charAt(0);
        String relativeMountPoint = fileSystem.getRelativeMountPoint();
        VariableValueAdjustment varAdj = fileSystem.getVarValueAdjustment();
        String fsRoot = varAdj.revertAdjustedVarValue((String) vars.get("ROOTDIR"));
        if (relativeMountPoint != null && relativeMountPoint.length() > 0) {
            fsRoot += ps + relativeMountPoint;//vars.get("MODULE");
        }
        String relativePath = varAdj.revertAdjustedVarValue((String) vars.get("COMMON_PARENT"));
        ArrayList filePaths = getFilePaths(
                varAdj.revertAdjustedVarValue((String) vars.get("COMMON_PARENT")),
                varAdj.revertAdjustedVarValue((String) vars.get("PATHS")),
                ps);
        boolean applyCVSROOT = "true".equals(vars.get("APPLY_CVSROOT"));
        fileStatusUpdater = new FileStatusUpdater(new File(fsRoot),
                                                  applyCVSROOT ? (String) vars.get("CVS_REPOSITORY") : null,
                                                  filePaths,
                                                  stdoutNRListener, stdoutListener);
        fsRoot = fsRoot.replace('/', ps);
        //final StringBuffer buff = new StringBuffer();
        CommandsPool cpool = fileSystem.getCommandsPool();
        //Hashtable varsOriginal = new Hashtable(vars);
        //boolean committed = false;
        String committedStr = (String) vars.get("IS_COMMITTED");
        boolean committed = committedStr != null && committedStr.length() > 0;
        List alreadyCommittedFiles;
        String alreadyCommittedFilesStr = (String) vars.get("ALREADY_COMMITTED_FILES");
        if (alreadyCommittedFilesStr == null) {
            alreadyCommittedFiles = new ArrayList();
        } else {
            try {
                alreadyCommittedFiles = (List) VcsUtilities.decodeValue(alreadyCommittedFilesStr);
            } catch (IOException ioex) {
                alreadyCommittedFiles = new ArrayList();
            }
        }
        //do {
            //VcsCommand cmdTemplate = fileSystem.getCommand(args[0]);
            VcsCommand cmdCommit1 = fileSystem.getCommand(args[0]);
            VcsCommand cmdCommit = new UserCommand();
            ((UserCommand) cmdCommit).copyFrom(cmdCommit1);
            cmdCommit.setDisplayName(NbBundle.getMessage(CvsCommit.class, "CvsCommit.commitCommandName"));
            //buff.delete(0, buff.length());
            String templateContent = (String) vars.get("ORIGINAL_TEMPLATE_CONTENT");
            if (templateContent == null) templateContent = "";
            List filesCommited = null;
            if ("-f".equals(vars.get("FORCE"))) {
                filesCommited = new ArrayList(filePaths);
            } else {
                if (filePaths.size() == 1) {
                    FileObject fo = fileSystem.findResource((String) filePaths.get(0));
                    if (fo != null && fo.isData()) {
                        filesCommited = new ArrayList(filePaths);
                    }
                }
                if (filesCommited == null) {
                    filesCommited = getCommitedFiles(fsRoot, relativePath, templateContent, ps);
                }
            }
            //buffered = addMessageComment(vars, buffered);
            //vars.put("FILE_TEMPLATE", fileOutput(buffered));
            // commit all remaining files if they can not be retrieved from the template
            if (filesCommited == null || filesCommited.size() == 0) {
                if (committed) return true;//break;
                filesCommited = new ArrayList(filePaths);
            }
            filesCommited.removeAll(alreadyCommittedFiles);
            if (filesCommited.size() == 0) return true;//break ;
            //setProcessingFiles(filesCommited, vars);
            VcsCommandExecutor[][] executors = new VcsCommandExecutor[1][];
            long[] IDs = doCommit(cpool, cmdCommit, filesCommited, vars,
                                  stderrNRListener, stderrListener, executors);
            //cmdCommit1.setProperty(CommandCustomizationSupport.INPUT_DESCRIPTOR_PARSED,
            //    cmdCommit.getProperty(CommandCustomizationSupport.INPUT_DESCRIPTOR_PARSED));
            if (IDs.length == 0) {
                return true;//break;
            }
            vars.put("IS_COMMITTED", "true");
            committed = true;
            alreadyCommittedFiles.addAll(filesCommited);
            filePaths.removeAll(filesCommited);
            //vars = new Hashtable(varsOriginal);
            StringBuffer IDBuff = new StringBuffer();
            for (int i = 0; i < IDs.length; i++) {
                if (i > 0) IDBuff.append(" ,");
                IDBuff.append(Long.toString(IDs[i]));
            }
            final String IDStr = IDBuff.toString();
            if (filePaths.size() > 0) {
                vars.put("TEMPLATE_FILE_PLEASE_WAIT_TEXT", NbBundle.getMessage(CvsCommit.class, "CvsCommit.templateFileCheck"));
                vars.put("COMMIT_COMMANDS_IDS", IDStr);
                try {
                    vars.put("ALREADY_COMMITTED_FILES", VcsUtilities.encodeValue(alreadyCommittedFiles));
                } catch (IOException ioex) {}
                CommandSupport cmdSupport = fileSystem.getCommandSupport("COMMIT"); // Myself
                Command cmd = cmdSupport.createCommand();
                if (cmd instanceof VcsDescribedCommand) {
                    ((VcsDescribedCommand) cmd).setAdditionalVariables(vars);
                }
                if (VcsManager.getDefault().showCustomizer(cmd)) {
                    cmd.execute();
                }
            }
            boolean status = true;
            for(int i = 0; i < executors[0].length; i++) {
                try {
                    cpool.waitToFinish(executors[0][i]);
                } catch (InterruptedException iexc) {
                    for (int j = i; j < executors[0].length; j++) {
                        cpool.kill(executors[0][j]);
                    }
                    Thread.currentThread().interrupt();
                    break;
                }
                status = status && (executors[0][i].getExitStatus() == VcsCommandExecutor.SUCCEEDED);
            }
            fileStatusUpdater.flushElements();
        //cpool.preprocessCommand(vce, vars);
        return status;
    }
    
    private static String fileOutput(String buffer) {
        File outputFile;
        Writer writer = null;
        try {
            outputFile = File.createTempFile(TEMP_FILE_PREFIX, TEMP_FILE_SUFFIX);
            outputFile.deleteOnExit();
            writer = new BufferedWriter(new FileWriter(outputFile));
            writer.write(buffer);
        } catch (IOException ioexc) {
            ErrorManager.getDefault().notify(ioexc);
            return null;
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ioexc) {
                    ErrorManager.getDefault().notify(ioexc);
                }
            }
        }
        return outputFile.getAbsolutePath();
    }
    
    /** Gets the output of "cvs commit" command and updates the status of committed files. */
    private static class FileStatusUpdater extends Object implements CommandOutputListener {
        
        private Map pathsBuildersByRelPaths;
        private File workingDir;
        private String cvsRepository;
        private int workPathLength;
        private CommandOutputListener stdoutNRListener; // The listener to propagate the standard output to
        private CommandDataOutputListener fileUpdateListener; // The listener to send data output to
        private String lastFilePath;
        private String lastFileDir;
        private List filePaths;
        private List elementsToSend;
        
        public FileStatusUpdater(File workingDir, String cvsRepository,
                                 List filePaths,
                                 CommandOutputListener stdoutNRListener,
                                 CommandDataOutputListener fileUpdateListener) {
            this.stdoutNRListener = stdoutNRListener;
            this.fileUpdateListener = fileUpdateListener;
            filePaths = new ArrayList(filePaths); // Have our own copy, we'll change the list to keep there just files
            this.filePaths = filePaths;
            this.pathsBuildersByRelPaths = createPathsBuilders(workingDir, filePaths, cvsRepository);//new StatusFilePathsBuilder(workingDir, cvsRepository);
            this.workingDir = workingDir;
            this.cvsRepository = cvsRepository;
            workPathLength = workingDir.getAbsolutePath().length();
            elementsToSend = new ArrayList();
            for (int i = 0; i < filePaths.size(); i++) {
                String path = (String) filePaths.get(i);
                File file = new File(workingDir, path);
                if (file.isDirectory()) {
                    filePaths.remove(i);
                    i--;
                }
            }
        }
        
        private static Map createPathsBuilders(File workingDir, List filePaths, String cvsRepository) {
            Map builders = new HashMap(filePaths.size());
            for (Iterator it = filePaths.iterator(); it.hasNext(); ) {
                String filePath = (String) it.next();
                if (filePath.equals(".")) filePath = "";// NOI18N
                File file = new File(workingDir, filePath);
                if (file.isDirectory()) {
                    String reposPath = cvsRepository;
                    if (reposPath == null) {
                        reposPath = getRepositoryPath(new File(file, "CVS"));
                    }
                    if (reposPath != null) {
                        builders.put(filePath, new StatusFilePathsBuilder(file, reposPath));
                    }
                }
            }
            return builders;
        }
        
        public void outputLine(String line) {
            stdoutNRListener.outputLine(line);
            int reposFileIndex = line.indexOf(REPOSITORY_FILE_PATTERN);
            if (reposFileIndex > 0) {
                String reposFile = line.substring(0, reposFileIndex);
                int fileIndex = reposFile.lastIndexOf('/');
                String file = reposFile.substring(fileIndex + 1);
                //lastFilePath = pathsBuilder.getStatusFilePath(file, reposFile);
                lastFilePath = findFilePath(file, reposFile.substring(0, fileIndex));
                if (lastFilePath == null) {
                    for (Iterator it = pathsBuildersByRelPaths.keySet().iterator(); it.hasNext(); ) {
                        String relPath = (String) it.next();
                        StatusFilePathsBuilder builder = (StatusFilePathsBuilder) pathsBuildersByRelPaths.get(relPath);
                        lastFilePath = builder.getStatusFilePath(file, reposFile);
                        if (lastFilePath != null) {
                            if (relPath.length() > 0) {
                                lastFilePath = relPath + "/" + lastFilePath;
                            }
                            break;
                        }
                    }
                }
                if (lastFilePath != null) {
                    lastFilePath = lastFilePath.replace(File.separatorChar, '/');
                }
            } else if (lastFilePath != null && line.startsWith(NEW_REVISION)) {
                int endRevision = line.indexOf(';', NEW_REVISION.length());
                if (endRevision <= 0) return ;
                String revision = line.substring(NEW_REVISION.length(), endRevision).trim();
                if (DELETED_REVISION.equals(revision)) {
                    endRevision += PREVIOUS_REVISION.length();
                    if (endRevision < line.length()) {
                        revision = line.substring(endRevision).trim();
                    }
                    revision(revision, CommitInformation.REMOVED);
                } else {
                    revision(revision, CommitInformation.CHANGED);
                }
                lastFilePath = null;
            } else if (lastFilePath != null && line.startsWith(INITIAL_REVISION)) {
                String revision = line.substring(INITIAL_REVISION.length()).trim();
                revision(revision, CommitInformation.ADDED);
                lastFilePath = null;
            } else {
                lastFilePath = null;
            }
        }
        
        private String findFilePath(String fileName, String reposDir) {
            for (Iterator it = filePaths.iterator(); it.hasNext(); ) {
                String path = (String) it.next();
                if (path.endsWith(fileName)) {
                    if (path.length() > fileName.length() && path.charAt(path.length() - fileName.length() - 1) != '/') {
                        continue;
                    }
                    if (!verifyRepository(new File(workingDir, path).getParentFile(), reposDir, cvsRepository)) {
                        continue;
                    }
                    filePaths.remove(path);
                    return path;
                }
            }
            return null;
        }
        
        /**
         * @param w the working directory
         * @param r the whole repository path
         * @param cvsRepo the repository path used by the IDE. Can be <code>null</code>.
         * @return true when the working directory corresponds to the repository path.
         */
        private static boolean verifyRepository(File w, String r, String cvsRepo) {
            File cvs = new File(w, "CVS");
            File repository = new File(cvs, "Repository");
            String repositoryStr;
            BufferedReader fr = null;
            try {
                fr = new BufferedReader(new FileReader(repository));
                repositoryStr = fr.readLine();
            } catch (IOException ioex) {
                return false;
            } finally {
                if (fr != null) {
                    try {
                        fr.close();
                    } catch (IOException ioex) {}
                }
            }
            if (repositoryStr == null) {
                return false;
            }
            if (cvsRepo == null) {
                cvsRepo = getRepositoryPath(cvs);
            }
            if (cvsRepo != null) {
                return r.equals(cvsRepo + '/' + repositoryStr);
            } else {
                return false;
            }
        }
        
        /**
         * Get the repository path of the CVSROOT in the working dir.
         * @param cvs The CVS folder in the working dir.
         */
        private static String getRepositoryPath(File cvs) {
            File root = new File(cvs, "Root");
            String rootStr;
            BufferedReader fr = null;
            try {
                fr = new BufferedReader(new FileReader(root));
                rootStr = fr.readLine();
            } catch (IOException ioex) {
                return null;
            } finally {
                if (fr != null) {
                    try {
                        fr.close();
                    } catch (IOException ioex) {}
                }
            }
            if (rootStr != null) {
                try {
                    CVSRoot cvsroot = CVSRoot.parse(rootStr);
                    return cvsroot.getRepository();
                } catch (IllegalArgumentException iaex) {}
            }
            return null;
        }
        
        private void revision(String revision, String operationType) {
            int fileIndex = lastFilePath.lastIndexOf('/');
            String fileDir;
            if (fileIndex <= 0) fileDir = "";
            else fileDir = lastFilePath.substring(0, fileIndex);
            if (!fileDir.equals(lastFileDir)) {
                // Do that asynchronously for all files in a given folder,
                // after there's a chance that the CVS/Entries file is updated and closed.
                flushElements();
                lastFileDir = fileDir;
            }
            String[] elements = new String[9];
            elements[0] = lastFilePath;
            elements[1] = "Up-to-date";
            elements[2] = revision;
            elements[3] = ""; // time
            elements[4] = ""; // date
            elements[5] = null;// findSticky(lastFilePath); - load it asynchronously in flushElements()
            elements[6] = ""; // lockers
            if (CommitInformation.REMOVED == operationType) {
                elements[7] = lastFilePath; // removed file
            } else {
                elements[7] = null; // removed file
            }
            elements[8] = operationType; // information for the visualizer
            //fileUpdateListener.outputData(elements);
            elementsToSend.add(elements);
            lastFilePath = null;
        }
        
        public void flushElements() {
            if (lastFileDir == null) return ;
            File folder = new File(workingDir, lastFileDir);
            File entries = new File(folder, "CVS/Entries"); // NOI18N
            Map entriesByFiles = CvsListOffline.createEntriesByFiles(CvsListOffline.loadEntries(entries));
            for (Iterator it = elementsToSend.iterator(); it.hasNext(); ) {
                String[] elements = (String[]) it.next();
                elements[5] = findSticky(elements[0], entriesByFiles);
                fileUpdateListener.outputData(elements);
            }
            elementsToSend.clear();
        }
        
        private String findSticky(String filePath, Map entriesByFiles) {
            int fileIndex = filePath.lastIndexOf('/');
            String fileName;
            if (fileIndex <= 0) fileName = filePath;
            else fileName = filePath.substring(fileIndex + 1);
            String entry = (String) entriesByFiles.get(fileName);
            if (entry == null) return "";
            String[] entryItems = CvsListOffline.parseEntry(entry);
            String sticky;
            if (entryItems.length > 4) {
                sticky = entryItems[4];
                if (sticky.length() > 0) sticky = sticky.substring(1, sticky.length());
            } else {
                sticky = "";
            }
            return sticky;
        }
    }
}

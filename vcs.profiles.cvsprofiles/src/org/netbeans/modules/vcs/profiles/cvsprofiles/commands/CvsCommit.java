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

package org.netbeans.modules.vcs.profiles.cvsprofiles.commands;

import java.lang.reflect.*;
import java.io.*;
import java.util.*;
import org.netbeans.api.vcs.VcsManager;
import org.netbeans.api.vcs.commands.Command;

import org.netbeans.modules.vcs.profiles.cvsprofiles.list.CvsListOffline;
import org.netbeans.modules.vcs.profiles.cvsprofiles.list.StatusFilePathsBuilder;
import org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.commit.CommitInformation;

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
    
    private HashMap cachedEntries = new HashMap();
    
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

    private ArrayList getCommitedFiles(String fsRoot, String relativePath,
                                       String template, char ps) {
        //System.out.println("getCommitedFiles("+template+")");
        ArrayList list = new ArrayList();
        int beginPath = template.indexOf(COMMITTING);
        //System.out.println("beginPath = "+beginPath);
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
                addFiles(list, root, path, files, ps);
                //list.add(path + "/" + file);
            } while (true);
        } while (beginPath < template.length());
        return list;
    }
    
    private void addFiles(List list, File root, String path, String files, char ps) {
        int begin = 0;
        int end = files.indexOf(' ');
        if (end < 0) end = files.length();
        while (begin < end) {
            String name;
            if (end < files.length()) {
                name = getFileFromRow(files.substring(begin), root, path, ps);
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
    
    private String getFileFromRow(String row, File root, String path, char ps) {
        int index;
        //System.out.println("getFileFromRow("+row+")");
        for (index = row.indexOf(' '); index > 0 && index < row.length(); index = row.indexOf(' ', index + 1)) {
            String file = row.substring(0, index);
            int sepIndex = file.lastIndexOf('/');
            if (sepIndex > 0) {
                String fileName = file.substring(sepIndex + 1);
                String filePath = file.substring(0, sepIndex);
                if (isCVSFile(root, path + "/" + filePath, fileName, ps)) break;
            } else {
                //System.out.println(" file = "+file);
                if (isCVSFile(root, path, file, ps)) break;
                //System.out.println("   is not CVS file!");
            }
        }
        //System.out.println("   have CVS file = "+((index > 0 && index < row.length()) ? row.substring(0, index) : row));
        if (index > 0 && index < row.length()) return row.substring(0, index);
        else return row;
    }
    
    private boolean isCVSFile(File root, String path, String name, char ps) {
        path = path.replace('/', ps);
        File dir = new File(root, path);
        File entries = new File(dir, "CVS/Entries");
        if (!entries.exists() || !entries.canRead()) return true;
        return isFileInEntries(name, entries);
    }
    
    private boolean isFileInEntries(String name, File entries) {
        List files = (List) cachedEntries.get(entries);
        if (files == null) {
            files = loadEntries(entries);
            cachedEntries.put(entries, files);
        }
        return files.contains(name);
    }
    
    private List loadEntries(File entries) {
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

    private Table getFiles(ArrayList filesList) {
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
    private long[] doCommit(CommandsPool cpool, VcsCommand cmdCommit, ArrayList filesList,
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
        String fsRoot = (String) vars.get("ROOTDIR");
        if (relativeMountPoint != null && relativeMountPoint.length() > 0) {
            fsRoot += ps + relativeMountPoint;//vars.get("MODULE");
        }
        String relativePath = (String) vars.get("COMMON_PARENT");
        if ("".equals(vars.get("MULTIPLE_FILES")) && !"".equals(vars.get("FILE_IS_FOLDER"))) {
            if (relativePath != null) {
                relativePath = relativePath + "/" + (String) vars.get("PATH");
            } else {
                relativePath = (String) vars.get("PATH");
                if (".".equals(relativePath)) relativePath = null;
            }
        }
        File updatedFileRoot;
        if (relativePath != null) {
            relativePath = relativePath.replace(File.separatorChar, '/');
            updatedFileRoot = new File(fsRoot, relativePath);
        } else {
            updatedFileRoot = new File(fsRoot);
        }
        fileStatusUpdater = new FileStatusUpdater(updatedFileRoot, relativePath, (String) vars.get("CVS_REPOSITORY"), stdoutNRListener, stdoutListener);
        fsRoot = fsRoot.replace('/', ps);
        //final StringBuffer buff = new StringBuffer();
        ArrayList filePaths = getFilePaths((String) vars.get("COMMON_PARENT"), (String) vars.get("PATHS"), ps);
        CommandsPool cpool = fileSystem.getCommandsPool();
        //Hashtable varsOriginal = new Hashtable(vars);
        //boolean committed = false;
        String committedStr = (String) vars.get("IS_COMMITTED");
        boolean committed = committedStr != null && committedStr.length() > 0;
        ArrayList alreadyCommittedFiles = new ArrayList();
        //do {
            //VcsCommand cmdTemplate = fileSystem.getCommand(args[0]);
            VcsCommand cmdCommit1 = fileSystem.getCommand(args[0]);
            VcsCommand cmdCommit = new UserCommand();
            ((UserCommand) cmdCommit).copyFrom(cmdCommit1);
            cmdCommit.setDisplayName(NbBundle.getMessage(CvsCommit.class, "CvsCommit.commitCommandName"));
            //buff.delete(0, buff.length());
            String templateContent = (String) vars.get("ORIGINAL_TEMPLATE_CONTENT");
            if (templateContent == null) templateContent = "";
            ArrayList filesCommited;
            if ("-f".equals(vars.get("FORCE"))) {
                filesCommited = new ArrayList(filePaths);
            } else {
                filesCommited = getCommitedFiles(fsRoot, relativePath, templateContent, ps);
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
        
        private StatusFilePathsBuilder pathsBuilder;
        private File workingDir;
        private int workPathLength;
        private String relativePath;
        private CommandOutputListener stdoutNRListener; // The listener to propagate the standard output to
        private CommandDataOutputListener fileUpdateListener; // The listener to send data output to
        private String lastFilePath;
        private String lastFileDir;
        private List elementsToSend;
        
        public FileStatusUpdater(File workingDir, String relativePath, String cvsRepository,
                                 CommandOutputListener stdoutNRListener,
                                 CommandDataOutputListener fileUpdateListener) {
            this.stdoutNRListener = stdoutNRListener;
            this.fileUpdateListener = fileUpdateListener;
            this.pathsBuilder = new StatusFilePathsBuilder(workingDir, cvsRepository);
            this.workingDir = workingDir;
            workPathLength = workingDir.getAbsolutePath().length();
            if (relativePath != null && relativePath.length() > 0) {
                this.relativePath += "/";
            } else {
                this.relativePath = "";
            }
            elementsToSend = new ArrayList();
        }
        
        public void outputLine(String line) {
            stdoutNRListener.outputLine(line);
            int reposFileIndex = line.indexOf(REPOSITORY_FILE_PATTERN);
            if (reposFileIndex > 0) {
                String reposFile = line.substring(0, reposFileIndex);
                int fileIndex = reposFile.lastIndexOf('/');
                String file = reposFile.substring(fileIndex + 1);
                lastFilePath = pathsBuilder.getStatusFilePath(file, reposFile);
                if (lastFilePath != null) {
                    lastFilePath = lastFilePath.replace(File.separatorChar, '/');
                }
            } else if (line.startsWith(NEW_REVISION) && lastFilePath != null) {
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
            } else if (line.startsWith(INITIAL_REVISION) && lastFilePath != null) {
                String revision = line.substring(INITIAL_REVISION.length()).trim();
                revision(revision, CommitInformation.ADDED);
            }
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
            elements[0] = relativePath + lastFilePath;
            elements[1] = "Up-to-date";
            elements[2] = revision;
            elements[3] = ""; // time
            elements[4] = ""; // date
            elements[5] = null;// findSticky(lastFilePath); - load it asynchronously in flushElements()
            elements[6] = ""; // lockers
            if (CommitInformation.REMOVED == operationType) {
                elements[7] = relativePath + lastFilePath; // removed file
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

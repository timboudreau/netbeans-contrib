/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.profiles.vss.list;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.VcsDirContainer;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.cmdline.VcsListRecursiveCommand;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;
import org.netbeans.modules.vcscore.util.VcsUtilities;

/**
 * The recursive refresh command.
 * Runs "ss diff -R" and "ss status -R" commands.
 *
 * @author  Martin Entlicher
 */
public class VssListRecursive extends VcsListRecursiveCommand implements CommandDataOutputListener {
    
    private static final String DIFFING_ENG = "Diffing:"; // NOI18N
    private static final String DIFFING_LOC = org.openide.util.NbBundle.getBundle(VssListRecursive.class).getString("VSS_ProjectDiffing"); // NOI18N
    private static final String AGAINST_ENG = "Against:"; // NOI18N
    private static final String AGAINST_LOC = org.openide.util.NbBundle.getBundle(VssListRecursive.class).getString("VSS_ProjectAgainst"); // NOI18N
    
    private String dir = null; // The local dir being refreshed.
    private String relDir = null;
    private CommandOutputListener stdoutNRListener = null;
    private CommandOutputListener stderrNRListener = null;
    private CommandDataOutputListener stdoutListener = null;
    private CommandDataOutputListener stderrListener = null;
    private String dataRegex = null;
    private String errorRegex = null;
    private VcsFileSystem fileSystem;
    private VcsDirContainer rootFilesByNameCont;
    private List undistiguishable = new ArrayList();
    
    private String PROJECT_BEGIN, LOCAL_FILES, SOURCE_SAFE_FILES, DIFFERENT_FILES;
    private String DIFFING, AGAINST;
    
    /** Creates a new instance of VssListRecursive */
    public VssListRecursive() {
    }
    
    public void setFileSystem(VcsFileSystem fileSystem) {
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
        String ps = (String) vars.get("PS");
        if (ps == null) ps = File.separator;
        else ps = Variables.expand(vars, ps, false);
        relDir = new String(dir);
        if (dir.equals("")) { // NOI18N
            dir=rootDir;
            if (module != null && module.length() > 0) {
                dir += ps + module;
                relDir = new String(module);
            }
        } else {
            if (module == null || module.length() == 0)
                dir=rootDir+ps+dir;
            else {
                dir=rootDir+ps+module+ps+dir;
                relDir = new String(module+ps+relDir);
            }
        }
        dir = rootDir;
        if (dir.charAt(dir.length() - 1) == File.separatorChar) {
            dir = dir.substring(0, dir.length() - 1);
        }
        relDir = "";//relDir.replace(ps.charAt(0), '/');
    }

    private boolean runCommand(Hashtable vars, String cmdName, final boolean[] errMatch,
                               CommandDataOutputListener outputListener) throws InterruptedException {
        String workingDirPath = "${ROOTDIR}${PS}${MODULE}${PS}${DIR}"; // NOI18N
        workingDirPath = Variables.expand(vars, workingDirPath, false);
        File workingDir = new File(workingDirPath);
        File tmpEmptyDir = null;
        if (!workingDir.exists()) {
            File tmpDir = new File(System.getProperty("java.io.tmpdir"));
            String tmpDirName = "refresh"; // NOI18N
            for (int i = 0; ; i++) {
                tmpEmptyDir = new File(tmpDir, tmpDirName + i);
                if (!tmpEmptyDir.exists()) {
                    tmpEmptyDir.mkdir();
                    break;
                }
            }
            vars.put("EMPTY_REFRESH_FOLDER", tmpEmptyDir.getAbsolutePath());
        }
        try {
            VcsCommand cmd = fileSystem.getCommand(cmdName);
            VcsCommandExecutor ec = fileSystem.getVcsFactory().getCommandExecutor(cmd, vars);
            ec.addDataOutputListener(outputListener);
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
        } finally {
            if (tmpEmptyDir != null) {
                tmpEmptyDir.delete();
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
        this.rootFilesByNameCont = filesByNameCont;
        boolean localized = false;
        if (args.length > 1 && VssListCommand.ARG_LOC.equals(args[0])) {
            localized = true;
            String[] newArgs = new String[args.length - 1];
            System.arraycopy(args, 1, newArgs, 0, newArgs.length);
            args = newArgs;
        }
        if (args.length < 3) {
            stderrNRListener.outputLine("Expecting three commands as arguments: directory reader, directory status reader and file status reader."); //NOI18N
            return false;
        }
        PROJECT_BEGIN = (localized) ? VssListCommand.PROJECT_BEGIN_LOC : VssListCommand.PROJECT_BEGIN_ENG;
        LOCAL_FILES = (localized) ? VssListCommand.LOCAL_FILES_LOC : VssListCommand.LOCAL_FILES_ENG;
        SOURCE_SAFE_FILES = (localized) ? VssListCommand.SOURCE_SAFE_FILES_LOC : VssListCommand.SOURCE_SAFE_FILES_ENG;
        DIFFERENT_FILES = (localized) ? VssListCommand.DIFFERENT_FILES_LOC : VssListCommand.DIFFERENT_FILES_ENG;
        DIFFING = (localized) ? DIFFING_LOC : DIFFING_ENG;
        AGAINST = (localized) ? AGAINST_LOC : AGAINST_ENG;
        initDir(vars);
        readLocalFiles(rootFilesByNameCont.getPath(), rootFilesByNameCont);
        boolean[] errMatch = new boolean[1];
        try {
            runCommand(vars, args[0], errMatch, this);
        } catch (InterruptedException iexc) {
            return false;
        }
        if (!errMatch[0]) {
            flushLastFile();
            try {
                runCommand(vars, args[1], errMatch, new CommandDataOutputListener() {
                    public void outputData(String[] elements) {
                        statusOutputData(elements);
                    }
                });
            } catch (InterruptedException iexc) {
                return false;
            }
            if (!errMatch[0] && undistiguishable.size() > 0) {
                try {
                    processUndistinguishableFiles(vars, args[2]);
                } catch (InterruptedException iexc) {
                    return false;
                }
            }
        }
        printOutputData(rootFilesByNameCont);
        return !errMatch[0];
    }
    
    private void printOutputData(VcsDirContainer filesByNameCont) {
        stdoutListener.outputData(new String[] { "Path:", filesByNameCont.getPath() });
        Hashtable filesByName = (Hashtable) filesByNameCont.getElement();
        if (filesByName != null) {
            for (Iterator it = filesByName.values().iterator(); it.hasNext(); ) {
                String[] statuses = (String[]) it.next();
                stdoutListener.outputData(statuses);
            }
        }
        VcsDirContainer[] subCont = filesByNameCont.getSubdirContainers();
        for (int i = 0; i < subCont.length; i++) {
            printOutputData(subCont[i]);
        }
    }
    
    private void processUndistinguishableFiles(Hashtable vars, String cmdName) throws InterruptedException {
        VcsCommand cmd = fileSystem.getCommand(cmdName);
        if (cmd != null) {
            String dir = (String) vars.get("DIR");
            if (dir == null) {
                dir = "";
            }
            boolean dirAppend = dir.length() > 0;
            String psStr = (String) vars.get("PS");
            char ps;
            if (psStr == null) ps = File.separatorChar;
            else {
                psStr = Variables.expand(vars, psStr, false);
                ps = psStr.charAt(0);
            }
            for (Iterator it = undistiguishable.iterator(); it.hasNext(); ) {
                String filePath = (String) it.next();
                int slashIndex = filePath.lastIndexOf('/');
                String path = filePath.substring(0, slashIndex);
                String pattern = filePath.substring(slashIndex + 1);
                VcsDirContainer filesByNameCont = (path.length() > 0) ? rootFilesByNameCont.getContainerWithPath(path) : rootFilesByNameCont;
                Hashtable filesByName = (Hashtable) filesByNameCont.getElement();
                Hashtable varsCmd = new Hashtable(vars);
                varsCmd.put("DIR", (dirAppend) ? (dir + ps + path.replace('/', ps)) : path.replace('/', ps));
                String[] files = getUndistinguishableFiles(pattern, filesByName.keySet());
                for (int i = 0; i < files.length; i++) {
                    String file = files[i];
                    varsCmd.put("FILE", file);
                    final String[] statuses = (String[]) filesByName.get(file);
                    //cmd.setProperty(UserCommand.PROPERTY_DATA_REGEX, "^"+file.substring(0, Math.min(STATUS_POSITION, file.length()))+" (.*$)");
                    statuses[2] = null;
                    VcsCommandExecutor vce = fileSystem.getVcsFactory().getCommandExecutor(cmd, varsCmd);
                    vce.addDataOutputListener(new CommandDataOutputListener() {
                        public void outputData(String[] elements) {
                            if (elements != null) {
                                //D.deb(" ****  status match = "+VcsUtilities.arrayToString(elements));
                                if (elements[0].indexOf(PROJECT_BEGIN) == 0) return ; // skip the $/... folder
                                if (statuses[2] != null) return ; // The status was already set and we get some garbage
                                VssListCommand.addStatuses(elements, statuses);
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
                }
            }
        }
    }
    
    private String[] getUndistinguishableFiles(String pattern, Collection allFiles) {
        List files = new ArrayList();
        for (Iterator it = allFiles.iterator(); it.hasNext(); ) {
            String file = (String) it.next();
            if (file.startsWith(pattern)) {
                files.add(file);
            }
        }
        return (String[]) files.toArray(new String[0]);
    }
    
    private VcsDirContainer getFilesContainer(String path) {
        VcsDirContainer filesContainer;
        if (path.length() == 0) {
            filesContainer = rootFilesByNameCont;
        } else {
            filesContainer = rootFilesByNameCont.getContainerWithPath(path);
            if (filesContainer == null) {
                filesContainer = rootFilesByNameCont.addSubdirRecursive(path);
            }
        }
        if (filesContainer.getElement() == null) {
            filesContainer.setElement(new Hashtable());
        }
        return filesContainer;
    }
    
    private void readLocalFiles(String path, VcsDirContainer filesCont) {
        File fileDir = (path.length() > 0) ? new File(dir, path) : new File(dir);
        Hashtable filesByName = (Hashtable) filesCont.getElement();
        if (filesByName == null) {
            filesByName = new Hashtable();
            filesCont.setElement(filesByName);
        }
        String[] subFiles = fileDir.list();
        if (subFiles == null) return ;
        //File ignoredFile = new File(dir, VssListCommand.IGNORED_FILE);
        for (int i = 0; i < subFiles.length; i++) {
            File file = new File(fileDir, subFiles[i]);
            if (file.isFile() && file.getName().compareTo(VssListCommand.IGNORED_FILE) != 0) {
                String[] statuses = (String[]) filesByName.get(subFiles[i]);
                if (statuses == null) {
                    statuses = new String[3];
                }
                statuses[0] = subFiles[i];
                statuses[1] = VssListCommand.STATUS_CURRENT;
                filesByName.put(subFiles[i], statuses);
            }
        }
        String cpath = filesCont.getPath();
        if (cpath.length() > 0) {
            int slashIndex = cpath.lastIndexOf('/');
            if (slashIndex < 0) slashIndex = 0;
            String dirParent = cpath.substring(0, slashIndex);
            if (slashIndex > 0) slashIndex++;
            String dirName = cpath.substring(slashIndex) + '/';
            VcsDirContainer parentCont = rootFilesByNameCont.getContainerWithPath(dirParent);
            if (parentCont != null && parentCont.getPath().equals(filesCont.getPath())) parentCont = null;
            if (parentCont != null) {
                filesByName = (Hashtable) parentCont.getElement();
                if (filesByName != null) {
                    String[] statuses = new String[3];
                    statuses[0] = dirName;
                    if (fileDir.exists()) {
                        statuses[1] = VssListCommand.STATUS_CURRENT;
                    } else {
                        statuses[1] = VssListCommand.STATUS_MISSING;
                    }
                    filesByName.put(dirName, statuses);
                }
            }
        }
    }
    
    
    private boolean gettingFolders = true;
    private boolean gettingLocalFiles = false;
    private boolean gettingSourceSafeFiles = false;
    private boolean gettingDifferentFiles = false;
    private String lastFileName = "";
    private VcsDirContainer lastFilesCont = null;
    
    private void removeLocalFiles() {
        if (lastFileName == null || lastFilesCont == null) return ;
        Hashtable filesByName = (Hashtable) lastFilesCont.getElement();
        Collection currentFiles = filesByName.keySet();
        while (lastFileName.length() > 0) {
            String fileName = VssListCommand.getFileFromRow(currentFiles, lastFileName);
            currentFiles.remove(fileName.trim());
            lastFileName = lastFileName.substring(fileName.length());
        }
    }
    
    private void flushLastFile() {
        if (lastFileName == null || lastFileName.length() == 0 || lastFilesCont == null) return ;
        if (gettingSourceSafeFiles) {
            Hashtable filesByName = (Hashtable) lastFilesCont.getElement();
            String fileName = lastFileName.trim();
            String[] statuses = (String[]) filesByName.get(fileName);
            if (statuses == null) {
                statuses = new String[3];
                statuses[0] = fileName;
            }
            filesByName.put(fileName, statuses);
            statuses[1] = VssListCommand.STATUS_MISSING;
            lastFileName = "";
        } else if (gettingDifferentFiles) {
            Hashtable filesByName = (Hashtable) lastFilesCont.getElement();
            String fileName = lastFileName.trim();
            String[] statuses = (String[]) filesByName.get(fileName);
            if (statuses == null) {
                statuses = new String[3];
                statuses[0] = fileName;
            }
            filesByName.put(fileName, statuses);
            statuses[1] = VssListCommand.STATUS_LOCALLY_MODIFIED;
            lastFileName = "";
        } else if (gettingLocalFiles) {
            removeLocalFiles();
        }
    }
    
    private boolean diffingPathFollows = false;
    
    /** Parse the output of "ss dir -R -F- && ss diff -R" commands
     * ss dir -R -F- gives the subfolders in the given folder
     * ss diff -R gives the differences between the local folders and the repository.
     */
    public void outputData(String[] elements) {
        String line = elements[0];
        //System.out.println("outputData("+line+")");
        if (line == null) return;
        String file = line.trim();
        if (!diffingPathFollows && file.startsWith(PROJECT_BEGIN)) { // Listing of a folder from "dir" will follow
            String folder = file.substring(PROJECT_BEGIN.length(), file.length() - 1);
            folder = folder.replace(File.separatorChar, '/');
            if (folder.startsWith(relDir)) {
                folder = folder.substring(relDir.length());
                if (folder.startsWith("/")) folder = folder.substring(1);
            }
            gettingFolders = true;
            lastFilesCont = getFilesContainer(folder);
        } else if (gettingFolders && file.startsWith("$")) { // A folder
            String folder = file.substring(1);
            folder = folder.replace(File.separatorChar, '/');
            folder = (lastFilesCont.getPath().length() > 0) ? (lastFilesCont.getPath() + '/' + folder) : folder;
            VcsDirContainer subDir = lastFilesCont.addSubdir(folder);
            readLocalFiles(folder, subDir);
        } else if (file.startsWith(DIFFING) || diffingPathFollows) {
            flushLastFile();
            gettingFolders = false;
            String folder;
            if (diffingPathFollows) {
                folder = file;
                diffingPathFollows = false;
            } else {
                folder = file.substring(DIFFING.length());
            }
            if (folder.length() == 0) {
                diffingPathFollows = true;
            } else {
                folder = folder.trim();
                folder = folder.substring(PROJECT_BEGIN.length()); // remove "/$"
                folder = folder.replace(File.separatorChar, '/');
                if (folder.startsWith(relDir)) {
                    folder = folder.substring(relDir.length());
                    if (folder.startsWith("/")) folder = folder.substring(1);
                }
                lastFilesCont = getFilesContainer(folder);
                gettingLocalFiles = false;
                gettingSourceSafeFiles = false;
                gettingDifferentFiles = false;
            }
        } else if (LOCAL_FILES.equals(file)) {
            gettingLocalFiles = true;
        } else if (SOURCE_SAFE_FILES.equals(file)) {
            if (gettingLocalFiles) {
                removeLocalFiles();
                gettingLocalFiles = false;
            }
            gettingSourceSafeFiles = true;
        } else if (DIFFERENT_FILES.equals(file)) {
            if (gettingLocalFiles) {
                removeLocalFiles();
                gettingLocalFiles = false;
            }
            flushLastFile();
            gettingSourceSafeFiles = false;
            gettingDifferentFiles = true;
        } else if (gettingLocalFiles) {
            lastFileName += line;
        } else if (gettingSourceSafeFiles || gettingDifferentFiles) {
            if (line.startsWith("  ")) {
                flushLastFile();
                lastFileName += line;
            }
        }
    }
    
    private boolean skipNextLine = false;
    
    private void statusOutputData(String[] elements) {
        if (elements[0] == null) return;
        String file = elements[0].trim();
        if (file.startsWith(PROJECT_BEGIN)) { // Listing of a folder from "dir" will follow
            String folder = file.substring(PROJECT_BEGIN.length(), file.length() - 1);
            folder = folder.replace(File.separatorChar, '/');
            if (folder.startsWith(relDir)) {
                folder = folder.substring(relDir.length());
                if (folder.startsWith("/")) folder = folder.substring(1);
            }
            gettingFolders = true;
            lastFilesCont = getFilesContainer(folder);
            skipNextLine = false;
        } else if (!skipNextLine) {
            int index = Math.min(VssListCommand.STATUS_POSITION + 1, elements[0].length());
            int index2 = elements[0].indexOf("  ", index);
            if (index2 < 0) index2 = elements[0].length();
            if (index < index2) {
                skipNextLine = true;
                Hashtable filesByName = (Hashtable) lastFilesCont.getElement();
                String pattern = elements[0].substring(0, VssListCommand.STATUS_POSITION).trim();
                file = getDistinguishable(pattern, filesByName);
                if (file != null) {
                    String[] statuses = (String[]) filesByName.get(file);
                    statuses[2] = VssListCommand.addLocker(statuses[2], VssListCommand.parseLocker(elements[0].substring(index, index2).trim()));
                } else {
                    undistiguishable.add(lastFilesCont.getPath()+"/"+pattern);
                }
            }
        } else {
            skipNextLine = false;
        }
    }
    
    private static String getDistinguishable(String pattern, Map files) {
        String file = null;
        for (Iterator it = files.keySet().iterator(); it.hasNext(); ) {
            String fileName = (String) it.next();
            if (pattern.equals(fileName.substring(0, Math.min(pattern.length(), fileName.length())))) {
                if (file != null) {
                    return null;
                } else {
                    file = fileName;
                }
            }
        }
        return file;
    }
}

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

import java.lang.reflect.*;
import java.io.*;
import java.util.*;

import org.openide.TopManager;
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

/**
 * The cvs commit command wrapper. This class ensures, that only files, that should
 * be committed with the current template are actually committed. If files with
 * different template are to be committed, the commit is executed multiple times.
 *
 * @author  Martin Entlicher
 */
public class CvsCommit extends Object implements VcsAdditionalCommand {

    private static final String WIN_CAT_NAME = "wincat";
    private static final String WIN_CAT_EXT = "bat";
    private static final String WIN_CAT_FOLDER = "vcs";
    private static final String WIN_CAT_CONTENT = "@echo off\ntype %1\n";
    
    private static final String SYSTEM = "system";

    private static final String COMMITTING = "CVS: Committing in";
    private static final String PRE_FILE = "CVS: \t";

    private static final String TEMP_FILE_PREFIX = "tempCommit";
    private static final String TEMP_FILE_SUFFIX = "output";
    
    private VcsFileSystem fileSystem = null;
    
    private HashMap cachedEntries = new HashMap();

    /** Creates new CvsCommit */
    public CvsCommit() {
    }

    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
    
    private static ArrayList getFilePaths(String paths, char ps) {
        ArrayList files = new ArrayList();
        if (paths != null && paths.length() > 0) {
            int len = paths.length();
            int begin = 0;
            do {
                int index = paths.indexOf(""+ps + ps, begin);
                if (index < 0) index = len;
                String file = paths.substring(begin, index);
                files.add(file.replace(ps, '/'));
                begin = index + 2;
            } while (begin < len);
        }
        return files;
    }

    /*
    private String getFilePath(FileSystem fs, FileObject fo) {
            File root = null;
            //if (fs instanceof LocalFileSystem) root = ((LocalFileSystem) fs).getRootDirectory();
            //if (fs instanceof VcsFileSystem) root = ((VcsFileSystem) fs).getRootDirectory();
            Method getRootMethod = null;
            try {
                getRootMethod = fs.getClass().getMethod("getRootDirectory", new Class[0]);
            } catch (NoSuchMethodException nmexc) {
            }
            if (getRootMethod != null) {
                try {
                    root = (File) getRootMethod.invoke(fs, new Object[0]);
                } catch (IllegalAccessException iaexc) {
                    TopManager.getDefault().notifyException(iaexc);
                } catch (InvocationTargetException itexc) {
                    TopManager.getDefault().notifyException(itexc);
                }
            }
            String packageName = fo.getPackageNameExt(java.io.File.separatorChar, '.');
            String path;
            if (root != null) {
                path = new File(root, packageName).getAbsolutePath();
            } else {
                path = packageName;
            }
            return path;
    }

    
    private String createCatExec() {
        String cat = "";
        if (org.openide.util.Utilities.isWindows()) {
            FileSystem fs = org.openide.TopManager.getDefault().getRepository().getDefaultFileSystem();
            FileObject winCat = fs.findResource(WIN_CAT_FOLDER+"/"+WIN_CAT);
            if (winCat == null && fs instanceof AbstractFileSystem.Change) {
                AbstractFileSystem.Change chfs = (AbstractFileSystem.Change) fs;
                try {
                    chfs.createFolder(WIN_CAT_FOLDER);
                } catch (IOException exc) {
                    // ignored
                }
                try {
                    chfs.createData(WIN_CAT);
                } catch (IOException exc) {
                    // ignored
                }
                winCat = fs.findResource(WIN_CAT_FOLDER+"/"+WIN_CAT);
                if (winCat != null) {
                    OutputStream out = null;
                    try {
                        out = winCat.getOutputStream(new FileLock());
                        out.write(WIN_CAT_CONTENT.getBytes());
                    } catch (IOException ioexc) {
                    } finally {
                        if (out != null) {
                            try {
                                out.close();
                            } catch (IOException ioexc2) {}
                        }
                    }
                }
            }
            if (winCat != null) {
                cat = getFilePath(fs, winCat);
            }
        }
        return cat;
    }
     */

    /**
     * Try to create a shared wincat.bat file.
     * @return the path to the file or <code>null</code> when the file can not be
     * created or the path contains spaces. CVS can not handle spaces in the editor executable.
     */
    private static String createCatExec() {
        String cat = "";
        //if (org.openide.util.Utilities.isWindows()) {
        FileSystem fs = org.openide.TopManager.getDefault().getRepository().getDefaultFileSystem();
        FileObject root = fs.getRoot();
        java.io.File rootFile = FileUtil.toFile(root);
        if (rootFile == null || rootFile.getAbsolutePath().indexOf(' ') >= 0) {
            // The default FS directory either does not exist or contains spaces.
            // Do not create the shared wincat.bat file in this case, we have to
            // perform a workaround - put wincat.bat into the current folder.
            return null;
        }
        FileObject winCat = fs.findResource(WIN_CAT_FOLDER+"/"+WIN_CAT_NAME+"."+WIN_CAT_EXT);
        //System.out.println("winCat = "+winCat+", fs = "+fs);
        //System.out.println("fs instanceof AbstractFileSystem.Change = "+(fs instanceof AbstractFileSystem.Change));
        if (winCat == null) {
            FileObject folder = root.getFileObject(WIN_CAT_FOLDER);
            if (folder == null) {
                try {
                    folder = root.createFolder(WIN_CAT_FOLDER);
                } catch (IOException exc) {
                    return cat;
                }
            }
            winCat = folder.getFileObject(WIN_CAT_NAME, WIN_CAT_EXT);
            if (winCat == null) {
                try {
                    winCat = folder.createData(WIN_CAT_NAME, WIN_CAT_EXT);
                } catch (IOException exc) {
                    return cat;
                }
            }
            if (winCat != null) {
                OutputStream out = null;
                try {
                    out = winCat.getOutputStream(winCat.lock());//new FileLock());
                    out.write(WIN_CAT_CONTENT.getBytes());
                } catch (IOException ioexc) {
                    //ioexc.printStackTrace();
                } finally {
                    if (out != null) {
                        try {
                            out.close();
                        } catch (IOException ioexc2) {}
                    }
                }
            }
        }
        if (winCat != null) {
            String userHome = System.getProperty("netbeans.user");
            cat = userHome + File.separator + SYSTEM + File.separator + winCat.getPackageNameExt(File.separatorChar, '.');
            cat = org.openide.util.Utilities.replaceString(cat, "\\", "\\\\");
            //System.out.println("cat = "+cat);
            //cat = getFilePath(fs, winCat);
        }
        return cat;
    }
    
    private static synchronized String createLocalCatExec(String dir) {
        File dirFile = new File(dir);
        String cat = WIN_CAT_NAME + "." + WIN_CAT_EXT;
        File catFile;
        for (int i = 0; (catFile = new File(dirFile, cat)).exists(); i++) {
            cat = WIN_CAT_NAME + i + "." + WIN_CAT_EXT;
        }
        OutputStream out = null;
        try {
            catFile.createNewFile();
            out = new BufferedOutputStream(new FileOutputStream(catFile));
            out.write(WIN_CAT_CONTENT.getBytes());
        } catch (IOException ioexc) {
            //ioexc.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ioexc2) {}
            }
        }
        return catFile.getName();
    }
    
    private static synchronized void removeLocalCatExec(String dir, String catName) {
        File catFile = new File(dir, catName);
        catFile.delete();
    }
    
    private ArrayList getCommitedFiles(String fsRoot, String template, char ps) {
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

    private VcsCommandExecutor[] doCommit(CommandsPool cpool, VcsCommand cmdCommit, ArrayList filesList, Hashtable vars) {
        Table files = getFiles(filesList);
        VcsCommandExecutor[] executors = VcsAction.doCommand(files, cmdCommit, vars, fileSystem);
        //VcsCommandExecutor commit = fileSystem.getVcsFactory().getCommandExecutor(cmdCommit, vars);
        //int preprocessStatus = cpool.preprocessCommand(commit, vars);
        //cpool.startExecutor(commit);
        //return preprocessStatus;
        for (int i = 0; i < executors.length; i++) {
            try {
                cpool.waitToFinish(executors[i]);
            } catch (InterruptedException iexc) {
                for (int j = i; j < executors.length; j++) {
                    cpool.kill(executors[j]);
                }
                return new VcsCommandExecutor[0];
            }
        }
        return executors;
    }
    
    public boolean exec(Hashtable vars, String[] args,
                        CommandOutputListener stdoutNRListener, CommandOutputListener stderrNRListener,
                        CommandDataOutputListener stdoutListener, String dataRegex,
                        CommandDataOutputListener stderrListener, String errorRegex) {
        if (args.length < 2) {
            stderrNRListener.outputLine("The cvs commit template getter and cvs commit command are expected as arguments");
            return false;
        }
        String psStr = (String) vars.get("PS");
        if (psStr != null) {
            psStr = Variables.expand(vars, psStr, true);
        }
        char ps = (psStr == null || psStr.length() < 1) ? java.io.File.pathSeparatorChar : psStr.charAt(0);
        String fsRoot = ((String) vars.get("ROOTDIR")) + ps + vars.get("MODULE");
        fsRoot = fsRoot.replace('/', ps);
        boolean haveLocalCat = false;
        String wincat = null;
        if (org.openide.util.Utilities.isWindows()) {
            wincat = createCatExec();
            if (wincat == null) {
                haveLocalCat = true;
                wincat = createLocalCatExec(fsRoot);
            }
            vars.put("WINCAT", wincat);
        }
        final StringBuffer buff = new StringBuffer();
        ArrayList filePaths = getFilePaths((String) vars.get("PATHS"), ps);
        CommandsPool cpool = fileSystem.getCommandsPool();
        Hashtable varsOriginal = new Hashtable(vars);
        boolean committed = false;
        ArrayList alreadyCommittedFiles = new ArrayList();
        do {
            VcsCommand cmdTemplate = fileSystem.getCommand(args[0]);
            VcsCommand cmdCommit1 = fileSystem.getCommand(args[1]);
            VcsCommand cmdCommit = new UserCommand();
            ((UserCommand) cmdCommit).copyFrom(cmdCommit1);
            cmdCommit.setDisplayName(NbBundle.getMessage(CvsCommit.class, "CvsCommit.commitCommandName"));
            buff.delete(0, buff.length());
            VcsCommandExecutor template = fileSystem.getVcsFactory().getCommandExecutor(cmdTemplate, vars);
            template.addDataOutputListener(new CommandDataOutputListener() {
                public void outputData(String[] elements) {
                    buff.append(((elements[0] != null) ? elements[0] : "") + "\n");
                }
            });
            cpool.preprocessCommand(template, vars, fileSystem);
            cpool.startExecutor(template);
            try {
                cpool.waitToFinish(template);
            } catch (InterruptedException iexc) {
                cpool.kill(template);
                break;
            }
            String buffered = buff.toString();
            ArrayList filesCommited = getCommitedFiles(fsRoot, buffered, ps);
            buffered = addGroupsComment(vars, buffered);
            vars.put("FILE_TEMPLATE", fileOutput(buffered));
            // commit all remaining files if they can not be retrieved from the template
            if (filesCommited == null || filesCommited.size() == 0) {
                if (committed) break;
                filesCommited = new ArrayList(filePaths);
            }
            filesCommited.removeAll(alreadyCommittedFiles);
            if (filesCommited.size() == 0) break ;
            //setProcessingFiles(filesCommited, vars);
            VcsCommandExecutor[] executors = doCommit(cpool, cmdCommit, filesCommited, vars);
            cmdCommit1.setProperty(CommandExecutorSupport.INPUT_DESCRIPTOR_PARSED,
                cmdCommit.getProperty(CommandExecutorSupport.INPUT_DESCRIPTOR_PARSED));
            if (executors.length == 0) {
                break;
            }
            committed = true;
            alreadyCommittedFiles.addAll(filesCommited);
            filePaths.removeAll(filesCommited);
            vars = new Hashtable(varsOriginal);
        } while (filePaths.size() > 0);
        //cpool.preprocessCommand(vce, vars);
        if (haveLocalCat && wincat != null) removeLocalCatExec(fsRoot, wincat);
        return true;
    }
    
    private static String addGroupsComment(Hashtable vars, String template) {
        String description = (String) vars.get(Variables.GROUP_DESCRIPTION);
        if (description != null && description.length() > 0) {
            template = description + "\n" + template; // NOI18N
        }
        return template;
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
            TopManager.getDefault().notifyException(ioexc);
            return null;
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ioexc) {
                    TopManager.getDefault().notifyException(ioexc);
                }
            }
        }
        return outputFile.getAbsolutePath();
    }
}

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

package org.netbeans.modules.vcs.profiles.cvsprofiles.commands;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Hashtable;
import java.util.regex.Pattern;

import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;

import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;
import org.netbeans.spi.vcs.commands.CommandSupport;

import org.netbeans.modules.vcscore.Variables;
import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.cmdline.ExecuteCommand;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.cmdline.exec.ExternalCommand;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.CommandProcessor;
import org.netbeans.modules.vcscore.commands.RegexOutputListener;
import org.netbeans.modules.vcscore.commands.VcsDescribedCommand;
import org.netbeans.modules.vcscore.util.VcsUtilities;

/**
 * Command, that executes the template getter command and stores the original
 * output into a temporary file.
 *
 * @author  Martin Entlicher
 */
public class CvsCommitTemplateGetter implements VcsAdditionalCommand, RegexOutputListener {
    
    private static final String WIN_CAT_NAME = ".nbcicat";                   // NOI18N
    private static final String WIN_CAT_EXT = "bat";                         // NOI18N
    private static final String WIN_CAT_FOLDER = "vcs";                      // NOI18N
    private static final String WIN_CAT_CONTENT = "@echo off\ntype %1\n";    // NOI18N
    
    private static final String TEMP_FILE_PREFIX = "tempVcsCmd"; // NOI18N
    private static final String TEMP_FILE_SUFFIX = "output";     // NOI18N

    private VcsFileSystem fileSystem = null;
    private StringBuffer output = new StringBuffer();
    private CommandDataOutputListener stdoutDataListener;
    
    /** Creates a new instance of Echo */
    public CvsCommitTemplateGetter() {
    }
    
    public void setFileSystem(VcsFileSystem fileSystem) {
        this.fileSystem = fileSystem;
    }
    
    /**
     * Try to create a shared wincat.bat file.
     * @return the path to the file or <code>null</code> when the file can not be
     * created or the path contains spaces. CVS can not handle spaces in the editor executable.
     */
    private static String createCatExec() {
        String cat = "";
        //if (org.openide.util.Utilities.isWindows()) {
        FileSystem fs = org.openide.filesystems.Repository.getDefault().getDefaultFileSystem();
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
            File catFile = FileUtil.toFile(winCat);
            if (catFile != null) {
                cat = catFile.getAbsolutePath();
            }
            cat = org.openide.util.Utilities.replaceString(cat, "\\", "\\\\");
            //System.out.println("cat = "+cat);
            //cat = getFilePath(fs, winCat);
        }
        return cat;
    }
    
    private static synchronized String createLocalCatExec(String dir, String relativePath) {
        File dirFile;
        if (relativePath == null) {
            dirFile = new File(dir);
        } else {
            dirFile = new File(dir, relativePath);
        }
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
        catFile.deleteOnExit();
        return catFile.getName();
    }
    
    private static boolean checkFileExistsInSubdirs(File folder, String fileName) {
        if (new File(folder, fileName).exists()) return true;
        File[] subFiles = folder.listFiles();
        if (subFiles == null) return false;
        for (int i = 0; i < subFiles.length; i++) {
            if (subFiles[i].isDirectory() && "CVS".compareToIgnoreCase(subFiles[i].getName()) != 0) {
                if (checkFileExistsInSubdirs(subFiles[i], fileName)) return true;
            }
        }
        return false;
    }
    
    private static void createCatExecsInSubfolders(File folder, String catName) {
        File catFile = new File(folder, catName);
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
        catFile.deleteOnExit();
        File[] subFiles = folder.listFiles();
        if (subFiles == null) return ;
        for (int i = 0; i < subFiles.length; i++) {
            if (subFiles[i].isDirectory() && "CVS".compareToIgnoreCase(subFiles[i].getName()) != 0) {
                createCatExecsInSubfolders(subFiles[i], catName);
            }
        }
    }
    
    private static synchronized String createLocalCatExecs(String dir, String relativePath) {
        File dirFile;
        if (relativePath == null) {
            dirFile = new File(dir);
        } else {
            dirFile = new File(dir, relativePath);
        }
        String cat = WIN_CAT_NAME + "." + WIN_CAT_EXT;
        for (int i = 0; checkFileExistsInSubdirs(dirFile, cat); i++) {
            cat = WIN_CAT_NAME + i + "." + WIN_CAT_EXT;
        }
        createCatExecsInSubfolders(dirFile, cat);
        return cat;
    }
    
    private static synchronized void removeLocalCatExec(String dir, String relativePath, String catName) {
        File dirFile;
        if (relativePath == null) {
            dirFile = new File(dir);
        } else {
            dirFile = new File(dir, relativePath);
        }
        File catFile = new File(dirFile, catName);
        catFile.delete();
    }
    
    private static void removeCatExecsInSubfolders(File folder, String catName) {
        new File(folder, catName).delete();
        File[] subFiles = folder.listFiles();
        if (subFiles == null) return ;
        for (int i = 0; i < subFiles.length; i++) {
            if (subFiles[i].isDirectory() && "CVS".compareToIgnoreCase(subFiles[i].getName()) != 0) {
                removeCatExecsInSubfolders(subFiles[i], catName);
            }
        }
    }
    
    private static synchronized void removeLocalCatExecs(String dir, String relativePath, String catName) {
        File dirFile;
        if (relativePath == null) {
            dirFile = new File(dir);
        } else {
            dirFile = new File(dir, relativePath);
        }
        removeCatExecsInSubfolders(dirFile, catName);
    }
    
    /**
     * This command implements messaging command and thus it can have the message
     * set in "message" variable.
     */
    private static String getMessageComment(Hashtable vars) {
        String description = (String) vars.get("message");
        if (description != null && description.length() > 0) {
            return description + "\n"; // NOI18N
        } else {
            return "";
        }
    }
    
    public boolean exec(Hashtable vars, String[] args,
                        CommandOutputListener stdoutListener,
                        CommandOutputListener stderrListener,
                        CommandDataOutputListener stdoutDataListener, String dataRegex,
                        CommandDataOutputListener stderrDataListener, String errorRegex) {
        if (args.length < 1) {
            stderrListener.outputLine("Expecting a command name as an argument.");
            return false;
        }
        String IDStr = (String) vars.get("COMMIT_COMMANDS_IDS");
        if (IDStr != null) {
            try {
                waitForCommands(IDStr);
            } catch (InterruptedException iex) {
                Thread.currentThread().interrupt();
                return false;
            }
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
        if (relativePath != null) {
            relativePath = relativePath.replace(File.separatorChar, '/');
        }
        fsRoot = fsRoot.replace('/', ps);
        boolean haveLocalCat = false;
        String wincat = null;
        if (org.openide.util.Utilities.isWindows()) {
            wincat = createCatExec();
            if (wincat == null) {
                haveLocalCat = true;
                wincat = createLocalCatExecs(fsRoot, relativePath);
            }
            vars.put("WINCAT", wincat);
        }
        
        output.append(getMessageComment(vars));
        try {
            this.stdoutDataListener = stdoutDataListener;
            CommandSupport cmdSupport = fileSystem.getCommandSupport(args[0]);
            if (cmdSupport == null) return true; // Nothing to run
            Command cmd = cmdSupport.createCommand();
            if (cmd instanceof VcsDescribedCommand) {
                ((VcsDescribedCommand) cmd).setAdditionalVariables(vars);
                ((VcsDescribedCommand) cmd).addRegexOutputListener(this);
                ((VcsDescribedCommand) cmd).addTextOutputListener(stdoutListener);
                ((VcsDescribedCommand) cmd).addTextErrorListener(stderrListener);
                //((VcsDescribedCommand) cmd).addRegexOutputListener(stdoutDataListener);
                //((VcsDescribedCommand) cmd).addRegexErrorListener(stderrDataListener);
            }
            //VcsCommandExecutor executor = fileSystem.getVcsFactory().getCommandExecutor(cmd, vars);
            //if (!VcsManager.getDefault().showCustomizer(cmd)) throw new UserCancelException();
            CommandTask task = cmd.execute();
            try {
                task.waitFinished(0);
            } catch (InterruptedException iex) {
                task.stop();
                Thread.currentThread().interrupt();
                return false;
            }
            String committedStr = (String) vars.get("IS_COMMITTED");
            vars.clear(); // So that many variables are not unnecessarily updated.
            String outputStr = output.toString();
            vars.put("ORIGINAL_TEMPLATE_CONTENT", outputStr);//outputFile.getAbsolutePath());
            vars.put(org.netbeans.modules.vcscore.util.VariableInputDialog.VAR_UPDATE_CHANGED_FROM_SELECTOR, "true");
            if (outputStr.trim().length() == 0) {
                boolean committed = committedStr != null && committedStr.length() > 0;
                if (committed) {
                    vars.put(org.netbeans.modules.vcscore.util.VariableInputDialog.VAR_CANCEL_DIALOG_BY_PRECOMMAND, "true");
                }
            }
        } finally {
            if (haveLocalCat && wincat != null) removeLocalCatExecs(fsRoot, relativePath, wincat);
        }
        return true;
    }
    
    private static void waitForCommands(String IDStr) throws InterruptedException {
        String[] IDStrs = VcsUtilities.getQuotedStrings(IDStr);
        for (int i = 0; i < IDStrs.length; i++) {
            try {
                long id = Long.parseLong(IDStrs[i]);
                CommandProcessor.getInstance().waitToFinish(id);
            } catch (NumberFormatException exc) {
                ErrorManager.getDefault().notify(exc);
            }
        }
    }
    
    public void outputMatchedGroups(String[] elements) {
        if (elements.length > 0) {
            output.append(((elements[0] != null) ? elements[0] : "") + "\n");
        }
        stdoutDataListener.outputData(elements);
    }
    
}

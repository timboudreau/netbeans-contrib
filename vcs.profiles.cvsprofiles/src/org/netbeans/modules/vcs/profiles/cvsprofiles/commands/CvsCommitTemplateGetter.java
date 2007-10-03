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

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
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
    private static File createCatExec() {        
        FileSystem fs = org.openide.filesystems.Repository.getDefault().getDefaultFileSystem();
        FileObject root = fs.getRoot();
        java.io.File rootFile = FileUtil.toFile(root);
        if (rootFile == null) {
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
                    return null;
                }
            }
            winCat = folder.getFileObject(WIN_CAT_NAME, WIN_CAT_EXT);
            if (winCat == null) {
                try {
                    winCat = folder.createData(WIN_CAT_NAME, WIN_CAT_EXT);
                } catch (IOException exc) {
                    return null;
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
            return catFile;            
        } else {
            return null;
        }        
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
            File wincatFile = createCatExec();
            if (wincatFile == null) {
                haveLocalCat = true;
                wincat = createLocalCatExecs(fsRoot, relativePath);
            } else {
                String wincatPath = wincatFile.getAbsolutePath();
                if (wincatPath.indexOf(' ') > 0) {
                    wincat = wincatFile.getName();
                    String path = wincatFile.getParent();
                    vars.putAll(getEnvWithPath(path, (String) vars.get(VcsFileSystem.VAR_ENVIRONMENT_PREFIX + "PATH")));
                } else {
                    wincat = wincatPath;
                }
            }
            vars.put("WINCAT", wincat);
        }
        
        String message = getMessageComment(vars);
        output.append(message);
        int begin = 0;
        do {
            int end = message.indexOf('\n', begin);
            if (end < 0) {
                end = message.length();
            }
            String line = message.substring(begin, end);
            stdoutListener.outputLine(line);
            stdoutDataListener.outputData(new String[] { line });
            begin = end + 1;
        } while (begin < message.length());
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
            String alreadyCommittedFilesStr = (String) vars.get("ALREADY_COMMITTED_FILES");
            vars.clear(); // So that many variables are not unnecessarily updated.
            String outputStr = output.toString();
            vars.put("ORIGINAL_TEMPLATE_CONTENT", outputStr);//outputFile.getAbsolutePath());
            vars.put(org.netbeans.modules.vcscore.util.VariableInputDialog.VAR_UPDATE_CHANGED_FROM_SELECTOR, "true");
            boolean areRemainingFiles = true;
            if (alreadyCommittedFilesStr != null) {
                List alreadyCommittedFiles;
                try {
                    alreadyCommittedFiles = (List) VcsUtilities.decodeValue(alreadyCommittedFilesStr);
                    areRemainingFiles = areSomeFilesRemaining(fsRoot, relativePath, outputStr, ps, alreadyCommittedFiles);
                } catch (IOException ioex) {
                }
            }
            if (outputStr.trim().length() == 0 || !areRemainingFiles) {
                boolean committed = committedStr != null && committedStr.length() > 0 || !areRemainingFiles;
                if (committed) {
                    vars.put(org.netbeans.modules.vcscore.util.VariableInputDialog.VAR_CANCEL_DIALOG_BY_PRECOMMAND, "true");
                }
            }
        } finally {
            if (haveLocalCat && wincat != null) removeLocalCatExecs(fsRoot, relativePath, wincat);
        }
        return true;
    }
    
    private Map getEnvWithPath(String path, String varPATH) {
        Map envMap = new HashMap();
        envMap.put("DYNAMIC_ENVIRONMENT_VARS", "true");
        String varName = "PATH";
        if (varPATH == null) {
            String[] envVars = fileSystem.getEnvironmentVars();
            for (int i = 0; i < envVars.length; i++) {
                int varEnd = envVars[i].indexOf('=');
                varName = envVars[i].substring(0, varEnd).trim();
                if (varName.equalsIgnoreCase("PATH")) {
                    varPATH = envVars[i].substring(varEnd + 1).trim();
                    break;
                }
            }
        }
        varPATH = path + ';' + varPATH;
        envMap.put(VcsFileSystem.VAR_ENVIRONMENT_PREFIX + varName, varPATH);
        return envMap;
    }
    
    private boolean areSomeFilesRemaining(String fsRoot, String relativePath,
                                          String templateContent, char ps,
                                          List filesCommitted) {
        List filesToBeCommited = CvsCommit.getCommitedFiles(fsRoot, relativePath, templateContent, ps);
        filesToBeCommited.removeAll(filesCommitted);
        return filesToBeCommited.size() > 0;
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

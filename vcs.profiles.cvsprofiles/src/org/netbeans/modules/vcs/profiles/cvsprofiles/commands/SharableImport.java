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

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;

import org.netbeans.api.queries.SharabilityQuery;
import org.netbeans.api.vcs.VcsManager;
import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;

import org.netbeans.modules.vcscore.VcsFileSystem;

import org.netbeans.modules.vcscore.cmdline.ExecuteCommand;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.CommandExecutionContext;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.TextErrorListener;
import org.netbeans.modules.vcscore.commands.VcsDescribedCommand;

import org.netbeans.spi.vcs.commands.CommandSupport;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * An import command, that checks whether the processed files and folders are sharable
 * before it invokes the cvs import command. <p>
 *
 * All folders that are given to this command are checked for sharability.
 * Sharable folders are given to import command, mixed folders
 * are checked for sharable files. If no sharable files are found,
 * sharable sub-folders are imported. If some sharable files are found,
 * the folder is imported, but unsharable files and folders are added
 * into ignore list. But in this case it needs to be checked that
 * the ignored files and folders do not exist as sharable in
 * the imported directory subtree. If some sharable files exist,
 * the import is not possible. If some sharable folders exist, they
 * need to be imported later.
 *
 * @author  Martin Entlicher
 */
public class SharableImport implements VcsAdditionalCommand {
    
    private static final String UNSHARABLE_IGNORE_VAR = "UNSHARABLE_IGNORE"; // NOI18N
    
    private CommandExecutionContext execContext;
    private VcsFileSystem fileSystem;
    private Hashtable vars;
    private CommandOutputListener stderrListener;
    
    
    /** Creates a new instance of Echo */
    public SharableImport() {
    }
    
    /** Set the VCS file system to use to execute commands.
     */
    public void setExecutionContext(CommandExecutionContext execContext) {
        this.execContext = execContext;
        if (execContext instanceof VcsFileSystem) {
            fileSystem = (VcsFileSystem) execContext;
        }
    }
    
    private FileObject[] getFiles(Collection fileNames, Collection diskFiles) {
        if (fileSystem == null) {
            for (Iterator it = fileNames.iterator(); it.hasNext(); ) {
                String name = (String) it.next();
                diskFiles.add(new File(name));
            }
            return new FileObject[0];
        } else {
            ArrayList fos = new ArrayList();
            for (Iterator it = fileNames.iterator(); it.hasNext(); ) {
                String name = (String) it.next();
                FileObject fo = fileSystem.findResource(name);
                if (fo != null) {
                    fos.add(fo);
                } else {
                    diskFiles.add(fileSystem.getFile(name));
                }
            }
            return (FileObject[]) fos.toArray(new FileObject[0]);
        }
    }
    
    private boolean runOnSharableFolders(CommandSupport cmdSupp, Map vars, Collection fileNames) {
        for (Iterator it = fileNames.iterator(); it.hasNext(); ) {
            String name = (String) it.next();
            File file;
            if (fileSystem != null) {
                file = fileSystem.getFile(name);
            } else {
                file = new File(name);
            }
            int sharability = SharabilityQuery.getSharability(file);
            //System.out.println("  runOnSharableFolders(): name '"+name+"' sharability = "+sharability);
            if (sharability == SharabilityQuery.SHARABLE) {
                //if (file.isDirectory()) {
                CommandTask task = runCommand(cmdSupp, vars, file, name); // the selected file must be a directory
                try {
                    task.waitFinished(0);
                } catch (InterruptedException iex) {
                    Thread.currentThread().interrupt();
                }
                if (task.getExitStatus() != task.STATUS_SUCCEEDED) {
                    return false;
                }
            } else if (sharability == SharabilityQuery.MIXED) {
                boolean status = runOnMixedFolders(cmdSupp, vars, file, name);
                if (!status) return false;
            }
        }
        return true;
    }
    
    private boolean runOnMixedFolders(CommandSupport cmdSupp, Map vars, File folder, String name) {
        File[] children = folder.listFiles();
        if (children == null) return true;
        Collection sharableFileNames = new ArrayList();
        Collection unsharableFileNames = new ArrayList();
        Collection mixedFolders = new ArrayList();
        Collection sharableFolders = new ArrayList();
        Collection unsharableFolders = new ArrayList();
        if (name.length() == 0 || name.length() == 1 && name.charAt(0) == '.') {
            name = "";
        } else {
            name += "/";
        }
        for (int i = 0; i < children.length; i++) {
            String fileName = children[i].getName();
            int sharability = SharabilityQuery.getSharability(children[i]);
            if (sharability == SharabilityQuery.SHARABLE) {
                if (children[i].isDirectory()) {
                    sharableFolders.add(fileName);
                } else {
                    sharableFileNames.add(fileName);
                }
            } else if (sharability == SharabilityQuery.MIXED) {
                mixedFolders.add(fileName);
            } else {
                if (children[i].isFile()) {
                    unsharableFileNames.add(fileName);
                } else {
                    unsharableFolders.add(fileName);
                }
            }
        }
        //System.out.println("sharableFileNames = "+sharableFileNames+", unsharableFileNames = "+unsharableFileNames+
        //                   ", mixedFolders = "+mixedFolders+", sharableFolders = "+sharableFolders+", unsharableFolders = "+unsharableFolders);
        if (sharableFileNames.size() == 0) {
            // When no sharable files are found, run command on sharable folders
            // and re-run this on mixed folders
            for (Iterator it = sharableFolders.iterator(); it.hasNext(); ) {
                String fileName = (String) it.next();
                CommandTask task = runCommand(cmdSupp, vars, new File(folder, fileName), name + fileName);
                try {
                    task.waitFinished(0);
                } catch (InterruptedException iex) {
                    Thread.currentThread().interrupt();
                }
                if (task.getExitStatus() != task.STATUS_SUCCEEDED) {
                    return false;
                }
            }
        } else {
            Collection ignoredNames = new HashSet(unsharableFileNames.size() + unsharableFolders.size() + mixedFolders.size());
            ignoredNames.addAll(unsharableFileNames);
            ignoredNames.addAll(unsharableFolders);
            ignoredNames.addAll(mixedFolders);
            String[] sharableFileRef = new String[1];
            if (existSharableFilesUnder(folder, ignoredNames, sharableFileRef)) {
                stderrListener.outputLine("It's not possible to import folder '"+folder+
                                          "', because it contains unsharable file '"+sharableFileRef[0]+
                                          "', but a file of the same name exists as sharable in the subtree.");
                stderrListener.outputLine("Remove the unsharable files, if possible, to resolve that conflict.");
                return false;
            }
            CommandTask task = runCommand(cmdSupp, vars, folder, name, ignoredNames);
            try {
                task.waitFinished(0);
            } catch (InterruptedException iex) {
                Thread.currentThread().interrupt();
            }
            if (task.getExitStatus() != task.STATUS_SUCCEEDED) {
                return false;
            }
            Map sharableSubFolders = new HashMap();
            if (sharableFoldersUnder(folder, name, ignoredNames, sharableSubFolders)) {
                for (Iterator it = sharableSubFolders.keySet().iterator(); it.hasNext(); ) {
                    File subFolder = (File) it.next();
                    String filePath = (String) sharableSubFolders.get(subFolder);
                    task = runCommand(cmdSupp, vars, subFolder, filePath);
                    try {
                        task.waitFinished(0);
                    } catch (InterruptedException iex) {
                        Thread.currentThread().interrupt();
                    }
                    if (task.getExitStatus() != task.STATUS_SUCCEEDED) {
                        return false;
                    }
                }
            }
        }
        for (Iterator it = mixedFolders.iterator(); it.hasNext(); ) {
            String fileName = (String) it.next();
            boolean status = runOnMixedFolders(cmdSupp, vars, new File(folder, fileName), name + fileName);
            if (!status) return false;
        }
        return true;
    }
    
    private static boolean existSharableFilesUnder(File folder, Collection names, String[] sharableFileRef) {
        File[] children = folder.listFiles();
        if (children == null) return false;
        boolean exist = false;
        for (int i = 0; i < children.length; i++) {
            if (children[i].isDirectory()) {
                exist = exist && existSharableFilesUnder(children[i], names, sharableFileRef);
            } else {
                String fileName = (String) children[i].getName();
                if (names.contains(fileName) && SharabilityQuery.getSharability(children[i]) != SharabilityQuery.NOT_SHARABLE) {
                    exist = true;
                    sharableFileRef[0] = fileName;
                    break;
                }
            }
        }
        return exist;
    }
    
    private boolean sharableFoldersUnder(File folder, String path, Collection names, Map sharableFolders) {
        File[] children = folder.listFiles();
        if (children == null) return false;
        boolean exist = false;
        for (int i = 0; i < children.length; i++) {
            if (children[i].isDirectory()) {
                String fileName = (String) children[i].getName();
                if (names.contains(fileName) && SharabilityQuery.getSharability(children[i]) != SharabilityQuery.NOT_SHARABLE) {
                    sharableFolders.put(children[i], path + fileName);
                }
                exist = exist && sharableFoldersUnder(children[i], path + fileName + '/', names, sharableFolders);
            }
        }
        return exist;
    }
    
    private CommandTask runCommand(CommandSupport cmdSupp, Map vars, File file, String path) {
        return runCommand(cmdSupp, vars, file, path, null);
    }
    
    private static String assembleIgnoreExpressions(Collection ignoredNames) {
        StringBuffer ignores = new StringBuffer();
        for (Iterator it = ignoredNames.iterator(); it.hasNext(); ) {
            String name = (String) it.next();
            ignores.append("-I ${QUOTE}");
            ignores.append(name);
            ignores.append("${QUOTE} ");
        }
        return ignores.toString();
    }
    
    private CommandTask runCommand(CommandSupport cmdSupp, Map vars, File file, String path, Collection ignoredNames) {
        if (path.endsWith("/")) path = path.substring(0, path.length() - 1);
        //System.out.println("runCommand("+file+", "+path+", "+ignoredNames+")");
        
        VcsDescribedCommand cmd = (VcsDescribedCommand) cmdSupp.createCommand();
        cmd.setAdditionalVariables(vars);
        cmd.addTextErrorListener(new TextErrorListener() {
            public void outputLine(String line) {
                stderrListener.outputLine(line);
            }
        });
        if (ignoredNames != null) {
            vars = cmd.getAdditionalVariables();
            vars.put(UNSHARABLE_IGNORE_VAR, assembleIgnoreExpressions(ignoredNames));
            cmd.setAdditionalVariables(vars);
        }
        FileObject fo = null;
        if (fileSystem != null) {
            fo = fileSystem.findResource(path);
            if (fo != null) {
                cmd.setFiles(new FileObject[] { fo });
            }
        }
        if (fo == null) {
            cmd.setDiskFiles(new File[] { file });
            vars = cmd.getAdditionalVariables();
            String repos = (String) vars.get("REPOS_DIR");
            if (repos != null) {
                String root = (String) vars.get("ROOTDIR");
                String relPath = path.substring(root.length());
                while(relPath.startsWith("/") || relPath.startsWith(File.separator)) relPath = relPath.substring(1);
                if (relPath.length() > 0) {
                    if (repos.length() == 0 || repos.equals(".")) repos = relPath;
                    else repos += "/" + relPath;
                    vars.put("REPOS_DIR", repos);
                }
            }
            vars.put("ROOTDIR", file.getAbsolutePath());
            cmd.setAdditionalVariables(vars);
        }
        VcsManager.getDefault().showCustomizer(cmd);
        //System.out.println("  assigned FileObjects = "+((cmd.getFiles() != null) ? java.util.Arrays.asList(cmd.getFiles()) : null));
        //System.out.println("  assigned Files       = "+((cmd.getDiskFiles() != null) ? java.util.Arrays.asList(cmd.getDiskFiles()) : null));
        return cmd.execute();
    }
    
    public boolean exec(Hashtable vars, String[] args,
                        final CommandOutputListener stdoutListener,
                        final CommandOutputListener stderrListener,
                        final CommandDataOutputListener stdoutDataListener, String dataRegex,
                        final CommandDataOutputListener stderrDataListener, String errorRegex) {
        this.vars = vars;
        int arglen = args.length;
        this.stderrListener = stderrListener;
        //System.out.println("DIFF: args = "+VcsUtilities.arrayToString(args));
        if (arglen < 1) {
            stderrListener.outputLine("Too few arguments, expecting a name of a command.");
            return false;
        }
        CommandSupport cmdSupp = execContext.getCommandSupport(args[0]);
        if (cmdSupp == null) {
            stderrListener.outputLine("Did not find command '"+args[0]+"'.");
            return false;
        }
        Collection processingFiles;
        if (fileSystem != null) {
            processingFiles = ExecuteCommand.createProcessingFiles(execContext, vars);
        } else {
            processingFiles = Collections.singleton(vars.get("ROOTDIR"));
        }
        //System.out.println("Processing files = "+processingFiles);
        boolean status = runOnSharableFolders(cmdSupp, vars, processingFiles);
        return status;
    }
    
}

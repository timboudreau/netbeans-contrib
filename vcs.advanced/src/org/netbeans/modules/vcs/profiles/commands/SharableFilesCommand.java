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

package org.netbeans.modules.vcs.profiles.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.netbeans.api.queries.SharabilityQuery;
import org.netbeans.api.vcs.VcsManager;
import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;

import org.netbeans.modules.vcscore.VcsFileSystem;
import org.netbeans.modules.vcscore.turbo.Turbo;
import org.netbeans.modules.vcscore.turbo.FileProperties;
import org.netbeans.modules.vcscore.turbo.IgnoreList;
import org.netbeans.modules.vcscore.cache.CacheHandler;
import org.netbeans.modules.vcscore.cache.FileSystemCache;
import org.netbeans.modules.vcscore.caching.VcsCacheDir;

import org.netbeans.modules.vcscore.cmdline.ExecuteCommand;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.CommandExecutionContext;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.RegexOutputListener;
import org.netbeans.modules.vcscore.commands.TextErrorListener;
import org.netbeans.modules.vcscore.commands.VcsDescribedCommand;
import org.netbeans.modules.vcscore.util.VcsUtilities;

import org.netbeans.spi.vcs.commands.CommandSupport;
import org.openide.ErrorManager;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

/**
 * A command, that checks whether the processed files and folders are sharable
 * before it invokes the sub-command. 
 *
 * @author  Martin Entlicher
 */
public class SharableFilesCommand implements VcsAdditionalCommand {
    
    public static final String LOCAL_DIR_ONLY = "-l"; // NOI18N
    
    private CommandExecutionContext execContext;
    private VcsFileSystem fileSystem;
    private Hashtable vars;
    
    
    /** Creates a new instance of Echo */
    public SharableFilesCommand() {
    }
    
    /** Set the VCS file system to use to execute commands.
     */
    public void setExecutionContext(CommandExecutionContext execContext) {
        this.execContext = execContext;
        if (execContext instanceof VcsFileSystem) {
            fileSystem = (VcsFileSystem) execContext;
        }
    }
    
    private Collection collectSharableSubfiles(Collection fileNames, Collection intermediateFolders, boolean recursive) {
        Collection sharableFileNames = new ArrayList();
        for (Iterator it = fileNames.iterator(); it.hasNext(); ) {
            String name = (String) it.next();
            File file;
            if (fileSystem != null) {
                file = fileSystem.getFile(name);
            } else {
                file = new File(name);
            }
            int sharability = SharabilityQuery.getSharability(file);
            //System.out.println("  collectSharableSubfiles(): name '"+name+"' sharability = "+sharability);
            if (sharability == SharabilityQuery.SHARABLE || file.isFile() && sharability == SharabilityQuery.UNKNOWN) {
                if (file.isDirectory() && intermediateFolders != null && isEmptyDirectory(file)) {
                    intermediateFolders.add(name);
                } else {
                    sharableFileNames.add(name);
                }
                //System.out.println("\t\t\tSHARABLE");
            } else if (sharability == SharabilityQuery.MIXED || file.isDirectory() && sharability == SharabilityQuery.UNKNOWN) {
                //System.out.println("\t\t\tMIXED; fo = "+file);
                if (intermediateFolders != null) {
                    intermediateFolders.add(name);
                }
                addSharableSubfiles(sharableFileNames, intermediateFolders, file, name, recursive);
            }
        }
        return sharableFileNames;
    }
    
    private void addSharableSubfiles(Collection sharableFileNames, Collection intermediateFolders, File file, String name, boolean recursive) {
        //FileObject[] children = fo.getChildren();
        File[] children = file.listFiles();
        if (name.length() == 0 || name.length() == 1 && name.charAt(0) == '.') {
            name = "";
        } else {
            name += "/";
        }
        for (int i = 0; i < children.length; i++) {
            String chName = name + children[i].getName();
            int sharability = SharabilityQuery.getSharability(children[i]);
            boolean isDirectory = children[i].isDirectory();
            //System.out.println("  addSharableSubfiles(): name '"+children[i]+"' sharability = "+sharability);
            if (sharability == SharabilityQuery.SHARABLE || !isDirectory && sharability == SharabilityQuery.UNKNOWN) {
                if (isDirectory && intermediateFolders != null && isEmptyDirectory(children[i])) {
                    intermediateFolders.add(chName);
                } else {
                    sharableFileNames.add(chName);
                }
            } else if ((sharability == SharabilityQuery.MIXED || isDirectory && sharability == SharabilityQuery.UNKNOWN) && recursive) {
                if (intermediateFolders != null) {
                    intermediateFolders.add(chName);
                }
                addSharableSubfiles(sharableFileNames, intermediateFolders, children[i], chName, recursive);
            }
        }
    }
    
    private FileObject[] getFiles(Collection fileNames, Collection diskFiles,
                                  Pattern ignoreList, Pattern relevantList) {
        if (fileSystem == null) {
            for (Iterator it = fileNames.iterator(); it.hasNext(); ) {
                String name = (String) it.next();
                diskFiles.add(new File(name));
            }
            return new FileObject[0];
        } else {
            ArrayList fos = new ArrayList();
            //FileCacheProvider cacheProvider = fileSystem.getCacheProvider();
            FileSystemCache cache = null;
            if (Turbo.implemented() == false) {
                cache = CacheHandler.getInstance().getCache(fileSystem.getCacheIdStr());
            }
            Object locker = new Object();
            /*
            if (cache != null) {
                //System.out.println("getting cache file ("+path+") = "+fileSystem.getFile(path));
                cache.getCacheFile(new File(fileSystem.getFile(path), "testing"), CacheHandler.STRAT_DISK_OR_REFRESH_RECURS, locker);
                VcsCacheDir cacheDir = (VcsCacheDir) cache.getCacheFile(fileSystem.getFile(path), CacheHandler.STRAT_DISK, locker);
            }
             */
            for (Iterator it = fileNames.iterator(); it.hasNext(); ) {
                String path = (String) it.next();
                String name = VcsUtilities.getFileNamePart(path);
                if (fileSystem != null) {

                    if (Turbo.implemented()) {
                        String folderPath = VcsUtilities.getDirNamePart(path);
                        FileObject folder = fileSystem.findResource(folderPath);
                        IgnoreList ilist = IgnoreList.forFolder(folder);
                        if (ilist.isIgnored(name)) continue;
                    } else {

                    String folder = VcsUtilities.getDirNamePart(path);
                    cache.getCacheFile(new File(fileSystem.getFile(folder), "testing"), CacheHandler.STRAT_DISK_OR_REFRESH, locker);
                    VcsCacheDir dir = (VcsCacheDir) cache.getCacheFile(fileSystem.getFile(folder), CacheHandler.STRAT_DISK, locker);
                    if (!dir.isIgnoreListSet() && fileSystem.getIgnoreListSupport() != null) {
                        dir.setIgnoreList(VcsUtilities.createIgnoreList(dir, dir.getFSPath(), fileSystem.getIgnoreListSupport()));
                    }
                    //Filter out files, that are ignored!!
                    if (dir.isIgnored(name)) continue;
                    } // Turbo.enabled()
                }
                if ((fileSystem != null && fileSystem.getFile(path).isFile()) ||
                    (fileSystem == null && new File(path).isFile())) {
                    
                    if (ignoreList != null && ignoreList.matcher(name).matches()) {
                        continue;
                    }
                    if (relevantList != null && !relevantList.matcher(name).matches()) {
                        continue;
                    }
                }
                
                FileObject fo = fileSystem.findResource(path);
                if (fo != null) {
                    fos.add(fo);
                } else {
                    diskFiles.add(fileSystem.getFile(path));
                }
            }
            return (FileObject[]) fos.toArray(new FileObject[0]);
        }
    }
    
    private boolean isEmptyDirectory(File directory) {

        File[] children = directory.listFiles();
        if (children == null) return true;

        if (Turbo.implemented()) {
            FileObject folder = FileUtil.toFileObject(directory);
            IgnoreList ilist = IgnoreList.forFolder(folder);
            for (int i = 0; i < children.length; i++) {
                if (ilist.isIgnored(children[i].getName()) == false) {
                    return false;
                }
            }
            return true;
        }

        // original implementation
        FileSystemCache cache = CacheHandler.getInstance().getCache(fileSystem.getCacheIdStr());
        Object locker = new Object();
        cache.getCacheFile(new File(directory, "testing"), CacheHandler.STRAT_DISK_OR_REFRESH, locker);
        VcsCacheDir dir = (VcsCacheDir) cache.getCacheFile(directory, CacheHandler.STRAT_DISK, locker);
        for (int i = 0; i < children.length; i++) {
            if (!dir.isIgnored(children[i].getName())) {
                return false;
            }
        }
        return true; // All subfiles are ignored
    }
    
    public boolean exec(Hashtable vars, String[] args,
                        final CommandOutputListener stdoutListener,
                        final CommandOutputListener stderrListener,
                        final CommandDataOutputListener stdoutDataListener, String dataRegex,
                        final CommandDataOutputListener stderrDataListener, String errorRegex) {
        this.vars = vars;
        int arglen = args.length;
        //System.out.println("DIFF: args = "+VcsUtilities.arrayToString(args));
        if (arglen < 1) {
            stderrListener.outputLine("Too few arguments, expecting a name of a command "+
                                      "and an optional name of the command that will handle intermediate mixed folders.\n"+
                                      "Possible options: -ic <name of command, that provides ignore names regular expression on data output>\n"+
                                      "                  -l To process only files in current directory (not recursively)");
            return false;
        }
        boolean localOnly = false;
        String cmdName;
        String cmdFolderName;
        String cmdIgnoreListName;
        int index = 0;
        if ("-ic".equals(args[index])) {
            cmdIgnoreListName = args[++index];
        } else {
            cmdIgnoreListName = null;
        }
        if (LOCAL_DIR_ONLY.equals(args[++index])) {
            localOnly = true;
            cmdName = args[++index];
        } else {
            cmdName = args[index];
        }
        if (args.length > index + 1) {
            cmdFolderName = args[++index];
        } else {
            cmdFolderName = null;
        }
        CommandSupport cmdSupp = execContext.getCommandSupport(cmdName);
        if (cmdSupp == null) {
            stderrListener.outputLine("Did not find command '"+cmdName+"'.");
            return false;
        }
        CommandSupport cmdFolderSupp = null;
        if (cmdFolderName != null) {
            cmdFolderSupp = execContext.getCommandSupport(cmdFolderName);
        }
        Collection processingFiles = ExecuteCommand.createProcessingFiles(execContext, vars);
        
        //System.out.println("Processing files = "+processingFiles);
        
        Collection intermediateFolders;
        if (cmdFolderSupp != null) {
            intermediateFolders = new ArrayList();
        } else {
            intermediateFolders = null;
        }
        
        final Pattern[] ignoreListPtr = new Pattern[] { null };
        final Pattern[] relevantListPtr = new Pattern[] { null };
        if (cmdIgnoreListName != null) {
            CommandSupport cmdIgnoreListSupp = execContext.getCommandSupport(cmdIgnoreListName);
            if (cmdIgnoreListSupp != null) {
                VcsDescribedCommand cmdIgnoreList = (VcsDescribedCommand) cmdIgnoreListSupp.createCommand();
                cmdIgnoreList.addRegexOutputListener(new RegexOutputListener() {
                    public void outputMatchedGroups(String[] data) {
                        if (data.length >= 1) {
                            try {
                                if (data[0] != null) ignoreListPtr[0] = Pattern.compile(data[0]);
                                if (data.length >= 2) {
                                    if (data[1] != null) relevantListPtr[0] = Pattern.compile(data[1]);
                                }
                            } catch (PatternSyntaxException psex) {
                                ErrorManager.getDefault().notify(ErrorManager.INFORMATIONAL,  psex);
                            }
                        }
                    }
                });
                CommandTask task = cmdIgnoreList.execute();
                try {
                    task.waitFinished(0);
                } catch (InterruptedException iex) {
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
        }
        Pattern ignoreList = ignoreListPtr[0];
        Pattern relevantList = relevantListPtr[0];
        
        Collection sharableFiles = collectSharableSubfiles(processingFiles, intermediateFolders, !localOnly);
        
        if (intermediateFolders != null && intermediateFolders.size() > 0) {
            Command cmd = cmdFolderSupp.createCommand();
            Collection diskFiles = new ArrayList();
            FileObject[] fileObjects = getFiles(intermediateFolders, diskFiles, ignoreList, relevantList);
            fileObjects = cmd.getApplicableFiles(fileObjects);
            if (cmd instanceof VcsDescribedCommand) {
                ((VcsDescribedCommand) cmd).setAdditionalVariables(vars);
                ((VcsDescribedCommand) cmd).setDiskFiles((File[]) diskFiles.toArray(new File[0]));
                ((VcsDescribedCommand) cmd).addTextErrorListener(new TextErrorListener() {
                    public void outputLine(String line) {
                        stderrListener.outputLine(line);
                    }
                });
                /*
                runsOnFiles = VcsCommandIO.getBooleanPropertyAssumeDefault(
                                ((VcsDescribedCommand) cmd).getVcsCommand(),
                                VcsCommand.PROPERTY_ON_FILE);
                 */
            }
            if (fileObjects != null) {
                cmd.setFiles(fileObjects);
            }

            //System.out.println("  assigned Folder FileObjects = "+java.util.Arrays.asList(cmd.getFiles()));
            //System.out.println("  assigned Folder Files       = "+java.util.Arrays.asList(((VcsDescribedCommand) cmd).getDiskFiles()));

            if (fileObjects != null || diskFiles.size() > 0) {
                VcsManager.getDefault().showCustomizer(cmd);
                CommandTask task = cmd.execute();
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
        
        Command cmd = cmdSupp.createCommand();
        Collection diskFiles = new ArrayList();
        FileObject[] fileObjects = getFiles(sharableFiles, diskFiles, ignoreList, relevantList);
        fileObjects = cmd.getApplicableFiles(fileObjects);
        if (cmd instanceof VcsDescribedCommand) {
            ((VcsDescribedCommand) cmd).setAdditionalVariables(vars);
            ((VcsDescribedCommand) cmd).setDiskFiles((File[]) diskFiles.toArray(new File[0]));
            ((VcsDescribedCommand) cmd).addTextErrorListener(new TextErrorListener() {
                public void outputLine(String line) {
                    stderrListener.outputLine(line);
                }
            });
        }
        if (fileObjects != null) {
            cmd.setFiles(fileObjects);
        }
        
        //System.out.println("  assigned FileObjects = "+java.util.Arrays.asList(cmd.getFiles()));
        //System.out.println("  assigned Files       = "+java.util.Arrays.asList(((VcsDescribedCommand) cmd).getDiskFiles()));
        
        if (fileObjects != null || diskFiles.size() > 0) {
            VcsManager.getDefault().showCustomizer(cmd);
            CommandTask task = cmd.execute();
            try {
                task.waitFinished(0);
            } catch (InterruptedException iex) {
                Thread.currentThread().interrupt();
            }
            return task.getExitStatus() == task.STATUS_SUCCEEDED;
        } else {
            return true;
        }
    }
    
}

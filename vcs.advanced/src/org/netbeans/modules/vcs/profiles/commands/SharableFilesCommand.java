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
            if (sharability == SharabilityQuery.SHARABLE) {
                sharableFileNames.add(name);
                //System.out.println("\t\t\tSHARABLE");
            } else if (sharability == SharabilityQuery.MIXED) {
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
            //System.out.println("  addSharableSubfiles(): name '"+children[i]+"' sharability = "+sharability);
            if (sharability == SharabilityQuery.SHARABLE) {
                sharableFileNames.add(chName);
            } else if (sharability == SharabilityQuery.MIXED && recursive) {
                if (intermediateFolders != null) {
                    intermediateFolders.add(chName);
                }
                addSharableSubfiles(sharableFileNames, intermediateFolders, children[i], chName, recursive);
            }
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
                                      "and an optional name of the command that will handle intermediate mixed folders.");
            return false;
        }
        boolean localOnly = false;
        String cmdName;
        String cmdFolderName;
        int index = 0;
        if (LOCAL_DIR_ONLY.equals(args[0])) {
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
        
        Collection sharableFiles = collectSharableSubfiles(processingFiles, intermediateFolders, !localOnly);
        
        if (intermediateFolders != null && intermediateFolders.size() > 0) {
            Command cmd = cmdFolderSupp.createCommand();
            Collection diskFiles = new ArrayList();
            FileObject[] fileObjects = getFiles(intermediateFolders, diskFiles);
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
            cmd.setFiles(fileObjects);

            //System.out.println("  assigned Folder FileObjects = "+java.util.Arrays.asList(cmd.getFiles()));
            //System.out.println("  assigned Folder Files       = "+java.util.Arrays.asList(((VcsDescribedCommand) cmd).getDiskFiles()));

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
        
        Command cmd = cmdSupp.createCommand();
        Collection diskFiles = new ArrayList();
        FileObject[] fileObjects = getFiles(sharableFiles, diskFiles);
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
        cmd.setFiles(fileObjects);
        
        //System.out.println("  assigned FileObjects = "+java.util.Arrays.asList(cmd.getFiles()));
        //System.out.println("  assigned Files       = "+java.util.Arrays.asList(((VcsDescribedCommand) cmd).getDiskFiles()));
        
        VcsManager.getDefault().showCustomizer(cmd);
        CommandTask task = cmd.execute();
        try {
            task.waitFinished(0);
        } catch (InterruptedException iex) {
            Thread.currentThread().interrupt();
        }
        return task.getExitStatus() == task.STATUS_SUCCEEDED;
    }
    
}

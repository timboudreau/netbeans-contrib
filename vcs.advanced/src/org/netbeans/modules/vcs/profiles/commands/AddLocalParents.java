/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.profiles.commands;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;

import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;

import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.CommandTask;

import org.netbeans.modules.vcscore.cmdline.ExecuteCommand;
import org.netbeans.modules.vcscore.cmdline.VcsAdditionalCommand;
import org.netbeans.modules.vcscore.commands.CommandDataOutputListener;
import org.netbeans.modules.vcscore.commands.CommandExecutionContext;
import org.netbeans.modules.vcscore.commands.CommandOutputListener;
import org.netbeans.modules.vcscore.commands.VcsDescribedCommand;
import org.netbeans.modules.vcscore.turbo.FileProperties;
import org.netbeans.modules.vcscore.turbo.Turbo;

import org.netbeans.spi.vcs.commands.CommandSupport;

/**
 * A command, that checks whether the parent folders are local and if yes,
 * it calls the add command on them in the hierarchical order.
 *
 * @author  Martin Entlicher
 */
public class AddLocalParents extends Object implements VcsAdditionalCommand {
    
    private CommandExecutionContext execContext;
    
    /** Creates a new instance of AddLocalParents */
    public AddLocalParents() {
    }
    
    /** Set the VCS file system to use to execute commands.
     */
    public void setExecutionContext(CommandExecutionContext execContext) {
        this.execContext = execContext;
        //if (execContext instanceof VcsFileSystem) {
        //    fileSystem = (VcsFileSystem) execContext;
        //}
    }
    
    public boolean exec(Hashtable vars, String[] args,
                        final CommandOutputListener stdoutListener,
                        final CommandOutputListener stderrListener,
                        final CommandDataOutputListener stdoutDataListener, String dataRegex,
                        final CommandDataOutputListener stderrDataListener, String errorRegex) {
        if (args.length < 1) {
            stderrListener.outputLine("Too few arguments, expecting a name of a command "+
                                      "that is to be executed to add local parent folders.");
            return false;
        }
        CommandSupport cmdSupp = execContext.getCommandSupport(args[0]);
        if (cmdSupp == null) {
            stderrListener.outputLine("Did not find command '"+args[0]+"'.");
            return false;
        }
        Collection processingFiles = ExecuteCommand.createProcessingFiles(execContext, vars);
        Collection localParents = collectLocalParents(processingFiles);
        if (localParents.size() > 0) {
            Command cmd = cmdSupp.createCommand();
            if (cmd instanceof VcsDescribedCommand) {
                ((VcsDescribedCommand) cmd).setAdditionalVariables(vars);
            }
            FileObject[] parentsArr = (FileObject[]) localParents.toArray(new FileObject[0]);
            parentsArr = cmd.getApplicableFiles(parentsArr);
            if (parentsArr == null || parentsArr.length == 0) {
                return true; // Nothing to run on.
            }
            cmd.setFiles(parentsArr);
            CommandTask task = cmd.execute();
            try {
                task.waitFinished(0);
            } catch (InterruptedException iex) {
                task.stop();
                Thread.currentThread().interrupt();
            }
            return task.getExitStatus() == task.STATUS_SUCCEEDED;
        } else {
            return true;
        }
    }
    
    private Collection collectLocalParents(Collection processingFiles) {
        Collection localParents = new ArrayList();
        if (execContext instanceof FileSystem) {
            FileSystem fs = ((FileSystem) execContext);
            for (Iterator it = processingFiles.iterator(); it.hasNext(); ) {
                String name = (String) it.next();
                FileObject fo = fs.findResource(name);
                addLocalParents(fo, localParents);
            }
        } else {
            for (Iterator it = processingFiles.iterator(); it.hasNext(); ) {
                String fullPath = (String) it.next();
                FileObject fo = FileUtil.toFileObject(new File(fullPath));
                if (fo != null) {
                    addLocalParents(fo, localParents);
                }
            }
        }
        return localParents;
    }
    
    private static void addLocalParents(FileObject fo, Collection localParents) {
        FileObject parent = fo.getParent();
        if (parent != null) {
            FileProperties fprops = Turbo.getMeta(parent);
            if (fprops != null && fprops.isLocal()) {
                addLocalParents(parent, localParents);
                localParents.add(parent);
            }
        }
    }
}

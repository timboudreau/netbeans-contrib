/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.commands;

import java.io.File;
import java.util.Map;

import org.netbeans.api.vcs.commands.Command;

/**
 * This class represents a command whose behavior is described by VcsCommand.
 *
 * @author  Martin Entlicher
 */
public interface VcsDescribedCommand extends Command, TextOutputCommand,
                                             RegexOutputCommand, FileReaderCommand,
                                             ChainingCommand {
    
    /**
     * Set the VcsCommand instance associated with this command.
     * @param cmd the VcsCommand.
     */
    public void setVcsCommand(VcsCommand cmd);
    
    /**
     * Get the VcsCommand instance associated with this command.
     * @return The VcsCommand.
     */
    public VcsCommand getVcsCommand();
    
    /**
     * Set additional variables for the command execution.
     * @param vars The map of variable names and values.
     */
    public void setAdditionalVariables(Map vars);
    
    /**
     * Get additional variables for the command execution.
     * @return the map of variable names and values.
     */
    public Map getAdditionalVariables();
    
    /**
     * Set a preferred execution string, which might have some variables
     * or patterns expanded.
     * @param preferredExec the preferred execution string
     */
    public void setPreferredExec(String preferredExec);
    
    /**
     * Get a preferred execution string, which might have some variables
     * or patterns expanded.
     * @return the preferred execution string
     */
    public String getPreferredExec();
    
    /**
     * Set the executor, which was already created to take care about executing
     * of this command.
     * @deprecated This is needed only for the compatibility with the old "API".
     */
    public void setExecutor(VcsCommandExecutor executor);
    
    /**
     * Get the executor, which was already created to take care about executing
     * of this command.
     * @deprecated This is needed only for the compatibility with the old "API".
     */
    public VcsCommandExecutor getExecutor();
    
    /**
     * Sometimes the FileObject can not be found for a desired file. In this
     * case this method should be used to specify directly the disk files to act on.
     * The command is expected to act on the union of all set FileObjects and java.io.Files.
     * @param files The array of files to act on.
     */
    public void setDiskFiles(File[] files);

    /**
     * Sometimes the FileObject can not be found for a desired file. In this
     * case this method should be used to specify directly the disk files to act on.
     * The command is expected to act on the union of all set FileObjects and java.io.Files.
     * @return The array of files to act on.
     */
    public File[] getDiskFiles();

    public Object clone();

}

/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2000 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.commands;

import java.util.Hashtable;
import java.util.Set;

/**
 * The <code>VcsCommand</code> interface should be implemented by any class
 * whose instances are intended to be executed as version control commands.
 * Each command is executed in a separate thread.
 *
 * @author  Martin Entlicher
 * @version 
 */
public interface VcsCommandExecutor extends Runnable {

    public static final int SUCCEEDED = 0;
    public static final int FAILED = 1;
    public static final int INTERRUPTED = 2;
    
    /**
     * The executed command.
     */
    public VcsCommand getCommand();
    
    /**
     * This method can be used to do some preprocessing of the command which is to be run.
     * The method is called before the prompt for user input is made and therefore can be used to
     * additionally specify the desired input.
     * @param vc the command to be preprocessed.
     * @param vars the variables
     * @return the updated exec property
     */
    public String preprocessCommand(VcsCommand vc, Hashtable vars);
    
    /**
     * Update the execution string. It may contain user input now.
     * @param exec the execution string updated with user input.
     */
    public void updateExec(String exec);
    
    /**
     * Get the set of files being processed by the command.
     * @return the set of files of type <code>String</code>
     */
    public Set getFiles();
    
    ///**
    // * Tests if this command is alive. A command is alive if it has been started and has not yet died.
    // * @return   true if this command is alive; false otherwise.
    // */
    //public boolean isAlive();
    
    ///**
    // * Interrupts this command.
    // */
    //public void interrupt();

    //public void addCommandListener(CommandListener listener);
    //public void removeCommandListener(CommandListener listener);
    
    /**
     * Get the exit status of the execution.
     * @return the exit value, it may be one of {@link SUCCEEDED}, {@link FAILED}, {@link INTERRUPTED}.
     */
    public int getExitStatus();

}


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

import org.netbeans.api.vcs.commands.Command;

import org.netbeans.spi.vcs.VcsCommandsProvider;

/**
 * This listener is to be used to listen on commands preprocessing and execution.
 *
 * @author  Martin Entlicher
 */
public interface CommandProcessListener {
    
    /**
     * Get the commands provider. The listener gets events only from commands,
     * that are instances of ProvidedCommand and their provider equals to this
     * provider. If returns <code>null</code>, the listener gets events from all
     * commands.
     * @return The provider or <code>null</code>.
     */
    public VcsCommandsProvider getProvider();
    
    /**
     * Called when the command is just to be preprocessed.
     */
    public void commandPreprocessing(Command cmd);
    
    /**
     * Called when the preprocessing of the command finished.
     * @param cmd The command which was preprocessed.
     * @param status The status of preprocessing. If false, the command is not executed.
     */
    public void commandPreprocessed(Command cmd, boolean status);

    /**
     * This method is called when the command is just to be started.
     */
    public void commandStarting(CommandTaskInfo info);
    
    /**
     * This method is called when the command is done.
     */
    public void commandDone(CommandTaskInfo info);
}


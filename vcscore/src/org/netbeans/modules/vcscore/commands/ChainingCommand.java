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

import org.netbeans.api.vcs.commands.Command;

/**
 * A command, that permits chaining of several command. This is used e.g. when
 * customizing a command with extra data for each file or when the corresponding
 * task can run only on one file at a time.
 *
 * @author  Martin Entlicher
 */
public interface ChainingCommand extends Command {
    
    /**
     * Get the next command, that should be procesed after this one.
     * @return The next command to be processed.
     */
    public Command getNextCommand();
    
    /**
     * Set the next command, that should be procesed after this one.
     * @param cmd The next command to be processed.
     */
    public void setNextCommand(Command cmd);
    
}

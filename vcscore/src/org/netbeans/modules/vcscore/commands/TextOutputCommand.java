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
 * This class represents a command which produce a text output.
 *
 * @author  Martin Entlicher
 */
public interface TextOutputCommand extends Command {
    
    /**
     * Add a text listener to the standard output of the command.
     */
    public void addTextOutputListener(TextOutputListener listener);
    
    /**
     * Remove a text listener from the standard output of the command.
     */
    public void removeTextOutputListener(TextOutputListener listener);
    
    /**
     * Add a text listener to the error output of the command.
     */
    public void addTextErrorListener(TextErrorListener listener);
    
    /**
     * Remove a text listener from the error output of the command.
     */
    public void removeTextErrorListener(TextErrorListener listener);
    
}

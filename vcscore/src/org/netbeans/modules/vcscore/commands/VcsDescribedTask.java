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
 * This class represents a command task whose behavior is described by VcsCommand.
 *
 * @author  Martin Entlicher
 */
public interface VcsDescribedTask {
    
    /**
     * Get the VcsCommand instance associated with this command.
     * @return The VcsCommand.
     */
    public VcsCommand getVcsCommand();
    
    /**
     * Get variables that are used for the task execution.
     * @return the map of variable names and values.
     */
    public Map getVariables();
    
    /**
     * Get the executor of the command.
     */
    public VcsCommandExecutor getExecutor();
    
    /**
     * Get the visualizer of the command.
     * @param gui Whether a GUI visualizer or plain text is requested.
     * @return The visualizer. If GUI visualizer is not defined for this task
     *         a plain text visualizer can be returned even when GUI is requested.
     *         Whether GUI visualizer is defined can be tested by method
     *         {@link #hasGUIVisualizer()}.
     */
    public VcsCommandVisualizer getVisualizer(boolean gui);
    
    /**
     * Test whether this task has a GUI visualizer.
     */
    public boolean hasGUIVisualizer();
    
}

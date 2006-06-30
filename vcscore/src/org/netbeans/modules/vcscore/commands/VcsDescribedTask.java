/*
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License (the License). You may not use this file except in
 * compliance with the License.
 *
 * You can obtain a copy of the License at http://www.netbeans.org/cddl.html
 * or http://www.netbeans.org/cddl.txt.
 *
 * When distributing Covered Code, include this CDDL Header Notice in each file
 * and include the License file at http://www.netbeans.org/cddl.txt.
 * If applicable, add the following below the CDDL Header, with the fields
 * enclosed by brackets [] replaced by your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
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

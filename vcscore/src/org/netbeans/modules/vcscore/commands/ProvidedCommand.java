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

import org.netbeans.spi.vcs.VcsCommandsProvider;

/**
 * This class represents a command which knows about it's VcsCommandsProvider.
 * Either Command or CommandTask can implement this interface so that it can be
 * found where did they come from.
 *
 * @author  Martin Entlicher
 */
public interface ProvidedCommand {
    
    /**
     * Get the VCS commands provider, that provided this Command or CommandTask.
     */
    public VcsCommandsProvider getProvider();
    
    /**
     * Set the VCS commands provider, that provided this Command or CommandTask.
     * This method should be called just by the implementator.
     */
    public void setProvider(VcsCommandsProvider provider);
    
}

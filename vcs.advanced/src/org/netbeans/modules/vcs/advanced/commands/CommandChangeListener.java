/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcs.advanced.commands;

import org.netbeans.modules.vcscore.commands.VcsCommand;
/**
 *
 * @author  Martin Entlicher
 */
public interface CommandChangeListener {

    /**
     * Called when the command is changed.
     */
    public void changed(VcsCommand cmd);
    
    /**
     * Called when new command is added.
     */
    public void added(VcsCommand cmd);
    
    /**
     * Called when the command is removed.
     */
    public void removed(VcsCommand cmd);
}

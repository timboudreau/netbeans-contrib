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

package org.netbeans.modules.vcscore;

import java.util.Hashtable;

import org.openide.util.actions.SystemAction;

import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;

/**
 *
 * @author  Pavel Buzek, Martin Entlicher
 * @version 
 */

public interface VcsFactory {
    
    public VcsAdvancedCustomizer getVcsAdvancedCustomizer ();

    /**
     * Get the VCS directory reader.
     */
    public VcsCommandExecutor getVcsDirReader (DirReaderListener listener, String path);

    /**
     * Get the VCS directory reader that reads the whole directory structure.
     */
    public VcsCommandExecutor getVcsDirReaderRecursive (DirReaderListener listener, String path);
    
    /**
     * Get the VCS action on the VCS filesystem. Used only in case when <code>getVcsActions</code> return null.
     */
    public VcsAction getVcsAction (); 
    
    /**
     * Get the VCS action on the VCS filesystem for a specified <code>FileObject</code>.
     */
    public VcsAction getVcsAction (org.openide.filesystems.FileObject fo); 
    
    /**
     * Get the VCS actions on the VCS filesystem. When null is returned, <code>getVcsAction</code> is used.
     */
    public SystemAction[] getActions (); // MK for VcsFileSystem.getActions to return more actions.
    /**
     * Get the command executor for the command.
     * @param command the command to get the executor for
     * @param variables the <code>Hashtable</code> of (variable name, variable value) pairs
     * @return the command executor or null when no executor is found for that command.
     */
    public VcsCommandExecutor getCommandExecutor(VcsCommand command, Hashtable variables);

}

/*
 * Log
 *  4    Jaga      1.2.1.0     3/8/00   Martin Entlicher Recursive VcsDir Reader 
 *       added
 *  3    Gandalf   1.2         10/25/99 Pavel Buzek     copyright and log
 *  2    Gandalf   1.1         10/23/99 Ian Formanek    NO SEMANTIC CHANGE - Sun
 *       Microsystems Copyright in File Comment
 *  1    Gandalf   1.0         9/30/99  Pavel Buzek     
 * $
 */

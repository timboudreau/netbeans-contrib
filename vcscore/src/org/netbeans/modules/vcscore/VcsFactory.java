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
import java.util.Collection;

import org.openide.util.actions.SystemAction;

import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;
import org.netbeans.modules.vcscore.caching.FileStatusProvider;
import org.netbeans.modules.vcscore.caching.FileCacheProvider;

/**
 *
 * @author  Pavel Buzek, Martin Entlicher
 */

public interface VcsFactory {
    
    //public VcsAdvancedCustomizer getVcsAdvancedCustomizer ();

    public FileStatusProvider getFileStatusProvider();
    
    public FileCacheProvider getFileCacheProvider();
    
    /**
     * Get the VCS directory reader.
     *
    public VcsCommandExecutor getVcsDirReader (DirReaderListener listener, String path);

    /**
     * Get the VCS directory reader that reads the whole directory structure.
     *
    public VcsCommandExecutor getVcsDirReaderRecursive (DirReaderListener listener, String path);
    
    /*
     * Get the VCS action for a collection of <code>FileObject</code>s.
     * If the collection is null, it should get the <code>FileObject</code>s from
     * currently selected nodes.
     * @param fos the collection of <code>FileObject</code>s or null.
     *
    public VcsAction getVcsAction (Collection fos); 
     */
    
    /*
     * Get the VCS action on the VCS filesystem for a specified <code>FileObject</code>.
     *
    public VcsAction getVcsAction (org.openide.filesystems.FileObject fo); 
     */
    
    /**
     * Get the array of VCS actions for a collection of <code>FileObject</code>s.
     * If the collection is null, it should get the <code>FileObject</code>s from
     * currently selected nodes.
     * @param fos the collection of <code>FileObject</code>s or null.
     */
    public SystemAction[] getActions (Collection fos);
    
    /**
     * Get the command executor for the command.
     * @param command the command to get the executor for
     * @param variables the <code>Hashtable</code> of (variable name, variable value) pairs
     * @return the command executor or null when no executor is found for that command.
     * @deprecated This method is retained for compatibility reasons. It may disappear
     *             after compatibility with old VCS "API" will not be needed.
     */
    public VcsCommandExecutor getCommandExecutor(VcsCommand command, Hashtable variables);

}

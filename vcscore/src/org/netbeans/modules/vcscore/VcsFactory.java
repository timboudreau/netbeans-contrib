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

package org.netbeans.modules.vcscore;

import java.util.Hashtable;
import java.util.Collection;

import org.openide.util.actions.SystemAction;

import org.netbeans.modules.vcscore.commands.VcsCommand;
import org.netbeans.modules.vcscore.commands.VcsCommandExecutor;
import org.netbeans.modules.vcscore.caching.FileStatusProvider;

/**
 *
 * @author  Pavel Buzek, Martin Entlicher
 */

public interface VcsFactory {

    //public VcsAdvancedCustomizer getVcsAdvancedCustomizer ();

    public FileStatusProvider getFileStatusProvider();

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

/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.vcscore.objectintegrity;

import org.openide.filesystems.FileObject;

import org.netbeans.api.vcs.commands.AddCommand;
import org.netbeans.api.vcs.commands.Command;

/**
 * Command that is called to assure the objects integrity.
 *
 * @author  Martin Entlicher
 */
public interface ObjectIntegrityCommand extends Command {
    
    public void setObjectIntegritySupport(VcsObjectIntegritySupport objIntSupport);
    
    public VcsObjectIntegritySupport getObjectIntegritySupport();
    
    public void setAddCommand(AddCommand addCmd);
    
    public AddCommand getAddCommand();
    
    public void setFilesToAdd(FileObject[] filesToAdd);
    
    public FileObject[] getFilesToAdd();
    
    /*
    /** Set the ignored paths, that will never be suggested for addition any more. *
    public void setIgnoredPaths(String[] paths);
    
    /** Get the ignored paths, that will never be suggested for addition any more. *
    public String[] getIgnoredPaths();
     */
    
}

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

import org.netbeans.modules.vcscore.DirReaderListener;
import org.netbeans.modules.vcscore.FileReaderListener;

/**
 * The command, that produces files and their VCS status with respect to the VCS
 * repository.
 *
 * @author  Martin Entlicher
 */
public interface FileReaderCommand extends Command {
    
    public void addFileReaderListener(FileReaderListener listener);
    
    public void removeFileReaderListener(FileReaderListener listener);
    
    public void addDirReaderListener(DirReaderListener listener);
    
    public void removeDirReaderListener(DirReaderListener listener);
    
}

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

package org.netbeans.spi.vcs;

import java.util.Collection;

import org.openide.filesystems.FileObject;

import org.netbeans.api.vcs.commands.Command;
import org.netbeans.api.vcs.commands.AddCommand;
import org.netbeans.api.vcs.commands.RemoveCommand;
import org.netbeans.api.vcs.commands.CheckInCommand;
import org.netbeans.api.vcs.commands.CheckOutCommand;
//import org.netbeans.api.vcs.commands.HistoryCommand;
//import org.netbeans.api.vcs.commands.DiffCommand;

/**
 * The provider of VCS commands and their executor.
 *
 * @author  Martin Entlicher
 */
public abstract class VcsCommandsProvider extends Object {
    
    /**
     * The name of FileObject attribute, that contains instance of VcsCommandsProvider
     * on VCS filesystems.
     */
    private static final String FO_ATTRIBUTE = "org.netbeans.spi.vcs.VcsCommandsProvider"; // NOI18N

    /**
     * Find the status provider for a FileObject.
     */
    public static VcsCommandsProvider findProvider(FileObject file) {
        return (VcsCommandsProvider) file.getAttribute(FO_ATTRIBUTE);
    }

    /**
     * Get the list of VCS command names.
     */
    public abstract String[] getCommandNames();
    
    /**
     * Create a new VCS command of the given name.
     * @return The command or <code>null</code> when the command of the given
     * name does not exist.
     */
    public abstract Command createCommand(String cmdName);
    
    /**
     * Create a new VCS command of the given class type.
     * @return The command or <code>null</code> when the command of the given
     * class type does not exist.
     */
    public abstract Command createCommand(Class cmdClass);
    
    //public abstract boolean canActOn(Set files);
    
}

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
    
    /**
     * Get the unique representation of the type of the provided version control system.
     * Paired with command name creates a unique command identification.
     * MUST be redefined to return meaningful value, it's not abstract just for
     * compatability reasons.
     * @return The unique type of the provided VCS.
     * @since 1.13
     */
    public String getType() {
        org.openide.ErrorManager.getDefault().notify(org.openide.ErrorManager.INFORMATIONAL,
                new UnsupportedOperationException("Override this method and return a String that uniquely "+
                                                  "identifies the type of the provided version control system."));
        return getClass().toString();
    }
    
}

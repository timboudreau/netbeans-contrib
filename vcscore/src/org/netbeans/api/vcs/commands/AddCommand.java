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

package org.netbeans.api.vcs.commands;

/**
 * This interface represents the VCS add command. This command will add new files
 * into the version control repository. It will create the corresponding repository
 * representation of this file.
 *
 * @author  Martin Entlicher
 */
public interface AddCommand extends MessagingCommand {
    
    /**
     * Set the files as binary in VCS repository. This serves as a suggestion
     * for the underlying VCS command, which may ignore this property if it
     * has it's own algorithm for the distinction of text and binary files.
     * @param binary True for binary files, false for text files
     */
    public void setBinary(boolean binary);
    
    /**
     * Whether the files will be treated as binary or not.
     * @return true for binary files, false for text files
     */
    public boolean isBinary();
    
}


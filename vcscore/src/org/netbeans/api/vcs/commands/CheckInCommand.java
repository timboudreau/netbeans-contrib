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
 * This interface represents the VCS check in command. This command will add
 * a new file revision into the version control repository. It's supposed,
 * that the processed files already have the corresponding repository representations
 * and new revisions will be created for them.
 * <p>
 * This command is not supposed to add new files into the repository. Use the
 * {@link AddCommand} for that purpose.
 *
 * @author  Martin Entlicher
 */
public interface CheckInCommand extends MessagingCommand {
    
}


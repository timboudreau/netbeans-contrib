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
 * This interface represents the VCS check out command. This command will retrieve
 * the last file revision (if a desired revision is not specified) from the
 * version control repository to the working space.
 *
 * @author  Martin Entlicher
 */
public interface CheckOutCommand extends RevisionCommand {
    
}


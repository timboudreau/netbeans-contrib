/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2005 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.api.vcs.commands;

/**
 * This interface represents the VCS uncheck out command.
 * This command releases the locks on files and revert them back
 * to the current versions in the version control repository.
 *
 * @author  Peter Liu
 */
public interface UncheckOutCommand extends RevisionCommand {
}

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
 * This interface represents the VCS update command.
 * This command updates files in your workspace with the latest version
 * (or the revision specified by {@link RevisionCommand#setRevision})
 * of files from the repository. No locks are applied (unlike {@link CheckOutCommand}).
 *
 * @author  Peter Liu
 */
public interface UpdateCommand extends RevisionCommand {
}

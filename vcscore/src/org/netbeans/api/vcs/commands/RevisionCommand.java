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
 * This interface represents a VCS command, that can operate on revisions.
 *
 * @author  Martin Entlicher
 */
public interface RevisionCommand extends Command {
    
    /**
     * Set a specific revision to act on.
     * @param revision The revision to act on
     */
    public void setRevision(String revision);
    
    /**
     * Get the revision to act on.
     * @return The revision to act on
     */
    public String getRevision();
    
}


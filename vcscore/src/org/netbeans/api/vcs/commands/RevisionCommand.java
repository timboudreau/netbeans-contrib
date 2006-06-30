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


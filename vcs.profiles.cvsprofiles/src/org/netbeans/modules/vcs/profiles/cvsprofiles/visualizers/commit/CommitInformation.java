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
package org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers.commit;

import org.netbeans.lib.cvsclient.command.*;

/**
 * Describes commit information for a file. This is the result of doing a
 * cvs commit command. The fields in instances of this object are populated
 * by response handlers.
 *
 * @author  Milos Kleint
 */
public class CommitInformation extends DefaultFileInfoContainer {

    public static final String ADDED = "Added";  //NOI18N
    public static final String REMOVED = "Removed";   //NOI18N
    public static final String CHANGED = "Changed"; //NOI18N
    public static final String UNKNOWN = "Unknown"; //NOI18N
    public static final String TO_ADD = "To-be-added"; //NOI18N

    /**
     * The new revision (for "Added" and "Changed") or old revision (for "Removed").
     */
    private String revision;

    public CommitInformation() {
    }

    /** Getter for property revision.
     * @return Value of property revision.
     */
    public String getRevision() {
        return revision;
    }

    /** Setter for property revision.
     * @param revision New value of property revision.
     */
    public void setRevision(String revision) {
        this.revision = revision;
    }
}
/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2004 Sun
 * Microsystems, Inc. All Rights Reserved.
 */
package org.netbeans.modules.vcscore.turbo;

import org.openide.util.NbBundle;

/**
 * Enumerates statuses with special meaning for VcsFileSystem.
 * These well known statuses can be set directly by REFRESH
 * command to assure the file status is properly recognized
 * by infrastructure (otherwise statuses are not interpreted).
 * <p>
 * TODO Profiles could be able to define real status values
 * for these symbolic constants. See FileStatusInfo.
 *
 * @author Petr Kuzel
 */
public final class Statuses {

    private static final String localStatus = NbBundle.getBundle(Statuses.class).getString("local");
    private static final String unknownStatus = NbBundle.getBundle(Statuses.class).getString("unknown");

    /**
     * The status of ignored files. These are files, that are ignored by the
     * targed version control system. It's assigned by cache if such files do
     * not get VCS specific status. The REFRESH command can assign it too.
     */
    public static final String STATUS_IGNORED = "Ignored"; // NOI18N

    /**
     * The status of dead files. These are files, that were deleted in the
     * targed version control system, but their old revisions still exist.
     * If REFRESH command mark file using this sttaus it can be recognized
     * and treated specially by VcsFileSystem (e.g. hidden but why).
     */
    public static final String STATUS_DEAD = "Dead"; // NOI18N

    // XXX see also FileStatusInfo.LOCAL
    public static String getLocalStatus() {
        return localStatus;
    }

    public static String getUnknownStatus() {
        return unknownStatus;
    }
}

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
 * Enumerates statuses that are not defined by repository.
 *
 * @author Petr Kuzel
 */
public final class Statuses {

    private static final String localStatus = NbBundle.getBundle(Statuses.class).getString("local");
    private static final String unknownStatus = NbBundle.getBundle(Statuses.class).getString("unknown");

    public static String getLocalStatus() {
        return localStatus;
    }

    public static String getUnknownStatus() {
        return unknownStatus;
    }
}

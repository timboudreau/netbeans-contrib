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

package org.netbeans.modules.tasklist.suggestions;

import org.openide.ErrorManager;

/**
 * Logs events about providers activity.
 * Usefull mainly for siagnostics purposes.
 *
 * @todo port to java.util.logging once become NB standard
 * @author Petr Kuzel
 */
final class SPIMonitor {

    private static ErrorManager err = ErrorManager.getDefault().getInstance("netbeans.debug.suggestions.spi");

    /** See these if -Dnetbeans.debug.suggestions.spi=-1 set. */
    public static void log(String msg) {
        err.log(ErrorManager.INFORMATIONAL, msg);
    }
}

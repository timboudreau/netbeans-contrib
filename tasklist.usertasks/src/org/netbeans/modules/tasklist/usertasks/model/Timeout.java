/*
 *                 Sun Public License Notice
 *
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 *
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2003 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.modules.tasklist.usertasks.model;

import org.netbeans.modules.tasklist.usertasks.*;

/**
 * This is the "callback" interface from the TimeoutProvider class...
 * @author Trond Norbye
 */
interface Timeout {
    /**
     * Callback function for the TimeoutProvider to call when the timeout
     * expired. This function will block the TimeoutProviders thread, so 
     * it should be used for a timeconsuming task (one should probably
     * reschedule oneself with the SwingUtilities.invokeLater() ???)
     * @param o the object provided as a user reference
     */
    void timeoutExpired(Object o);
};

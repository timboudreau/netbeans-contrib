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

package org.netbeans.modules.tasklist.core.util;

import java.awt.AWTEvent;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;

/**
 * Listener for the user activity
 */
public class ActivityListener implements AWTEventListener {
    private static boolean initialized = false;
    private static long lastActivity = System.currentTimeMillis();

    /**
     * Initializes the listener
     */
    public static void init() {
        if (!initialized)
            Toolkit.getDefaultToolkit().addAWTEventListener(
                new ActivityListener(), 
                AWTEvent.KEY_EVENT_MASK | AWTEvent.MOUSE_EVENT_MASK |
                AWTEvent.MOUSE_MOTION_EVENT_MASK | 
                AWTEvent.MOUSE_WHEEL_EVENT_MASK);
    }

    /**
     * Returns the time of the last activity
     *
     * @see java.lang.System.currentTimeMillis
     * @return time of the last activity
     */
    public static long getLastActivityMillis() {
        return lastActivity;
    }
    
    /**
     * Creates a new instance of ActivityListener
     */
    private ActivityListener() {
    }
    
    public void eventDispatched(AWTEvent event) {
        lastActivity = System.currentTimeMillis();
    }
}

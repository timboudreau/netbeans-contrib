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

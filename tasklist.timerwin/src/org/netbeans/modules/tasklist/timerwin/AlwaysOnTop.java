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

package org.netbeans.modules.tasklist.timerwin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.swing.JDialog;
import javax.swing.JWindow;

/**
 * "Always on top" attribute for dialogs. 
 *
 * @author tl
 */
public class AlwaysOnTop {
    private static boolean libLoaded;
    private static Method setAlwaysOnTopMethod;
    
    static {
        try {
            setAlwaysOnTopMethod = JWindow.class.getDeclaredMethod(
                    "setAlwaysOnTop", // NOI18N
                    new Class[] {Boolean.TYPE});
        } catch (Throwable t) {
            // ignore
        }
        
        if (setAlwaysOnTopMethod == null) {
            try {
                System.loadLibrary("alwaysontop"); // NOI18N
                libLoaded = true;
            } catch (Throwable t) {
                libLoaded = false;
            }
        }
    }
    
    /**
     * Sets the "always on top" attribute for a dialog.
     * Works on JDK 5.0 or Win32.
     *
     * @param d a dialog
     * @return true = success
     */
    public static boolean setAlwaysOnTop(JDialog d) {
        if (setAlwaysOnTopMethod != null) {
            try {
                setAlwaysOnTopMethod.invoke(d, new Object[] {Boolean.TRUE});
                return true;
            } catch (Throwable ex) {
                // ignore
            }
        }
        if (libLoaded) {
            return setAlwaysOnTopWin32(d);
        }
        
        return false;
    }
    
    /**
     * Sets the "always on top" attribute for a dialog on win32
     *
     * @param d a dialog
     * @return true = success
     */
    private static native boolean setAlwaysOnTopWin32(JDialog d);
}

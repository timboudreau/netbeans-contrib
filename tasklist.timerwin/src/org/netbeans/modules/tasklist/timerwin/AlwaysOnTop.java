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

package org.netbeans.modules.tasklist.timerwin;

import java.awt.Window;
import java.lang.reflect.Method;
import javax.swing.JDialog;

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
            setAlwaysOnTopMethod = Window.class.getDeclaredMethod(
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

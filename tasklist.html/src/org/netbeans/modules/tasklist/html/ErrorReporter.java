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

package org.netbeans.modules.tasklist.html;

public interface ErrorReporter {
    /** 
     * @param line The line number where the error occurred
     * @param col The column on the line where the error occurred
     * @param error If true, it's an error, otherwise it's a warning
     * @param message The message text
     */
    void reportError(int line, int col, boolean error, String message);
}


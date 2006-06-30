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
package org.netbeans.modules.vcs.profiles.cvsprofiles.visualizers;

/**
 * Assertion tool class.
 * @author  Thomas Singer
 */
public class BugLog {

    private static BugLog instance;

    public synchronized static BugLog getInstance() {
        if (instance == null) {
            instance = new BugLog();
        }
        return instance;
    }

    public synchronized static void setInstance(BugLog instance) {
        BugLog.instance = instance;
    }

    public BugLog() {
    }

    public void showException(Exception ex) {
        ex.printStackTrace();
    }

    public void assertTrue(boolean value, String message) {
        if (value) {
            return;
        }

        throw new BugException(message);
    }

    public void assertNotNull(Object obj) {
        if (obj != null) {
            return;
        }

        throw new BugException("Value must not be null!"); // NOI18N
    }

    public void bug(String message) {
        new Exception(message).printStackTrace();
    }

    public static class BugException extends RuntimeException {
        public BugException(String message) {
            super(message);
        }
    }
}

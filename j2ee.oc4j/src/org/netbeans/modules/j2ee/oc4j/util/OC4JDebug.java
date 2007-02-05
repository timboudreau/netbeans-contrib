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

package org.netbeans.modules.j2ee.oc4j.util;

/**
 *
 * @author pblaha
 */
public class OC4JDebug {
    
    private static final String PROPERTY = "serverplugins.oc4j.debug";
    private static boolean isEnabled = System.getProperty(PROPERTY) != null;   // NOI18N
    
    public static boolean isEnabled() {
        //return isEnabled;
        return true;
    }
    
    public static void log(String className, String msg) {
       if(isEnabled()) {
           System.out.println("serverplugins.oc4j.debug: Class " + className + ", Message: " + msg);
       }
    }
}
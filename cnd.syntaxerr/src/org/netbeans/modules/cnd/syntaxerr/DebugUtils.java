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
 * Portions Copyrighted 2007 Sun Microsystems, Inc.
 */
package org.netbeans.modules.cnd.syntaxerr;

/**
 * Miscelaneous (mostly trace-oriented) flags
 * @author Vladimir Kvashin
 */
public class DebugUtils {
    
    /** 
     * If set to TRUE, produces lots of tracing output to stderr 
     * Used for debugging purposes only
     */
    public static final boolean TRACE = getBoolean("cnd.synterr.trace", false); // NOI18N
    
    /** 
     * If the flag is set to FALSE, the temporary files are not deleted.
     * Used for debugging purposes only
     */
    public static final boolean CLEAN_TMP = getBoolean("cnd.synterr.clean.tmp", true); // NOI18N

    /**
     * If set to TRUE, forces data provider to sleep several seconds when invoking compiler.
     * I need this to emulate the situation when parsing takes a while
     * Used for debugging purposes only
     */
    public static final boolean SLEEP_ON_PARSE = getBoolean("cnd.synterr.sleep", false);
    
    public static boolean getBoolean(String name, boolean result) {
        String text = System.getProperty(name);
        if( text != null ) {
            result = Boolean.parseBoolean(text);
        }
        return result;
    } 
    
    public static void sleep(long timeout) {
        System.err.printf("Sleeping for %d ms\n", timeout);
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException ex) {
        }        
        System.err.printf("Awoke\n");
    }
    
}

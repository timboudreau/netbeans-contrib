/*
 * Scanner.java
 *
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2002 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * Contributor(s): Thomas Ball
 *
 * Version: $Revision$
 */

package org.netbeans.modules.metrics;

import org.netbeans.modules.classfile.Code;
import org.netbeans.modules.classfile.Method;

/**
 * Code shared between scanners.
 *
 * @author  tball
 * @version 
 */
abstract class Scanner {

    protected Method method;
    protected byte[] codeBytes;
    protected int offset;

    /** Don't instantiate. */
    protected Scanner(Method m) {
        this.method = m;
        Code code = m.getCode();
        if (code == null) // true for abstract methods
            codeBytes = new byte[0];
        else
            codeBytes = code.getByteCodes();
    }

    /**
     * Return the byte stored at a given index from the offset
     * within code bytes
     */  
    protected final int at(int index) {
    	return codeBytes[offset+index] & 0xFF;
    }

    /**
     * Return the short stored at a given index from the offset
     * within code bytes
     */  
    protected final int shortAt(int index) {
        int base = offset + index;
    	return ((codeBytes[base] & 0xFF) << 8) 
    	    | (codeBytes[base+1] & 0xFF);
    }

    /**
     * Given the table at the specified index, return the specified entry
     */  
    protected final long intAt(int tbl, int entry) {
        int base = tbl + (entry << 2);
    	return (codeBytes[base] << 24) 
    	    | ((codeBytes[base+1] & 0xFF) << 16)
    	    | ((codeBytes[base+2] & 0xFF) << 8)
    	    | ((codeBytes[base+3] & 0xFF));
    }
}

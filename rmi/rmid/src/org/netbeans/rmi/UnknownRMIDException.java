/*
 *                 Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License
 * Version 1.0 (the "License"). You may not use this file except in
 * compliance with the License. A copy of the License is available at
 * http://www.sun.com/
 * 
 * The Original Code is NetBeans. The Initial Developer of the Original
 * Code is Sun Microsystems, Inc. Portions Copyright 1997-2001 Sun
 * Microsystems, Inc. All Rights Reserved.
 */

package org.netbeans.rmi;


/** Unknown rmid implementation version.
 * @author  Jan Pokorsky
 */    
public final class UnknownRMIDException extends Exception {

    private static final String ERR_WrongImplementation = "Unknown rmid implementation."; // NOI18N
    public Exception detail;

    public UnknownRMIDException() {
        this(ERR_WrongImplementation, null);
    }

    public UnknownRMIDException(String s) {
        this(s, null);
    }

    public UnknownRMIDException(Exception detail) {
        this(ERR_WrongImplementation, detail);
    }

    public UnknownRMIDException(String s,Exception detail) {
        super(s);
        this.detail = detail;
    }

}

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

package org.netbeans.modules.corba.utils;

/**
 *
 * @author  Tomas Zezula
 */
public class InvalidIORException extends Exception {
    
    private Exception rootCaseException;
    
    /** Creates a new instance of InvalidIORException */
    public InvalidIORException () {
        super ();
    }
    
    public InvalidIORException (String message) {
        super (message);
    }
    
    public InvalidIORException (Exception rootCaseException) {
        this.rootCaseException = rootCaseException;
    }
    
    
    public Exception getRootCaseException () {
        return this.rootCaseException;
    }
    
    
    public String toString () {
        if (this.rootCaseException != null) {
            return this.rootCaseException.toString ();
        }
        else if (this.getMessage () != null) {
            return this.getMessage ();
        }
        else {
            return super.toString ();
        }
    }
    
}

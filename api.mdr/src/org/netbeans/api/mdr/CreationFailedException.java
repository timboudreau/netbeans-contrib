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
package org.netbeans.api.mdr;

/** Exception thrown when creation of a new repository package instance fails.
 *
 * @author Martin Matula
 * @version 0.1
 */
public class CreationFailedException extends java.lang.Exception {
    
    /**
     * Creates new <code>CreationFailedException</code> without detail message.
     */
    public CreationFailedException() {
    }
    
    
    /**
     * Constructs an <code>CreationFailedException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public CreationFailedException(String msg) {
        super(msg);
    }
}



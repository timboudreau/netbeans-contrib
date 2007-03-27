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

package org.netbeans.api.jackpot;

/**
 * Returns an Exception or Error thrown during Query or Transformer execution.
 */
public class QueryException extends RuntimeException {
    
    /**
     * Constructs a new exception with the specified cause and a detail
     * message of <tt>(cause==null ? null : cause.toString())</tt> (which
     * typically contains the class and detail message of <tt>cause</tt>).
     *
     * @param  cause the cause (which is saved for later retrieval by the
     *         {@link #getCause()} method).  (A <tt>null</tt> value is
     *         permitted, and indicates that the cause is nonexistent or
     *         unknown.)
     */
    public QueryException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a new exception with the specified detail message.  The
     * cause is not initialized.
     *
     * @param   message   the detail message.
     */
    public QueryException(String message) {
	super(message);
    }

    public String toString() {
	Throwable cause = getCause();
        return (cause != null) ? 
	    ("QueryException: " + cause.toString()) : getMessage();
    }

    static final long serialVersionUID = -7166849769175914785L;
}

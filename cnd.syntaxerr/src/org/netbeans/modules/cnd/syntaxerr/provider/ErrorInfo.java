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
package org.netbeans.modules.cnd.syntaxerr.provider;

/**
 * Represents an error or warning
 * @author Vladimir Kvashin
 */
public interface ErrorInfo {
    
    /**
     * Represents severity level
     */
    enum Severity {
        ERROR,
        WARNING
    }
    
    /** 
     * Gets error message 
     * @return the message
     */
    String getMessage();
    
    /** 
     * Gets severity 
     * @return this error/warning message severity
     */
    Severity getSeverity();

    /**
     * Gets the line number (as reported by compiler)
     * @return line number
     */
    public int getLineNumber();

    /**
     * Gets the column number (as reported by compiler)
     * @return column number; -1 means that the column is 
     */
    public int getColumn();
    
}


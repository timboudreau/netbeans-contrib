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
 * A trivial ErrorInfo implementation
 * @author Vladimir Kvashin
 */
public class ErrorInfoImpl implements ErrorInfo {
    
    private String message;
    private Severity severity;
    private int line;
    private int column;

    public ErrorInfoImpl(String message, Severity severity, int line) {
        this(message, severity, line, -1);
    }
    
    public ErrorInfoImpl(String message, Severity severity, int line, int column) {
        this.message = message;
        this.severity = severity;
        this.line = line;
        this.column = column;
    }
    
    
    public int getColumn() {
        return column;
    }

    public int getLineNumber() {
        return line;
    }

    public String getMessage() {
        return message;
    }

    public Severity getSeverity() {
        return severity;
    }

}

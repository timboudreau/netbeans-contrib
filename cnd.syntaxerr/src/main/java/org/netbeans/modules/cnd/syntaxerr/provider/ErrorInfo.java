/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2007 Sun Microsystems, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
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

